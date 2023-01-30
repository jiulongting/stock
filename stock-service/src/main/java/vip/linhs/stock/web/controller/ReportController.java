package vip.linhs.stock.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vip.linhs.stock.dao.DailyIndexDao;
import vip.linhs.stock.model.po.DailyIndex;
import vip.linhs.stock.model.po.StockInfo;
import vip.linhs.stock.model.vo.DailyIndexVo;
import vip.linhs.stock.model.vo.PageParam;
import vip.linhs.stock.model.vo.PageVo;
import vip.linhs.stock.service.StockService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("report")
public class ReportController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private StockService stockService;
    @Autowired
    private DailyIndexDao dailyIndexDao;

    @RequestMapping("stockList")
    public PageVo<StockInfo> getStockList(PageParam pageParam) {
        return stockService.getStockList(pageParam);
    }

    @RequestMapping("selectStockList")
    public PageVo<StockInfo> getSelectStockList(PageParam pageParam) {
        return stockService.getAllSeledted();
        //dikai2(pageParam);
        // pageParam.getCondition().put("code","600210");
        //dikai3(pageParam);
//        gaokai(-9.5,-9);
//        gaokai(-9,-8.5);
//        gaokai(-8.5,-8);
//        gaokai(-8,-7.5);
//        gaokai(-7.5,-7);
//        gaokai(-7,-6.5);
//        gaokai(-6.5,-6);
//        gaokai(-6,-5.5);
//        gaokai(-5.5,-5);
//        gaokai(-5,-4.5);
//        gaokai(-4.5,-4);
//        gaokai(-4,-3.5);
//        gaokai(-3.5,-3);
//        gaokai(-3,-2.5);
//        gaokai(-2.5,-2);
//        gaokai(-2,-1.5);
//        gaokai(-1.5,-1);
//        gaokai(-1,-0.5);
//        gaokai(-0.5,0);
//        gaokai(0,0.5);
//        gaokai(0.5,1);
//        gaokai(1,1.5);
//        gaokai(1.5,2);
//        gaokai(2,2.5);
//        gaokai(2.5,3);
//        gaokai(3,3.5);
//        gaokai(3.5,4);
//        gaokai(4,4.5);
//        gaokai(4.5,5);
//        gaokai(5,5.5);
//        gaokai(5.5,6);
//        gaokai(6,6.5);
//        gaokai(6.5,7);
//        gaokai(7,7.5);
//        gaokai(7.5,8);
//        gaokai(8,8.5);
//        gaokai(8.5,9);
//        gaokai(9,9.5);
    }

    @RequestMapping("hotMap")
    public PageVo<StockInfo> getHotMap(StockInfo pageParam) {
        return stockService.getStockZtTagFromDate(pageParam);
    }
    @RequestMapping("hotMapStockList")
    public PageVo<StockInfo> hotMapStockList(StockInfo pageParam) {
        return stockService.getStockZtFromDate(pageParam);
    }

    /**
     * 高开回测
     */
    public void gaokai(double b,double e) {
        logger.info("高开回测开始");
        List<StockInfo> stockInfoList = stockService.getAllListed();
        double gaokaiRat = 0l;
        double gaokaiNumber = 0l;
        for (StockInfo stockInfo : stockInfoList) {
            if (filterZhuBan(stockInfo)) continue;
            stockInfo.setLength(200);
            PageVo<DailyIndexVo> dailyIndexVoPageVo = getDailyIndexVoPageVo(stockInfo);
            double gaokaiNum = 0;
            double gaokaiTotal = 0;
            double rate=0l;
            for (int i = 0; i < dailyIndexVoPageVo.getData().size() - 2; i++) {
                DailyIndexVo dailyIndexVo=dailyIndexVoPageVo.getData().get(i);
                DailyIndexVo dailyIndexVoZ=dailyIndexVoPageVo.getData().get(i+1);
                if (dailyIndexVoZ.getRurnoverRate().doubleValue() > b && dailyIndexVoZ.getRurnoverRate().doubleValue() < e ) {
                    gaokaiTotal++;
                    if (dailyIndexVo.getOpeningPrice().doubleValue() > dailyIndexVoZ.getClosingPrice().doubleValue()) {
                        gaokaiNum++;
                        rate = rate + dailyIndexVoPageVo.getData().get(i).getRurnoverRate().doubleValue();
                    }
                }
            }
            if(gaokaiTotal>0) {
                gaokaiNumber++;
                gaokaiRat = gaokaiRat + (gaokaiNum / gaokaiTotal);
               // logger.info(stockInfo.getName()+"gaokaiNum"+gaokaiNum+"gaokaiTotal"+gaokaiTotal+" 平均:"+gaokaiNum / gaokaiTotal);
            }
        }
        logger.info(b+"到"+e+"个点gaokaiRat:" + gaokaiRat+"股票个数："+gaokaiNumber+" 平均:"+gaokaiRat/gaokaiNumber);

    }


    private void daban(PageParam pageParam) {
        logger.info("打板策略回测开始");
        List<StockInfo> stockInfoList = stockService.getAllListed();
        double totalSs = 0;
        double totalNn = 0;
        for (StockInfo stockInfo : stockInfoList) {

            if (filterZhuBan(stockInfo)) continue;

            PageVo<DailyIndexVo> dailyIndexVoPageVo = getDailyIndexVoPageVo(stockInfo);
            double totalS = 0;
            double totalN = 0;
            for (int i = 0; i < dailyIndexVoPageVo.getData().size() - 1; i++) {

                DailyIndexVo dailyIndexVo = dailyIndexVoPageVo.getData().get(i);
                DailyIndexVo dailyIndexVoN = dailyIndexVoPageVo.getData().get(i + 1);

                //在当日跌幅1个点以上则买入，收盘价卖出。

                //当日最大跌幅
                double onePre = zhanFu(dailyIndexVo.getLowestPrice(), dailyIndexVo.getOpeningPrice());
                if (onePre > -5) {
                    continue;
                }


                //买入价
                double buyPrice = 0l;
                if (zhanFu(dailyIndexVo.getOpeningPrice(), dailyIndexVoN.getClosingPrice()) < -5) {
                    buyPrice = dailyIndexVo.getOpeningPrice().doubleValue();
                } else {
                    //买入价
                    buyPrice = 0.95 * dailyIndexVo.getOpeningPrice().doubleValue();
                }
                // logger.info(dailyIndexVo.getName() + "在" + dailyIndexVo.getDate() + "日内跌幅" + String.format("%.2f", onePre) + "%，买入价："+buyPrice);
                double s = 0;
                //模拟计算收益
                s = zhanFu(dailyIndexVo.getClosingPrice(), BigDecimal.valueOf(buyPrice));
                // logger.info("模拟收益:" + String.format("%.2f", s) + "%");
                totalS = totalS + s;
                totalN++;
                totalSs = totalSs + s;
                totalNn++;
            }

            logger.info(stockInfo.getName() + "模拟总收益:" + totalS + " 一共交易：" + totalN + "笔 平均：" + totalS / totalN);
        }
        logger.info("模拟总收益:" + totalSs + " 一共交易：" + totalNn + "笔 平均：" + totalSs / totalNn);
    }


    private PageVo<DailyIndexVo> baoHanXian(PageParam pageParam) {
        List<DailyIndexVo> list = new ArrayList<>();
        List<StockInfo> stockInfoList = stockService.getAllListed();
        for (int i = pageParam.getStart(); i < stockInfoList.size(); i++) {
            StockInfo stockInfo = stockInfoList.get(i);
            if (filterZhuBan(stockInfo)) continue;
            PageVo<DailyIndexVo> dailyIndexVoPageVo = getDailyIndexVoPageVo(stockInfo);
            for (int j = 1; j < dailyIndexVoPageVo.getData().size() - 4; j++) {
                DailyIndexVo dailyIndexVo = dailyIndexVoPageVo.getData().get(j);
                dailyIndexVo.setName(stockInfo.getName());
                //找到振幅超过10个点的K
                double tempZ = (dailyIndexVo.getClosingPrice().doubleValue() - dailyIndexVo.getOpeningPrice().doubleValue()) / dailyIndexVo.getOpeningPrice().doubleValue();
                if (tempZ < -0.1 || tempZ > 0.1) {
                    logger.info(dailyIndexVo.getName() + "在" + dailyIndexVo.getDate() + (tempZ < 0 ? "下跌" : "上涨") + "振幅超过10%：");

                    //检测是否到达包含线下沿
                    DailyIndexVo dailyIndexVoLow = isLowBao(j, dailyIndexVo, dailyIndexVoPageVo);
                    if (dailyIndexVoLow != null) {
                        logger.info(dailyIndexVo.getName() + "在" + dailyIndexVoLow.getDate() + "找到买点http://quote.eastmoney.com/concept/" + dailyIndexVoLow.getCode() + ".html?from=classic#fschart-k");
                        list.add(dailyIndexVoLow);
                        break;
                    }
                }
            }

        }
        return new PageVo<>(list, list.size());
    }

    private DailyIndexVo isLowBao(int j, DailyIndexVo dailyIndexVo, PageVo<DailyIndexVo> dailyIndexVoPageVo) {
        int end = -1;
        for (int k = j - 1; k > end; k--) {
            DailyIndexVo dailyIndexVoT = dailyIndexVoPageVo.getData().get(k);
            //当前股价是否已运行超过包含线
            if (dailyIndexVoT.getHighestPrice().doubleValue() > dailyIndexVo.getHighestPrice().doubleValue()) {
                break;
            }
            //当前股价跌穿包含线，则需在5日内返回包含线
            if (dailyIndexVoT.getLowestPrice().doubleValue() < dailyIndexVo.getLowestPrice().doubleValue() && end == -1) {
                end = k - 5 > -2 ? k - 5 : -1;
            }
            //当前k线的最低价高于包含线的最低价，并且最高价低于包含线振幅的20%
            if (dailyIndexVoT.getLowestPrice().doubleValue() - dailyIndexVo.getLowestPrice().doubleValue() > 0 &&
                    (dailyIndexVoT.getHighestPrice().doubleValue() - dailyIndexVo.getLowestPrice().doubleValue()) / dailyIndexVo.getLowestPrice().doubleValue() < 0.03) {
                return dailyIndexVoT;
            }
        }
        return null;

    }

    private void dikai3(PageParam pageParam) {
        logger.info("做T策略回测开始");
        List<StockInfo> stockInfoList = stockService.getAllListed();//stockService.getStockList(pageParam).getData();
        double totalSs = 0;
        double totalNn = 0;
        for (StockInfo stockInfo : stockInfoList) {

            if (filterZhuBan(stockInfo)) continue;

            PageVo<DailyIndexVo> dailyIndexVoPageVo = getDailyIndexVoPageVo(stockInfo);
            double totalS = 0;
            double totalN = 0;
            for (int i = 0; i < dailyIndexVoPageVo.getData().size() - 1; i++) {

                DailyIndexVo dailyIndexVo = dailyIndexVoPageVo.getData().get(i);
                DailyIndexVo dailyIndexVoN = dailyIndexVoPageVo.getData().get(i + 1);

                //在当日跌幅1个点以上则买入，收盘价卖出。

                //当日最大跌幅
                double onePre = zhanFu(dailyIndexVo.getLowestPrice(), dailyIndexVo.getOpeningPrice());
                if (onePre > -5) {
                    continue;
                }


                //买入价
                double buyPrice = 0l;
                if (zhanFu(dailyIndexVo.getOpeningPrice(), dailyIndexVoN.getClosingPrice()) < -5) {
                    buyPrice = dailyIndexVo.getOpeningPrice().doubleValue();
                } else {
                    //买入价
                    buyPrice = 0.95 * dailyIndexVo.getOpeningPrice().doubleValue();
                }
                // logger.info(dailyIndexVo.getName() + "在" + dailyIndexVo.getDate() + "日内跌幅" + String.format("%.2f", onePre) + "%，买入价："+buyPrice);
                double s = 0;
                //模拟计算收益
                s = zhanFu(dailyIndexVo.getClosingPrice(), BigDecimal.valueOf(buyPrice));
                // logger.info("模拟收益:" + String.format("%.2f", s) + "%");
                totalS = totalS + s;
                totalN++;
                totalSs = totalSs + s;
                totalNn++;
            }

            logger.info(stockInfo.getName() + "模拟总收益:" + totalS + " 一共交易：" + totalN + "笔 平均：" + totalS / totalN);
        }
        logger.info("模拟总收益:" + totalSs + " 一共交易：" + totalNn + "笔 平均：" + totalSs / totalNn);
    }

    private void dikai2(PageParam pageParam) {
        logger.info("低开策略2回测开始");
        List<StockInfo> stockInfoList = stockService.getAllListed();
        double totalS = 0;
        double totalN = 0;
        for (StockInfo stockInfo : stockInfoList) {

            if (filterZhuBan(stockInfo)) continue;

            PageVo<DailyIndexVo> dailyIndexVoPageVo = getDailyIndexVoPageVo(stockInfo);

            for (int i = 1; i < dailyIndexVoPageVo.getData().size() - 4; i++) {
                DailyIndexVo dailyIndexVoN = dailyIndexVoPageVo.getData().get(i - 1);
                DailyIndexVo dailyIndexVo = dailyIndexVoPageVo.getData().get(i);
                DailyIndexVo dailyIndexVoPre = dailyIndexVoPageVo.getData().get(i + 1);
                DailyIndexVo dailyIndexVoPreP = dailyIndexVoPageVo.getData().get(i + 2);
                DailyIndexVo dailyIndexVoPrePp = dailyIndexVoPageVo.getData().get(i + 3);
                DailyIndexVo dailyIndexVoPrePpp = dailyIndexVoPageVo.getData().get(i + 4);
                //4跟k线确定买入

                //前一天
                double t = zhanFu(dailyIndexVoPre.getClosingPrice(), dailyIndexVoPreP.getClosingPrice());
                //前前一天
                double tt = zhanFu(dailyIndexVoPreP.getClosingPrice(), dailyIndexVoPrePp.getClosingPrice());
                //前前前一天
                double ttt = zhanFu(dailyIndexVoPrePp.getClosingPrice(), dailyIndexVoPrePpp.getClosingPrice());

                if (ttt < 9.97) {
                    continue;
                }

                if (tt < 9.97) {
                    continue;
                }

                if (!(t < 9.97)) {
                    continue;
                }

                //今天是否低开
                double k = zhanFu(dailyIndexVo.getOpeningPrice(), dailyIndexVo.getPreClosingPrice());
                if (k > -4 || k < -9.95) {
                    continue;
                }
                logger.info(dailyIndexVo.getName() + "在" + dailyIndexVoPre.getDate() + "涨停且在" + dailyIndexVo.getDate() + "低开" + String.format("%.2f", k) + "%");

                double s = 0;
                //模拟计算收益
                //低开买入当日产生收益则第二日尾盘卖出，反之开盘卖出
                if (dailyIndexVo.getOpeningPrice().doubleValue() < dailyIndexVo.getClosingPrice().doubleValue()) {
                    s = zhanFu(dailyIndexVoN.getClosingPrice(), dailyIndexVo.getOpeningPrice());
                } else {
                    s = zhanFu(dailyIndexVoN.getOpeningPrice(), dailyIndexVo.getOpeningPrice());
                }
                logger.info("模拟收益:" + String.format("%.2f", s) + "%");
                totalS = totalS + s;
                totalN++;
            }


        }
        logger.info("模拟总收益:" + totalS + " 一共交易：" + totalN + "笔 平均：" + totalS / totalN);
    }

    private double zhanFu(BigDecimal dailyIndexVoPre, BigDecimal dailyIndexVoPreP) {
        return (dailyIndexVoPre.doubleValue() - dailyIndexVoPreP.doubleValue()) / dailyIndexVoPreP.doubleValue() * 100;
    }

    private void dikai(PageParam pageParam) {
        logger.info("低开策略回测开始");
        List<StockInfo> stockInfoList = stockService.getAllListed();
        double totalS = 0;
        double totalN = 0;
        for (StockInfo stockInfo : stockInfoList) {

            if (filterZhuBan(stockInfo)) continue;

            PageVo<DailyIndexVo> dailyIndexVoPageVo = getDailyIndexVoPageVo(stockInfo);

            for (int i = 2; i < dailyIndexVoPageVo.getData().size() - 4; i++) {
                DailyIndexVo dailyIndexVoNN = dailyIndexVoPageVo.getData().get(i - 2);
                DailyIndexVo dailyIndexVoN = dailyIndexVoPageVo.getData().get(i - 1);
                DailyIndexVo dailyIndexVo = dailyIndexVoPageVo.getData().get(i);
                DailyIndexVo dailyIndexVoPre = dailyIndexVoPageVo.getData().get(i + 1);
                DailyIndexVo dailyIndexVoPreP = dailyIndexVoPageVo.getData().get(i + 2);
                DailyIndexVo dailyIndexVoPrePp = dailyIndexVoPageVo.getData().get(i + 3);


                //前一天是否涨停
                double t = zhanFu(dailyIndexVoPre.getClosingPrice(), dailyIndexVoPreP.getClosingPrice());
                //前前一天是否涨停
                double tt = zhanFu(dailyIndexVoPreP.getClosingPrice(), dailyIndexVoPrePp.getClosingPrice());
                if (tt < 9.97) {
                    continue;
                }

                if (t < 9.97) {
                    continue;
                }
                //今天是否低开
                double k = zhanFu(dailyIndexVo.getOpeningPrice(), dailyIndexVo.getPreClosingPrice());
                if (k > -5 || k < -9.96) {
                    continue;
                }
                logger.info(dailyIndexVo.getName() + "在" + dailyIndexVoPre.getDate() + "涨停且在" + dailyIndexVo.getDate() + "低开" + String.format("%.2f", k) + "%");

                double s = 0;
                //模拟计算收益
                //低开买入当日产生收益则第二日尾盘卖出，反之开盘卖出
                if (dailyIndexVo.getOpeningPrice().doubleValue() < dailyIndexVo.getClosingPrice().doubleValue()) {
                    s = zhanFu(dailyIndexVoN.getClosingPrice(), dailyIndexVo.getOpeningPrice());
                } else {
                    s = zhanFu(dailyIndexVoN.getOpeningPrice(), dailyIndexVo.getOpeningPrice());
                }
                logger.info("模拟收益:" + String.format("%.2f", s) + "%");
                totalS = totalS + s;
                totalN++;
            }


        }
        logger.info("模拟总收益:" + totalS + " 一共交易：" + totalN + "笔 平均：" + totalS / totalN);
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

    private boolean filterZhuBan(StockInfo stockInfo) {
        //主板 || stockInfo.getCode().startsWith("002") || stockInfo.getCode().startsWith("000")
        if (stockInfo.getCode().startsWith("600")
                || stockInfo.getName().contains("ST") || stockInfo.getName().contains("退市")) {
            return true;
        }
        return false;
    }


    @RequestMapping("dailyIndexList")
    public PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam) {
        return stockService.getDailyIndexList(pageParam);
    }

}
