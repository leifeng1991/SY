package com.xxzlkj.zhaolinshouyin.event;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/1/4 18:06
 */


public class ReceiveOrderPushEvent {
    private int unPrintOrderNum;

    public ReceiveOrderPushEvent(int unPrintOrderNum) {
        this.unPrintOrderNum = unPrintOrderNum;
    }

    public int getUnPrintOrderNum() {
        return unPrintOrderNum;
    }

    public void setUnPrintOrderNum(int unPrintOrderNum) {
        this.unPrintOrderNum = unPrintOrderNum;
    }
}
