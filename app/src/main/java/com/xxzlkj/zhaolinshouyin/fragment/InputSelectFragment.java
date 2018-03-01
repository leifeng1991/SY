package com.xxzlkj.zhaolinshouyin.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xxzlkj.zhaolinshare.base.base.BaseActivity;
import com.xxzlkj.zhaolinshare.base.base.BaseFragment;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.activity.ShouYinActivity;
import com.xxzlkj.zhaolinshouyin.adapter.CenterGoodsAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.OneLevelClassificationAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.ThreeLevelClassificationAdapter;
import com.xxzlkj.zhaolinshouyin.db.CommonGoods;
import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.db.GoodsClass;
import com.xxzlkj.zhaolinshouyin.event.UpdataCommonGoodsEvent;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.RecyclerViewHelperListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 描述:选择商品
 *
 * @author zhangrq
 *         2017/12/2 17:54
 */
public class InputSelectFragment extends BaseFragment {
    public static final String COLUMN_NUMBER = "column_number";
    public static final String IS_WEIGHTING = "is_weighting";
    private RecyclerView mTopRecyclerView, mCenterRecyclerView, mRightRecyclerView;
    private ImageView mLeftImageView, mRightImageView, mTopImageView, mBottomImageView;
    private OneLevelClassificationAdapter mOneLevelClassificationAdapter;
    private ThreeLevelClassificationAdapter mThreeLevelClassificationAdapter;
    private CenterGoodsAdapter mCenterGoodsAdapter;
    private OnGoodsClickListener onGoodsClickListener;
    private LinearLayoutManager mTopLinearLayoutManager;
    private RecyclerViewHelperListener mTopHelperListener;
    private LinearLayoutManager mRightLinearLayoutManager;
    private RecyclerViewHelperListener mRightHelperListener;
    private int columnNumber = 3;
    private boolean isWeighting;
    private Map<Long, List<Long>> oneThreeMap;
    long[] mHits = new long[2];


    /**
     * @param columnNumber         列数
     * @param isWeighting          true:称重界面 false：收银界面
     * @param onGoodsClickListener 商品点击回调
     */
    public static InputSelectFragment newInstance(int columnNumber, boolean isWeighting, OnGoodsClickListener onGoodsClickListener) {
        InputSelectFragment inputSelectFragment = new InputSelectFragment();
        inputSelectFragment.onGoodsClickListener = onGoodsClickListener;
        Bundle bundle = new Bundle();
        bundle.putInt(COLUMN_NUMBER, columnNumber);
        bundle.putBoolean(IS_WEIGHTING, isWeighting);
        inputSelectFragment.setArguments(bundle);
        return inputSelectFragment;
    }

    @Override
    public View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_input_select, container, false);
    }

    @Override
    protected void findViewById() {
        mTopRecyclerView = getView(R.id.id_top_recycler_view);// 一级分类列表
        mLeftImageView = getView(R.id.id_top_arrow_left);// 一级列表左边箭头
        mRightImageView = getView(R.id.id_top_arrow_right);// 一级列表右侧箭头
        mRightRecyclerView = getView(R.id.id_right_recycler_view);// 三级列表
        mTopImageView = getView(R.id.id_right_arrow_top);// 三级列表顶部箭头
        mBottomImageView = getView(R.id.id_right_arrow_bottom);// 三级列表底部箭头
        mCenterRecyclerView = getView(R.id.id_center_recycler_view);// 中间商品列表
        init();
    }

    private void init() {
        // 获取传值
        Bundle bundle = getArguments();
        if (bundle != null) {
            columnNumber = bundle.getInt(COLUMN_NUMBER);
            isWeighting = bundle.getBoolean(IS_WEIGHTING);
        }
        // 一级分类列表
        mTopLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mTopRecyclerView.setLayoutManager(mTopLinearLayoutManager);
        mTopHelperListener = new RecyclerViewHelperListener(mTopRecyclerView, mTopLinearLayoutManager);
        mTopRecyclerView.addOnScrollListener(mTopHelperListener);
        mOneLevelClassificationAdapter = new OneLevelClassificationAdapter(mContext, R.layout.adapter_top_list_item);
        mTopRecyclerView.setAdapter(mOneLevelClassificationAdapter);
        // 三级列表
        mRightLinearLayoutManager = new LinearLayoutManager(mContext);
        mRightRecyclerView.setLayoutManager(mRightLinearLayoutManager);
        mRightHelperListener = new RecyclerViewHelperListener(mRightRecyclerView, mRightLinearLayoutManager);
        mRightRecyclerView.addOnScrollListener(mRightHelperListener);
        mThreeLevelClassificationAdapter = new ThreeLevelClassificationAdapter(mContext, R.layout.adapter_right_list_item);
        mRightRecyclerView.setAdapter(mThreeLevelClassificationAdapter);
        // 中间商品列表
        mCenterRecyclerView.setLayoutManager(new GridLayoutManager(mContext, columnNumber));
        mCenterGoodsAdapter = new CenterGoodsAdapter(mContext, R.layout.adapter_center_list_item);
        mCenterRecyclerView.setAdapter(mCenterGoodsAdapter);
        // 称重界面时显示分割线、重置宽度
        if (isWeighting) {
            getView(R.id.id_line).setVisibility(View.VISIBLE);
            getView(R.id.vg_number_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void setListener() {
        EventBus.getDefault().register(this);
        // 一级分类列表item点击事件
        mOneLevelClassificationAdapter.setOnItemClickListener((position, item) -> {
            // 设置选中
            mOneLevelClassificationAdapter.setSelectedPosition(position);
            mOneLevelClassificationAdapter.notifyDataSetChanged();
            // 获取三级列表
            if (position != 0 && isWeighting) {
                // 称重界面
                if (oneThreeMap != null)
                    getAllThreeTitles(oneThreeMap.get(item.getId()), item.getId());
            } else {
                // 收银界面
                getThreeLevelClassList();
            }


        });
        // 三级分类列表item点击事件
        mThreeLevelClassificationAdapter.setOnItemClickListener((position, item) -> {
            // 设置选中
            mThreeLevelClassificationAdapter.setSelectedPosition(position);
            mThreeLevelClassificationAdapter.notifyDataSetChanged();
            if (mOneLevelClassificationAdapter.getSelectedPosition() == 0) {
                // 一级列表是常用商品
                // 获取常用商品列表
                getCommonGoodsList(position == 0 ? 2 : 1);
            } else {
                // 一级列表不是常用商品
                // 获取商品列表
                getCenterGoodsList();
            }

        });
        //商品列表item点击事件
        mCenterGoodsAdapter.setOnItemClickListener((position, item) -> {
            // 设置回调值
            if (onGoodsClickListener != null)
                onGoodsClickListener.onGoodsClick(item);
        });
        // 一级列表左边箭头
        mLeftImageView.setOnClickListener(v -> {
            // 非称重页面，双击，显示长内容
            GridLayoutManager layoutManager = (GridLayoutManager) mCenterRecyclerView.getLayoutManager();
            if (isHandlerDoubleClick(layoutManager.getSpanCount() == 3)) return;// 处理了双击事件，就不处理单击事件了
            // 处理单击滚动
            int firstCompletelyVisibleItemPosition = mTopLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
            int firstVisibleItemPosition = mTopLinearLayoutManager.findFirstVisibleItemPosition();
            int lastCompletelyVisibleItemPosition = mTopLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            // 完全显示的个数
            int number = lastCompletelyVisibleItemPosition - firstVisibleItemPosition;
            // 水平滚动
//            int left = mTopRecyclerView.getChildAt(number).getLeft();
//            mTopRecyclerView.scrollBy(-left, 0);
            // 第一个完全显示item等于第一个可见item
            if (firstCompletelyVisibleItemPosition == firstVisibleItemPosition) {
                int i = firstVisibleItemPosition - number - 1;
                if (i > 0) {
                    mTopLinearLayoutManager.scrollToPositionWithOffset(i, 0);
                } else {
                    // 直接跳到第一个
                    mTopRecyclerView.scrollToPosition(0);
                }

            } else {
                int i = firstVisibleItemPosition - number;
                if (i > 0) {
                    mTopLinearLayoutManager.scrollToPositionWithOffset(i, 0);
                } else {
                    // 直接跳到第一个
                    mTopRecyclerView.scrollToPosition(0);
                }

            }
        });
        // 一级列表右侧箭头
        mRightImageView.setOnClickListener(v -> {
            // 非称重页面，双击，显示短内容
//            if (isHandlerDoubleClick(false)) return;// 处理了双击事件，就不处理单击事件了
            // 处理单击滚动
            int lastVisibleItemPosition = mTopLinearLayoutManager.findLastVisibleItemPosition();
            int lastCompletelyVisibleItemPosition = mTopLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            // 最后一个完全显示item等于最后一个可见item
            if (lastCompletelyVisibleItemPosition == lastVisibleItemPosition) {
                mTopHelperListener.scrollToPosition(lastVisibleItemPosition + 1);
            } else {
                mTopHelperListener.scrollToPosition(lastVisibleItemPosition);
            }
        });
        // 三级列表顶部箭头
        mTopImageView.setOnClickListener(v -> {
            int firstCompletelyVisibleItemPosition = mRightLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
            int firstVisibleItemPosition = mRightLinearLayoutManager.findFirstVisibleItemPosition();
            int lastCompletelyVisibleItemPosition = mRightLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            // 完全显示的个数
            int number = lastCompletelyVisibleItemPosition - firstVisibleItemPosition;
            // 第一个完全显示item等于第一个可见item
            if (firstCompletelyVisibleItemPosition == firstVisibleItemPosition) {
                int i = firstVisibleItemPosition - number - 1;
                if (i > 0) {
                    mRightLinearLayoutManager.scrollToPositionWithOffset(i, 0);
                } else {
                    // 直接跳到第一个
                    mRightRecyclerView.scrollToPosition(0);
                }

            } else {
                int i = firstVisibleItemPosition - number;
                if (i > 0) {
                    mRightLinearLayoutManager.scrollToPositionWithOffset(i, 0);
                } else {
                    // 直接跳到第一个
                    mRightRecyclerView.scrollToPosition(0);
                }

            }
        });
        // 三级列表底部箭头
        mBottomImageView.setOnClickListener(v -> {
            int lastVisibleItemPosition = mRightLinearLayoutManager.findLastVisibleItemPosition();
            int lastCompletelyVisibleItemPosition = mRightLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            // 最后一个完全显示item等于最后一个可见item
            if (lastCompletelyVisibleItemPosition == lastVisibleItemPosition) {
                mRightHelperListener.scrollToPosition(lastVisibleItemPosition + 1);
            } else {
                mRightHelperListener.scrollToPosition(lastVisibleItemPosition);
            }
        });
    }

    /**
     * 是否处理了双击事件
     *
     * @param isShowBigLayout 是否显示大布局，是：显示大布局，否：显示小布局
     * @return true 代表处理了双击事件，false，代表没有处理
     */
    private boolean isHandlerDoubleClick(boolean isShowBigLayout) {
        int width = isShowBigLayout ? 916 : 550;
        int spanCount = isShowBigLayout ? 6 : 3;
        int lineVisible = isShowBigLayout ? View.VISIBLE : View.GONE;
        if (!isWeighting) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {// 1000代表多击事件的限定时间
                // 处理多击事件的代码
                View vg_number_layout = getView(R.id.vg_number_layout);
                ViewGroup.LayoutParams layoutParams = vg_number_layout.getLayoutParams();
                if (layoutParams != null && layoutParams.width != width) {
                    // 切换布局的显示
                    getView(R.id.id_line).setVisibility(lineVisible);
                    layoutParams.width = width;
                    vg_number_layout.setLayoutParams(layoutParams);
                    mCenterRecyclerView.setLayoutManager(new GridLayoutManager(mContext, spanCount));
                    if (mActivity instanceof ShouYinActivity) {
                        // 收银页面特殊处理，调用切换布局
                        ((ShouYinActivity) mActivity).switchSelectGoodsLayoutShow(isShowBigLayout);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void processLogic() {
        // 获取一级分类
        if (isWeighting) {
            // 称重
            getAllGoodsListByStandard();
        } else {
            // 收银
            getOneLevelClassList();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取一级分类列表(标品和非标品)
     */
    private void getOneLevelClassList() {
        DaoUtils.getGoodsClassOneTitleList(mActivity, new DaoUtils.OnDaoResultListListener<GoodsClass>() {
            @Override
            public void onSuccess(List<GoodsClass> list) {
                mOneLevelClassificationAdapter.clearAndAddList(list);
                mOneLevelClassificationAdapter.setSelectedPosition(0);
                mOneLevelClassificationAdapter.notifyDataSetChanged();
                // 获取三级列表
                getThreeLevelClassList();
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 获取三级分类列表(标品和非标品)
     */
    private void getThreeLevelClassList() {
        if (mOneLevelClassificationAdapter.getSelectedPosition() < mOneLevelClassificationAdapter.getItemCount()) {
            if (mOneLevelClassificationAdapter.getSelectedPosition() == 0) {
                // 自己构建两个三级分类标题
                List<GoodsClass> list = new ArrayList<>();
                GoodsClass goodsClass1 = new GoodsClass();
                goodsClass1.setTitle("非标品");
                list.add(goodsClass1);
                if (!isWeighting) {
                    // 只有收银界面有标品 称重界面无标品
                    GoodsClass goodsClass2 = new GoodsClass();
                    goodsClass2.setTitle("标品");
                    list.add(goodsClass2);
                }

                // 设置数据
                mThreeLevelClassificationAdapter.clearAndAddList(list);
                // 设置选中
                mThreeLevelClassificationAdapter.setSelectedPosition(0);
                mThreeLevelClassificationAdapter.notifyDataSetChanged();
                // 获取常用商品列表
                getCommonGoodsList(2);
            } else {
                // 数据正常，获取三级分类列表
                GoodsClass selectedGoodsClass = mOneLevelClassificationAdapter.getList().get(mOneLevelClassificationAdapter.getSelectedPosition());
                DaoUtils.getGoodsClassThreeTitleListByOneGroupId(mActivity, selectedGoodsClass.getId(), new DaoUtils.OnDaoResultListListener<GoodsClass>() {
                    @Override
                    public void onSuccess(List<GoodsClass> list) {
                        // 设置数据
                        mThreeLevelClassificationAdapter.clearAndAddList(list);
                        // 设置选中
                        mThreeLevelClassificationAdapter.setSelectedPosition(0);
                        mThreeLevelClassificationAdapter.notifyDataSetChanged();
                        // 获取商品列表
                        getCenterGoodsList();
                    }

                    @Override
                    public void onFailed() {
                        // 没查到数据，清空
                        mThreeLevelClassificationAdapter.clear();
                        mCenterGoodsAdapter.clear();
                    }
                });
            }

        }
    }

    /**
     * 获取商品列表
     */
    private void getCenterGoodsList() {
        if (mThreeLevelClassificationAdapter.getSelectedPosition() < mThreeLevelClassificationAdapter.getItemCount()) {
            // 数据正常，获取三级分类列表
            GoodsClass selectedGoodsClass = mThreeLevelClassificationAdapter.getList().get(mThreeLevelClassificationAdapter.getSelectedPosition());
            // 获取所有商品
            DaoUtils.getGoodsListByGoodsClass((BaseActivity) mActivity, selectedGoodsClass.getId(), isWeighting, new DaoUtils.OnDaoResultListListener<Goods>() {
                @Override
                public void onSuccess(List<Goods> list) {
                    // 设置数据
                    mCenterGoodsAdapter.clearAndAddList(list);
                }

                @Override
                public void onFailed() {
                    // 没查到数据，清空
                    mCenterGoodsAdapter.clear();
                }
            });
        }
    }

    /**
     * 获取常用商品列表
     *
     * @param standard 1:标品 2：非标品
     */
    private void getCommonGoodsList(int standard) {
        DaoUtils.getCommonGoodsList(mActivity, "", standard, new DaoUtils.OnDaoResultListListener<CommonGoods>() {
            @Override
            public void onSuccess(List<CommonGoods> list) {
                List<Goods> goodsList = new ArrayList<>();
                // 遍历获取商品集合
                for (int i = 0; i < list.size(); i++) {
                    goodsList.add(list.get(i).getGoods());
                }
                // 设置数据
                mCenterGoodsAdapter.clearAndAddList(goodsList);
            }

            @Override
            public void onFailed() {
                // 没查到数据，清空
                mCenterGoodsAdapter.clear();
            }
        });
    }

    /**
     * 获取所有非标品
     */
    private void getAllGoodsListByStandard() {
        DaoUtils.getGoodsListBySelection(mActivity, "", -1, -1, -1, 2, new DaoUtils.OnDaoResultListListener<Goods>() {
            @Override
            public void onSuccess(List<Goods> list) {
                getAllOneThreeLevelTitle(list);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 获取所有非标品对应的一级和三级标题（非标品）
     */
    private void getAllOneThreeLevelTitle(List<Goods> list) {
        // 所有三级groupId
        Map<Long, Long> threeMap = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            threeMap.put(list.get(i).getGroupid3(), list.get(i).getGroupid1());
        }
        // 所有一级groupId
        Set<Long> oneSet = new TreeSet<>();
        for (Long key : threeMap.keySet()) {
            oneSet.add(threeMap.get(key));
        }
        // 一级对应的所有三级
        oneThreeMap = new TreeMap<>();
        // 遍历添加
        for (Long one : oneSet) {
            List<Long> three = new ArrayList<>();
            for (Long key : threeMap.keySet()) {
                if (one.equals(threeMap.get(key))) {
                    three.add(key);
                }

            }
            oneThreeMap.put(one, three);
        }
        // 所有一级标题
        DaoUtils.getGoodsClassOneTitleListById(mActivity, oneSet, new DaoUtils.OnDaoResultListListener<GoodsClass>() {
            @Override
            public void onSuccess(List<GoodsClass> list) {
                mOneLevelClassificationAdapter.setSelectedPosition(0);
                mOneLevelClassificationAdapter.clearAndAddList(list);
                getThreeLevelClassList();
            }

            @Override
            public void onFailed() {

            }
        });

    }

    /**
     * 获取一级标题对应的三级标题（非标品）
     *
     * @param threeGroups 所有三级非标品groupId
     * @param oneGroupId  一级标题groupId
     */
    private void getAllThreeTitles(List<Long> threeGroups, long oneGroupId) {
        DaoUtils.getGoodsClassThreeTitleListByOneThreeGroupId(mActivity, threeGroups, oneGroupId, new DaoUtils.OnDaoResultListListener<GoodsClass>() {
            @Override
            public void onSuccess(List<GoodsClass> list) {
                mThreeLevelClassificationAdapter.setSelectedPosition(0);
                mThreeLevelClassificationAdapter.clearAndAddList(list);
                getCenterGoodsList();
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUpdataCommonGoodsList(UpdataCommonGoodsEvent event){
        if (mOneLevelClassificationAdapter.getSelectedPosition() == 0){
            getCommonGoodsList(2);
        }
    }

    public interface OnGoodsClickListener {
        void onGoodsClick(Goods goods);
    }
}
