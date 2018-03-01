package com.xxzlkj.zhaolinshouyin.app;

import android.content.Context;

import com.xxzlkj.zhaolinshare.base.util.ToastManager;

import io.rong.imlib.RongIMClient.ConnectionStatusListener;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/5/23 17:27
 */
public class MyConnectionStatusListener implements ConnectionStatusListener {
    private final Context context;

    public MyConnectionStatusListener(Context context) {
        this.context = context;
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
//        NETWORK_UNAVAILABLE(-1, "Network is unavailable."),
//                CONNECTED(0, "Connect Success."),
//                CONNECTING(1, "Connecting"),
//                DISCONNECTED(2, "Disconnected"),
//                KICKED_OFFLINE_BY_OTHER_CLIENT(3, "Login on the other device, and be kicked offline."),
//                TOKEN_INCORRECT(4, "Token incorrect."),
//                SERVER_INVALID(5, "Server invalid.");
        switch (connectionStatus) {
            case NETWORK_UNAVAILABLE://网络不可用。
                System.out.println("网络不可用");
                break;
            case CONNECTED://连接成功。
                System.out.println("连接成功");
                break;
            case CONNECTING://连接中。
                System.out.println("连接中");
                break;
            case DISCONNECTED://断开连接。
                System.out.println("断开连接");
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                System.out.println("用户账户在其他设备登录，本机会被踢掉线");
//                Intent intent = ExitLoginDialogActivity.newIntent(context, "您的帐号在别的设备上登录，您被迫下线！");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(intent);
                ToastManager.showLongToast(context,"用户账户在其他设备登录，本机将无法接收到订单推送");
                break;
            case TOKEN_INCORRECT://TOKEN不正确
                System.out.println("TOKEN不正确");
                break;
            case SERVER_INVALID://服务无效
                System.out.println("服务无效");
                break;
        }
    }
}

