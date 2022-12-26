package vip.linhs.stock.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vip.linhs.stock.api.TradeResultVo;
import vip.linhs.stock.api.request.*;
import vip.linhs.stock.api.request.SubmitBatTradeV2Request.SubmitData;
import vip.linhs.stock.api.response.*;
import vip.linhs.stock.api.response.GetCanBuyNewStockListV3Response.NewQuotaInfo;
import vip.linhs.stock.config.SpringUtil;
import vip.linhs.stock.dao.ExecuteInfoDao;
import vip.linhs.stock.model.po.*;
import vip.linhs.stock.model.vo.PageParam;
import vip.linhs.stock.model.vo.PageVo;
import vip.linhs.stock.model.vo.TaskVo;
import vip.linhs.stock.model.vo.trade.TradeRuleVo;
import vip.linhs.stock.service.*;
import vip.linhs.stock.trategy.handle.StrategyHandler;
import vip.linhs.stock.util.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private Map<String, BigDecimal> lastPriceMap = new HashMap<>();
    private Map<String, String> last500Map = new HashMap<>();

    @Value("${ocr.service}")
    private String ocrServiceName;

    @Autowired
    private HolidayCalendarService holidayCalendarService;

    @Autowired
    private ExecuteInfoDao executeInfoDao;

    @Autowired
    private StockCrawlerService stockCrawlerService;

    @Autowired
    private StockService stockService;

    @Autowired
    private MessageService messageServicve;

    @Autowired
    private StockSelectedService stockSelectedService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeApiService tradeApiService;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CloseableHttpClient httpClient;

    private static final boolean CrawIndexFromSina = false;

    @Override
    public List<ExecuteInfo> getPendingTaskListById(int... id) {
        return executeInfoDao.getByTaskIdAndState(id, StockConsts.TaskState.Pending.value());
    }

    @Override
    public void executeTask(ExecuteInfo executeInfo) {
        executeInfo.setStartTime(new Date());
        executeInfo.setMessage("");
        int id = executeInfo.getTaskId();
        Task task = Task.valueOf(id);
        try {
            switch (task) {
                case BeginOfYear:
                    holidayCalendarService.updateCurrentYear();
                    break;
                case BeginOfDay:
                    lastPriceMap.clear();
                    break;
                case UpdateOfStock:
                    runUpdateOfStock();
                    break;
                case UpdateOfDailyIndex:
                    runUpdateOfDailyIndex();
                    break;
                case Ticker:
                    //runTicker();
                    runTickerMy();
                    break;
                case TradeTicker:
                    runTradeTicker();
                    break;
                case ApplyNewStock:
                    applyNewStock();
                    break;
                case AutoLogin:
                    autoLogin();
                    break;
                case UpdateOfStockInfo:
                    updateOfStockInfo();
                    break;
                case UpdateOfSelectEdInfo:
                    updateOfselectEdInfo();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            executeInfo.setMessage(e.getMessage());
            logger.error("task {} {} error", task.getName(), executeInfo.getId(), e);

            String body = String.format("task: %s, error: %s", task.getName(), e.getMessage());
            messageServicve.send(body);
        }

        executeInfo.setCompleteTime(new Date());
        executeInfoDao.update(executeInfo);
    }

    private void updateOfStockInfo() {
        //stockService.fixDailyIndex(Integer.parseInt(DateFormatUtils.format(new Date(), "yyyyMM")), null);
        stockService.fixDailyIndex(202205, null);


//        stockService.fixDailyIndex(202202, null);
//        stockService.fixDailyIndex(202201, null);
    }

    private void updateOfselectEdInfo() {
        stockService.momoRetracement();
    }

    private void autoLogin() {
        final int userId = 1;
        TradeUser tradeUser = tradeService.getTradeUserById(userId);
        TradeMethod tradeMethod = tradeService.getTradeMethodByName(BaseTradeRequest.TradeRequestMethod.YZM.value());

        String randNum = "0.903" + new Date().getTime();
        String yzmUrl = tradeMethod.getUrl() + randNum;

        OcrService ocrService = SpringUtil.getBean(ocrServiceName, OcrService.class);
        String identifyCode = ocrService.process(yzmUrl);

        AuthenticationRequest request = new AuthenticationRequest(tradeUser.getId());
        request.setIdentifyCode(identifyCode);
        request.setRandNumber(randNum);
        request.setPassword(tradeUser.getPassword());

        TradeResultVo<AuthenticationResponse> resultVo = tradeApiService.authentication(request);
        if (resultVo.isSuccess()) {
            AuthenticationResponse response = resultVo.getData().get(0);
            tradeUser.setCookie(response.getCookie());
            tradeUser.setValidateKey(response.getValidateKey());
            tradeService.updateTradeUser(tradeUser);
        } else {
            logger.error("auto login {} {}", request, resultVo.getMessage());
            throw new RuntimeException("auto login failed");
        }

    }

    private void runUpdateOfStock() {
        List<StockInfo> list = stockService.getAll().stream().filter(v -> !v.isIndex()).collect(Collectors.toList());
        Map<String, List<StockInfo>> dbStockMap = list.stream().collect(Collectors.groupingBy(StockInfo::getCode));

        ArrayList<StockInfo> needAddedList = new ArrayList<>();
        ArrayList<StockInfo> needUpdatedList = new ArrayList<>();
        ArrayList<StockLog> stockLogList = new ArrayList<>();

        final Date date = new Date();

        List<StockInfo> crawlerList = stockCrawlerService.getStockList();
        for (StockInfo stockInfo : crawlerList) {
            StockConsts.StockLogType stocLogType = null;
            List<StockInfo> stockGroupList = dbStockMap.get(stockInfo.getCode());
            String oldValue = null;
            String newValue = null;
            if (stockGroupList == null) {
                stocLogType = StockConsts.StockLogType.New;
                oldValue = "";
                newValue = stockInfo.getName();
            } else {
                StockInfo stockInfoInDb = stockGroupList.get(0);
                if (!stockInfo.getName().equals(stockInfoInDb.getName())
                        && StockUtil.isOriName(stockInfo.getName())) {
                    stocLogType = StockConsts.StockLogType.Rename;
                    oldValue = stockInfoInDb.getName();
                    newValue = stockInfo.getName();
                    stockInfo.setId(stockInfoInDb.getId());
                }
            }

            if (stocLogType != null) {
                StockLog stockLog = new StockLog(stockInfo.getId(), date, stocLogType.value(), oldValue, newValue);
                if (stocLogType == StockConsts.StockLogType.New) {
                    needAddedList.add(stockInfo);
                } else {
                    needUpdatedList.add(stockInfo);
                }
                stockLogList.add(stockLog);
            }
        }

        stockService.update(needAddedList, needUpdatedList, stockLogList);
    }

    private void runUpdateOfDailyIndex() {
        List<StockInfo> list = stockService.getAll().stream()
                .filter(stockInfo -> (stockInfo.isA() || stockInfo.isIndex()) && stockInfo.isValid())
                .collect(Collectors.toList());

        Date date = new Date();

        List<DailyIndex> dailyIndexList = stockService.getDailyIndexListByDate(date);
        List<String> codeList = dailyIndexList.stream().map(DailyIndex::getCode).collect(Collectors.toList());
        list = list.stream().filter(v -> !codeList.contains(v.getFullCode())).collect(Collectors.toList());

        if (CrawIndexFromSina) {
            crawDailyIndexFromSina(list);
        } else {
            crawDailyIndexFromSina(list.stream().filter(StockInfo::isIndex).collect(Collectors.toList()));
            crawDailyIndexFromEastMoney(list);
        }
    }

    private void crawDailyIndexFromEastMoney(List<StockInfo> list) {
        List<DailyIndex> dailyIndexList = stockCrawlerService.getDailyIndexFromEastMoney();
        dailyIndexList = dailyIndexList.stream().filter(d -> list.stream().anyMatch(s -> d.getCode().equals(s.getFullCode()))).collect(Collectors.toList());
        stockService.saveDailyIndex(filterInvalid(dailyIndexList));
    }

    private void crawDailyIndexFromSina(List<StockInfo> list) {
        final int tCount = 500;
        ArrayList<String> stockCodeList = new ArrayList<>(tCount);
        for (StockInfo stockInfo : list) {
            stockCodeList.add(stockInfo.getFullCode());
            if (stockCodeList.size() == tCount) {
                saveDailyIndex(stockCodeList);
                stockCodeList.clear();
            }
        }

        if (!stockCodeList.isEmpty()) {
            saveDailyIndex(stockCodeList);
        }
    }

    private void saveDailyIndex(ArrayList<String> stockCodeList) {
        List<DailyIndex> dailyIndexList = stockCrawlerService.getDailyIndex(stockCodeList);
        stockService.saveDailyIndex(filterInvalid(dailyIndexList));
    }

    private List<DailyIndex> filterInvalid(List<DailyIndex> dailyIndexList) {
        final String currentDateStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        return dailyIndexList.stream().filter(dailyIndex ->
                DecimalUtil.bg(dailyIndex.getOpeningPrice(), BigDecimal.ZERO)
                        && dailyIndex.getTradingVolume() > 0
                        && DecimalUtil.bg(dailyIndex.getTradingValue(), BigDecimal.ZERO)
                        && currentDateStr.equals(DateFormatUtils.format(dailyIndex.getDate(), "yyyy-MM-dd"))
        ).collect(Collectors.toList());
    }

    /**
     * http://500order.10jqka.com.cn:8081/ordersque?market=USZA&code=002567&orderside=2&orderlevels=1-10
     * orderside 这里填写1是买盘、2是卖盘
     * 沪市的股票吧USZA改成USHA
     */
    @Autowired
    private DingTalkUtil dingTalker;

    private void runTickerMy() {
        List<StockSelected> selectList = stockSelectedService.getList();

        for (StockSelected stockSelected : selectList) {
            StringBuilder sb = new StringBuilder();
            sb.append(stockSelected.getCode());
            //买盘价格检查
            String url = "http://500order.10jqka.com.cn:8081/ordersque?market=" + stockSelected.getDescription() + "&code=" + stockSelected.getCode() + "&orderside=1&orderlevels=1-200";
            JSONObject bodyJson = JSONObject.parseObject(HttpUtil.sendGet(httpClient, url));
            sb.append("买价：");
            for (Object j : bodyJson.getJSONArray("orderlevels")) {
                JSONObject jsonObject = JSONObject.parseObject(j.toString());
                if (jsonObject.getString("ordersque").contains("50000") || jsonObject.getString("ordersque").contains("134267728")) {
                    sb.append(jsonObject.getString("price") + "，");
                }
            }

            //卖盘价格检查
            url = "http://500order.10jqka.com.cn:8081/ordersque?market=" + stockSelected.getDescription() + "&code=" + stockSelected.getCode() + "&orderside=2&orderlevels=1-200";
            bodyJson = JSONObject.parseObject(HttpUtil.sendGet(httpClient, url));
            sb.append("卖价：");
            for (Object m : bodyJson.getJSONArray("orderlevels")) {
                JSONObject jsonObjectm = JSONObject.parseObject(m.toString());
                if (jsonObjectm.getString("ordersque").contains("50000") || jsonObjectm.getString("ordersque").contains("134267728")) {
                    sb.append(jsonObjectm.getString("price") + "，");
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
                if (last500Map.get(stockSelected.getCode()) != null && last500Map.get(stockSelected.getCode()).equals(sb.toString())) {
                    logger.debug(stockSelected.getCode() + "无变化");
                }else {
                    last500Map.put(stockSelected.getCode(), sb.toString());
                    messageServicve.send(sb.toString());
                    try {
                        dingTalker.sendMsg("code", sb.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


    }


    private void runTicker() {
        List<StockSelected> selectList = stockSelectedService.getList();
        List<String> codeList = selectList.stream().map(v -> StockUtil.getFullCode(v.getCode())).collect(Collectors.toList());
        List<DailyIndex> dailyIndexList = stockCrawlerService.getDailyIndex(codeList);

        StringBuilder sb = new StringBuilder();
        for (StockSelected stockSelected : selectList) {
            String code = stockSelected.getCode();
            DailyIndex dailyIndex = dailyIndexList.stream().filter(d -> d.getCode().contains(stockSelected.getCode())).findAny().orElse(null);
            if (dailyIndex == null) {
                continue;
            }
            if (lastPriceMap.containsKey(code)) {
                BigDecimal lastPrice = lastPriceMap.get(code);
                double rate = Math.abs(StockUtil.calcIncreaseRate(dailyIndex.getClosingPrice(), lastPrice).doubleValue());
                if (Double.compare(rate, stockSelected.getRate().doubleValue()) >= 0) {
                    lastPriceMap.put(code, dailyIndex.getClosingPrice());
                    String name = stockService.getStockByFullCode(StockUtil.getFullCode(code)).getName();
                    String body = String.format("%s:当前价格:%.02f, 涨幅%.02f%%", name,
                            dailyIndex.getClosingPrice().doubleValue(),
                            StockUtil.calcIncreaseRate(dailyIndex.getClosingPrice(),
                                    dailyIndex.getPreClosingPrice()).movePointRight(2).doubleValue());
                    sb.append(body + "\n");
                }
            } else {
                lastPriceMap.put(code, dailyIndex.getPreClosingPrice());
                String name = stockService.getStockByFullCode(StockUtil.getFullCode(code)).getName();
                String body = String.format("%s:当前价格:%.02f", name, dailyIndex.getClosingPrice().doubleValue());
                sb.append(body + "\n");
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            messageServicve.send(sb.toString());
        }
    }

    private void runTradeTicker() {
        runStrategy();
        runDealNotice();
    }

    private void runStrategy() {
        PageParam pageParam = new PageParam();
        pageParam.setStart(0);
        pageParam.setLength(Integer.MAX_VALUE);
        //获取自动交易的股票，并匹配对应的策略。
        PageVo<TradeRuleVo> pageVo = tradeService.getTradeRuleList(pageParam);

        pageVo.getData().forEach(v -> {
            if (v.isValid()) {
                String beanName = v.getStrategyBeanName();
                StrategyHandler strategyHandler = SpringUtil.getBean(beanName, StrategyHandler.class);
                try {
                    //执行对应的策略
                    strategyHandler.handle(v);
                } catch (Exception e) {
                    logger.error("strategyHandler {} {} error", v.getStockCode(), v.getStrategyName(), e);
                }
            }
        });
    }

    private void runDealNotice() {
        TradeResultVo<GetDealDataResponse> dealData = tradeApiService.getDealData(new GetDealDataRequest(1));
        if (!dealData.isSuccess()) {
            logger.error("runDealNotice error {}", dealData.getMessage());
        }
        List<GetDealDataResponse> list = TradeUtil.mergeDealList(dealData.getData());
        List<TradeDeal> tradeDealList = tradeService.getTradeDealListByDate(new Date());

        List<String> dealCodeList = tradeDealList.stream().map(TradeDeal::getDealCode).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();

        List<TradeDeal> needNotifyList = list.stream().filter(v -> !dealCodeList.contains(v.getCjbh())).map(v -> {
            TradeDeal tradeDeal = new TradeDeal();
            tradeDeal.setDealCode(v.getCjbh());
            tradeDeal.setPrice(new BigDecimal(v.getCjjg()));
            tradeDeal.setStockCode(v.getZqdm());

            Date tradeTime = new Date();
            tradeTime = DateUtils.setHours(tradeTime, Integer.valueOf(v.getCjsj().substring(0, 2)));
            tradeTime = DateUtils.setMinutes(tradeTime, Integer.valueOf(v.getCjsj().substring(2, 4)));
            tradeTime = DateUtils.setSeconds(tradeTime, Integer.valueOf(v.getCjsj().substring(4, 6)));

            tradeDeal.setTradeTime(tradeTime);
            tradeDeal.setTradeType(v.getMmlb());
            tradeDeal.setVolume(Integer.valueOf(v.getCjsl()));

            sb.append(String.format("deal %s %s %s %s %s\n",
                    v.getFormatDealTime(), v.getZqmc(), v.getMmlb(), v.getCjjg(), v.getCjsl()));

            return tradeDeal;
        }).collect(Collectors.toList());

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            messageServicve.send(sb.toString());
        }

        tradeService.saveTradeDealList(needNotifyList);
    }

    private void applyNewStock() {
        TradeResultVo<GetCanBuyNewStockListV3Response> getCanBuyResultVo = getCanBuyStockListV3ResultVo();

        GetCanBuyNewStockListV3Response getCanBuyResponse = getCanBuyResultVo.getData().get(0);

        List<SubmitData> newStockList = getCanBuyResponse.getNewStockList().stream()
                .filter(newStock -> getCanBuyResponse.getNewQuota().stream().anyMatch(v -> v.getMarket().equals(newStock.getMarket())))
                .map(newStock -> {
                    NewQuotaInfo newQuotaInfo = getCanBuyResponse.getNewQuota().stream().filter(v -> v.getMarket().equals(newStock.getMarket())).findAny().orElse(null);
                    SubmitData submitData = new SubmitData();

                    submitData.setAmount(Integer.min(Integer.parseInt(newStock.getKsgsx()), Integer.parseInt(newQuotaInfo.getKsgsz())));
                    submitData.setMarket(newStock.getMarket());
                    submitData.setPrice(newStock.getFxj());
                    submitData.setStockCode(newStock.getSgdm());
                    submitData.setStockName(newStock.getZqmc());
                    submitData.setTradeType(SubmitRequest.B);
                    return submitData;
                }).collect(Collectors.toList());

        if (systemConfigService.isApplyNewConvertibleBond()) {
            TradeResultVo<GetConvertibleBondListV2Response> getConvertibleBondResultVo = getGetConvertibleBondListV2ResultVo();
            if (getConvertibleBondResultVo.isSuccess()) {
                List<SubmitData> convertibleBondList = getConvertibleBondResultVo.getData().stream().filter(GetConvertibleBondListV2Response::getExIsToday).map(convertibleBond -> {
                    SubmitData submitData = new SubmitData();
                    submitData.setAmount(Integer.parseInt(convertibleBond.getLIMITBUYVOL()));
                    submitData.setMarket(convertibleBond.getMarket());
                    submitData.setPrice(convertibleBond.getPARVALUE());
                    submitData.setStockCode(convertibleBond.getSUBCODE());
                    submitData.setStockName(convertibleBond.getBONDNAME());
                    submitData.setTradeType(SubmitRequest.B);
                    return submitData;
                }).collect(Collectors.toList());

                newStockList.addAll(convertibleBondList);
            } else {
                messageServicve.send("apply new stock: " + getConvertibleBondResultVo.getMessage());
            }
        }

        TradeResultVo<GetOrdersDataResponse> orderReponse = tradeApiService.getOrdersData(new GetOrdersDataRequest(1));
        if (orderReponse.isSuccess()) {
            List<GetOrdersDataResponse> orderList = orderReponse.getData().stream().filter(v -> v.getWtzt().equals(GetOrdersDataResponse.YIBAO)).collect(Collectors.toList());
            newStockList = newStockList.stream().filter(v -> orderList.stream().noneMatch(order -> order.getZqdm().equals(v.getStockCode()))).collect(Collectors.toList());
        }

        if (newStockList.isEmpty()) {
            return;
        }

        SubmitBatTradeV2Request request = new SubmitBatTradeV2Request(1);
        request.setList(newStockList);

        TradeResultVo<SubmitBatTradeV2Response> tradeResultVo = tradeApiService.submitBatTradeV2(request);
        messageServicve.send("apply new stock: " + tradeResultVo.getMessage());
    }

    private TradeResultVo<GetCanBuyNewStockListV3Response> getCanBuyStockListV3ResultVo() {
        GetCanBuyNewStockListV3Request request = new GetCanBuyNewStockListV3Request(1);
        return tradeApiService.getCanBuyNewStockListV3(request);
    }

    private TradeResultVo<GetConvertibleBondListV2Response> getGetConvertibleBondListV2ResultVo() {
        GetConvertibleBondListV2Request request = new GetConvertibleBondListV2Request(1);
        return tradeApiService.getConvertibleBondListV2(request);
    }

    @Override
    public PageVo<TaskVo> getAllTask(PageParam pageParam) {
        return executeInfoDao.get(pageParam);
    }

    @Override
    public void changeTaskState(int state, int id) {
        executeInfoDao.updateState(state, id);
    }

}
