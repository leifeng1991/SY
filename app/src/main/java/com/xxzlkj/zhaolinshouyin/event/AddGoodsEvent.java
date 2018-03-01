package com.xxzlkj.zhaolinshouyin.event;

import com.xxzlkj.zhaolinshouyin.db.Goods;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/1/4 18:06
 */


public class AddGoodsEvent {
    private Goods goods;

    public AddGoodsEvent(Goods goods) {
        this.goods = goods;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}
