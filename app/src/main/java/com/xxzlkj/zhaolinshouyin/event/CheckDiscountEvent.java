package com.xxzlkj.zhaolinshouyin.event;

import com.xxzlkj.zhaolinshouyin.model.CheckDiscountBean;

/**
 * 描述:支付完成
 *
 * @author leifeng
 *         2018/1/12 15:28
 */


public class CheckDiscountEvent {
    private CheckDiscountBean checkDiscountBean;

    public CheckDiscountEvent(CheckDiscountBean checkDiscountBean) {
        this.checkDiscountBean = checkDiscountBean;
    }

    public CheckDiscountBean getCheckDiscountBean() {
        return checkDiscountBean;
    }

    public void setCheckDiscountBean(CheckDiscountBean checkDiscountBean) {
        this.checkDiscountBean = checkDiscountBean;
    }
}
