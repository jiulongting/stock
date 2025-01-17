package vip.linhs.stock.model.po;

import vip.linhs.stock.util.StockConsts;

import java.util.Date;

public class StockInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private String exchange;
    private String abbreviation;

    private String tag;
    private int state;
    private int type;

    private int num;
    private Date maxPriceDate;
    private Date minPriceDate;

    private String retracementRate;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRetracementRate() {
        return retracementRate;
    }

    public void setRetracementRate(String retracementRate) {
        this.retracementRate = retracementRate;
    }

    public Date getMaxPriceDate() {
        return maxPriceDate;
    }

    public void setMaxPriceDate(Date maxPriceDate) {
        this.maxPriceDate = maxPriceDate;
    }

    public Date getMinPriceDate() {
        return minPriceDate;
    }

    public void setMinPriceDate(Date minPriceDate) {
        this.minPriceDate = minPriceDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isValid() {
        return state != StockConsts.StockState.Terminated.value();
    }

    public boolean isA() {
        return type == StockConsts.StockType.A.value();
    }

    public boolean isIndex() {
        return type == StockConsts.StockType.Index.value();
    }

    public String getFullCode() {
        return exchange + code;
    }

    @Override
    public String toString() {
        return "StockInfo [code=" + code + ", name=" + name + ", exchange=" + exchange + ", abbreviation="
                + abbreviation + ", state=" + state + ", type=" + type + ", BaseModel=" + super.toString() + "]";
    }

}
