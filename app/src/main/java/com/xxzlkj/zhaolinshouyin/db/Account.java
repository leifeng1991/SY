package com.xxzlkj.zhaolinshouyin.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述: 对账表
 *
 * @author zhangrq
 *         2017/12/4 14:55
 */
@Entity
public class Account {
    private long starttime;// 开始时间
    private long endtime;// 结束时间，既也是此表的增加时间
    private String uid;// 操作人员id
    private double cash_price;// 现金总额
    private double all_price;// 全部总额
    private int order_num;// 订单数

    @Generated(hash = 971192476)
    public Account(long starttime, long endtime, String uid, double cash_price,
            double all_price, int order_num) {
        this.starttime = starttime;
        this.endtime = endtime;
        this.uid = uid;
        this.cash_price = cash_price;
        this.all_price = all_price;
        this.order_num = order_num;
    }

    @Generated(hash = 882125521)
    public Account() {
    }

    public long getStarttime() {
        return this.starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return this.endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getCash_price() {
        return this.cash_price;
    }

    public void setCash_price(double cash_price) {
        this.cash_price = cash_price;
    }

    public double getAll_price() {
        return this.all_price;
    }

    public void setAll_price(double all_price) {
        this.all_price = all_price;
    }

    public int getOrder_num() {
        return this.order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }


}
