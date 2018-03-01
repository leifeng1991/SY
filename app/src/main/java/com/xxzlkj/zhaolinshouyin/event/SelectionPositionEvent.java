package com.xxzlkj.zhaolinshouyin.event;

/**
 * 描述:设置当前选中位置
 *
 * @author leifeng
 *         2018/1/9 17:54
 */


public class SelectionPositionEvent {
    private int selectPosition;

    public SelectionPositionEvent(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
