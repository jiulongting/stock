package vip.linhs.stock.dao;

import java.util.Date;
import java.util.List;

import vip.linhs.stock.model.po.DailyIndex;
import vip.linhs.stock.model.po.StockInfo;
import vip.linhs.stock.model.vo.DailyIndexVo;
import vip.linhs.stock.model.vo.PageParam;
import vip.linhs.stock.model.vo.PageVo;

public interface DailyIndexDao {

    void save(List<DailyIndex> list);
    void saveSelected(List<StockInfo> list);

    PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam);

    List<DailyIndex> getDailyIndexListByDate(Date date);
    List<StockInfo> selectAllSelected();

    void truncateSelected();
}
