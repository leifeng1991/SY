package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/15 11:02
 */
public class AddOrderBean extends BaseBean{
    private OrderItemBean data = new OrderItemBean();

    public OrderItemBean getData() {
        return data;
    }

    public void setData(OrderItemBean data) {
        this.data = data;
    }
}
