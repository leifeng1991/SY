package com.xxzlkj.zhaolinshare.base.net;


import android.content.Context;

import com.xxzlkj.zhaolinshare.base.base.BaseActivity;

/**
 * 描述:
 *
 * @author zhangrq
 *         2016/8/16 10:34
 */
public abstract class OnActivityRequestListener<T> implements OnRequestListener<T> {
    private BaseActivity baseActivity;

    public OnActivityRequestListener(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public Context getContext() {
        return baseActivity.getApplicationContext();
    }

    @Override
    public void handlerStart() {
        baseActivity.showLoadDataAnim();
    }

    @Override
    public void handlerEnd() {
        baseActivity.hideLoadDataAnim();
        baseActivity = null;
    }
}
