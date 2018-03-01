package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

public class UnOperatingBean extends BaseBean {

    private DataBean data = new DataBean();

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String not_operating;

        public String getNot_operating() {
            return not_operating;
        }

        public void setNot_operating(String not_operating) {
            this.not_operating = not_operating;
        }
    }
}
