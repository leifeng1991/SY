package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;
import com.xxzlkj.zhaolinshouyin.db.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/5 11:20
 */
public class OrderListBean extends BaseBean {
    private List<Order> data = new ArrayList<>();

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}
