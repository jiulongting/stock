package vip.linhs.stock.dao;

import java.util.List;

import vip.linhs.stock.model.po.StockInfo;
import vip.linhs.stock.model.vo.PageParam;
import vip.linhs.stock.model.vo.PageVo;

public interface StockInfoDao {

    List<StockInfo> getStockZtFromDate(StockInfo stockInfo);

    void add(List<StockInfo> list);

    void update(List<StockInfo> list);
    void updatezt(List<StockInfo> list);

    PageVo<StockInfo> get(PageParam pageParam);
    PageVo<StockInfo> getzt(PageParam pageParam);

    StockInfo getStockByFullCode(String code);

    void addzt(List<StockInfo> list);
}
