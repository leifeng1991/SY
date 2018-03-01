package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;
import com.xxzlkj.zhaolinshouyin.db.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/5 11:20
 */
public class SynGoodsBean extends BaseBean {
    private DataBean data = new DataBean();

    public static class DataBean {
        private String last_edit_time;
        private List<Goods> list = new ArrayList<>();

        public String getLast_edit_time() {
            return last_edit_time;
        }

        public void setLast_edit_time(String last_edit_time) {
            this.last_edit_time = last_edit_time;
        }

        public List<Goods> getList() {
            return list;
        }

        public void setList(List<Goods> list) {
            this.list = list;
        }
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }
}
