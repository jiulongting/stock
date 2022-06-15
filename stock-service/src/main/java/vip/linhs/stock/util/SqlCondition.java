package vip.linhs.stock.util;

import java.util.*;

import org.springframework.util.StringUtils;

public class SqlCondition {

    private StringBuilder sqlString;
    private Map<String, Object> params;
    private List<Object> objList = new ArrayList<>();
    private Map<String, Object> notEquals = new HashMap<>();
    private Map<String, Object> stringGE = new HashMap<>();
    private Map<String, Object> stringLE = new HashMap<>();

    public SqlCondition(String sqlString, Map<String, Object> params, Map<String, Object> notEquals, Map<String, Object> stringGE, Map<String, Object> stringLE) {
        this.sqlString = new StringBuilder(sqlString);
        this.params = params;
        this.stringGE = stringGE;
        this.notEquals = notEquals;
        this.stringLE = stringLE;
    }

    public SqlCondition(String sqlString, Map<String, Object> params, List<Object> objList) {
        this.sqlString = new StringBuilder(sqlString);
        this.params = params;
        this.objList = objList;
    }

    /**
     * 新增字符串查询条件(等价于 == )
     */
    public SqlCondition addString(String key, String column) {
        String value = Objects.toString(params.get(key), null);
        if (StringUtils.hasLength(value)) {
            sqlString.append(String.format(" and %s = ? ", column));
            objList.add(value);
        }
        return this;
    }

    /**
     * 新增字符串查询条件(等价于 != )
     */
    public SqlCondition addStringNotEquals(String key, String column) {
        String value = Objects.toString(notEquals.get(key), null);
        if (StringUtils.hasLength(value)) {
            sqlString.append(String.format(" and %s <> ? ", column));
            objList.add(value);
        }
        return this;
    }

    /**
     * 新增SQL
     */
    public SqlCondition addSql(String sql, Object... args) {
        if (StringUtils.hasLength(sql)) {
            sqlString.append(sql);
            objList.addAll(Arrays.asList(args));
        }
        return this;
    }

    /**
     * 新增字符串查询条件(等价于 >= )
     */
    public SqlCondition addStringGE(String key, String column) {
        String value = Objects.toString(stringGE.get(key), null);
        if (StringUtils.hasLength(value)) {
            sqlString.append(String.format(" and %s >= ? ", column));
            objList.add(value);
        }
        return this;
    }

    /**
     * 新增字符串查询条件(等价于 <= )
     */
    public SqlCondition addStringLE(String key, String column) {
        String value = Objects.toString(stringLE.get(key), null);
        if (StringUtils.hasLength(value)) {
            sqlString.append(String.format(" and %s <= ? ", column));
            objList.add(value);
        }
        return this;
    }

    /**
     * 新增字符串查询条件(等价于 < )
     */
    public SqlCondition addStringLT(String key, String column) {
        String value = Objects.toString(params.get(key), null);
        if (StringUtils.hasLength(value)) {
            sqlString.append(String.format(" and %s < ? ", column));
            objList.add(value);
        }
        return this;
    }

    /**
     * 新增字符串查询条件(等价于 > )
     */
    public SqlCondition addStringGT(String key, String column) {
        String value = Objects.toString(params.get(key), null);
        if (StringUtils.hasLength(value)) {
            sqlString.append(String.format(" and %s > ? ", column));
            objList.add(value);
        }
        return this;
    }

    /**
     * 模糊查询
     */
    public SqlCondition addStringLike(String key, String column) {
        String value = Objects.toString(params.get(key), null);
        if (StringUtils.hasLength(value)) {
            sqlString.append(String.format(" and %s like ?", column));
            objList.add("%" + value + "%");
        }
        return this;
    }

    /**
     * 添加排序
     */
    public void addSort(String column, boolean isAsc, boolean first) {
       sqlString.append(String.format(first ? " order by %s %s " : ", %s %s ", column, isAsc ? "asc" : "desc"));
    }

    /**
     * 添加分页参数
     *
     * @param start
     * @param length
     */
    public void addPage(int start, int length) {
        objList.add(start);
        objList.add(length);
    }

    public Object[] toArgs() {
        return objList.toArray();
    }

    public String toSql() {
        return sqlString.toString();
    }

    public String getCountSql() {
        return String.format("select count(1) from (%s) _row", sqlString.toString());
    }

    public List<Object> getObjectList() {
        return objList;
    }

    public Map<String, Object> getNotEquals() {
        return notEquals;
    }

    public void setNotEquals(Map<String, Object> notEquals) {
        this.notEquals = notEquals;
    }

    public Map<String, Object> getStringGE() {
        return stringGE;
    }

    public void setStringGE(Map<String, Object> stringGE) {
        this.stringGE = stringGE;
    }

    public Map<String, Object> getStringLE() {
        return stringLE;
    }

    public void setStringLE(Map<String, Object> stringLE) {
        this.stringLE = stringLE;
    }
}
