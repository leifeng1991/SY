package com.xxzlkj.zhaolinshare.base.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.R;


/**
 * Fragment的基类
 *
 * @author zhangrq
 */
public abstract class BaseFragment extends Fragment implements OnClickListener {
    protected Activity mActivity;
    protected View rootView;
    protected Context mContext;
    protected ImageView ivTitleLeft, ivTitleRight;
    protected TextView tvTitleName, tvTitleRight;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mContext = context;
    }

    /**
     * 返回一个需要展示的View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = loadViewLayout(inflater, container);
        findViewById();
        setListener();
        processLogic();
        return rootView;
    }

    /**
     * 设置标题头左边返回键
     */
    protected void setTitleLeftBack() {
        setTitleLeft(R.mipmap.back_arrow);
    }

    /**
     * 设置标题头左边
     */
    protected void setTitleLeft(int imageRedId) {
        ivTitleLeft = ivTitleLeft == null ? (ImageView) getView(R.id.iv_title_left) : ivTitleLeft;
        ivTitleLeft.setVisibility(View.VISIBLE);
        ivTitleLeft.setImageResource(imageRedId);
        ivTitleLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
    }

    /**
     * 设置标题头名字
     */
    protected void setTitleName(String titleName) {
        tvTitleName = tvTitleName == null ? (TextView) getView(R.id.tv_title_name) : tvTitleName;
        tvTitleName.setVisibility(View.VISIBLE);
        tvTitleName.setText(titleName);
    }

    /**
     * 设置标题头右边文字
     */
    protected void setTitleRightText(String text) {
        tvTitleRight = tvTitleRight == null ? (TextView) getView(R.id.tv_title_right) : tvTitleRight;
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onTitleRightClick(view);
            }
        });
        tvTitleRight.setText(text);
    }

    /**
     * 设置标题头右边图片
     */
    protected void setTitleRightImage(int imageRedId) {
        ivTitleRight = ivTitleRight == null ? (ImageView) getView(R.id.iv_title_right) : ivTitleRight;
        ivTitleRight.setVisibility(View.VISIBLE);
        ivTitleRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onTitleRightClick(view);
            }
        });
        ivTitleRight.setImageResource(imageRedId);
    }

    /**
     * 标题头右边点击了
     */
    public void onTitleRightClick(View view) {

    }


    @Override
    public void onClick(View v) {
    }

    /**
     * 根据资源id 获取View
     *
     * @param id 资源id
     * @return 返回id所指向的View
     */
    @SuppressWarnings("unchecked")
    public <A extends View> A getView(int id) {
        return (A) rootView.findViewById(id);
    }

    /**
     * 加载View进行展示
     */
    public abstract View loadViewLayout(LayoutInflater inflater, ViewGroup container);

    /**
     * 初始化控件
     */
    protected abstract void findViewById();

    /**
     * 设置监听
     */
    public abstract void setListener();

    /**
     * 逻辑处理、数据的初始化
     */
    public abstract void processLogic();

}