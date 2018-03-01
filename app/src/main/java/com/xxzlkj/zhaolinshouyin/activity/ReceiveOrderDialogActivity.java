package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.TextViewUtils;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.model.RongPushBean;


/**
 * 描述:退出登录的DialogActivity
 *
 * @author zhangrq
 *         2017/1/7 13:16
 */

public class ReceiveOrderDialogActivity extends AppCompatActivity {

    public static final String RONG_PUSH_BEAN = "rong_push_bean";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_receive_order_dialog);
        // 初始化控件
        TextView tv_order_all_num = findViewById(R.id.tv_order_all_num);// 商品数量
        TextView tv_order_all_price = findViewById(R.id.tv_order_all_price);// 实付金额
        TextView tv_order_add_time = findViewById(R.id.tv_order_add_time);// 下单时间

        TextView tv_order_name = findViewById(R.id.tv_order_name);// 收货人
        TextView tv_order_phone = findViewById(R.id.tv_order_phone);// 电话
        TextView tv_order_address = findViewById(R.id.tv_order_address);// 配送地址

        TextView tv_cancel = findViewById(R.id.tv_cancel);// 按钮-取消
        TextView tv_config = findViewById(R.id.tv_config);// 按钮-查看
        // 获取值
        RongPushBean rongPushBean = (RongPushBean) getIntent().getSerializableExtra(RONG_PUSH_BEAN);// 推送过来的消息
        if (rongPushBean == null) {
            return;
        }
        // 设置值
        TextViewUtils.setTextHasValue(tv_order_all_num, "商品数量:", StringUtil.saveThreeDecimal(rongPushBean.getNum()));// 商品数量
        TextViewUtils.setTextHasValue(tv_order_all_price, "实付金额:", StringUtil.saveTwoDecimal(rongPushBean.getPrice()));// 实付金额
        TextViewUtils.setTextHasValue(tv_order_add_time, "下单时间:", DateUtils.getYearMonthDay(NumberFormatUtils.toLong(rongPushBean.getBuytime()) * 1000, "yyyy/MM/dd HH:mm"));// 下单时间

        TextViewUtils.setTextHasValue(tv_order_name, "收货人:", rongPushBean.getAddress_name());// 收货人
        TextViewUtils.setTextHasValue(tv_order_phone, "电话:", rongPushBean.getAddress_phone());// 电话
        TextViewUtils.setTextHasValue(tv_order_address, "配送地址:", rongPushBean.getAddress_address());// 配送地址
        // 设置点击
        // 取消按钮
        tv_cancel.setOnClickListener(v -> finish());
        // 查看按钮
        tv_config.setOnClickListener(v -> {
            // 调到详情
            startActivity(OrderDesActivity.newIntent(getApplicationContext(), true, rongPushBean.getId(), null));
            finish();
        });
        // 点击底部，销毁弹框
        findViewById(R.id.vg_rootView).setOnClickListener(v -> finish());
    }

    /**
     * @param rongPushBean （必传）推送过来的消息
     */
    public static Intent newIntent(Context context, RongPushBean rongPushBean) {
        Intent intent = new Intent(context, ReceiveOrderDialogActivity.class);
        intent.putExtra(RONG_PUSH_BEAN, rongPushBean);
        return intent;
    }
}
