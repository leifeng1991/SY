package com.xxzlkj.zhaolinshare.base.net;

/**
 * @author zhangrq
 */
public interface OnRequestListener<T> {
    void handlerStart();

    void handlerSuccess(T bean);

    void handlerError(int errorCode, String errorMessage);

    void handlerEnd();
}
