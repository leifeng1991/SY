package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseActivity;
import com.xxzlkj.zhaolinshare.base.net.OnMyActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.PreferencesSaver;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.activity.OrderListActivity;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.event.ReceiveOrderPushEvent;
import com.xxzlkj.zhaolinshouyin.model.UnOperatingBean;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * 描述:
 *
 * @author zhangrq
 *         2018/1/22 9:52
 */

public class FloatWindowUtils {

    private static final String TAG_ORDER = "tag_order";

    /**
     * 初始化订单推送状态
     */
    public static void initOrderPush(Application application) {
        // 没有创建过，创建展示
        View rootView = View.inflate(application, R.layout.view_float_order, null);
        // 点击调到列表
        rootView.setOnClickListener(v -> ActivityUtils.jumpToActivity(application, OrderListActivity.newIntent(application, 1)));
        // 展示
        FloatWindow
                .with(application)
                .setTag(TAG_ORDER)
                .setView(rootView)
                .setWidth(application.getResources().getDimensionPixelOffset(R.dimen.float_order_width))
                .setHeight(application.getResources().getDimensionPixelOffset(R.dimen.float_order_width))
                .setX(Screen.width, 1f)
                .setY(Screen.height, 0.4f)
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new BounceInterpolator())
                .setDesktopShow(false)
                .build();

        // 注册,这个要放到FloatWindow的后面
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                // 进入到前台,先判断是否登陆，再判断显示隐藏
                User loginUser = ZhaoLinApplication.getInstance().getLoginUser();
                if (loginUser != null) {
                    // 登陆了判断显示隐藏
                    int unPrintOrderNum = PreferencesSaver.getIntAttr(application, C.S.UN_PRINT_ORDER_NUM, 0);// 未打印订单数量
                    setOrderPushShowOrHide(application, unPrintOrderNum);
                } else {
                    // 未登陆，直接隐藏
                    setOrderPushHide();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                // 进入到后台,隐藏，FloatWindow已处理

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 设置订单推送显示或者隐藏
     *
     * @param unPrintOrderNum 未打印订单的数量
     */
    public static void setOrderPushShowOrHide(Context mContext, int unPrintOrderNum) {
        // 保存数量
        PreferencesSaver.setIntAttr(mContext, C.S.UN_PRINT_ORDER_NUM, unPrintOrderNum);// 未打印订单数量
        if (unPrintOrderNum <= 0) {
            // 不显示弹框
            setOrderPushHide();
        } else {
            // 显示订单数量
            setOrderPushShow(unPrintOrderNum);
        }
    }

    /**
     * 设置订单推送显示
     */
    private static void setOrderPushShow(int unPrintOrderNum) {
        IFloatWindow iFloatWindow = FloatWindow.get(TAG_ORDER);
        if (iFloatWindow != null) {
            // 设置数量
            View view = iFloatWindow.getView();
            TextView tv_num = view.findViewById(R.id.tv_num);
            tv_num.setText(unPrintOrderNum > 99 ? "99+" : String.valueOf(unPrintOrderNum));
            // 设置展示
            iFloatWindow.show();
        }
    }

    /**
     * 设置订单推送隐藏
     */
    public static void setOrderPushHide() {
        IFloatWindow iFloatWindow = FloatWindow.get(TAG_ORDER);
        if (iFloatWindow != null) {
            iFloatWindow.hide();
        }
    }

    /**
     * 获取未处理的数量
     */
    public static void getNoOperatingNum(BaseActivity activity) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(C.P.STORE_ID, ZLUtils.getStoreId());
        RequestManager.createRequest(ZLURLConstants.ORDER_NOT_OPERATING_URL, stringStringHashMap, new OnMyActivityRequestListener<UnOperatingBean>(activity) {

            @Override
            public void onSuccess(UnOperatingBean bean) {
                String not_operating = bean.getData().getNot_operating();
                EventBus.getDefault().postSticky(new ReceiveOrderPushEvent(NumberFormatUtils.toInt(not_operating)));
            }
        });
    }
}
