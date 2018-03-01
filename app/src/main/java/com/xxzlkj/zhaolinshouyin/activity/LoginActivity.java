package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.MainActivity;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.utils.CountDownTimerUtils;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.DisplayUtils;
import com.xxzlkj.zhaolinshouyin.utils.ZLLooper;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;


/**
 * 检测登录页
 *
 * @author zhangrq
 */
public class LoginActivity extends ZLBaseActivity implements TextWatcher {
    private EditText mAccountEditText, mPasswordEditText;
    private Button mLoginButton;
    private TextView mEquipmentNumberTextView, mTimeTextView, mLoginErrorTipTextView;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void findViewById() {
        mEquipmentNumberTextView = getView(R.id.id_equipment_number);// 设备编号
        mTimeTextView = getView(R.id.id_time);// 时间
        mAccountEditText = getView(R.id.id_account);// 账号
        mPasswordEditText = getView(R.id.id_password);// 账号
        mLoginButton = getView(R.id.id_login);// 账号
        mLoginErrorTipTextView = getView(R.id.id_login_error_tip);
    }

    @Override
    protected void setListener() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mAccountText = mAccountEditText.getText().toString().trim();
                if (TextUtils.isEmpty(mAccountText)) {
                    ToastManager.showShortToast(mContext, "请输入账号");
                    return;
                }
                String mPasswordText = mPasswordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(mPasswordText)) {
                    ToastManager.showShortToast(mContext, "请输入密码");
                    return;
                }

                login(mAccountText, mPasswordText);
            }
        });
        mAccountEditText.addTextChangedListener(this);
        mPasswordEditText.addTextChangedListener(this);
    }


    @Override
    protected void processLogic() {
        CountDownTimerUtils.addCountDownTimer(mTimeTextView);
        mEquipmentNumberTextView.setText(String.format("设备编号：%s", ZLUtils.getDevicesNum()));
        // 判断是否初始化,初始化了，本地的用户列表已经有了
        String store_id = ZLUtils.getStoreId();
        if (TextUtils.isEmpty(store_id)) {
            // 没初始化，跳到初始化页面
            startActivity(InitStoreActivity.newIntent(mContext));
            finish();
        }else {
            // 已经初始化了，重新的获取所有用户，防止新增的没有加入
            ZLLooper.synUser(store_id,null);
        }
        // 展示副屏
        DisplayUtils.showViceScreen(mContext);// 展示副屏

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CountDownTimerUtils.removeCountDownTimer(mTimeTextView);
    }

    private void login(String mAccountText, String mPasswordText) {
        DaoUtils.login(this, mAccountText, mPasswordText, new DaoUtils.OnDaoResultListener<User>() {
            @Override
            public void onSuccess(User bean) {
                mLoginErrorTipTextView.setVisibility(View.INVISIBLE);
                // 登录成功，保存状态，跳到主页面
                ToastManager.showShortToast(mContext, "登录成功");
                ZhaoLinApplication.getInstance().setSuccessLoginUser(bean);
                startActivity(MainActivity.newIntent(mContext));
                finish();
            }

            @Override
            public void onFailed() {
                mLoginErrorTipTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(mAccountEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(mPasswordEditText.getText().toString().trim())) {
            mLoginButton.setBackgroundResource(R.drawable.shape_rectangle_radius_ff725c);
        } else {
            mLoginButton.setBackgroundResource(R.drawable.shape_rectangle_radius_e7e7e7);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
