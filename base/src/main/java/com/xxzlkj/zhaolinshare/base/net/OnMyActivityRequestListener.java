package com.xxzlkj.zhaolinshare.base.net;


import com.xxzlkj.zhaolinshare.base.base.BaseActivity;
import com.xxzlkj.zhaolinshare.base.model.BaseBean;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;

/**
 * 描述:根据钱罐返回字段定的规则
 *
 * @author zhangrq
 *         2016/12/27 14:34
 */
public abstract class OnMyActivityRequestListener<T extends BaseBean> extends OnActivityRequestListener<T> {
    private BaseActivity activity;

    public OnMyActivityRequestListener(BaseActivity baseActivity) {
        super(baseActivity);
        this.activity = baseActivity;
    }

    @Override
    public void handlerSuccess(BaseBean bean) {
        //        200	表示成功
//        400	数据错误或者为空，直接打印message信息
//        404	没有接收到传值

        if ("200".equals(bean.getCode())) {
            // 200	表示成功
            onSuccess((T) bean);
        } else {
            // 其余失败
            onFailed(true, bean.getCode(), bean.getMessage());
        }
    }

    @Override
    public void handlerError(int errorCode, String errorMessage) {
        onFailed(false, errorCode + "", errorMessage);
    }

    /**
     * 成功信息
     */
    public abstract void onSuccess(T bean);

    /**
     * 成功信息
     */
    public void onFailed(boolean isError, String code, String message) {
        if (isError) {
            // 400	数据错误或者为空，直接打印message信息
            // 404	没有接收到传值
            if ("400".equals(code))
                onError(code, message);
            else
                onException(404, message);
        } else {
            // 本地异常
            onException(Integer.parseInt(code), message);
        }
    }

    /**
     * 服务器返回的错误信息
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public void onError(String code, String message) {
        // 服务器返回的错误
        ToastManager.showMsgToast(getContext(), message);
    }

    /**
     * 本地异常信息
     *
     * @param exceptionCode    异常码
     * @param exceptionMessage 异常信息
     */
    public void onException(int exceptionCode, String exceptionMessage) {
        // 异常错误
        ToastManager.showErrorToast(getContext(), exceptionCode, exceptionMessage);
    }
}
