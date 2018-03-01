package com.xxzlkj.zhaolinshare.base.net;

import android.os.Handler;
import android.os.Message;


/**
 * @param <T> 代表要返回的数据 类型
 * @author zhangrq
 */
class RequestHandler<T> extends Handler {

    private static final int ON_METHOD_START = 0;
    private static final int ERROR = 1;
    private static final int DATA_BACK_SUCCESS = 2;
    private static final int ON_METHOD_END = 3;
    OnRequestListener<T> listener;

    RequestHandler(OnRequestListener<T> listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ON_METHOD_START:
                // 开始访问网络
                listener.handlerStart();
                break;
            case ERROR:
                // 返回数据错误
                int errorCode = msg.arg1;
                String errorMessage = (String) msg.obj;
                if (errorCode == RequestRunnable.EXIT_LOGIN_USER) {
                    // 退出登录
                    exitLoginUser(errorMessage);
                }

                listener.handlerError(errorCode, errorMessage);
                break;
            case DATA_BACK_SUCCESS:
                // 返回数据成功
                T result = (T) msg.obj;
                listener.handlerSuccess(result);
                break;
            case ON_METHOD_END:
                // 访问网络结束
                listener.handlerEnd();
                break;
        }
    }

    // 在子线程中发消息 通知任务开始
    void notifyStart() {
        Message msg = Message.obtain();
        msg.what = ON_METHOD_START;
        sendMessage(msg);
    }

    // 在子线程中发消息 通知任务失败
    void notifyFail(int errorCode, String errorMessage) {
        Message msg = Message.obtain();
        msg.what = ERROR;
        msg.arg1 = errorCode;
        msg.obj = errorMessage;
        sendMessage(msg);
    }

    // 在子线程中发消息 通知任务失成功数据返回
    void notifySuccess(Object object) {
        Message msg = Message.obtain();
        msg.obj = object;
        msg.what = DATA_BACK_SUCCESS;
        sendMessage(msg);
    }

    // 在子线程中发消息 通知任务结束
    void notifyEnd() {
        Message msg = Message.obtain();
        msg.what = ON_METHOD_END;
        sendMessage(msg);
    }

    // 退出登录用户
    private void exitLoginUser(String errorMessage) {
        // 不要Toast提示需要在handlerError里面处理
//        Context context = QGApplication.getInstance().getApplicationContext();
//        ActivityUtils.jumpToActivity(context, ExitLoginDialogActivity.newIntent(context, errorMessage));
    }
}
