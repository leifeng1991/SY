package com.xxzlkj.zhaolinshouyin.event;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/1/10 9:55
 */


public class ClosePayEvent {
    private boolean isPaySuccess;// true:支付完成并且关闭支付界面 false:只是关闭支付界面

    public ClosePayEvent(boolean isPaySuccess) {
        this.isPaySuccess = isPaySuccess;
    }

    public boolean isPaySuccess() {
        return isPaySuccess;
    }

    public void setPaySuccess(boolean paySuccess) {
        isPaySuccess = paySuccess;
    }
}
