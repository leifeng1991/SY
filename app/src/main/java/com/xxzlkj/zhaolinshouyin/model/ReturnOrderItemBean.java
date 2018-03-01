package com.xxzlkj.zhaolinshouyin.model;

import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/18 10:39
 */
public class ReturnOrderItemBean {
    private ReturnOrder order;
    private List<ReturnOrderDetail> orderDetails;

    public ReturnOrder getOrder() {
        return order;
    }

    public void setOrder(ReturnOrder order) {
        this.order = order;
    }

    public List<ReturnOrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<ReturnOrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
