package vip.linhs.stock.model.vo;

import java.util.HashMap;
import java.util.Map;

public class PageParam {

    private int start;
    private int length;
    private Map<String, Object> condition = new HashMap<>();
    private Map<String, Object> notEquals = new HashMap<>();
    private Map<String, Object> stringGE = new HashMap<>();
    private Map<String, Object> stringLE = new HashMap<>();

    private String sort;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void putCondition(Map<String, ?> map) {
        condition.putAll(map);
    }

    public void putCondition(String key, Object value) {
        condition.put(key, value);
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
