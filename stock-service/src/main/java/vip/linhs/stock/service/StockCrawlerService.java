package vip.linhs.stock.service;

import vip.linhs.stock.model.po.DailyIndex;
import vip.linhs.stock.model.po.StockInfo;

import java.util.List;
import java.util.Map;

public interface StockCrawlerService {

    List<StockInfo> getStockList();

    DailyIndex getDailyIndex(String code);

    List<DailyIndex> getDailyIndex(List<String> codeList);

    List<DailyIndex> getDailyIndexFromEastMoney();

    List<DailyIndex> getHistoryDailyIndexs(String code);

    String getHistoryDailyIndexsString(String code);

    String getHistoryDailyIndexsStringFrom163(String code, int year, int season);

    String getHistoryDailyIndexsStringFromXueQiu(String code, int day);
    List<StockInfo> getZTfromddxgubitcn(String date);

    List<StockInfo> getZTfromjiuyangongshe(String dateStr);

    List<StockInfo> getZTfromTonghuashun(String yyyyMMdd);
}
