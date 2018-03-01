package com.xxzlkj.zhaolinshouyin;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.xxzlkj.zhaolinshare.base.base.BaseActivity;

import aclasdriver.OpScale;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class TestActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void loadViewLayout() {

    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic() {

    }

    public void open(View view) {
        OpScale scale = new OpScale();
        scale.Open(null);
        scale.OpenDrawer();
    }
}
