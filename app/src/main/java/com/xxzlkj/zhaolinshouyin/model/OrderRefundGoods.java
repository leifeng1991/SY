package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshouyin.db.OrderDetail;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/2/5 16:08
 */


public class OrderRefundGoods {
    private OrderDetail orderDetail;

    private boolean isChecked;

    private double refundNumber;

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public double getRefundNumber() {
        return refundNumber;
    }

    public void setRefundNumber(double refundNumber) {
        this.refundNumber = refundNumber;
    }
}
