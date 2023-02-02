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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
                case UpdateOfStockZt:
                    updateStockZt();
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

    public void updateStockZt() {
        String dateStr="20230130,20230131,20230201,20230202,20230120,20230119,20230118,20230117,20230116,20230113,20230112,20230111,20230110,20230109,20230106,20230105,20230104,20230103,20221230,20221229,20221228,20221227,20221226,20221223,20221222,20221221,20221220,20221219,20221216,20221215,20221214,20221213,20221212,20221209,20221208,20221207,20221206,20221205,20221202,20221201,20221130,20221129,20221128,20221125,20221124,20221123,20221122,20221121,20221118,20221117,20221116,20221115,20221114,20221111,20221110,20221109,20221108,20221107,20221104,20221103,20221102,20221101,20221031,20221028,20221027,20221026,20221025,20221024,20221021,20221020,20221019,20221018,20221017,20221014,20221013,20221012,20221011,20221010,20220930,20220929,20220928,20220927,20220926,20220923,20220922,20220921,20220920,20220919,20220916,20220915,20220914,20220913,20220909,20220908,20220907,20220906,20220905,20220902,20220901,20220831,20220830,20220829,20220826,20220825,20220824,20220823,20220822,20220819,20220818,20220817,20220816,20220815,20220812,20220811,20220810,20220809,20220808,20220805,20220804,20220803,20220802,20220801,20220729,20220728,20220727,20220726,20220725,20220722,20220721,20220720,20220719,20220718,20220715,20220714,20220713,20220712,20220711,20220708,20220707,20220706,20220705,20220704,20220701,20220630,20220629,20220628,20220627,20220624,20220623,20220622,20220621,20220620,20220617,20220616,20220615,20220614,20220613,20220610,20220609,20220608,20220607,20220606,20220602,20220601,20220531,20220530,20220527,20220526,20220525,20220524,20220523,20220520,20220519,20220518,20220517,20220516,20220513,20220512,20220511,20220510,20220509,20220506,20220505,20220429,20220428,20220427,20220426,20220425,20220422,20220421,20220420,20220419,20220418,20220415,20220414,20220413,20220412,20220411,20220408,20220407,20220406,20220401,20220331,20220330,20220329,20220328,20220325,20220324,20220323,20220322,20220321,20220318,20220317,20220316,20220315,20220314,20220311,20220310,20220309,20220308,20220307,20220304,20220303,20220302,20220301,20220228,20220225,20220224,20220223,20220222,20220221,20220218,20220217,20220216,20220215,20220214,20220211,20220210,20220209,20220208,20220207,20220128,20220127,20220126,20220125,20220124,20220121,20220120,20220119,20220118,20220117,20220114,20220113,20220112,20220111,20220110,20220107,20220106,20220105,20220104";
        for(String date:dateStr.split(",")) {
            List<StockInfo> ddxgubitList = stockCrawlerService.getZTfromddxgubitcn(date);
        updateStockZtByList(ddxgubitList);
        List<StockInfo> jiuyangongsheList = stockCrawlerService.getZTfromTonghuashun(date);
        updateStockZtByList(jiuyangongsheList);

        }

//        List<StockInfo> ddxgubitList = stockCrawlerService.getZTfromddxgubitcn(new SimpleDateFormat("yyyyMMdd").format(new Date()));
//        updateStockZtByList(ddxgubitList);
//        List<StockInfo> jiuyangongsheList = stockCrawlerService.getZTfromTonghuashun(new SimpleDateFormat("yyyyMMdd").format(new Date()));
//        updateStockZtByList(jiuyangongsheList);
    }

    private void updateStockZtByList(List<StockInfo> dataList) {
//        List<StockInfo> list = stockService.getZtAll().stream().filter(v -> v.getState() == StockConsts.StockZTState.Valid.value()).collect(Collectors.toList());
//        Map<String,StockInfo> dbStockMap = list.stream().collect(Collectors.toMap(stockInfo ->getStockInfoString(stockInfo),stockInfo -> stockInfo));

        ArrayList<StockInfo> needAddedList = new ArrayList<>();
        ArrayList<StockInfo> needUpdatedList = new ArrayList<>();
        ArrayList<StockLog> stockLogList = new ArrayList<>();

        for (StockInfo stockInfo : dataList) {
            StockInfo StockInfoExit = stockService.getStockZtByStock(stockInfo);
            if (StockInfoExit == null) {
                needAddedList.add(stockInfo);
            }
        }
        stockService.updatezt(needAddedList, needUpdatedList, stockLogList);
    }

    private String getStockInfoString(StockInfo stockInfo) {
        return stockInfo.getCode() + stockInfo.getExchange() + stockInfo.getTag() + stockInfo.getType() + stockInfo.getCreateTime();
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
        List<String> codeList = selectList.stream().map(v -> StockUtil.getFullCode(v.getCode())).collect(Collectors.toList());
        List<DailyIndex> dailyIndexList = stockCrawlerService.getDailyIndex(codeList);

        for (StockSelected stockSelected : selectList) {
            DailyIndex dailyIndex = dailyIndexList.stream().filter(d -> d.getCode().contains(stockSelected.getCode())).findAny().orElse(null);
            if (dailyIndex == null) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            boolean importInfo = false;
            sb.append("%s");
            sb.append("高：" + dailyIndex.getHighestPrice());
            sb.append("现：%s" );
            sb.append("低：" + dailyIndex.getLowestPrice());
            //买盘价格检查
            String url = "http://500order.10jqka.com.cn:8081/ordersque?market=" + stockSelected.getDescription() + "&code=" + stockSelected.getCode() + "&orderside=1&orderlevels=1-200";
            JSONObject bodyJson = JSONObject.parseObject(HttpUtil.sendGet(httpClient, url));
            sb.append("买：");
            for (Object j : bodyJson.getJSONArray("orderlevels")) {
                JSONObject jsonObject = JSONObject.parseObject(j.toString());
                if (Double.valueOf(jsonObject.getString("price")) < Double.valueOf(dailyIndex.getHighestPrice().doubleValue()) && Double.valueOf(jsonObject.getString("price")) > Double.valueOf(dailyIndex.getLowestPrice().doubleValue())) {
                    importInfo=true;

                int count = 0;
                    for (Object tempNum : jsonObject.getJSONArray("ordersque")) {
                        int tempNumDouble = Integer.valueOf(tempNum.toString());
                        if (tempNumDouble == 134267728 || tempNumDouble == 50000) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        sb.append(jsonObject.getString("price") + "-" + count + ",");
                    }
                }
            }

            //卖盘价格检查
            url = "http://500order.10jqka.com.cn:8081/ordersque?market=" + stockSelected.getDescription() + "&code=" + stockSelected.getCode() + "&orderside=2&orderlevels=1-200";
            bodyJson = JSONObject.parseObject(HttpUtil.sendGet(httpClient, url));
            sb.append("卖：");
            for (Object m : bodyJson.getJSONArray("orderlevels")) {
                JSONObject jsonObjectm = JSONObject.parseObject(m.toString());
                if (Double.valueOf(jsonObjectm.getString("price")) < Double.valueOf(dailyIndex.getHighestPrice().doubleValue()) && Double.valueOf(jsonObjectm.getString("price")) > Double.valueOf(dailyIndex.getLowestPrice().doubleValue())) {
                    importInfo=true;

                int count = 0;
                    for (Object tempNum : jsonObjectm.getJSONArray("ordersque")) {
                        int tempNumDouble = Integer.valueOf(tempNum.toString());
                        if (tempNumDouble == 134267728 || tempNumDouble == 50000 ) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        sb.append(jsonObjectm.getString("price") + "-" + count + ",");
                    }
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
                if (last500Map.get(stockSelected.getCode()) != null && last500Map.get(stockSelected.getCode()).equals(sb.toString())) {
                    logger.debug(stockSelected.getCode() + "无变化");
                }else {
                    last500Map.put(stockSelected.getCode(), sb.toString());
                    messageServicve.sendMd(stockSelected.getName(),String.format(sb.toString(), importInfo ? "★️" : "", dailyIndex.getClosingPrice()));
//                    try {
//                        dingTalker.sendMsg("code", sb.toString());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
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
