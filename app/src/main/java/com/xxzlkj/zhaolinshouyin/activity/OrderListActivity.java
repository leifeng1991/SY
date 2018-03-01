package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xxzlkj.zhaolinshare.base.MyRecyclerView;
import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.net.OnMyActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.LogUtil;
import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.OnLineOrderListAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.OrderListAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.SpinnerAdapter;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.model.OnLineOrderListBean;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.FloatWindowUtils;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 描述:订单列表
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class OrderListActivity extends ZLBaseActivity implements AdapterView.OnItemSelectedListener {
    public static final String order_type = "order_type";
    private MyRecyclerView mRecyclerView;
    private BaseAdapter mAdapter;
    private TextView mOfflineTextView, mOnlineTextView, mOrderNumberTextView, mStartDateTextView,
            mEndDateTextView, mCashierOrOrderTypeTextView, mSyncTypeOrOrderStateTextView,
            mSpinnerTextView1, mSpinnerTextView2, mPsTypeTextView;
    private View mPsTypeLineView;
    private EditText mInputOrderNumberEditText;
    private LinearLayout mPsTypeLayout;
    private RelativeLayout mSearchRelativeLayout, mButtonRelativeLayout;
    private Spinner[] spinners = new Spinner[5];
    private ImageButton mUpImageButton, mDownImageButton;
    private Button mStatisticsButton, mAllOrderButton;
    // 开始时间
    private long mStartTime = -1;
    // 结束时间
    private long mEndTime = -1;
    // 订单类型
    private int mOrderType = 2;
    private boolean[] mIsFistLoad;
    private List<User> userList;
    private int page;
    private StringBuffer mStringBufferResult = new StringBuffer();// 扫码内容

    public static Intent newIntent(Context context, int orderType) {
        Intent intent = new Intent(context, OrderListActivity.class);
        intent.putExtra(order_type, orderType);
        return intent;
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_order_list);
    }

    @Override
    protected void findViewById() {
        mOfflineTextView = getView(R.id.id_offline);// 线下
        mOnlineTextView = getView(R.id.id_online);// 线上
        mRecyclerView = getView(R.id.recyclerView);// 订单列表
        mInputOrderNumberEditText = getView(R.id.id_input_order_number);// 订单号输入框
        mSearchRelativeLayout = getView(R.id.id_search);// 搜索
        mStartDateTextView = getView(R.id.id_start_date);// 开始日期
        mEndDateTextView = getView(R.id.id_end_time);// 开始日期
        mButtonRelativeLayout = getView(R.id.id_button_layout);// 按钮布局
        mStatisticsButton = getView(R.id.id_statistics);// 统计
        mAllOrderButton = getView(R.id.id_all_order);// 全部订单
        spinners[0] = getView(R.id.id_spinner_cashier);// 收银员
        spinners[1] = getView(R.id.id_spinner_water_type);// 流水类型
        spinners[2] = getView(R.id.id_spinner_pay_type);// 支付方式
        spinners[3] = getView(R.id.id_sync_type);// 同步方式
        mPsTypeLayout = getView(R.id.id_ps_type_layout);
        spinners[4] = getView(R.id.id_pei_song_type);// 配送方式
        mSpinnerTextView1 = getView(R.id.id_spinner_title_1);
        mSpinnerTextView2 = getView(R.id.id_spinner_title_2);
        mOrderNumberTextView = getView(R.id.id_order_number);// 订单号
        mCashierOrOrderTypeTextView = getView(R.id.id_order_type);// 收银员/订单类型
        mSyncTypeOrOrderStateTextView = getView(R.id.id_sync_type_order);// 同步类型/订单状态
        mPsTypeTextView = getView(R.id.id_ps_type);// 配送方式
        mPsTypeLineView = getView(R.id.id_ps_type_line);// 配送方式右边分割线
        mUpImageButton = getView(R.id.id_up_page);// 上一页
        mDownImageButton = getView(R.id.id_down_page);// 下一页

    }


    @Override
    public void setListener() {
        // 线下
        mOfflineTextView.setOnClickListener(v -> setTextViewSelectState(false));
        // 线上
        mOnlineTextView.setOnClickListener(v -> setTextViewSelectState(true));
        // 设置监听
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                // 刷新数据
                getData();
            }

            @Override
            public void onLoadMore() {
                page = mAdapter.getItemCount() / 20 + 1;
                getData();
            }
        });
        // 搜索
        mSearchRelativeLayout.setOnClickListener(v -> getData());
        // 开始日期
        mStartDateTextView.setOnClickListener(v -> showSelectDateDialog(true));
        // 结束日期
        mEndDateTextView.setOnClickListener(v -> showSelectDateDialog(false));
        // 统计
        mStatisticsButton.setOnClickListener(v -> {
            if (mStartTime == -1) {
                ToastManager.showShortToast(mContext, "请选择开始时间");
                return;
            }
            if (mEndTime == -1) {
                ToastManager.showShortToast(mContext, "请选择结束时间");
                return;
            }
            if (mAdapter.getItemCount() == 0) {
                ToastManager.showShortToast(mContext, "暂无统计订单");
                return;
            }
            startActivity(OrderStatisticsDialogActivity.newIntent(mContext, this, mStartTime, mEndTime));
        });
        // 全部订单
        mAllOrderButton.setOnClickListener(v -> {

        });
        // 收银员/订单类型
        spinners[0].setOnItemSelectedListener(this);
        // 流水类型
        spinners[1].setOnItemSelectedListener(this);
        // 支付方式
        spinners[2].setOnItemSelectedListener(this);
        // 同步类型/订单状态
        spinners[3].setOnItemSelectedListener(this);
        // 配送方式
        spinners[4].setOnItemSelectedListener(this);
        // 上一页
        mUpImageButton.setOnClickListener(v -> {

        });
        // 下一页
        mDownImageButton.setOnClickListener(v -> {

        });
        // 订单列表item点击事件
       /* mAdapter.setOnItemClickListener((position, item) -> {
            // 跳转到详情界面
            startActivity(OrderDesActivity.newIntent(mContext, item.getOrder_id()));
        });*/
    }

    @Override
    public void processLogic() {
        // 设置网络状态，刷新的时候重新获取状态
        boolean netIsAvailable = NetStateManager.isAvailable(mContext);
        setNetStateHint(netIsAvailable);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 重置颜色
        mOrderNumberTextView.setTextColor(0xff282828);
        mOrderType = getIntent().getIntExtra(order_type, 2);
        // 刷新数据
        setTextViewSelectState(mOrderType == 1);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mOrderType = intent.getIntExtra(order_type, 2);
        // 刷新数据
        setTextViewSelectState(mOrderType == 1);
    }

    /**
     * @param isOnLine true:线上 false:线下
     */
    private void setTextViewSelectState(boolean isOnLine) {
        mInputOrderNumberEditText.setHint(isOnLine ? "输入订单号" : "输入订单号/商品货号/商品名称");
        mInputOrderNumberEditText.setText("");
        mInputOrderNumberEditText.setInputType(isOnLine ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT);
        mOnlineTextView.setBackgroundResource(isOnLine ? R.drawable.shape_rectangle_radius_ffeae2_8 : R.drawable.shape_rectangle_radius_white_8);
        mOfflineTextView.setBackgroundResource(isOnLine ? R.drawable.shape_rectangle_radius_white_8 : R.drawable.shape_rectangle_radius_ffeae2_8);
        mOnlineTextView.setTextColor(isOnLine ? ContextCompat.getColor(mContext, R.color.orange_ff725c) : ContextCompat.getColor(mContext, R.color.black_282828));
        mOfflineTextView.setTextColor(isOnLine ? ContextCompat.getColor(mContext, R.color.black_282828) : ContextCompat.getColor(mContext, R.color.orange_ff725c));
        mOrderType = isOnLine ? 1 : 2;
        refreshData();
    }

    /**
     * 获取收银员列表
     */
    private void getCashierList() {
        userList = DaoUtils.getUserList();
        String[] cashierList = new String[userList.size() + 1];
        cashierList[0] = ("全部");
        for (int i = 0; i < userList.size(); i++) {
            cashierList[i + 1] = userList.get(i).getName();
        }
        // 收银员
        setAdapter(spinners[0], cashierList);

    }

    /**
     * 设置Spinner数据
     *
     * @param spinner 目标
     * @param strings 数据
     */
    private void setAdapter(Spinner spinner, String[] strings) {
        SpinnerAdapter adapter = new SpinnerAdapter(mContext, android.R.layout.simple_spinner_item, Arrays.asList(strings), spinner);
        spinner.setAdapter(adapter);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        // 获取数据
        reset();
    }

    /**
     * 重置
     */
    private void reset() {
        mIsFistLoad = new boolean[]{true, true, true, true, true};
        mRecyclerView.setPullRefreshAndLoadingMoreEnabled(true, mOrderType == 1);
        if (mOrderType == 1) {
            // 订单类型
            setAdapter(spinners[0], getResources().getStringArray(R.array.orderType));
        } else {
            // 收银员
            getCashierList();
        }
        // 流水类型
        setAdapter(spinners[1], getResources().getStringArray(R.array.liuShuiType));
        // 支付方式
        setAdapter(spinners[2], getResources().getStringArray(mOrderType == 1 ? R.array.payType1 : R.array.payType));
        // 同步方式/订单状态
        setAdapter(spinners[3], getResources().getStringArray(mOrderType == 1 ? R.array.orderState1 : R.array.syncType));
        // 配送方式
        setAdapter(spinners[4], getResources().getStringArray(R.array.psType));

        mButtonRelativeLayout.setVisibility(mOrderType == 1 ? View.GONE : View.VISIBLE);
        mPsTypeTextView.setVisibility(mOrderType == 1 ? View.VISIBLE : View.GONE);
        mPsTypeLineView.setVisibility(mOrderType == 1 ? View.VISIBLE : View.GONE);
        mPsTypeLayout.setVisibility(mOrderType == 1 ? View.VISIBLE : View.GONE);
        spinners[4].setVisibility(mOrderType == 1 ? View.VISIBLE : View.GONE);
        mCashierOrOrderTypeTextView.setText(mOrderType == 1 ? "订单类型" : "收银员");
        mSyncTypeOrOrderStateTextView.setText(mOrderType == 1 ? "订单状态" : "同步类型");
        mSpinnerTextView1.setText(mOrderType == 1 ? "订单类型" : "收银员");
        mSpinnerTextView2.setText(mOrderType == 1 ? "订单状态" : "是否已同步");
        // 如果选中的不是0重置为0 否则不重置
        for (int i = 0; i < spinners.length; i++) {
            if (spinners[i].getSelectedItemPosition() != 0) {
                mIsFistLoad[i] = true;
                spinners[i].setSelection(0);
            }
        }
        // 订单类型为1时使用线上适配器否则使用线下适配器
        mAdapter = mOrderType == 1 ? new OnLineOrderListAdapter(mContext, this, R.layout.item_order_list) :
                new OrderListAdapter(mContext, OrderListActivity.this, R.layout.item_order_list);
        mRecyclerView.setAdapter(mAdapter);
        page = 1;
        // 加载数据
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        // 获取未打印的数量
        FloatWindowUtils.getNoOperatingNum(this);
        if (mOrderType == 1) {
            // 线上
            getNetOrderListData();
        } else {
            // 线下
            getLocalOrderListData();
        }
    }

    /**
     * 获取本地订单数据
     */
    private void getLocalOrderListData() {
        int position1 = spinners[0].getSelectedItemPosition();// 收银员
        int position2 = spinners[1].getSelectedItemPosition();// 流水类型
        int position3 = spinners[2].getSelectedItemPosition();// 支付方式
        int position4 = spinners[3].getSelectedItemPosition();// 是否已同步
        // 收银员
        String mCashier = position1 == 0 ? "" : userList != null ? userList.get(position1 - 1).getUid() : "";
        // -1:全部 1：消费 2：退款
        int mWaterType = position2 == 0 ? -1 : position2;
        // -1:全部 1：支付宝 2：微信 6：现金
        int mPayType = -1;
        switch (position3) {
            case 0:// 全部
                mPayType = -1;
                break;
            case 1:// 支付宝
                mPayType = 1;
                break;
            case 2:// 微信
                mPayType = 2;
                break;
            case 3:// 现金
                mPayType = 6;
                break;
        }
        // 同步类型 -1:全部 0：未上传，1：已上传，2：无库存
        int mSyncType = position4 == 0 ? -1 : position4 - 1;
        LogUtil.e("参数：order_id=" + mInputOrderNumberEditText.getText().toString().trim() +
                "  mStartTime=" + mStartTime + "  mEndTime=" + mEndTime + "  mOrderType=" + mCashier +
                "  mWaterType=" + mWaterType + "  mPayType=" + mPayType);
        // 本地获取
        DaoUtils.getOrderList(this, mInputOrderNumberEditText.getText().toString().trim(),
                mStartTime, mEndTime, mCashier, mWaterType, mPayType, mSyncType, new DaoUtils.OnDaoResultListListener<Order>() {
                    @Override
                    public void onSuccess(List<Order> list) {
                        // 设置数据
                        if (mOrderType == 2)
                            mRecyclerView.handlerSuccessOnlyHasRefresh(mAdapter, list);
                    }

                    @Override
                    public void onFailed() {
                        if (mOrderType == 2)
                            mRecyclerView.handlerError(mAdapter, true);
                    }
                });

    }


    /**
     * @param isStartTime true:开始时间 false:结束时间
     */
    private void showSelectDateDialog(boolean isStartTime) {
        Calendar endCalendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(1900, 1, 1);
        endCalendar.setTimeInMillis(System.currentTimeMillis());
        TimePickerView pvTime = new TimePickerView.Builder(this, (date, v) -> {//选中事件回调
            LogUtil.e("date", date.getTime() + "");
            long time = date.getTime();
            if (isStartTime) {
                // 开始时间
                mStartTime = time / 1000;
                mStartDateTextView.setText(DateUtils.getYearMonthDayHourMinute(time));
            } else {
                // 结束时间
                mEndTime = time / 1000;
                mEndDateTextView.setText(DateUtils.getYearMonthDayHourMinute(time));
            }

            getData();
            LogUtil.e("time" + DateUtils.getYearMonthDayHourMinuteSeconds2(mEndTime * 1000));

        }).setTitleText("请选择时间")
                .setTitleSize(20)
                .setTitleColor(0xff000000)
                .setBgColor(0xffffffff)
                .setLineSpacingMultiplier(2.0f)
                .setRangDate(startCalendar, endCalendar)
                .setTitleBgColor(0xFFE3E3E3)
                .isDialog(true)
                .setType(new boolean[]{true, true, true, true, true, true}).build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        View view = pvTime.findViewById(com.bigkoo.pickerview.R.id.content_container);
        view.setLayoutParams(new FrameLayout.LayoutParams(600, FrameLayout.LayoutParams.WRAP_CONTENT));
        pvTime.showDialog();
    }

    /**
     * 从网上获取线上订单列表
     */
    private void getNetOrderListData() {
        int position1 = spinners[0].getSelectedItemPosition();// 订单类型
        int position2 = spinners[1].getSelectedItemPosition();// 流水类型
        int position3 = spinners[2].getSelectedItemPosition();// 支付方式
        int position4 = spinners[3].getSelectedItemPosition();// 订单状态
        int position5 = spinners[4].getSelectedItemPosition();// 配送方式

        HashMap<String, String> map = new HashMap<>();
        // 订单类型 0：普通订单 2：团购订单
        String orderType = position1 == 0 ? "" : position1 == 1 ? "0" : "2";
        if (!TextUtils.isEmpty(orderType))
            map.put("activity_type", orderType);
        // state	1待付款 2待发货 3待收货 4已完成 5已取消 6已退款 全部则不传
        String mOrderState = "";
        if (position2 == 0) {
            switch (position4) {
                case 1:// 待发货
                    mOrderState = "2";
                    break;
                case 2:// 配送中
                    mOrderState = "3";
                    break;
                case 3:// 已完成
                    mOrderState = "4";
                    break;
                case 4:// 退款
                    mOrderState = "6";
                    break;
            }
        } else if (position2 == 1) {
            switch (position4) {
                case 0:// 待发货
                    mOrderState = "2";
                    break;
                case 1:// 配送中
                    mOrderState = "3";
                    break;
                case 2:// 已完成
                    mOrderState = "4";
                    break;
            }
        } else if (position2 == 2) {
            // 退款
            mOrderState = "6";
        }

        if (!TextUtils.isEmpty(mOrderState))
            map.put("state", mOrderState);
        // 1:支付宝 2：微信
        String mPayment = position3 == 0 ? "" : position3 == 1 ? "1" : "2";
        if (!TextUtils.isEmpty(mPayment))
            map.put("payment", mPayment);
        // 1:兆邻配送 2：门店自提
        String mOrderPsType = position5 == 0 ? "" : position5 == 1 ? "1" : "2";
        if (!TextUtils.isEmpty(mOrderPsType))
            map.put("delivery", mOrderPsType);
        // 门店id 不传为全部
        map.put("store_id", ZLUtils.getStoreId());
        // 开始时间
        if (mStartTime != -1)
            map.put("starttime", mStartTime + "");
        // 结束时间
        if (mEndTime != -1)
            map.put("stoptime", mEndTime + "");
        // 关键字
        String orderId = mInputOrderNumberEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(orderId))
            map.put("keyword", orderId);
        // 分页
        map.put("page", page + "");
        RequestManager.createRequest(ZLURLConstants.ORDER_URL, map, new OnMyActivityRequestListener<OnLineOrderListBean>(this) {
            @Override
            public void onSuccess(OnLineOrderListBean bean) {
                if (mOrderType == 1)
                    mRecyclerView.handlerSuccessHasRefreshAndLoad(mAdapter, page == 1, bean.getData());
            }

            @Override
            public void onFailed(boolean isError, String code, String message) {
                super.onFailed(isError, code, message);
                if (mOrderType == 1)
                    mRecyclerView.handlerError(mAdapter, page == 1);
            }
        });
    }

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        if (userList != null)
            for (int i = 0; i < userList.size(); i++) {
                map.put(userList.get(i).getUid(), userList.get(i).getName());
            }

        return map;
    }

    /**
     * 获取订单列表
     */
    public List<Order> getOrderList() {
        return mAdapter.getList();
    }

    public String getNames() {
        String names = "";
        List<Order> list = mAdapter.getList();
        Set<String> sets = new TreeSet<>();
        Map<String, String> map = getMap();
        for (int i = 0; i < list.size(); i++) {
            sets.add(map.get(list.get(i).getStore_uid()));
        }

        for (String name : sets) {
            names = TextUtils.isEmpty(names) ? name : names + "、" + name;
        }
        return names;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.e("----------------------------1" + id);
        page = 1;
        int index = 0;
        switch (parent.getId()) {
            case R.id.id_spinner_cashier:// 收银员/订单类型
                index = 0;
                break;
            case R.id.id_spinner_water_type:// 流水类型
                index = 1;
                break;
            case R.id.id_spinner_pay_type:// 支付类型
                index = 2;
                break;
            case R.id.id_sync_type:// 同步类型/订单状态
                index = 3;
                break;
        }

        // 防止第一次进来执行
        if (!mIsFistLoad[index]) {
            LogUtil.e("----------------------------2" + index);
            if (mOrderType == 1 && index == 1) {
                setAdapter(spinners[3], getResources().getStringArray(position == 0 ?
                        R.array.orderState1 : position == 1 ? R.array.orderState2 : R.array.orderState3));
            } else {
                getData();
            }

        }
        mIsFistLoad[index] = false;
        LogUtil.e("----------------------------3" + index);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                getData();
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
