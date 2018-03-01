package com.xxzlkj.zhaolinshare.base.config;

/**
 * @author zhangrq
 */
public class BaseURLConstants {

    private static String BASE_URL;// 商城基地址
    public static String BASE_URL_INDEX;// 商城接口地址
    private static String BASE_URL_WEB;// 商城web地址
    private static String BASE_URL_WUYE;// 物业

    private static String BASE_URL_JZ;// 家政
    private static String BASE_URL_YL;// 养老
    private static String BASE_H5_URL;// 商城H5
    private static String BASE_H5_URL_YL;// 养老H5
    @SuppressWarnings({"FieldCanBeLocal", "ConstantConditions"})
//    private static int LX_NET_MODEL = BuildConfig.isTest ? 1 : 2;//当前访问的网络模式
    private static int LX_NET_MODEL = true ? 1 : 2;//当前访问的网络模式

    static {
        switch (LX_NET_MODEL) {
            case 1:
                //测试地址
                BASE_URL = "http://365test.planetgroup.org.cn";// 商城基地址
                BASE_URL_INDEX = BASE_URL + "/Api/Index2/";// 商城接口地址
                BASE_URL_WEB = BASE_URL + "/api/web/";// 商城web地址

                BASE_URL_WUYE = BASE_URL + "/Api/Wuye/";// 物业

                BASE_URL_JZ = BASE_URL + "/api/jiazheng/";// 家政
                BASE_URL_YL = BASE_URL + "/yanglao/Index/";// 养老
                break;
            case 2:
                //正式地址
                BASE_URL = "https://appad.zhaolin365.com";// 商城基地址
                BASE_URL_INDEX = BASE_URL + "/Api/Index2/";// 商城接口地址
                BASE_URL_WEB = BASE_URL + "/api/web/";// 商城web地址

                BASE_URL_WUYE = BASE_URL + "/Api/Wuye/";// 物业

                BASE_URL_JZ = BASE_URL + "/api/jiazheng/";// 家政
                BASE_URL_YL = BASE_URL + "/yanglao/Index/";// 养老
                break;
        }
    }
    /**
     * 获取腾讯token
     */
    public static final String TX_IMG_SIGN_URL = BASE_URL_INDEX + "tx_img_sign";

}
