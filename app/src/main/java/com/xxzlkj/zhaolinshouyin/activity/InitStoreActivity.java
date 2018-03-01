package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.utils.CountDownTimerUtils;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;


/**
 * 初始化店铺
 *
 * @author zhangrq
 */
public class InitStoreActivity extends ZLBaseActivity {

    public static final int REQUEST_CODE_SELECT_STORE = 333;
    private EditText mPasswordEditText;
    private TextView mEquipmentNumberTextView, mTimeTextView;

    public static Intent newIntent(Context context) {
        return new Intent(context, InitStoreActivity.class);
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_init_store);
    }

    @Override
    protected void findViewById() {
        mEquipmentNumberTextView = getView(R.id.id_equipment_number);// 设备编号
        mTimeTextView = getView(R.id.id_time);// 时间
        mPasswordEditText = getView(R.id.id_password);// 初始化密码
    }

    @Override
    protected void setListener() {
        // 确定按钮
        getView(R.id.id_sure).setOnClickListener(v -> {
            String password = mPasswordEditText.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                ToastManager.showShortToast(mContext, "请输入密码");
            } else
                startActivityForResult(SelectStoreActivity.newIntent(mContext, password), REQUEST_CODE_SELECT_STORE);
        });
    }

    @Override
    protected void processLogic() {
        CountDownTimerUtils.addCountDownTimer(mTimeTextView);
        mEquipmentNumberTextView.setText(String.format("设备编号：%s", ZLUtils.getDevicesNum()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_STORE) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CountDownTimerUtils.removeCountDownTimer(mTimeTextView);
    }
}
