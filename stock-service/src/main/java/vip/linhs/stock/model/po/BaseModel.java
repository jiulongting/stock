package vip.linhs.stock.model.po;

import java.io.Serializable;
import java.util.Date;

public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private Date createTime;
    private Date updateTime;

    private int start;
    private int length;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setBaiscModel(boolean insert) {
        updateTime = new Date();
        if (insert) {
            createTime = updateTime;
        }
    }

    @Override
    public String toString() {
        return "BaseModel [id=" + id + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
    }

}
