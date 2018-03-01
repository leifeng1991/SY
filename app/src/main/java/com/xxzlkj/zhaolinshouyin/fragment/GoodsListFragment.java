package com.xxzlkj.zhaolinshouyin.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xxzlkj.zhaolinshare.base.MyRecyclerView;
import com.xxzlkj.zhaolinshare.base.base.BaseActivity;
import com.xxzlkj.zhaolinshare.base.base.BaseFragment;
import com.xxzlkj.zhaolinshare.base.net.OnMyActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.GoodsListAdapter;
import com.xxzlkj.zhaolinshouyin.model.GoodsListBean;

import java.util.HashMap;

/**
 * 描述:商品列表
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class GoodsListFragment extends BaseFragment {

    private MyRecyclerView goodsListRecyclerView;
    private GoodsListAdapter goodsListAdapter;
    private int page = 1;

    @Override
    public View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_goods_list, container, false);
    }

    @Override
    protected void findViewById() {
        goodsListRecyclerView = getView(R.id.recyclerView);
        // 初始化
        goodsListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        goodsListAdapter = new GoodsListAdapter(mContext, false, R.layout.item_goods_list, null,null);
        goodsListRecyclerView.setAdapter(goodsListAdapter);

    }

    @Override
    public void setListener() {
        // 设置监听
        goodsListRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getGoodsListData();
            }

            @Override
            public void onLoadMore() {
                page = goodsListAdapter.getItemCount() / goodsListRecyclerView.loadSize + 1;
                getGoodsListData();
            }
        });

    }

    @Override
    public void processLogic() {

    }

    private void getGoodsListData() {
        HashMap<String, String> map = new HashMap<>();
        RequestManager.createRequest("", map, new OnMyActivityRequestListener<GoodsListBean>((BaseActivity) getActivity()) {
            @Override
            public void onSuccess(GoodsListBean bean) {
                goodsListRecyclerView.handlerSuccessHasRefreshAndLoad(goodsListAdapter, page == 1, bean.getData());
            }

            @Override
            public void onFailed(boolean isError, String code, String message) {
                super.onFailed(isError, code, message);
                // 网络失败，获取本地的
                goodsListRecyclerView.handlerError(goodsListAdapter, page == 1);
            }
        });
    }
}
