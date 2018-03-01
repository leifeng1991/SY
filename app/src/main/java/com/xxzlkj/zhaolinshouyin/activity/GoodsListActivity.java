package com.xxzlkj.zhaolinshouyin.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xxzlkj.zhaolinshare.base.MyRecyclerView;
import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.GoodsListAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.SpinnerAdapter;
import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.db.GoodsClass;
import com.xxzlkj.zhaolinshouyin.event.UpdataCommonGoodsEvent;
import com.xxzlkj.zhaolinshouyin.listener.OnConnectDevicesListener;
import com.xxzlkj.zhaolinshouyin.listener.OnSyncListener;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.PrintBarCodeHelper;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;
import com.xxzlkj.zhaolinshouyin.utils.ZLLooper;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;
import com.xxzlkj.zhaolinshouyin.weight.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述:商品列表
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class GoodsListActivity extends ZLBaseActivity {
    public static final String IS_CAN_CHECKED = "is_can_checked";
    private ClearEditText mInputOrderNumberEditText;
    private RelativeLayout mSearchRelativeLayout, mPrintLayout;
    private LinearLayout mCheckedLayout;
    private ImageView mCheckedImageView, mLastPageImageView;
    private Spinner mOneLevelSpinner, mTwoLevelSpinner, mThreeLevelSpinner, mProductSpinner;
    private Button mUpdateDataButton, mPrintButton;
    private MyRecyclerView mRecyclerView;
    private TextView mPrintTextView;
    private GoodsListAdapter mAdapter;
    private int page = 1;
    private boolean netIsAvailable;
    private List<GoodsClass> oneList;
    private List<GoodsClass> twoList;
    private List<GoodsClass> threeList;
    // 是否是第一次加载
    private boolean[] isFirstLoad = new boolean[]{true, true, true, true};
    private ProgressDialog progressDialog;
    // 是否全选
    private boolean mIsAllChecked;
    private boolean isCanChecked;
    private PrintBarCodeHelper printBarCodeHelper;
    private StringBuffer mStringBufferResult = new StringBuffer();// 扫码内容

    /**
     * @param isCanChecked 是否可以选中
     */
    public static Intent newIntent(Context context, boolean isCanChecked) {
        Intent intent = new Intent(context, GoodsListActivity.class);
        intent.putExtra(IS_CAN_CHECKED, isCanChecked);
        return intent;
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_goods_list);
    }

    @Override
    protected void findViewById() {
        mLastPageImageView = getView(R.id.id_last_page);// 上一页
        mInputOrderNumberEditText = getView(R.id.id_input_order_number);// 订单号输入框
        mSearchRelativeLayout = getView(R.id.id_search);// 搜索
        mOneLevelSpinner = getView(R.id.id_one_level);// 一级
        mTwoLevelSpinner = getView(R.id.id_second_level);// 二级
        mThreeLevelSpinner = getView(R.id.id_three_level);// 三级
        mProductSpinner = getView(R.id.id_product_type);// 分类
        setAdapter(mProductSpinner, Arrays.asList(getResources().getStringArray(R.array.goodsType)));
        mUpdateDataButton = getView(R.id.id_update_data);// 更新数据
        mPrintButton = getView(R.id.id_print);// 打印价签
        mRecyclerView = getView(R.id.recyclerView);// 商品列表
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        isCanChecked = getIntent().getBooleanExtra(IS_CAN_CHECKED, false);
        mCheckedLayout = getView(R.id.id_check_all_layout);// 全选布局
        TextView mCheckedTextView = getView(R.id.id_check_all);
        mCheckedImageView = getView(R.id.id_check_image);// 全选图片
        mPrintLayout = getView(R.id.id_print_layout);// 打印布局
        mPrintTextView = getView(R.id.id_print_tv);// 确认打印
        mPrintTextView.setVisibility(View.VISIBLE);

        mCheckedLayout.setVisibility(isCanChecked ? View.VISIBLE : View.GONE);
        mCheckedTextView.setVisibility(isCanChecked ? View.VISIBLE : View.GONE);
        mLastPageImageView.setVisibility(isCanChecked ? View.VISIBLE : View.GONE);
        mPrintButton.setVisibility(isCanChecked ? View.GONE : View.VISIBLE);
        mUpdateDataButton.setText(isCanChecked ? "确定" : "更新数据");

        // 打印
        mAdapter = new GoodsListAdapter(mContext, isCanChecked, R.layout.item_goods_list, isAllChecked -> {
            // 选中
            mIsAllChecked = isAllChecked;
            mCheckedImageView.setImageResource(isAllChecked ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);
        }, this::print);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void setListener() {
        // 上一页
        mLastPageImageView.setOnClickListener(v -> finish());
        // 设置监听
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getGoodsList();

            }

            @Override
            public void onLoadMore() {
                page = mAdapter.getItemCount() / mRecyclerView.loadSize + 1;
                getGoodsList();
            }
        });
        // 搜索
        mSearchRelativeLayout.setOnClickListener(v -> {
            page = 1;
            getGoodsList();
        });
        // 一级
        mOneLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirstLoad[0]) {
                    // 非第一次加载
                    if (position == 0) {
                        // 二级列表只展示全部
                        setAdapter(mTwoLevelSpinner, getStringList(new ArrayList<>()));
                    } else {
                        // 二级列表展示所有分类title+全部
                        if (oneList != null && oneList.size() > 0) {
                            getTwoLevelTitleList(oneList.get(position - 1).getId());
                        }
                    }
                }
                isFirstLoad[0] = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 二级
        mTwoLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirstLoad[1]) {
                    // 非第一次加载
                    if (position == 0) {
                        // 三级列表只展示全部
                        setAdapter(mThreeLevelSpinner, getStringList(new ArrayList<>()));
                    } else {
                        // 三级列表展示所有分类title+全部
                        if (twoList != null && twoList.size() > 0) {
                            getThreeLevelTitleList(twoList.get(position - 1).getId());
                        }
                    }
                }
                isFirstLoad[1] = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 三级
        mThreeLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirstLoad[2]) {
                    // 非第一次加载
                    page = 1;
                    getGoodsList();
                }
                isFirstLoad[2] = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 分类
        mProductSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirstLoad[3]) {
                    // 非第一次加载
                    page = 1;
                    getGoodsList();
                }
                isFirstLoad[3] = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 更新数据
        mUpdateDataButton.setOnClickListener(v -> {
            if (isCanChecked) {
                // 确定添加
                List<Goods> goodList = mAdapter.getGoodList();
                if (goodList != null && goodList.size() > 0) {
                    ZLDialogUtil.showRawDialog(GoodsListActivity.this, "已选择" + goodList.size() + "个商品，是否添加到常用商品列表?", () -> {
                        // 插入
                        insertOrReplaceCommonGoodsList(goodList);
                    });

                } else {
                    ToastManager.showShortToast(mContext, "请选择要添加的商品");
                }
            } else {
                // 更新数据
                updateData();
            }

        });
        // 全选
        if (mCheckedLayout != null)
            mCheckedLayout.setOnClickListener(v -> {
                List<Goods> list = mAdapter.getList();
                mIsAllChecked = !mIsAllChecked;
                mCheckedImageView.setImageResource(mIsAllChecked ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);
                if (list != null && list.size() > 0) {
                    // 遍历改变每个商品状态
                    for (int i = 0; i < list.size(); i++) {
                        // 设置选中状态
                        list.get(i).setIsChecked(mIsAllChecked);
                    }
                    // 更新
                    mAdapter.notifyDataSetChanged();


                }
            });
        // 打印价签/取消打印
        mPrintButton.setOnClickListener(v -> {
            String printStr = mPrintButton.getText().toString();
            if ("打印价签".equals(printStr)) {
                // 打印价签
                // 打开打印链接
                printBarCodeHelper = PrintBarCodeHelper.getInstance(this);
                printBarCodeHelper.autoConnectPrintDevices(this, new OnConnectDevicesListener() {
                    @Override
                    public void onSuccess() {
                        // 链接打印成功，修改状态
                        ToastManager.showShortToast(mContext, "链接打印设备成功");
                        mPrintLayout.setVisibility(View.VISIBLE);
                        mPrintButton.setText("取消打印");
                        if (mAdapter != null && mAdapter.getItemCount() > 0) {
                            // 显示打印按钮
                            mAdapter.isHasPrint = true;
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailed() {
                        ToastManager.showShortToast(mContext, "链接打印失败，请重新选择");
                    }
                });
            } else {
                // 取消打印
                mPrintLayout.setVisibility(View.GONE);
                mPrintButton.setText("打印价签");
                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                    // 隐藏打印按钮
                    mAdapter.isHasPrint = false;
                    mAdapter.notifyDataSetChanged();
                }
                // 断开打印链接
                if (printBarCodeHelper != null) {
                    printBarCodeHelper.disconnectConnectPrintDevices();
                    printBarCodeHelper = null;
                }
            }
        });

    }


    @Override
    public void processLogic() {
        mRecyclerView.setPullRefreshAndLoadingMoreEnabled(true, false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("数据更新中...");
        progressDialog.setCancelable(false);

        // 设置网络状态，刷新的时候重新获取状态
        netIsAvailable = NetStateManager.isAvailable(mContext);
        setNetStateHint(netIsAvailable);
        // 刷新数据
        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 断开打印链接
        if (printBarCodeHelper != null) {
            printBarCodeHelper.disconnectConnectPrintDevices();
            printBarCodeHelper = null;
        }
    }

    /**
     * 打印价签
     */
    private void print(Goods goods) {
        if (goods != null) {
            // 打印
            DaoUtils.getGoodBarCodeByGoodsCode(this, goods.getCode(), new DaoUtils.OnDaoResultListener<String>() {

                @Override
                public void onSuccess(String goodsBarCode) {
                    if (printBarCodeHelper != null)
                        printBarCodeHelper.printPriceTag(mContext, goods.getTitle(), goods.getPrice(), goodsBarCode);
                    else
                        ToastManager.showShortToast(mContext, "请重新链接打印设备");
                }

                @Override
                public void onFailed() {
                    ToastManager.showShortToast(mContext, "该商品信息不完善");
                }
            });
        } else {
            // 提示
            ToastManager.showShortToast(mContext, "请录入商品");
        }

    }

    /**
     * 插入常用商品
     *
     * @param goodList 商品列表
     */
    private void insertOrReplaceCommonGoodsList(List<Goods> goodList) {
        DaoUtils.insertOrReplaceCommonGoodsList(goodList, new DaoUtils.OnDaoResultListener<String>() {
            @Override
            public void onSuccess(String bean) {
                ToastManager.showShortToast(mContext, "添加成功");
                // 更新称重界面常用商品
                EventBus.getDefault().postSticky(new UpdataCommonGoodsEvent());
                getGoodsList();
            }

            @Override
            public void onFailed() {
                ToastManager.showShortToast(mContext, "添加失败");
            }
        });
    }

    /**
     * 更新数据
     */
    private void updateData() {
        if (netIsAvailable) {
            progressDialog.show();
            // 有网络
            // 同步是否成功
            final boolean[] isSyncSuccess = new boolean[]{false, false};
            // 同步商品分类
            ZLLooper.synGoodsClass(new OnSyncListener() {
                @Override
                public void onSyncSuccess() {
                    // 同步成功为true
                    isSyncSuccess[0] = true;
                    judgeIsAllSuccess(isSyncSuccess[0], isSyncSuccess[1]);
                }

                @Override
                public void onSyncFailed() {
                    // 同步失败为false
                    isSyncSuccess[0] = false;
                    progressDialog.dismiss();
                }
            });
            // 门店id
            String storeId = ZLUtils.getStoreId();
            if (!TextUtils.isEmpty(storeId)){
                // 同步商品
                ZLLooper.synGoods(storeId, new OnSyncListener() {
                    @Override
                    public void onSyncSuccess() {
                        // 同步成功为true
                        isSyncSuccess[1] = true;
                        judgeIsAllSuccess(isSyncSuccess[0], isSyncSuccess[1]);
                    }

                    @Override
                    public void onSyncFailed() {
                        // 同步失败为false
                        isSyncSuccess[1] = false;
                        progressDialog.dismiss();
                    }
                });
                // 同步条形码
                ZLLooper.synGoodsCode(new OnSyncListener() {
                    @Override
                    public void onSyncSuccess() {

                    }

                    @Override
                    public void onSyncFailed() {
                        Toast.makeText(mContext,"同步条形码失败",Toast.LENGTH_SHORT).show();
                    }
                });
                // 同步三方商品
                ZLLooper.synThreeGoods(storeId, new OnSyncListener() {
                    @Override
                    public void onSyncSuccess() {

                    }

                    @Override
                    public void onSyncFailed() {
                        Toast.makeText(mContext,"同步三方商品失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } else {
            // 无网络
            ToastManager.showShortToast(mContext, "暂无网络");
        }

    }

    /**
     * 判断
     *
     * @param isSyncGoodsClassSuccess 同步分类是否成功
     * @param isSyncGoodsSuccess      同步商品是否成功
     */
    private void judgeIsAllSuccess(boolean isSyncGoodsClassSuccess, boolean isSyncGoodsSuccess) {
        if (isSyncGoodsClassSuccess && isSyncGoodsSuccess) {
            progressDialog.dismiss();
            refreshData();
        }
    }

    private void setAdapter(Spinner spinner, List<String> strings) {
        SpinnerAdapter adapter = new SpinnerAdapter(mContext, android.R.layout.simple_spinner_item, strings, spinner);
        spinner.setAdapter(adapter);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        page = 1;
        // 获取数据
        getGoodsListData();
    }

    private void getGoodsListData() {
        // 本地获取
        DaoUtils.getGoodsClassOneTitleList(this, new DaoUtils.OnDaoResultListListener<GoodsClass>() {
            @Override
            public void onSuccess(List<GoodsClass> list) {
                if (list != null && list.size() > 0) {
                    oneList = list;
                    // 设置一级分类数据
                    setAdapter(mOneLevelSpinner, getStringList(list));
                    // 设置二级分类数据
                    setAdapter(mTwoLevelSpinner, getStringList(new ArrayList<>()));
                    // 设置三级分类数据
                    setAdapter(mThreeLevelSpinner, getStringList(new ArrayList<>()));
                    // 获取商品列表
                    getGoodsList();
                }

            }

            @Override
            public void onFailed() {
                // 加载数据失败处理
                oneList = null;
                setAdapter(mOneLevelSpinner, new ArrayList<>());
                setAdapter(mTwoLevelSpinner, new ArrayList<>());
                setAdapter(mThreeLevelSpinner, new ArrayList<>());
            }
        });


    }

    /**
     * 获取二级分类title
     *
     * @param id 分组id
     */
    private void getTwoLevelTitleList(long id) {
        // 获取二级分类
        DaoUtils.getGoodsClassTwoTitleList(this, id, new DaoUtils.OnDaoResultListListener<GoodsClass>() {
            @Override
            public void onSuccess(List<GoodsClass> list) {
                twoList = list;
                // 设置二级分类数据
                setAdapter(mTwoLevelSpinner, getStringList(list));

            }

            @Override
            public void onFailed() {
                // 获取三级分类失败
                twoList = null;
                setAdapter(mTwoLevelSpinner, new ArrayList<>());
            }
        });
    }

    /**
     * 获取三级分类title
     *
     * @param id 分组id
     */
    private void getThreeLevelTitleList(long id) {
        // 获取三级分类
        DaoUtils.getGoodsClassThreeTitleList(this, id, new DaoUtils.OnDaoResultListListener<GoodsClass>() {
            @Override
            public void onSuccess(List<GoodsClass> list) {
                threeList = list;
                // 设置三级分类数据
                setAdapter(mThreeLevelSpinner, getStringList(list));

            }

            @Override
            public void onFailed() {
                // 获取三级分类失败
                threeList = null;
                setAdapter(mThreeLevelSpinner, new ArrayList<>());
            }
        });
    }

    /**
     * 获取三级分类列表
     */
    private void getGoodsList() {
        // 商品编号
        String code = mInputOrderNumberEditText.getText().toString().trim();
        // 一级分类id
        long groupId1;
        // 二级分类id
        long groupId2;
        // 三级分类id
        long groupId3;
        // 如果选中的是第一个位置（全部） groupId1、groupId2、groupId3、standard为-1
        // 得到一级分类id
        int selectedItemPosition1 = mOneLevelSpinner.getSelectedItemPosition();
        if (oneList != null) {
            groupId1 = selectedItemPosition1 == 0 ? -1 : oneList.get(selectedItemPosition1 - 1).getId();
        } else {
            groupId1 = -1;
        }
        // 得到二级分类id
        int selectedItemPosition2 = mTwoLevelSpinner.getSelectedItemPosition();
        if (twoList != null) {
            groupId2 = selectedItemPosition2 == 0 ? -1 : twoList.get(selectedItemPosition2 - 1).getId();
        } else {
            groupId2 = -1;
        }
        // 得到三级分类id
        int selectedItemPosition3 = mThreeLevelSpinner.getSelectedItemPosition();
        if (threeList != null) {
            groupId3 = selectedItemPosition3 == 0 ? -1 : threeList.get(selectedItemPosition3 - 1).getId();
        } else {
            groupId3 = -1;
        }

        int selectedItemPosition = mProductSpinner.getSelectedItemPosition();
        int standard = selectedItemPosition == 0 ? -1 : selectedItemPosition;
        // 获取商品列表
        DaoUtils.getGoodsListBySelection(this, code, groupId1, groupId2, groupId3, standard, new DaoUtils.OnDaoResultListListener<Goods>() {
            @Override
            public void onSuccess(List<Goods> list) {
                mIsAllChecked = false;
                mCheckedImageView.setImageResource(mIsAllChecked ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);
                mRecyclerView.handlerSuccessOnlyHasRefresh(mAdapter, list);
            }

            @Override
            public void onFailed() {
                mRecyclerView.handlerError(mAdapter, true);
            }
        });

    }

    /**
     * 遍历获取筛选标题
     *
     * @param list 标题集合
     */
    private List<String> getStringList(List<GoodsClass> list) {
        List<String> stringList = new ArrayList<>();
        stringList.add("全部");
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getTitle());
        }
        return stringList;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                //数字
                mStringBufferResult.append((char) ('0' + keyCode - KeyEvent.KEYCODE_0));
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //若为回车键，直接返回
                String string = mStringBufferResult.toString();
                mStringBufferResult.setLength(0);
                // 扫描录入商品
                mInputOrderNumberEditText.requestFocus();
                mInputOrderNumberEditText.setText(string);
                // 搜索
                page = 1;
                getGoodsList();
            }
        }
        return super.dispatchKeyEvent(event);
    }
}

