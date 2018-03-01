package com.xxzlkj.zhaolinshare.base;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;

import java.util.List;


/**
 * 描述: 封装的 XRecyclerView
 *
 * @author zhangrq
 *         2016/9/2 14:08
 */
public class MyRecyclerView extends RelativeLayout {
    /**
     * 每次加载的数量
     */
    public int loadSize = 20;
    private XRecyclerView xRecyclerView;
    private View mNoDataView;
    private OnClickListener onNoNetViewClickListener;

    public MyRecyclerView(Context context) {
        this(context, null);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View rootView = View.inflate(context, R.layout.view_mrecyclerview, null);
        xRecyclerView = (XRecyclerView) rootView.findViewById(R.id.recyclerView);
        TextView tv_no_data_btn = (TextView) rootView.findViewById(R.id.tv_no_data_btn);
        mNoDataView = rootView.findViewById(R.id.rl_no_data);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 有监听的话，先走监听，其次没监听的话，走刷新
                if (onNoNetViewClickListener != null) {
                    onNoNetViewClickListener.onClick(view);
                } else {
                    if (getLoadingListener() != null) {
                        getLoadingListener().onRefresh();
                    }
                }
            }
        };
        tv_no_data_btn.setOnClickListener(onClickListener);// 设置暂无网络可点击
        addView(rootView);
    }

    public XRecyclerView getxRecyclerView() {
        return xRecyclerView;
    }

    /**
     * 增加XRecyclerView的paddingTop值
     */
    public void setXRecyclerViewPaddingTop(int paddingTop) {
        xRecyclerView.setPadding(xRecyclerView.getPaddingLeft(), xRecyclerView.getPaddingTop() + paddingTop,
                xRecyclerView.getPaddingRight(), xRecyclerView.getPaddingBottom());
    }

    /**
     * 增加XRecyclerView的paddingBottom值
     */
    public void setXRecyclerViewPaddingBottom(int paddingBottom) {
        xRecyclerView.setPadding(xRecyclerView.getPaddingLeft(), xRecyclerView.getPaddingTop(),
                xRecyclerView.getPaddingRight(), xRecyclerView.getPaddingBottom() + paddingBottom);
    }

    /**
     * 设置XRecyclerView的ClipToPadding为true或false，默认为false
     */
    public void setXRecyclerViewClipToPadding(boolean clipToPadding) {
        xRecyclerView.setClipToPadding(clipToPadding);
    }


    /**
     * RecyclerView处理失败，即设置刷新完成，加载完成
     */
    public void handlerError(BaseAdapter adapter) {
        xRecyclerView.refreshComplete();
        xRecyclerView.loadMoreComplete();
        // 检查是否显示哪个
        checkDataShow(adapter);
    }

    public void handlerError(BaseAdapter adapter, boolean isRefresh) {
        if (isRefresh) {
            if (adapter == null) return;
            adapter.clear();
            xRecyclerView.setNoMore(false);// 设置取消没有更多数据，做归原处理
            xRecyclerView.refreshComplete();
        } else {
            xRecyclerView.loadMoreComplete();
        }
        // 检查是否显示哪个
        checkDataShow(adapter);
    }

    public <T> void handlerSuccessHasRefreshAndLoad(BaseAdapter<T> adapter, boolean isRefresh, List<T> addDatas) {

        handlerSuccessHasRefreshAndLoad(xRecyclerView, adapter, loadSize, isRefresh, addDatas);
        // 检查是否显示哪个
        checkDataShow(adapter);

    }

    public static <T> void handlerSuccessHasRefreshAndLoad(XRecyclerView xRecyclerView, BaseAdapter<T> adapter, int loadSize, boolean isRefresh, List<T> addDatas) {
        if (adapter == null) return;
        if (isRefresh) {
            adapter.clear();
            adapter.addList(addDatas);
            xRecyclerView.setNoMore(false);// 设置取消没有更多数据，做归原处理
            xRecyclerView.refreshComplete();
        } else {
            adapter.addList(addDatas);
            xRecyclerView.loadMoreComplete();
        }

        if (addDatas == null || addDatas.size() < loadSize) {
            xRecyclerView.setNoMore(true);
        } else {
            xRecyclerView.setNoMore(false);
        }
    }

    public static void handlerComplete(XRecyclerView xRecyclerView) {
        xRecyclerView.refreshComplete();
        xRecyclerView.loadMoreComplete();
    }

    public <T> void handlerSuccessNoRefreshAndLoad(BaseAdapter<T> adapter, List<T> addDatas) {
        if (adapter == null) return;
        adapter.addList(addDatas);
        // 检查是否显示哪个
        checkDataShow(adapter);
    }

    public <T> void handlerSuccessOnlyHasRefresh(BaseAdapter<T> adapter, List<T> addDatas) {
        if (adapter == null) return;
        adapter.clear();
        adapter.addList(addDatas);
        xRecyclerView.refreshComplete();
        // 检查是否显示哪个
        checkDataShow(adapter);
    }

    // 检查是否显示哪个
    public void checkDataShow(BaseAdapter adapter) {
        if (adapter.getItemCount() != 0) {
            // 列表有数据
            showView(true, false);
        } else {
            // 列表无数据
            showView(false, true);
        }
    }

    /**
     * 显示对应的view
     */
    public void showView(boolean showRecyclerView, boolean showNoDataView) {
        xRecyclerView.setVisibility(showRecyclerView ? VISIBLE : GONE);
        mNoDataView.setVisibility(showNoDataView ? VISIBLE : GONE);
    }

    /**
     * 获取滚动的View
     */
    public View getScrollTarget() {
        return xRecyclerView != null && mNoDataView != null ?
                xRecyclerView.isShown() ? xRecyclerView : mNoDataView : null;
    }

    /**
     * 设置每次加载的数量
     *
     * @param loadSize 加载的数量
     */
    public void setLoadSize(int loadSize) {
        this.loadSize = loadSize;
    }

    public int getLoadSize() {
        return loadSize;
    }

    /**
     * 设置刷新和加载是否可用
     *
     * @param pullRefreshEnabled true为设置刷新可用
     * @param loadingMoreEnabled true为设置加载可用
     */
    public void setPullRefreshAndLoadingMoreEnabled(boolean pullRefreshEnabled, boolean loadingMoreEnabled) {
        xRecyclerView.setPullRefreshEnabled(pullRefreshEnabled);
        xRecyclerView.setLoadingMoreEnabled(loadingMoreEnabled);
    }

    /**
     * 设置刷新和加载的监听
     *
     * @param listener 监听
     */
    public void setLoadingListener(XRecyclerView.LoadingListener listener) {
        xRecyclerView.setLoadingListener(listener);
    }

    public XRecyclerView.LoadingListener getLoadingListener() {
        return xRecyclerView.getLoadingListener();
    }

    /**
     * 设置xRecyclerView的adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        xRecyclerView.setAdapter(adapter);
    }

    /**
     * 设置RecyclerView的排列方式
     */
    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        xRecyclerView.setLayoutManager(layout);
    }

    /**
     * 设置没数据的view
     */
    public void setEmptyView(View noDataView) {
        // 先把原来的设置隐藏，再让其控制noDataView
        this.mNoDataView.setVisibility(GONE);
        this.mNoDataView = noDataView;
    }

    /**
     * 设置xRecyclerView刷新
     */
    public void refresh() {
        xRecyclerView.refresh();
//        xRecyclerView.setRefreshing(true);
    }

    /**
     * 设置xRecyclerView加载是否可用
     */
    public void setLoadingMoreEnabled(boolean enabled) {
        xRecyclerView.setLoadingMoreEnabled(enabled);
    }

    /**
     * 设置xRecyclerView加载是否可用
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        xRecyclerView.addItemDecoration(decor);
    }

    /**
     * xRecyclerView增加头
     */
    public void addHeaderView(View view) {
        xRecyclerView.addHeaderView(view);
    }

    /**
     * 刷新完成
     */
    public void refreshComplete() {
        xRecyclerView.refreshComplete();
    }

    /**
     * 加载完成
     */
    public void loadMoreComplete() {
        xRecyclerView.loadMoreComplete();
    }

    /**
     * 设置无网络图片的点击监听
     */
    public void setOnNoNetViewClickListener(OnClickListener onNoNetViewClickListener) {
        this.onNoNetViewClickListener = onNoNetViewClickListener;
    }

    // 解决魅族手机上的提示的view id重名冲突
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            super.onRestoreInstanceState(state);
        } catch (Exception e) {

        }
    }
}
