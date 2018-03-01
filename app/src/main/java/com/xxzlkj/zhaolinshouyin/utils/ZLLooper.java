package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.xxzlkj.zhaolinshare.base.net.OnBaseRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.db.GoodsBarCode;
import com.xxzlkj.zhaolinshouyin.db.GoodsClass;
import com.xxzlkj.zhaolinshouyin.db.Params;
import com.xxzlkj.zhaolinshouyin.db.ThreeGoods;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.listener.OnSyncListener;
import com.xxzlkj.zhaolinshouyin.model.SynGoodsBean;
import com.xxzlkj.zhaolinshouyin.model.SynGoodsClassBean;
import com.xxzlkj.zhaolinshouyin.model.SynGoodsCodeBean;
import com.xxzlkj.zhaolinshouyin.model.SynThreeGoodsBean;
import com.xxzlkj.zhaolinshouyin.model.UserListBean;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/4/26 16:28
 */
public class ZLLooper {
    private static int PERIOD = 5 * 60 * 1000;
    private static Timer timer;
    private static TimerTask timerTask;

    public static void startLooper(Activity activity) {
        Context context = activity.getApplicationContext();
        // 归原
        removeLooper();
        timer = new Timer();
        // 提交数据
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 提交数据
                if (NetStateManager.isAvailable(context)) {
                    // 有网，同步数据
                    activity.runOnUiThread(() -> {
                        // 同步分类表
                        synGoodsClass(null);
                        // 同步条形码表
                        synGoodsCode(null);
                        String store_id = ZLUtils.getStoreId();
                        if (!TextUtils.isEmpty(store_id)) {
                            // 同步商品表
                            synGoods(store_id, null);
                            // 同步三方商品表
                            synThreeGoods(store_id, null);
                            // 同步人员列表
                            synUser(store_id, null);
                        }
                    });
                }
            }


        };
        // 常用于轮询
        timer.schedule(timerTask, 0, PERIOD);// 5000(5秒)后，开始执行第一次run方法，此后每隔2秒调用一次
    }

    /**
     * 移除操作
     */
    public static void removeLooper() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 同步商品
     */
    public static void synGoods(String store_id, OnSyncListener listener) {
        HashMap<String, String> map = new HashMap<>();
        Params params = DaoUtils.getParams();
        map.put(C.P.LAST_EDIT_TIME, params.getSyn_time_goods());
        map.put(C.P.STORE_ID, store_id);
        RequestManager.createRequest(ZLURLConstants.GET_GOODS_LIST_URL, map, new OnBaseRequestListener<SynGoodsBean>() {
            @Override
            public void handlerSuccess(SynGoodsBean bean) {
                // 获取成功
                final SynGoodsBean.DataBean data = bean.getData();
                List<Goods> goodsList = data.getList();
                if (goodsList != null && goodsList.size() > 0) {
                    // 添加到本地数据库，保存同步时间
                    DaoUtils.insertOrReplaceGoodsInChildThread(goodsList, new DaoUtils.OnDaoResultListener<String>() {
                        @Override
                        public void onSuccess(String results) {
                            // 保存同步时间
                            params.setSyn_time_goods(data.getLast_edit_time());
                            DaoUtils.insertOrReplaceParams(params);
                            // 设置监听
                            if (listener != null)
                                listener.onSyncSuccess();
                        }

                        @Override
                        public void onFailed() {
                            if (listener != null)
                                listener.onSyncFailed();
                        }
                    });
                } else {
                    if (listener != null)
                        listener.onSyncSuccess();

                }
            }

            @Override
            public void handlerError(int errorCode, String errorMessage) {
                if (listener != null)
                    listener.onSyncFailed();
            }
        });
    }

    /**
     * 同步三方商品
     */
    public static void synThreeGoods(String store_id, OnSyncListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put(C.P.STORE_ID, store_id);
        RequestManager.createRequest(ZLURLConstants.THREE_STOCK_GOODS_URL, map, new OnBaseRequestListener<SynThreeGoodsBean>() {
            @Override
            public void handlerSuccess(SynThreeGoodsBean bean) {
                // 获取成功
                List<ThreeGoods> threeGoodsList = bean.getData();
                if (threeGoodsList != null && threeGoodsList.size() > 0) {
                    // 添加到本地数据库，保存同步时间
                    DaoUtils.insertOrReplaceThreeGoodsInChildThread(threeGoodsList, new DaoUtils.OnDaoResultListener<String>() {
                        @Override
                        public void onSuccess(String results) {
                            // 设置监听
                            if (listener != null)
                                listener.onSyncSuccess();
                        }

                        @Override
                        public void onFailed() {
                            if (listener != null)
                                listener.onSyncFailed();
                        }
                    });
                } else {
                    if (listener != null)
                        listener.onSyncSuccess();

                }
            }

            @Override
            public void handlerError(int errorCode, String errorMessage) {
                if (listener != null)
                    listener.onSyncFailed();
            }
        });
    }

    /**
     * 同步商品分类
     */
    public static void synGoodsClass(OnSyncListener listener) {
        HashMap<String, String> map = new HashMap<>();
        Params params = DaoUtils.getParams();
        map.put(C.P.LAST_EDIT_TIME, params.getSyn_time_goods_class());
        RequestManager.createRequest(ZLURLConstants.GET_GROUP_LIST_URL, map, new OnBaseRequestListener<SynGoodsClassBean>() {
            @Override
            public void handlerSuccess(SynGoodsClassBean bean) {
                // 获取成功
                final SynGoodsClassBean.DataBean data = bean.getData();
                List<GoodsClass> list = data.getList();
                if (list != null && list.size() > 0) {
                    // 添加到本地数据库，保存同步时间
                    DaoUtils.insertOrReplaceGoodsClassInChildThread(list, results -> {
                        // 回调在子线程
                        // 保存同步时间
                        params.setSyn_time_goods_class(data.getLast_edit_time());
                        DaoUtils.insertOrReplaceParams(params);
                        // 设置监听
                        if (listener != null)
                            listener.onSyncSuccess();
                    });
                } else {
                    if (listener != null)
                        listener.onSyncSuccess();
                }
            }

            @Override
            public void handlerError(int errorCode, String errorMessage) {
                if (listener != null)
                    listener.onSyncFailed();
            }
        });
    }

    /**
     * 同步商品条形码
     */
    public static void synGoodsCode(OnSyncListener listener) {
        HashMap<String, String> map = new HashMap<>();
        Params params = DaoUtils.getParams();
        map.put(C.P.LAST_EDIT_TIME, params.getSyn_time_goods_code());
        RequestManager.createRequest(ZLURLConstants.GET_GOODS_CODE_LIST_URL, map, new OnBaseRequestListener<SynGoodsCodeBean>() {
            @Override
            public void handlerSuccess(SynGoodsCodeBean bean) {
                // 获取成功
                final SynGoodsCodeBean.DataBean data = bean.getData();
                List<GoodsBarCode> list = data.getList();
                if (list != null && list.size() > 0) {
                    // 添加到本地数据库，保存同步时间
                    DaoUtils.insertOrReplaceGoodsCodeInChildThread(list, new DaoUtils.OnDaoResultListener<String>() {
                        @Override
                        public void onSuccess(String results) {
                            // 保存同步时间
                            params.setSyn_time_goods_code(data.getLast_edit_time());
                            DaoUtils.insertOrReplaceParams(params);
                            // 设置监听
                            if (listener != null)
                                listener.onSyncSuccess();
                        }

                        @Override
                        public void onFailed() {
                            if (listener != null)
                                listener.onSyncFailed();
                        }
                    });
                }
            }

            @Override
            public void handlerError(int errorCode, String errorMessage) {
                if (listener != null)
                    listener.onSyncFailed();
            }
        });
    }

    /**
     * 同步用户表
     *
     * @param store_id 必传，店铺id
     * @param listener 选传，可以为null
     */
    public static void synUser(String store_id, DaoUtils.OnDaoResultListener<String> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put(C.P.STORE_ID, store_id);
        RequestManager.createRequest(ZLURLConstants.GET_STORE_USER_URL, map, new OnBaseRequestListener<UserListBean>() {
            @Override
            public void handlerSuccess(UserListBean bean) {
                // 获取成功
                List<User> data = bean.getData();
                if (data != null && data.size() > 0) {
                    DaoUtils.deleteAndInsertUserInChildThread(data, listener);
                } else if (listener != null)
                    listener.onFailed();
            }

            @Override
            public void handlerError(int errorCode, String errorMessage) {
                if (listener != null)
                    listener.onFailed();
            }
        });
    }
}
