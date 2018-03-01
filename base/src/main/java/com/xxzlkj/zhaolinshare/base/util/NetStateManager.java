package com.xxzlkj.zhaolinshare.base.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络状态的管理者
 *
 * @author zhangrq
 */
public class NetStateManager {
    private static NetStateManager netStateManager;
    private List<OnNetStateChangeListener> onNetStateChangeListeners = new ArrayList<>();
    private BroadcastReceiver mNetStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAvailable(context)) {
                // 网络可用
                notifyNetAvailable(true);
            } else {
                // 网络不用
                notifyNetAvailable(false);
            }
        }
    };

    /**
     * 通知所有的监听，当前的网络状态
     */
    private void notifyNetAvailable(boolean isAvailable) {
        for (OnNetStateChangeListener netStateChangeListener : onNetStateChangeListeners) {
            netStateChangeListener.onNetStateChange(isAvailable);
        }
    }

    public static NetStateManager getInstance() {
        if (null == netStateManager)
            netStateManager = new NetStateManager();
        return netStateManager;
    }

    /**
     * 判断当前网络是否可用，true为可用
     */
    public static boolean isAvailable(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager != null) {
            NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();// 此类不能两次获取
            return netInfo != null && netInfo.isAvailable();
        }
        return false;
    }

    /**
     * 注册监听网络状态改变的广播
     */
    public void registerNetStateReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mNetStateReceiver, filter);
    }


    /***
     * 反注册监听网络状态改变的广播
     */
    public void unregisterNetStateReceiver(Context context) {
        context.unregisterReceiver(mNetStateReceiver);
    }

    /**
     * 增加某个监听到集合监听中
     */
    public void addOnNetStateChangeListener(OnNetStateChangeListener listener) {
        onNetStateChangeListeners.add(listener);
    }

    /**
     * 移除集合中某个监听
     */
    public void removeOnNetStateChangeListener(OnNetStateChangeListener listener) {
        onNetStateChangeListeners.remove(listener);
    }

    /**
     * 移除集合中所有的监听
     */
    public void removeAllOnNetStateChangeListener() {
        onNetStateChangeListeners.clear();
    }

    public interface OnNetStateChangeListener {
        /**
         * 网络状态改变
         *
         * @param isAvailable 网络是否可用，true为可用
         */
        void onNetStateChange(boolean isAvailable);
    }
}
