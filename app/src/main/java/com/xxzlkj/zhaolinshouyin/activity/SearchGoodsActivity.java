package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.util.PreferencesSaver;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.HistoryKeywordAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.SearchGoodsListAdapter;
import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.event.AddGoodsEvent;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.ZLCheckUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述:搜索商品
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class SearchGoodsActivity extends ZLBaseActivity {
    public static final String PREFERENCES_KEY_KEYWORD_LIST = "keyword_list";
    public static final String IS_WEIGHTING = "is_weight";
    private EditText mSearchEditText;
    private ImageView mLastPageImageView;
    private Button mSureButton, mAddButton;
    private LinearLayout mLeftTipLayout, mLeftLayout, mRightTipLayout, mRightLayout;
    private TextView mRightTipTextView, mClearTextView;
    private RecyclerView mSearchHistoryRecyclerView;
    private SearchGoodsListAdapter mSearchGoodsListAdapter;
    private boolean isWeighting;

    /**
     * @param isWeighting true:正在称重 false:不在称重
     */
    public static Intent newIntent(Context context, boolean isWeighting) {
        Intent intent = new Intent(context, SearchGoodsActivity.class);
        intent.putExtra(IS_WEIGHTING, isWeighting);
        return intent;
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_search_goods);
    }

    @Override
    protected void findViewById() {
        mLastPageImageView = getView(R.id.id_last_page);// 上一页
        mSearchEditText = getView(R.id.id_search_edit_text);// 搜索框
        mSureButton = getView(R.id.id_sure);// 确定按钮
        mAddButton = getView(R.id.id_add);// 添加商品列表按钮
        mLeftTipLayout = getView(R.id.id_left_tip_layout);// 没有搜索历史展示的布局
        mLeftLayout = getView(R.id.id_left_layout);// 有搜索历史展示的布局
        mRightTipTextView = getView(R.id.id_right_tip);// 进来时右侧提示语
        mRightTipLayout = getView(R.id.id_right_tip_layout);// 没有搜索到商品展示的布局
        mRightLayout = getView(R.id.id_right_layout);// 搜索到商品展示的布局
        mSearchHistoryRecyclerView = getView(R.id.id_search_history);// 搜索历史列表
        mClearTextView = getView(R.id.id_clear);// 清空
        mSearchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        RecyclerView mGoodsRecyclerView = getView(R.id.id_recycler_view);
        mGoodsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSearchGoodsListAdapter = new SearchGoodsListAdapter(mContext, R.layout.item_search_goods_list);
        mGoodsRecyclerView.setAdapter(mSearchGoodsListAdapter);
        getView(R.id.id_check_all).setVisibility(View.VISIBLE);

    }

    @Override
    public void setListener() {
        // 上一页
        mLastPageImageView.setOnClickListener(v -> finish());
        // 添加销售列表
        mAddButton.setOnClickListener(v -> {
            if (mSearchGoodsListAdapter.selectPosition != -1) {

                // 有选中
                Goods goods = mSearchGoodsListAdapter.getList().get(mSearchGoodsListAdapter.selectPosition);
                if (!ZLCheckUtils.checkHasNoCanBuyGoods(SearchGoodsActivity.this,goods)){
                    // 商品可销售
                    if (isWeighting && goods.getStandard() == 1) {
                        // 提示
                        ToastManager.showShortToast(mContext, "标品不可称重");
                    } else {
                        EventBus.getDefault().postSticky(new AddGoodsEvent(goods));
                        finish();
                    }
                }

            } else {
                // 无选中 提示
                ToastManager.showShortToast(mContext, "请选择一个商品添加");
            }

        });
        // 确定搜索
        mSureButton.setOnClickListener(v -> searchGoods(mSearchEditText.getText().toString().trim()));
        // 清空
        mClearTextView.setOnClickListener(v -> {
            PreferencesSaver.removeAttr(mContext, PREFERENCES_KEY_KEYWORD_LIST);
            setHistoryData();
        });
        // 列表点击事件
        mSearchGoodsListAdapter.setOnItemClickListener((position, item) -> {
            // 设置选中
            mSearchGoodsListAdapter.selectPosition= position;
            mSearchGoodsListAdapter.notifyDataSetChanged();
        });
    }


    @Override
    public void processLogic() {
        isWeighting = getIntent().getBooleanExtra(IS_WEIGHTING, false);
        setHistoryData();
    }

    /**
     * 搜索
     *
     * @param keyword 搜索关键字
     */
    private void searchGoods(String keyword) {
        addKeyword(keyword);
        DaoUtils.getGoodsListByCodeTitle(this, keyword, new DaoUtils.OnDaoResultListListener<Goods>() {
            @Override
            public void onSuccess(List<Goods> list) {
                // 选中归原
                mSearchGoodsListAdapter.selectPosition = -1;
                mSearchGoodsListAdapter.clearAndAddList(list);
                // 设置右侧商品列表显示、提示和无商品隐藏
                mRightLayout.setVisibility(View.VISIBLE);
                mRightTipLayout.setVisibility(View.GONE);
                mRightTipTextView.setVisibility(View.GONE);
            }

            @Override
            public void onFailed() {
                // 选中归原
                mSearchGoodsListAdapter.selectPosition = -1;
                mSearchGoodsListAdapter.clear();
                // 设置右侧无商品显示、提示和商品列表隐藏
                mRightLayout.setVisibility(View.GONE);
                mRightTipLayout.setVisibility(View.VISIBLE);
                mRightTipTextView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 设置搜索历史数据
     */
    private void setHistoryData() {
        ArrayList<String> list = PreferencesSaver.getStrList(this, PREFERENCES_KEY_KEYWORD_LIST);
        if (list != null && list.size() > 0) {
            Collections.reverse(list);
            HistoryKeywordAdapter mAdapter = new HistoryKeywordAdapter(this, R.layout.adapter_history_keyword_layout, isHasKeyword -> setIsVisible(!isHasKeyword));
            mAdapter.addList(list);
            mSearchHistoryRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener((position, item) -> {
                // 搜索
                searchGoods(item);
            });
            setIsVisible(true);
        } else {
            setIsVisible(false);
        }
    }

    /**
     * 设置搜索历史展示状态
     *
     * @param isVisible true:展示搜索历史界面 false：展示没有搜搜历史时的界面
     */
    private void setIsVisible(boolean isVisible) {
        mLeftLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mLeftTipLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    /**
     * 添加搜索历史关键字
     *
     * @param keyword 搜索关键字
     */
    private void addKeyword(String keyword) {
        // 判断搜索历史里是否有盖关键字
        boolean flag = false;
        ArrayList<String> list = PreferencesSaver.getStrList(this, PREFERENCES_KEY_KEYWORD_LIST);
        if (list == null) {
            list = new ArrayList<>();
        } else {
            // 循环遍历是否有当前输入的关键词
            for (int i = 0; i < list.size(); i++) {
                if (keyword.equals(list.get(i))) {
                    flag = true;
                    break;
                }
            }
        }

        if (!flag) {
            // 搜索历史列表没有 加进去
            list.add(keyword);
            PreferencesSaver.putStrList(this, PREFERENCES_KEY_KEYWORD_LIST, list);
        }

        setHistoryData();
    }

}
