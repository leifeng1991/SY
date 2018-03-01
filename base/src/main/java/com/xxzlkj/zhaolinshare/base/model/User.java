package com.xxzlkj.zhaolinshare.base.model;

public class User extends BaseBean{

    /**
     * data : {"id":"111346","phone":"15110181021","jpushid":"1234564","token":"Jbh7UWfHegwAEzS8Yr3crvb3dcf3wo98g51InF7+5eTxNIrKisKwjtFSgM1SdY85cAiFfjoV85TqVN90kun5qQ==","username":"","simg":"/Public/upfile/touxiang.jpg","type":"1"}
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
         * id : 111346
         * phone : 15110181021
         * jpushid : 1234564
         * token : Jbh7UWfHegwAEzS8Yr3crvb3dcf3wo98g51InF7+5eTxNIrKisKwjtFSgM1SdY85cAiFfjoV85TqVN90kun5qQ==
         * username :
         * simg : /Public/upfile/touxiang.jpg
         * type : 1
         */
        private String id = "";
        private String phone;
        private String jpushid;
        private String token;
        private String username;
        private String simg;
        private String type;
        private String attention;

        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getJpushid() {
            return jpushid;
        }

        public void setJpushid(String jpushid) {
            this.jpushid = jpushid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSimg() {
            return simg;
        }

        public void setSimg(String simg) {
            this.simg = simg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
