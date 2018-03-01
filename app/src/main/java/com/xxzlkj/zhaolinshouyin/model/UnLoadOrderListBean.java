package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshouyin.db.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/15 11:02
 */
public class UnLoadOrderListBean {
    private List<OrderItemBean> requestList = new ArrayList<>();// 请求服务器的列表
    private List<Order> unLoadOrderList = new ArrayList<>();// 未上传的订单列表

    public List<OrderItemBean> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<OrderItemBean> requestList) {
        this.requestList = requestList;
    }

    public List<Order> getUnLoadOrderList() {
        return unLoadOrderList;
    }

    public void setUnLoadOrderList(List<Order> unLoadOrderList) {
        this.unLoadOrderList = unLoadOrderList;
    }
}
