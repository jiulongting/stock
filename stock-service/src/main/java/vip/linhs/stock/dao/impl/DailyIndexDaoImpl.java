package vip.linhs.stock.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.stereotype.Repository;

import vip.linhs.stock.dao.BaseDao;
import vip.linhs.stock.dao.DailyIndexDao;
import vip.linhs.stock.model.po.DailyIndex;
import vip.linhs.stock.model.po.StockInfo;
import vip.linhs.stock.model.vo.DailyIndexVo;
import vip.linhs.stock.model.vo.PageParam;
import vip.linhs.stock.model.vo.PageVo;
import vip.linhs.stock.scheduled.ScheduledTasks;
import vip.linhs.stock.util.SqlCondition;

@Repository
public class DailyIndexDaoImpl extends BaseDao implements DailyIndexDao {
    private final Logger logger = LoggerFactory.getLogger(DailyIndexDaoImpl.class);

    private static final String INSERT_SQL = "insert into daily_index(code, date, opening_price, pre_closing_price, highest_price, closing_price, lowest_price, trading_volume, trading_value, rurnover_rate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void save(List<DailyIndex> list) {

        jdbcTemplate.batchUpdate(DailyIndexDaoImpl.INSERT_SQL, list, list.size(),
                DailyIndexDaoImpl::setArgument);
    }

    @Override
    public void saveSelected(List<StockInfo> list) {
        jdbcTemplate.batchUpdate("insert into selected_info (name,code, maxPriceDate, minPriceDate, retracementRate) values(?,?, ?, ?, ?)", list, list.size(),
                DailyIndexDaoImpl::setArgumentSelected);
    }

    public void truncateSelected() {
        jdbcTemplate.batchUpdate("truncate table selected_info");
    }

    public List<StockInfo> selectAllSelected(){
        List<StockInfo> list = jdbcTemplate.query("select name,code,maxPriceDate,minPriceDate,retracementRate from selected_info order by minPriceDate desc",BeanPropertyRowMapper.newInstance(StockInfo.class));
        return list;
    }

    private static void setArgumentSelected(PreparedStatement ps, StockInfo dailyIndex)
            throws SQLException {
        ps.setString(1,dailyIndex.getName());
        ps.setString(2, dailyIndex.getCode());
        StatementCreatorUtils.setParameterValue(ps, 3, Types.DATE,
                dailyIndex.getMaxPriceDate());
        StatementCreatorUtils.setParameterValue(ps, 4, Types.DATE,
                dailyIndex.getMinPriceDate());
        ps.setString(5, dailyIndex.getRetracementRate());
    }

    private static void setArgument(PreparedStatement ps, DailyIndex dailyIndex)
            throws SQLException {
        ps.setString(1, dailyIndex.getCode());
        StatementCreatorUtils.setParameterValue(ps, 2, Types.DATE,
                dailyIndex.getDate());
        ps.setBigDecimal(3, dailyIndex.getOpeningPrice());
        ps.setBigDecimal(4, dailyIndex.getPreClosingPrice());
        ps.setBigDecimal(5, dailyIndex.getHighestPrice());
        ps.setBigDecimal(6, dailyIndex.getClosingPrice());
        ps.setBigDecimal(7, dailyIndex.getLowestPrice());
        ps.setLong(8, dailyIndex.getTradingVolume());
        ps.setBigDecimal(9, dailyIndex.getTradingValue());
        ps.setBigDecimal(10, dailyIndex.getRurnoverRate());
    }

    @Override
    public PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam) {
        String sql = "select"
                + " s.name, s.abbreviation, d.code, d.date, d.pre_closing_price as preClosingPrice,"
                + " d.closing_price as closingPrice, d.lowest_price as lowestPrice,"
                + " d.highest_price as highestPrice, d.opening_price as openingPrice,"
                + " d.trading_value as tradingValue, d.trading_volume as tradingVolume,"
                + " d.rurnover_rate as rurnoverRate"
                + " from daily_index d, stock_info s where d.code = concat(s.exchange, s.code)";

        SqlCondition dataSqlCondition = new SqlCondition(sql, pageParam.getCondition(), pageParam.getNotEquals(), pageParam.getStringGE(), pageParam.getStringLE());

        for (String key : pageParam.getCondition().keySet()) {
            dataSqlCondition.addString(key, key);
        }
        for (String key : pageParam.getStringGE().keySet()) {
            dataSqlCondition.addStringGE(key, key);
        }
        for (String key : pageParam.getStringLE().keySet()) {
            dataSqlCondition.addStringLE(key, key);
        }
        for (String key : pageParam.getNotEquals().keySet()) {
            dataSqlCondition.addStringNotEquals(key, key);
        }

        int totalRecords = jdbcTemplate.queryForObject(dataSqlCondition.getCountSql(), Integer.class, dataSqlCondition.toArgs());

        dataSqlCondition.addSort(pageParam.getSort(), false, true);
        dataSqlCondition.addSql(" limit ?, ? ");
        dataSqlCondition.addPage(pageParam.getStart(), pageParam.getLength());

        List<DailyIndexVo> list = jdbcTemplate.query(dataSqlCondition.toSql(),
                BeanPropertyRowMapper.newInstance(DailyIndexVo.class), dataSqlCondition.toArgs());
        return new PageVo<>(list, totalRecords);
    }

    @Override
    public List<DailyIndex> getDailyIndexListByDate(Date date) {
        String sql = "select"
            + " id, code, date, pre_closing_price as preClosingPrice,"
            + " closing_price as closingPrice, lowest_price as lowestPrice,"
            + " highest_price as highestPrice, opening_price as openingPrice,"
            + " trading_value as tradingValue, trading_volume as tradingVolume,"
            + " rurnover_rate as rurnoverRate"
            + " from daily_index where date = ?";
        List<DailyIndex> list = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(DailyIndex.class),
                new java.sql.Date(date.getTime()));
        return list;
    }

}
