package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;
import com.xxzlkj.zhaolinshouyin.db.ThreeGoods;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/5 11:20
 */
public class SynThreeGoodsBean extends BaseBean {
    private List<ThreeGoods> data = new ArrayList<>();

    public SynThreeGoodsBean(List<ThreeGoods> data) {
        this.data = data;
    }

    public List<ThreeGoods> getData() {
        return data;
    }

    public void setData(List<ThreeGoods> data) {
        this.data = data;
    }
}
