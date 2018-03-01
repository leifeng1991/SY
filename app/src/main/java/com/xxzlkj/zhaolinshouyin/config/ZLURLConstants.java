package com.xxzlkj.zhaolinshouyin.config;


import com.xxzlkj.zhaolinshouyin.BuildConfig;

/**
 * @author zhangrq
 */
public class ZLURLConstants {

    private static String BASE_URL;// 商城基地址
    private static String BASE_WEB_URL;// 商城 H5 基地址
    private static String BASE_URL_INDEX;// 商城接口地址
    private static String BASE_WEB_URL_WEB;// 商城 H5 地址
    private static String BASE_ZHAOLIN_URL;// 兆邻社区商家端基地址
    @SuppressWarnings("FieldCanBeLocal")
    private static int LX_NET_MODEL = BuildConfig.isTest ? 1 : 2;//当前访问的网络模式

    static {
        switch (LX_NET_MODEL) {
            case 1:
                // 测试地址
                BASE_URL = "http://sysp7test.zhaolin365.com";//  基地址
                BASE_WEB_URL = "http://365test.planetgroup.org.cn";//  H5 基地址
                break;
            case 2:
                // 正式地址
                BASE_URL = "https://zlsys.zhaolin365.com";//  基地址
                BASE_WEB_URL = "https://appad.zhaolin365.com";//  H5 基地址
                break;
            case 3:
                // 本地地址
                BASE_URL = "http://192.168.16.89";//  基地址
                BASE_WEB_URL = "http://365test.planetgroup.org.cn";//  H5 基地址
                break;
        }
        BASE_URL_INDEX = BASE_URL + "/api/index/";//  接口地址
        BASE_WEB_URL_WEB = BASE_WEB_URL + "/api/web/";// H5 接口地址
        BASE_ZHAOLIN_URL = BASE_WEB_URL + "/shop/Index/";// 兆邻社区商家端基地址
    }

    /**
     * app启动
     */
    public static final String REQUEST_LOGIN_UP = BASE_URL_INDEX + "loginUp";
    /**
     * 同步商品分类
     */
    public static final String GET_GROUP_LIST_URL = BASE_URL_INDEX + "getGroupList";
    /**
     * 同步商品条形码
     */
    public static final String GET_GOODS_CODE_LIST_URL = BASE_URL_INDEX + "getGoodsCodeList";
    /**
     * 同步商品
     */
    public static final String GET_GOODS_LIST_URL = BASE_URL_INDEX + "getGoodsList";
    /**
     * 同步三方商品
     */
    public static final String THREE_STOCK_GOODS_URL = BASE_URL_INDEX + "threeStockGoods";
    /**
     * 同步订单表
     */
    public static final String ORDER_SYN_URL = BASE_URL_INDEX + "orderSyn";
    /**
     * 增加订单
     */
    public static final String ADD_ORDER_URL = BASE_URL_INDEX + "addOrder";
    /**
     * 订单退款
     */
    public static final String ORDER_RETURN_URL = BASE_URL_INDEX + "orderReturn";
    /**
     * 订单前检测
     */
    public static final String CHECK_ORDER_URL = BASE_URL_INDEX + "checkOrder";
    /**
     * 增加退款订单
     */
    public static final String ADD_RETURN_ORDER_URL = BASE_URL_INDEX + "addReturnOrder";
    /**
     * 人员同步
     */
    public static final String GET_STORE_USER_URL = BASE_URL_INDEX + "getStoreUser";
    /**
     * 获取小区id
     */
    public static final String GET_STORE_LIST_URL = BASE_URL_INDEX + "getStoreList";
    /**
     * 初始化密码验证
     */
    public static final String CHECK_STORE_PASSWORD_URL = BASE_URL_INDEX + "checkStorePassword";
    /**
     * 修改密码
     */
    public static final String EDIT_USER_PASSWORD_URL = BASE_URL_INDEX + "editUserPassword";

    // ---------------------------------- H5 ----------------------------------------
    /**
     * 副屏广告
     */
    public static final String WEB_URL_STORE_ADS = BASE_WEB_URL_WEB + "storeAds/?";

    // ---------------------------------- 兆邻商家端 ----------------------------------------
    /**
     * 订单列表数据
     */
    public static final String ORDER_URL = BASE_ZHAOLIN_URL + "orderShouyin";
    /**
     * 订单详情数据
     */
    public static final String ORDER_DETAIL_URL = BASE_ZHAOLIN_URL + "order_detail";
    /**
     * 店铺员工接单
     */
    public static final String RECEIVE_ORDER_URL = BASE_ZHAOLIN_URL + "receive_order";
    /**
     * 店铺员工确认送到
     */
    public static final String CONFIRM_RECEIPT_URL = BASE_ZHAOLIN_URL + "confirm_receipt";
    /**
     * 未处理订单数量
     */
    public static final String ORDER_NOT_OPERATING_URL = BASE_ZHAOLIN_URL + "OrderNotOperating";
    /**
     * 修改订单为已打印状态
     */
    public static final String ORDER_PRINT_URL = BASE_ZHAOLIN_URL + "orderPrint";
}
