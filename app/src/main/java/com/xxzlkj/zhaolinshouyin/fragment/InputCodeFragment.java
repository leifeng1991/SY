package com.xxzlkj.zhaolinshouyin.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.xxzlkj.zhaolinshare.base.base.BaseFragment;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.activity.SearchGoodsActivity;
import com.xxzlkj.zhaolinshouyin.activity.ShouYinActivity;
import com.xxzlkj.zhaolinshouyin.utils.NumberKeyboardUtil;

/**
 * 描述:输入商品货号
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class InputCodeFragment extends BaseFragment {
    public static final String IS_WEIGHTING = "is_weighting";
    private OnInputCodeListener onInputCodeListener;
    private EditText mEditText;
    private RelativeLayout mSearchRelativeLayout;
    private ShouYinActivity shouYinActivity;

    public static InputCodeFragment newInstance(OnInputCodeListener onInputCodeListener,ShouYinActivity shouYinActivity) {
        InputCodeFragment inputCodeFragment = new InputCodeFragment();
        inputCodeFragment.onInputCodeListener = onInputCodeListener;
        inputCodeFragment.shouYinActivity = shouYinActivity;
        return inputCodeFragment;
    }

    @Override
    public View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_input_code, container, false);
    }

    @Override
    protected void findViewById() {
        mEditText = getView(R.id.id_edit_text);
        mSearchRelativeLayout = getView(R.id.id_search);
        mSearchRelativeLayout.setVisibility(View.VISIBLE);
        NumberKeyboardUtil.setClick(rootView, mEditText, 0, (inputCode, clickButton) -> {
            mEditText.setText("");
            if (onInputCodeListener != null)
                onInputCodeListener.onInputCodeFinish(inputCode);
        }, null);

    }

    @Override
    public void setListener() {
        // 搜索
        mSearchRelativeLayout.setOnClickListener(v -> {
            // 跳转到搜索商品界面
            startActivity(SearchGoodsActivity.newIntent(mContext,shouYinActivity.isHasWeightingGoods()));
        });
    }

    @Override
    public void processLogic() {

    }

    public interface OnInputCodeListener {
        void onInputCodeFinish(String inputCode);
    }

}
