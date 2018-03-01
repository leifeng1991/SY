package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.xxzlkj.zhaolinshare.base.model.BaseBean;
import com.xxzlkj.zhaolinshare.base.net.OnMyActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;

import java.util.HashMap;


/**
 * 修改密码页面
 *
 * @author zhangrq
 */
public class ChangePasswordActivity extends ZLBaseActivity implements TextWatcher {
    private EditText mOldPasswordEditText, mNewPasswordEditText, mSureNewPasswordEditText;
    private Button mSureButton, mBackButton;

    public static Intent newIntent(Context context) {
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_change_password);
    }

    @Override
    protected void findViewById() {
        mOldPasswordEditText = getView(R.id.id_old_password);// 旧密码
        mNewPasswordEditText = getView(R.id.id_new_password);// 新密码
        mSureNewPasswordEditText = getView(R.id.id_sure_new_password);// 确认新密码
        mSureButton = getView(R.id.id_sure);// 确定
        mBackButton = getView(R.id.id_back);// 确定
    }

    @Override
    protected void setListener() {
        mSureButton.setOnClickListener(v -> {
            String mOldPasswordText = mOldPasswordEditText.getText().toString().trim();
            if (TextUtils.isEmpty(mOldPasswordText)) {
                ToastManager.showShortToast(mContext, "请输入原密码");
                return;
            }
            String mPasswordText = mNewPasswordEditText.getText().toString().trim();
            if (TextUtils.isEmpty(mPasswordText)) {
                ToastManager.showShortToast(mContext, "请输入新密码");
                return;
            }
            String mNewPasswordText = mSureNewPasswordEditText.getText().toString().trim();
            if (TextUtils.isEmpty(mNewPasswordText)) {
                ToastManager.showShortToast(mContext, "请输入确认新密码");
                return;
            }
            if (!mPasswordText.equals(mNewPasswordText)) {
                ToastManager.showShortToast(mContext, "两次输入的密码不一致");
                return;
            }

            changePassword(mOldPasswordText, mNewPasswordText);
        });
        // 返回
        mBackButton.setOnClickListener(v -> finish());

        mOldPasswordEditText.addTextChangedListener(this);
        mNewPasswordEditText.addTextChangedListener(this);
        mSureNewPasswordEditText.addTextChangedListener(this);
    }


    @Override
    protected void processLogic() {

    }

    private void changePassword(String mOldPasswordText, String mNewPasswordText) {
        User user = ZhaoLinApplication.getInstance().getLoginUserDoLogin(this);
        if (user == null) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(C.P.ID, String.valueOf(user.getId()));
        map.put(C.P.PASS, mOldPasswordText);
        map.put(C.P.PASSWORD, mNewPasswordText);
        RequestManager.createRequest(ZLURLConstants.EDIT_USER_PASSWORD_URL, map, new OnMyActivityRequestListener<BaseBean>(this) {
            @Override
            public void onSuccess(BaseBean bean) {
                // 修改成功，设置本地密码
                DaoUtils.updateUserPassword(ChangePasswordActivity.this, user.getUid(), mNewPasswordText, new DaoUtils.OnDaoResultListener<User>() {

                    @Override
                    public void onSuccess(User bean) {
                        ToastManager.showShortToast(mContext, "修改密码成功");
                        finish();
                    }

                    @Override
                    public void onFailed() {
                        ToastManager.showShortToast(mContext, "保存密码失败");
                    }
                });
            }
        });

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(mOldPasswordEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(mNewPasswordEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(mSureNewPasswordEditText.getText().toString().trim())) {
            mSureButton.setBackgroundResource(R.drawable.shape_rectangle_radius_ff725c);
        } else {
            mSureButton.setBackgroundResource(R.drawable.shape_rectangle_radius_e7e7e7);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
