package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseActivity;
import com.xxzlkj.zhaolinshouyin.MainActivity;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.utils.CountDownTimerUtils;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/14 13:25
 */
public abstract class ZLBaseActivity extends BaseActivity {

    private ViewGroup titleViewLayout;
    private TextView tv_shop_name, tv_user_name, tv_current_time, tv_net_state, btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        // 判断是否有title
        titleViewLayout = getView(R.id.ll_title_layout);
        if (titleViewLayout != null) {
            // title
            tv_shop_name = getView(R.id.tv_shop_name);// 门店名
            tv_user_name = getView(R.id.tv_user_name);// 操作用户名
            tv_current_time = getView(R.id.tv_current_time);// 当前的时间
            tv_net_state = getView(R.id.tv_net_state);// 网络状态提示
            btn_back = getView(R.id.btn_back);// 返回
            // 设置点击
            // 返回
            btn_back.setOnClickListener(this::onBackHomeClick);
            // 设置值
            // 设置头的信息
            setTitleInfo();
        }

    }

    /**
     * 设置头的信息
     */
    private void setTitleInfo() {
        User user = ZhaoLinApplication.getInstance().getLoginUserDoLogin(this);
        if (user == null) {
            finish();
            return;
        }
        String store_id = ZLUtils.getStoreId();
        String store_title = ZLUtils.getStoreTitle();
        tv_shop_name.setText(String.format("门店：[%s]%s", store_id, store_title));// 门店名
        tv_user_name.setText(String.format("操作用户：%s", user.getName()));// 操作用户名
        // 设置时间
        CountDownTimerUtils.addCountDownTimer(tv_current_time);
    }

    public void setNetStateHint(boolean hasNet) {
        if (tv_net_state != null) {
            tv_net_state.setText(hasNet ? "网络已连接" : "网络未连接");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除，里面判断null了
        CountDownTimerUtils.removeCountDownTimer(tv_current_time);
    }

    public void onBackHomeClick(View view){
        ZLDialogUtil.showRawDialogTwoButton(this, "将要返回首页，当前页面不会记录数据，是否返回",
                "取消", null,
                "确定", (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(mContext, MainActivity.class));
                });
    }
}
