package com.xxzlkj.zhaolinshare.base.base;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.R;


/**
 * 界面公共部分处理
 *
 * @author zhangrq
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected Context mContext;
    private View loadDataView;
    protected ImageView ivTitleLeft, ivTitleRight;
    protected TextView tvTitleName, tvTitleRight;
    public static final String TAG = BaseActivity.class.getSimpleName();
    private AnimationDrawable animationDrawable;

    /**
     * 生命周期回调方法---创建
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseContext();
        initView();
    }

    /**
     * 设置app字体大小恢复成系统默认的
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    /**
     * 初始化界面
     */
    protected void initView() {
        loadViewLayout();
        findViewById();
        setListener();
        processLogic();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
        tvTitleRight.setOnClickListener(new View.OnClickListener() {
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
        ivTitleRight.setOnClickListener(new View.OnClickListener() {
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

    /**
     * 隐藏加载动画
     */
    public void hideLoadDataAnim() {
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        if (loadDataView != null) {
            loadDataView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示加载动画
     */
    public void showLoadDataAnim() {
        if (isFinishing()) // 已完成
            return;
        if (loadDataView == null) {
            synchronized (BaseActivity.class) {
                if (loadDataView == null) {
                    loadDataView = View.inflate(mContext, R.layout.dialog_load_data, null);
                    ((ViewGroup) findViewById(Window.ID_ANDROID_CONTENT)).addView(loadDataView);
                    ImageView imageView = (ImageView) loadDataView.findViewById(R.id.loadingImageView);
                    animationDrawable = (AnimationDrawable) imageView.getBackground();
                    animationDrawable.start();
                }
            }
        } else if (!loadDataView.isShown()) {
            loadDataView.setVisibility(View.VISIBLE);
            if (animationDrawable != null && !animationDrawable.isRunning()) {
                animationDrawable.start();
            }
        }
    }

    /**
     * 根据资源id 获取View ，不用强制转换
     *
     * @param id 资源id
     * @return 返回id所指向的View
     */
    @SuppressWarnings("unchecked")
    public <A extends View> A getView(int id) {
        return (A) findViewById(id);
    }

    /**
     * 通过view获取view
     *
     * @param fromView 在哪个view上查找
     * @param viewId   控件id
     * @return 返回控件
     */
    @SuppressWarnings("unchecked")
    public <A extends View> A getView(View fromView, int viewId) {
        return (A) fromView.findViewById(viewId);
    }

    /**
     * 给此id控件设置点击监听
     */
    public void setOnClickListener(int... viewIds) {
        if (viewIds == null || viewIds.length == 0) return;
        for (int viewId : viewIds) {
            getView(viewId).setOnClickListener(this);
        }
    }

    /**
     * 加载布局
     */
    protected abstract void loadViewLayout();

    /**
     * 寻找view组件
     */
    protected abstract void findViewById();

    /**
     * 为组件注册监听事件
     */
    protected abstract void setListener();

    /**
     * 初始化View组件数据
     */
    protected abstract void processLogic();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataView != null) {
            loadDataView = null;
        }
    }

    @Override
    public void onClick(View view) {

    }
}
