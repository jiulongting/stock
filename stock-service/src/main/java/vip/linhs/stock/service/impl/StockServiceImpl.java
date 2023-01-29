package vip.linhs.stock.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import vip.linhs.stock.dao.DailyIndexDao;
import vip.linhs.stock.dao.StockInfoDao;
import vip.linhs.stock.dao.StockLogDao;
import vip.linhs.stock.model.po.DailyIndex;
import vip.linhs.stock.model.po.StockInfo;
import vip.linhs.stock.model.po.StockLog;
import vip.linhs.stock.model.vo.DailyIndexVo;
import vip.linhs.stock.model.vo.PageParam;
import vip.linhs.stock.model.vo.PageVo;
import vip.linhs.stock.parser.DailyIndexParser;
import vip.linhs.stock.service.HolidayCalendarService;
import vip.linhs.stock.service.StockCrawlerService;
import vip.linhs.stock.service.StockService;
import vip.linhs.stock.util.DateUtil;
import vip.linhs.stock.util.StockConsts;

@Service
public class StockServiceImpl implements StockService {

    private final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    private static final String LIST_MESSAGE = "'list' must not be null";

    @Autowired
    private StockInfoDao stockInfoDao;

    @Autowired
    private StockLogDao stockLogDao;

    @Autowired
    private DailyIndexDao dailyIndexDao;

    @Autowired
    private StockCrawlerService stockCrawlerService;

    @Autowired
    private DailyIndexParser dailyIndexParser;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private HolidayCalendarService holidayCalendarService;
    @Override
    public List<StockInfo> getAll() {
        PageParam pageParam = getPageParam();
        PageVo<StockInfo> pageVo = stockInfoDao.get(pageParam);
        return pageVo.getData();
    }

    private PageParam getPageParam() {
        PageParam pageParam = new PageParam();
        pageParam.setStart(0);
        pageParam.setLength(Integer.MAX_VALUE);
        return pageParam;
    }

    public List<StockInfo> getZtAll() {
        PageParam pageParam = getPageParam();
        PageVo<StockInfo> pageVo = stockInfoDao.getzt(pageParam);
        return pageVo.getData();
    }
    @Override
    public List<StockInfo> getAllListed() {
        return getAll().stream().filter(stockInfo ->
                stockInfo.isValid() && stockInfo.isA()
        ).collect(Collectors.toList());
    }

    @Override
    public void addStockLog(List<StockLog> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockLogDao.add(list);
        }
    }

    @CacheEvict(value = StockConsts.CACHE_KEY_DATA_STOCK, allEntries = true)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void update(List<StockInfo> needAddedList, List<StockInfo> needUpdatedList, List<StockLog> stockLogList) {
        if (needAddedList != null) {
            add(needAddedList);
        }
        if (needUpdatedList != null) {
            update(needUpdatedList);
        }
        if (stockLogList != null) {
            addStockLog(stockLogList);
        }
        if (needAddedList != null && !needAddedList.isEmpty()) {
            List<String> newCodeList = needAddedList.stream().map(StockInfo::getCode)
                    .collect(Collectors.toList());
            stockLogDao.setStockIdByCodeType(newCodeList, StockConsts.StockLogType.New.value());
        }
    }

    @CacheEvict(value = StockConsts.CACHE_KEY_DATA_STOCK_ZT, allEntries = true)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void updatezt(List<StockInfo> needAddedList, List<StockInfo> needUpdatedList, List<StockLog> stockLogList) {
        if (needAddedList != null) {
            addzt(needAddedList);
        }
        if (needUpdatedList != null) {
            updatezt(needUpdatedList);
        }

    }


    private void add(List<StockInfo> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockInfoDao.add(list);
        }
    }

    private void addzt(List<StockInfo> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockInfoDao.addzt(list);
        }
    }

    private void update(List<StockInfo> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockInfoDao.update(list);
        }
    }

    private void updatezt(List<StockInfo> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockInfoDao.updatezt(list);
        }
    }

    @Override
    public void saveDailyIndexToFile(String rootPath) {
        List<StockInfo> list = getAll().stream().filter(StockInfo::isA).collect(Collectors.toList());

        File root = new File(rootPath);

        list.forEach(stockInfo -> {
            logger.info("start save {}: {}", stockInfo.getName(), stockInfo.getCode());
            try {
                File file = new File(root, stockInfo.getExchange() + "/" + stockInfo.getCode() + ".txt");
                if (file.length() < 5 * 1024) {
                    String content = stockCrawlerService.getHistoryDailyIndexsString(stockInfo.getCode());
                    try (FileWriter out = new FileWriter(file)) {
                        FileCopyUtils.copy(content, out);
                    }
                }
            } catch (Exception e) {
                logger.error("get daily index error {} {}", stockInfo.getName(), stockInfo.getCode(), e);
            }
        });
    }

    @Override
    public void saveDailyIndexFromFile(String rootPath) {
        List<StockInfo> list = getAll().stream().filter(StockInfo::isA).collect(Collectors.toList());

        File root = new File(rootPath);

        CountDownLatch countDownLatch = new CountDownLatch(list.size());

        list.forEach(stockInfo -> {
            threadPoolTaskExecutor.execute(() -> {
                try {
                    handleStockDaily(root, stockInfo);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("countDownLatch await interrupt", e);
        }
        logger.info("sub task is not completed");
    }

    private void handleStockDaily(File root, StockInfo stockInfo) {
        logger.info("start save {}: {}", stockInfo.getName(), stockInfo.getCode());
        try {
            File file = new File(root, stockInfo.getExchange() + "/" + stockInfo.getCode() + ".txt");
            try (FileReader in = new FileReader(file)) {
                String content = FileCopyUtils.copyToString(in);
                List<DailyIndex> dailyIndexList = dailyIndexParser.parseHistoryDailyIndexList(content);
                dailyIndexList.forEach(dailyIndex -> dailyIndex.setCode(stockInfo.getFullCode()));
                dailyIndexDao.save(dailyIndexList);
            }
        } catch (Exception e) {
            logger.error("save daily index error {}", stockInfo.getFullCode(), e);
        }
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveDailyIndex(List<DailyIndex> list) {
        dailyIndexDao.save(list);
    }

    @Override
    public PageVo<StockInfo> getStockList(PageParam pageParam) {
        return stockInfoDao.get(pageParam);
    }

    @Cacheable(value = StockConsts.CACHE_KEY_DATA_STOCK, key = "#code")
    @Override
    public StockInfo getStockByFullCode(String code) {
        StockInfo stockInfo = stockInfoDao.getStockByFullCode(code);
        if (stockInfo == null) {
            stockInfo = new StockInfo();
            stockInfo.setAbbreviation("wlrzq");
            stockInfo.setCode(code);
            stockInfo.setName("未录入证券");
            stockInfo.setExchange(StockConsts.Exchange.SH.getName());
            stockInfo.setState(StockConsts.StockState.Terminated.value());
            stockInfo.setType(StockConsts.StockType.A.value());
        }
        return stockInfo;
    }

    @Override
    public PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam) {
        pageParam.getCondition().put("date", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        pageParam.setSort("tradingValue");
        return dailyIndexDao.getDailyIndexList(pageParam);
    }

    @Override
    public List<DailyIndex> getDailyIndexListByDate(Date date) {
        return dailyIndexDao.getDailyIndexListByDate(date);
    }

    /**
     * 按照季度采集数据，https://quotes.money.163.com/trade/lsjysj_300898.html?year=2022&season=1
     *
     * @param date
     * @param code
     */
    @Override
    public void fixDailyIndex(int date, List<String> code) {
        List<StockInfo> list = getAll().stream().filter(StockInfo::isA).collect(Collectors.toList());
        if (code != null && !code.isEmpty()) {
            list = getAll().stream().filter(v -> code.contains(v.getCode())).collect(Collectors.toList());
        }
        list.forEach(stockInfo -> {
            //logger.info("start fixDailyIndex {}: {}", stockInfo.getName(), stockInfo.getCode());
            try {
                String content = stockCrawlerService.getHistoryDailyIndexsStringFromXueQiu(stockInfo.getExchange().toUpperCase() + stockInfo.getCode(), 20);
                List<DailyIndex> dailyIndexList = dailyIndexParser.parseXueQiuHistoryDailyIndexList(content);
                logger.info("start fixDailyIndex {}: {},更新记录:{}", stockInfo.getName(), stockInfo.getCode(), dailyIndexList.size());
                dailyIndexList.forEach(d -> d.setCode(stockInfo.getFullCode()));
                dailyIndexDao.save(dailyIndexList);
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                logger.error("fixDailyIndex error {} {}", stockInfo.getName(), stockInfo.getCode(), e);
            }
        });
    }


    /**
     * 一段行情的涨幅超过40% 期间包含1个涨停板 回撤 25% 30%等待机会
     * 一段行情 涨幅35%但是振幅达到40% 包含两个涨停板，然后回撤25% 回撤30% 等待机会
     */

    public void momoRetracement() {
        dailyIndexDao.truncateSelected();
        //获取待筛选的股票
        logger.info("默默回撤选股策略");
        List<StockInfo> stockInfoList = getAllListed();
        for (StockInfo stockInfo : stockInfoList) {
            try {
                List<StockInfo> data = new ArrayList<>();
                //过滤st，科创板
                // logger.info("{}",stockInfo.getName());
                if (filterZhuBan(stockInfo)) continue;
                //logger.info("{}",stockInfo.getName());
                //获取最近一百天k线数据
                stockInfo.setDate(null);
                stockInfo.setLength(100);
                stockInfo.setStart(0);
                PageVo<DailyIndexVo> dailyIndexVoPageVo = getDailyIndexVoPageVo(stockInfo);
                if (CollectionUtils.isEmpty(dailyIndexVoPageVo.getData())) continue;

                DailyIndexVo maxDaily = dailyIndexVoPageVo.getData().stream().max(Comparator.comparing(DailyIndex::getHighestPrice)).get();
                DailyIndexVo minDaily = dailyIndexVoPageVo.getData().stream().min(Comparator.comparing(DailyIndex::getLowestPrice)).get();
                logger.info("{},最大价格：{},最小价格:{},差价{}", stockInfo.getName(), maxDaily.getClosingPrice().doubleValue(), minDaily.getLowestPrice().doubleValue(), (maxDaily.getClosingPrice().doubleValue() - minDaily.getClosingPrice().doubleValue()) / maxDaily.getClosingPrice().doubleValue());
                //计算回撤幅度大于25%的
                double retracementRate = (maxDaily.getClosingPrice().doubleValue() - minDaily.getClosingPrice().doubleValue()) / maxDaily.getClosingPrice().doubleValue();
                if (minDaily.getDate().compareTo(maxDaily.getDate()) < 0 || retracementRate < 0.25) {
                    continue;
                }
                stockInfo.setDate(maxDaily.getDate());
                if (extracted(stockInfo) && extracted2(stockInfo)) continue;

                stockInfo.setMaxPriceDate(maxDaily.getDate());
                stockInfo.setMinPriceDate(minDaily.getDate());
                stockInfo.setRetracementRate(String.format("%.2f", retracementRate * 100));
                stockInfo.setCode(stockInfo.getExchange()+stockInfo.getCode());
                data.add(stockInfo);
                dailyIndexDao.saveSelected(data);
            } catch (Exception e) {
                logger.error("出错了", e);
            }
        }
    }

    private boolean extracted2(StockInfo stockInfo) {
        //计算高点前50个交易日内是涨幅35%但是振幅达到40% 包含两个涨停板
        stockInfo.setLength(100);
        stockInfo.setStart(0);
        PageVo<DailyIndexVo> dailyIndexVoPageVoOne = getDailyIndexVoPageVo(stockInfo);
        //包含一个涨停
        if (dailyIndexVoPageVoOne.getData().stream().filter(dailyIndexVo -> dailyIndexVo.getRurnoverRate().doubleValue() > 9.95).count() < 2) {
            return true;
        }
        DailyIndexVo maxDaily = dailyIndexVoPageVoOne.getData().stream().max(Comparator.comparing(DailyIndex::getClosingPrice)).get();
        DailyIndexVo minDaily = dailyIndexVoPageVoOne.getData().stream().min(Comparator.comparing(DailyIndex::getClosingPrice)).get();

        //计算回撤幅度大于25%的
        double retracementRate = (maxDaily.getClosingPrice().doubleValue() - minDaily.getClosingPrice().doubleValue()) / maxDaily.getClosingPrice().doubleValue();
        if (minDaily.getDate().compareTo(maxDaily.getDate()) > 0 || retracementRate < 0.35) {
            return true;
        }

        retracementRate = (maxDaily.getHighestPrice().doubleValue() - minDaily.getLowestPrice().doubleValue()) / maxDaily.getHighestPrice().doubleValue();
        if (minDaily.getDate().compareTo(maxDaily.getDate()) > 0 || retracementRate < 0.40) {
            return true;
        }

        logger.info("{},extracted2", stockInfo.getName());
        return false;
    }

    private boolean extracted(StockInfo stockInfo) {
        //计算高点前50个交易日内是涨幅超过40% 期间包含1个涨停板
        stockInfo.setLength(100);
        stockInfo.setStart(0);
        PageVo<DailyIndexVo> dailyIndexVoPageVoOne = getDailyIndexVoPageVo(stockInfo);
        //包含一个涨停
        if (dailyIndexVoPageVoOne.getData().stream().filter(dailyIndexVo -> dailyIndexVo.getRurnoverRate().doubleValue() > 9.95).count() < 1) {
            return true;
        }
        DailyIndexVo maxDaily = dailyIndexVoPageVoOne.getData().stream().max(Comparator.comparing(DailyIndex::getHighestPrice)).get();
        DailyIndexVo minDaily = dailyIndexVoPageVoOne.getData().stream().min(Comparator.comparing(DailyIndex::getLowestPrice)).get();

        //计算回撤幅度大于25%的
        double retracementRate = (maxDaily.getClosingPrice().doubleValue() - minDaily.getClosingPrice().doubleValue()) / maxDaily.getClosingPrice().doubleValue();
        if (minDaily.getDate().compareTo(maxDaily.getDate()) > 0 || retracementRate < 0.4) {
            return true;
        }

        logger.info("{},extracted", stockInfo.getName());
        return false;
    }


    private boolean filterZhuBan(StockInfo stockInfo) {
        //主板 || stockInfo.getCode().startsWith("002") || stockInfo.getCode().startsWith("000")
        if (stockInfo.getCode().startsWith("688") || stockInfo.getCode().startsWith("300")
                || stockInfo.getName().contains("ST") || stockInfo.getName().contains("st") || stockInfo.getName().contains("退市")
                || stockInfo.getExchange().startsWith("bj") || (stockInfo.getExchange().contains("sz") && stockInfo.getCode().startsWith("30"))
        ||(stockInfo.getExchange().contains("sh") && stockInfo.getCode().startsWith("68")) ) {
            return true;
        }
        return false;
    }

    @Override
    public PageVo<StockInfo> getAllSeledted() {
        List<StockInfo> data = new ArrayList<>();
        data = dailyIndexDao.selectAllSelected();
        return new PageVo<>(data, data.size());
    }

    public PageVo<StockInfo> getStockZtFromDate(StockInfo stockInfo) {
        try {
            stockInfo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").parse(stockInfo.getCreateTimeStr()));
            stockInfo.setUpdateTime(holidayCalendarService.businesDateSubtraction(stockInfo.getCreateTimeStr(),stockInfo.getNum()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        List<StockInfo> data  = stockInfoDao.getStockZtFromDate(stockInfo);
        return new PageVo<>(data, data.size());
    }

    private PageVo<DailyIndexVo> getDailyIndexVoPageVo(StockInfo stockInfo) {
        PageParam pageParam = new PageParam();
        //获取所有数据
        pageParam.getCondition().put("d.code", stockInfo.getExchange() + stockInfo.getCode());
        pageParam.setStart(stockInfo.getStart());
        pageParam.setLength(stockInfo.getLength());
        pageParam.setSort("date");
        if (stockInfo.getDate() != null) {
            pageParam.getStringLE().put("date", stockInfo.getDate());
        }
        PageVo<DailyIndexVo> dailyIndexVoPageVo = dailyIndexDao.getDailyIndexList(pageParam);
        return dailyIndexVoPageVo;
    }

}
