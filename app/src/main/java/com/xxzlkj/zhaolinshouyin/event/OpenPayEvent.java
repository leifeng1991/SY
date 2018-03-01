package com.xxzlkj.zhaolinshouyin.event;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/1/10 9:55
 */


public class OpenPayEvent {
    // 支付类型  1：支付宝 2：微信 6：现金
    private int payType;

    public OpenPayEvent(int payType) {
        this.payType = payType;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }
}
