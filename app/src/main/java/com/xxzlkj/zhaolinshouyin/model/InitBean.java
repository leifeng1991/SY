package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/9 15:28
 */


public class InitBean extends BaseBean {
    /**
     * data : {"token":"oON5SG/GDTNxhQBvfOYOZO7wvIVa11xjsysZcWrUiAYMNUqJi2JZRrI0uUHBqzZYu6t5OV+Wye2sIwCwzuCE5A==","id":1}
     */

    private DataBean data = new DataBean();

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * token : oON5SG/GDTNxhQBvfOYOZO7wvIVa11xjsysZcWrUiAYMNUqJi2JZRrI0uUHBqzZYu6t5OV+Wye2sIwCwzuCE5A==
         * id : 1
         */

        private String token;
        private String id;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
