package com.xxzlkj.zhaolinshouyin.event;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/2/1 10:25
 */


public class RealDiscountMoneyEvent {
    private double realMoney;// 实付金额
    private double dicountsMoney;// 优惠金额

    public RealDiscountMoneyEvent(double realMoney, double dicountsMoney) {
        this.realMoney = realMoney;
        this.dicountsMoney = dicountsMoney;
    }

    public double getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(double realMoney) {
        this.realMoney = realMoney;
    }

    public double getDicountsMoney() {
        return dicountsMoney;
    }

    public void setDicountsMoney(double dicountsMoney) {
        this.dicountsMoney = dicountsMoney;
    }
}
