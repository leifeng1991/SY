package com.xxzlkj.zhaolinshare.base.app;

import android.support.multidex.MultiDexApplication;


/**
 * 描述:
 *
 * @author zhangrq
 *         2017/9/21 10:21
 */
public class BaseApplication extends MultiDexApplication {
    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;

    }

    public static BaseApplication getBaseApplication() {
        return baseApplication;
    }
}
