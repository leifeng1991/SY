package com.xxzlkj.zhaolinshouyin.event;

import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;

import java.util.List;

/**
 * 描述:刷新副屏幕显示列表
 *
 * @author leifeng
 *         2018/1/12 11:34
 */
public class RefreshViceDisplayListEvent {
    private String number;// 总数量
    private String price;// 总价格
    private int selectPosition;
    private final List<InputGoodsBean> list;

    public RefreshViceDisplayListEvent(String number, String price, List<InputGoodsBean> list, int selectPosition) {
        this.number = number;
        this.price = price;
        this.list = list;
        this.selectPosition = selectPosition;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<InputGoodsBean> getList() {
        return list;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
