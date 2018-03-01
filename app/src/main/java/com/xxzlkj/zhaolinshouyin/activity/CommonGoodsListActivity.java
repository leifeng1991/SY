package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.CommonGoodsListAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.SpinnerAdapter;
import com.xxzlkj.zhaolinshouyin.db.CommonGoods;
import com.xxzlkj.zhaolinshouyin.event.UpdataCommonGoodsEvent;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述:常用商品列表
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class CommonGoodsListActivity extends ZLBaseActivity {
    private EditText mInputOrderNumberEditText;
    private RelativeLayout mSearchRelativeLayout;
    private LinearLayout mCheckedLayout;
    private ImageView mCheckedImageView, mLastPageImageView;
    private Spinner mProductSpinner;
    private Button mDeleteButton, mAddButton;
    private RecyclerView mRecyclerView;
    private TextView mTopTextView, mBottomTextView, mCheckedTextView, mAddTimeTextView;
    private CommonGoodsListAdapter mAdapter;
    // 是否是第一次加载
    private boolean[] isFirstLoad = new boolean[]{true};
    // 是否全选
    private boolean mIsAllChecked;
    private int standard = -1;
//    private ItemTouchHelper itemTouchHelper;

    public static Intent newIntent(Context context) {
        return new Intent(context, CommonGoodsListActivity.class);
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_common_goods_list);
    }

    @Override
    protected void findViewById() {
        mLastPageImageView = getView(R.id.id_last_page);// 上一页
        mInputOrderNumberEditText = getView(R.id.id_input_order_number);// 订单号输入框
        mSearchRelativeLayout = getView(R.id.id_search);// 搜索
        mProductSpinner = getView(R.id.id_product_type);// 分类
        mDeleteButton = getView(R.id.id_delete);// 删除
        mAddButton = getView(R.id.id_add);// 添加
        mRecyclerView = getView(R.id.recyclerView);// 商品列表
        mCheckedLayout = getView(R.id.id_check_all_layout);// 全选布局
        mCheckedTextView = getView(R.id.id_check_all);
        mCheckedImageView = getView(R.id.id_check_image);// 全选图片
        mAddTimeTextView = getView(R.id.id_add_time);// 添加时间
        mTopTextView = getView(R.id.id_top);// 置顶
        mBottomTextView = getView(R.id.id_bottom);// 置底
        init();
    }

    private void init() {
        // 设置分类数据
        setAdapter(mProductSpinner, Arrays.asList(getResources().getStringArray(R.array.goodsType)));
        // 商品列表
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CommonGoodsListAdapter(mContext, R.layout.item_goods_list, isAllChecked -> {
            mIsAllChecked = isAllChecked;
            mCheckedImageView.setImageResource(isAllChecked ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);
        });
//        itemTouchHelper = new ItemTouchHelper(new DragItemTouchHelper(mAdapter));
//        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
//            @Override
//            public void onLongClick(RecyclerView.ViewHolder vh) {
//                    itemTouchHelper.startDrag(vh);
//            }
//        });
        // 初始化
        mAddTimeTextView.setVisibility(View.VISIBLE);
        mCheckedLayout.setVisibility(View.VISIBLE);
        mCheckedTextView.setVisibility(View.VISIBLE);
        mLastPageImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setListener() {
        // 上一页
        mLastPageImageView.setOnClickListener(v -> finish());
        // 搜索
        mSearchRelativeLayout.setOnClickListener(v -> getGoodsList());
        // 分类
        mProductSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirstLoad[0]) {
                    standard = position == 0 ? -1 : position;
                    // 非第一次加载
                    getGoodsList();
                }
                isFirstLoad[0] = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 全选
        if (mCheckedLayout != null)
            mCheckedLayout.setOnClickListener(v -> {
                List<CommonGoods> list = mAdapter.getList();
                mIsAllChecked = !mIsAllChecked;
                mCheckedImageView.setImageResource(mIsAllChecked ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);
                if (list != null && list.size() > 0) {
                    // 遍历改变每个商品状态
                    for (int i = 0; i < list.size(); i++) {
                        // 设置选中状态
                        CommonGoods commonGoods = list.get(i);
                        commonGoods.setIsChecked(mIsAllChecked);
                    }
                    // 更新
                    mAdapter.notifyDataSetChanged();


                }
            });
        // 删除
        mDeleteButton.setOnClickListener(v -> {
            if (mAdapter != null && mAdapter.getGoodList().size() > 0) {
                // 删除
                deleteCommonGoods();
            } else {
                // 没选中提示
                ToastManager.showShortToast(mContext, "请选择要删除的商品");
            }
        });
        // 添加
        mAddButton.setOnClickListener(v -> {
            // 跳转到商品列表
            startActivity(GoodsListActivity.newIntent(mContext, true));
        });
        // 置顶
        mTopTextView.setOnClickListener(v -> {
            if (mAdapter != null && mAdapter.getGoodList().size() > 0) {
                // 置顶
                goodsTopOrBottom(true);
            } else {
                // 没选中提示
                ToastManager.showShortToast(mContext, "请选择要置顶的商品");
            }
        });
        // 置底
        mBottomTextView.setOnClickListener(v -> {
            if (mAdapter != null && mAdapter.getGoodList().size() > 0) {
                // 置底
                goodsTopOrBottom(false);
            } else {
                // 没选中提示
                ToastManager.showShortToast(mContext, "请选择要置底的商品");
            }
        });

    }


    @Override
    public void processLogic() {
        // 设置网络状态，刷新的时候重新获取状态
        boolean netIsAvailable = NetStateManager.isAvailable(mContext);
        setNetStateHint(netIsAvailable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取商品列表
        getGoodsList();
    }

    /**
     * 删除商品
     */
    private void deleteCommonGoods() {
        DaoUtils.deleteCommonGoodsList(mAdapter.getGoodList(), new DaoUtils.OnDaoResultListener<String>() {
            @Override
            public void onSuccess(String bean) {
                // 删除成功
                ToastManager.showShortToast(mContext, "删除成功");
                // 更新称重界面常用商品
                EventBus.getDefault().postSticky(new UpdataCommonGoodsEvent());
                // 获取商品列表
                getGoodsList();
            }

            @Override
            public void onFailed() {
                // 删除失败
                ToastManager.showShortToast(mContext, "删除失败");
            }
        });
    }

    /**
     * 商品置顶或者置底
     *
     * @param isTop true:置顶 false:置底
     */
    private void goodsTopOrBottom(boolean isTop) {
        DaoUtils.insertOrReplaceCommonGoodsList(mAdapter.getGoodList(), isTop, new DaoUtils.OnDaoResultListener<String>() {
            @Override
            public void onSuccess(String bean) {
                // 置顶/置底成功
                ToastManager.showShortToast(mContext, isTop ? "置顶成功" : "置底成功");
                // 更新称重界面常用商品
                EventBus.getDefault().postSticky(new UpdataCommonGoodsEvent());
                // 获取商品列表
                getGoodsList();
            }

            @Override
            public void onFailed() {
                ToastManager.showShortToast(mContext, isTop ? "置顶失败" : "置底失败");
            }
        });
    }

    private void setAdapter(Spinner spinner, List<String> strings) {
        SpinnerAdapter adapter = new SpinnerAdapter(mContext, android.R.layout.simple_spinner_item, strings, spinner);
        spinner.setAdapter(adapter);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * 获取商品列表
     */
    private void getGoodsList() {
        DaoUtils.getCommonGoodsList(this, mInputOrderNumberEditText.getText().toString().trim(), standard, new DaoUtils.OnDaoResultListListener<CommonGoods>() {
            @Override
            public void onSuccess(List<CommonGoods> list) {
                // 全选归原
                mCheckedImageView.setImageResource(R.mipmap.ic_check_normal);
                mAdapter.clearAndAddList(list);
            }

            @Override
            public void onFailed() {
                // 全选归原
                mCheckedImageView.setImageResource(R.mipmap.ic_check_normal);
                mAdapter.clearAndAddList(new ArrayList<>());
            }
        });
    }

}
