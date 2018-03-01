package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;

import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/18 10:39
 */
public class OrderItemBean {
    private Order order;
    private List<OrderDetail> orderDetails;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
