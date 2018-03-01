package com.xxzlkj.zhaolinshouyin.model;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;

public class LoginUpBean extends BaseBean {
    /**
     * data : {"android":"0.1","android_url":"","ios":"0.1"}
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
         * android : 0.1
         * android_url :
         * ios : 0.1
         */
//        version	更新的版本号 不需更新为空
//        url	安卓更新地址 不需更新为空
//        state	0不更新 1可选更新 2强制更新
        private String version;
        private String url;
        private String state;
        private String desc;
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
