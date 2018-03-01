package com.xxzlkj.zhaolinshare.base.base;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.xxzlkj.zhaolinshare.base.R;
import com.xxzlkj.zhaolinshare.base.util.SystemBarUtils;


/**
 * 钱罐应用生成BaseActivity
 *
 * @author zhangrq
 */
public abstract class MyBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 自己应用的特色
        super.onCreate(savedInstanceState);
        SystemBarUtils.setStatusBarColor(this, ContextCompat.getColor(mContext, R.color.white));
        SystemBarUtils.setStatusBarLightMode(this, true);
        View titleBar = findViewById(R.id.titleBar);
        if (titleBar != null)
            titleBar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        findViewById(android.R.id.content).setBackgroundColor(ContextCompat.getColor(mContext, R.color.content_bg_zhaolin));
    }

}
