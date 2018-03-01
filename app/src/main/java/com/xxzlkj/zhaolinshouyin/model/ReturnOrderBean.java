package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/15 11:02
 */
public class ReturnOrderBean extends BaseBean {
    private ReturnOrderItemBean data = new ReturnOrderItemBean();

    public ReturnOrderBean(ReturnOrderItemBean data) {
        this.data = data;
    }

    public ReturnOrderItemBean getData() {
        return data;
    }

    public void setData(ReturnOrderItemBean data) {
        this.data = data;
    }
}
