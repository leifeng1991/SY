package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;
import android.os.Handler;

import com.google.gson.Gson;
import com.xxzlkj.zhaolinshare.base.net.OnBaseRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.model.OrderItemBean;
import com.xxzlkj.zhaolinshouyin.model.SynUnloadOrderListBean;
import com.xxzlkj.zhaolinshouyin.model.UnLoadOrderListBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 描述: 同步订单的轮询
 *
 * @author zhangrq
 *         2017/4/26 16:28
 */
public class SynOrderLooper {
    public static final int DELAY_MILLIS = 5 * 60 * 1000;

    /**
     * 同步未上传订单
     */
    public static void synUploadOrder(Activity activity) {
        // 获取未上传订单列表、清单列表
        DaoUtils.getUnLoadOrderList(activity, new DaoUtils.OnDaoResultListener<UnLoadOrderListBean>() {

            @Override
            public void onSuccess(UnLoadOrderListBean unLoadOrderListBean) {
                List<OrderItemBean> requestList = unLoadOrderListBean.getRequestList();
                // 提交第一条
                submitRequest(activity, requestList, 0);
            }

            @Override
            public void onFailed() {
                // 没有未上传的订单，不同步
                // 5 分钟继续查询，继续上传
                new Handler().postDelayed(() -> synUploadOrder(activity), DELAY_MILLIS);
            }
        });

    }

    /**
     * 提交同步请求
     *
     * @param currentIndex 要提交的位置
     */
    private static void submitRequest(Activity activity, List<OrderItemBean> requestList, int currentIndex) {
        if (requestList == null || currentIndex >= requestList.size()) {
            // 数据有误，返回
            return;
        }
        // 提交一条
        OrderItemBean submitOrderItemBean = requestList.get(currentIndex);
        ArrayList<OrderItemBean> submitRequestList = new ArrayList<>();
        submitRequestList.add(submitOrderItemBean);

        ArrayList<Order> updateOrderList = new ArrayList<>();
        updateOrderList.add(submitOrderItemBean.getOrder());
        // 提交
        HashMap<String, String> map = new HashMap<>();
        map.put(C.P.JSON, new Gson().toJson(submitRequestList));
        RequestManager.createRequest(ZLURLConstants.ORDER_SYN_URL, map, new OnBaseRequestListener<SynUnloadOrderListBean>() {

            @Override
            public void handlerSuccess(SynUnloadOrderListBean bean) {
                SynUnloadOrderListBean.DataBean data = bean.getData();
                String code = bean.getCode();
                long synTime = data != null && data.getTime() != 0 ? data.getTime() : System.currentTimeMillis() / 1000;
                int upload_state = 0;// 上传状态，0：未上传，1：已上传，2：无库存
                if ("200".equals(code)) {
                    // 已上传
                    upload_state = 1;
                } else if ("400".equals(code)) {
                    // 无库存
                    upload_state = 2;
                }
                // 更新订单列表，更新编辑时间、上传状态
                DaoUtils.updateOrderList(updateOrderList, upload_state, synTime);
                // 判断是否，还有下一条数据，有提交下一条数据
                checkHasNextData(currentIndex, requestList, activity);
            }

            @Override
            public void handlerError(int errorCode, String errorMessage) {
                // 更新订单列表，更新上传状态
                DaoUtils.updateOrderList(updateOrderList, -1, System.currentTimeMillis() / 1000);
                // 判断是否，还有下一条数据，有提交下一条数据
                checkHasNextData(currentIndex, requestList, activity);
            }

        });
    }

    private static void checkHasNextData(int currentIndex, List<OrderItemBean> requestList, Activity activity) {
        // 判断是否，还有下一条数据，有提交下一条数据，没有，5 分钟继续查询，继续上传
        if (currentIndex + 1 < requestList.size()) {
            // 有下一条数据，继续提交
            submitRequest(activity, requestList, currentIndex + 1);
        } else {
            // 没有下一条数据，5 分钟继续查询，继续上传
            new Handler().postDelayed(() -> synUploadOrder(activity), DELAY_MILLIS);
        }
    }
}
