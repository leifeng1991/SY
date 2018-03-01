package com.xxzlkj.zhaolinshouyin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.model.BaseBean;
import com.xxzlkj.zhaolinshare.base.net.OnMyActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.TextViewUtils;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.OnLineOrderDesGoodsAdapter;
import com.xxzlkj.zhaolinshouyin.adapter.OrderDesGoodsAdapter;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.model.OrderDesBean;
import com.xxzlkj.zhaolinshouyin.model.OrderItemBean;
import com.xxzlkj.zhaolinshouyin.model.SummitOrderButtonBean;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.FloatWindowUtils;
import com.xxzlkj.zhaolinshouyin.utils.PrintHelper;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;
import com.zrq.spanbuilder.Spans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 描述:订单详情
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class OrderDesActivity extends ZLBaseActivity {
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_TYPE = "order_type";
    public static final String CASHIER = "cashier";
    private Button mUpPageButton, mPrintOrderButton, mTuiPriceButton;
    private TextView mStoreNameTextView, mPosNumberTextView, mPosPeopelNumberTextView, mOrderNumberTextView,
            mCashierTextView, mTitleTextView, mTimeTextView, mNumberTextView, mDiscountsPriceTextView,
            mPayTypeTextView, mTotalPriceTextView, mOriginalPriceTextView, mPsAddressTextView, mPsInfoTextView, mReceivableTimeTextView;
    private RecyclerView mRecyclerView;
    private BaseAdapter mOrderDesGoodsAdapter;
    private String mOrderId;
    private String mCashier;
    private boolean orderType;
    private OrderItemBean mOrderItemBean;
    private PrintHelper printHelper = new PrintHelper();// 初始化打印
    private OrderDesBean.DataBean data;
    private Dialog dialog;
    private boolean isOperating;// 是否已经处理

    /**
     * @param orderType 订单类型 true：线上 false：线下(必传)
     * @param orderId   订单id(必传)
     */
    public static Intent newIntent(Context context, boolean orderType, String orderId, String cashier) {
        Intent intent = new Intent(context, OrderDesActivity.class);
        intent.putExtra(ORDER_TYPE, orderType);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(CASHIER, cashier);
        return intent;
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_order_des);
    }

    @Override
    protected void findViewById() {
        mUpPageButton = getView(R.id.id_up_page);// 上一页
        mTitleTextView = getView(R.id.id_title);// 标题
        mPrintOrderButton = getView(R.id.id_print_order);// 打印订单
        mTuiPriceButton = getView(R.id.id_tui_price);// 退款/接单
        mStoreNameTextView = getView(R.id.id_store_name);// 线下店铺名字/线上订单状态
        mPosNumberTextView = getView(R.id.id_pos_number);// 线下收银机编号/线上订单类型
        mPosPeopelNumberTextView = getView(R.id.id_pos_peopel_number);// 线下收银员工号/线上订单号
        mOrderNumberTextView = getView(R.id.id_order_number);// 线下订单号/线上收货人
        mCashierTextView = getView(R.id.id_cashier_names);// 线下收银员/线上电话
        mTimeTextView = getView(R.id.id_time);// 线下时间/线上下单时间
        mReceivableTimeTextView = getView(R.id.id_receivable_time);// 线上接单时间
        mPsAddressTextView = getView(R.id.id_ps_address);// 线上配送地址
        mPsInfoTextView = getView(R.id.id_ps_info);// 配送人信息
        mNumberTextView = getView(R.id.id_number);// 数量
        mDiscountsPriceTextView = getView(R.id.id_discounts_price);// 优惠金额
        mPayTypeTextView = getView(R.id.id_pay_type);// 支付方式
        mTotalPriceTextView = getView(R.id.id_total_price);// 合计
        mOriginalPriceTextView = getView(R.id.id_original_price);// 原价金额
        mRecyclerView = getView(R.id.id_recycler_view);

    }

    @Override
    public void setListener() {
        // 上一页
        mUpPageButton.setOnClickListener(v -> finish());
        // 打印订单
        mPrintOrderButton.setOnClickListener(v -> {
            if (mOrderItemBean != null) {
                FloatWindowUtils.getNoOperatingNum(this);// 获取未打印的数量
                // 打印小票
                if (orderType) {
                    // 线上
                    if (data != null) {
                        // 如果之前没有打印过 修改订单为已打印状态
                        if (!isOperating) {
                            OrderPrint();
                        } else {
                            // 打印小票
                            printHelper.printGoodsList(OrderDesActivity.this, mOrderItemBean);
                        }

                    } else {
                        // 提示
                        ToastManager.showShortToast(mContext, "网络加载错误");
                    }

                } else {
                    // 线下
                    printHelper.printGoodsList(OrderDesActivity.this, mOrderItemBean);
                }

            }

        });
        // 退款/接单
        mTuiPriceButton.setOnClickListener(v -> {
            String text = mTuiPriceButton.getText().toString();
            if ("接单".equals(text)) {
                // 接单
                if (data != null) {
                    // 接单弹框
                    showReceiveOrderDialog("确认接单");
                } else {
                    // 提示
                    ToastManager.showShortToast(mContext, "网络加载错误");
                }

            } else if ("退款".equals(text)) {
                // 退款
                startActivity(OrderRefundActivity.newIntent(mContext, mOrderId));
            } else if ("确认送达".equals(text)) {
                if (data != null) {
                    // 确认送达
                    confirmReceipt();
                } else {
                    // 提示
                    ToastManager.showShortToast(mContext, "网络加载错误");
                }
            }
        });
    }


    @Override
    public void processLogic() {
        mOrderId = getIntent().getStringExtra(ORDER_ID);
        mCashier = getIntent().getStringExtra(CASHIER);
        orderType = getIntent().getBooleanExtra(ORDER_TYPE, false);
        if (orderType) {
            // 线上
            mOrderDesGoodsAdapter = new OnLineOrderDesGoodsAdapter(mContext, R.layout.adapter_order_des_goods_list_item);
        } else {
            // 线下
            mOrderDesGoodsAdapter = new OrderDesGoodsAdapter(mContext, R.layout.adapter_order_des_goods_list_item);
        }
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mOrderDesGoodsAdapter);
        // 刷新数据
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        printHelper.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printHelper.onDestroy();
    }

    /**
     * 接单弹框
     */
    private void showReceiveOrderDialog(String title) {
        dialog = ZLDialogUtil.showRawDialog(OrderDesActivity.this, title, () -> {
            if ("确认接单".equals(title)) {
                // 接单
                receiveOrder();
            } else {
                dialog.dismiss();
                if (mOrderItemBean != null) {
                    // 获取未打印的数量
                    FloatWindowUtils.getNoOperatingNum(this);
                    OrderPrint();
                }
            }

        });
    }

    /**
     * 修改订单为已打印状态
     */
    private void OrderPrint() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        // 订单id
        stringStringHashMap.put("id", data.getId());
        RequestManager.createRequest(ZLURLConstants.ORDER_PRINT_URL, stringStringHashMap, new OnMyActivityRequestListener<BaseBean>(this) {

            @Override
            public void onSuccess(BaseBean bean) {
                isOperating = true;
                // 打印小票
                printHelper.printGoodsList(OrderDesActivity.this, mOrderItemBean);
                getOnLineOrderDes();
            }
        });
    }

    /**
     * 接单
     */
    private void receiveOrder() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        // 员工id
        stringStringHashMap.put("store_uid", ZhaoLinApplication.getInstance().getLoginUser().getUid());
        // 订单id
        stringStringHashMap.put("id", data.getId());
        RequestManager.createRequest(ZLURLConstants.RECEIVE_ORDER_URL, stringStringHashMap, new OnMyActivityRequestListener<SummitOrderButtonBean>(this) {

            @Override
            public void onSuccess(SummitOrderButtonBean bean) {
                mTuiPriceButton.setText("确认送达");
                getOnLineOrderDes();
                showReceiveOrderDialog("是否打印订单");

            }
        });
    }

    /**
     * 确认送达
     */
    private void confirmReceipt() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        // 员工id
        stringStringHashMap.put("store_uid", ZhaoLinApplication.getInstance().getLoginUser().getUid());
        // 订单id
        stringStringHashMap.put("id", data.getId());
        RequestManager.createRequest(ZLURLConstants.CONFIRM_RECEIPT_URL, stringStringHashMap, new OnMyActivityRequestListener<SummitOrderButtonBean>(this) {

            @Override
            public void onSuccess(SummitOrderButtonBean bean) {
                mTuiPriceButton.setVisibility(View.GONE);
                getOnLineOrderDes();
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        boolean available = NetStateManager.isAvailable(mContext);
        // 设置网络状态，刷新的时候重新获取状态
        setNetStateHint(available);
        // 获取数据
        if (orderType) {
            // 线上订单详情
            getOnLineOrderDes();
        } else {
            // 线下订单详情
            getLocalOrderDes();
        }

    }

    /**
     * 获取线下订单详情
     */
    private void getLocalOrderDes() {
        DaoUtils.getOrderDetail(this, mOrderId, new DaoUtils.OnDaoResultListener<OrderItemBean>() {
            @Override
            public void onSuccess(OrderItemBean bean) {
                mOrderItemBean = bean;
                setLocalOrderData(bean);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 设置本地订单详情数据
     */
    private void setLocalOrderData(OrderItemBean bean) {
        Order order = bean.getOrder();
        List<OrderDetail> orderDetails = bean.getOrderDetails();
        mOrderNumberTextView.setText(String.format("订单号：%s", order.getOrder_id()));
        mStoreNameTextView.setText(String.format("分店：%s", ZLUtils.getStoreTitle()));
        mPosNumberTextView.setText(String.format("收银机编号：%s", ZLUtils.getDevicesNum()));
        mCashierTextView.setText(String.format("收银员：%s", mCashier));
        mPosPeopelNumberTextView.setText(String.format("收银员工号：%s", order.getStore_uid()));
        mTimeTextView.setText(DateUtils.getYearMonthDayHourMinuteSeconds2(order.getAddtime() * 1000));
        int state = bean.getOrder().getState();
        // 如果是线下退款显示其原消费订单号
        if (state != 1)
            mPsAddressTextView.setText(String.format("原消费订单号：%s", order.getSource_order_id()));
        mTitleTextView.setText(state == 1 ? "线下消费" : "线下退款");
        mTuiPriceButton.setText("退款");
        mTuiPriceButton.setVisibility(state == 1 ? View.VISIBLE : View.GONE);
        mPrintOrderButton.setVisibility(View.VISIBLE);
        // 商品数量
        double totalNumber = 0;
        for (int i = 0; i < orderDetails.size(); i++) {
            totalNumber += orderDetails.get(i).getNum();
        }
        mNumberTextView.setText(String.format(bean.getOrder().getState() == 1 ? "数量：%s" : "退货数量：%s", StringUtil.saveThreeDecimal(totalNumber)));
        double discount_pre_price = order.getDiscount_pre_price();// 商品未优惠前的价格
        mDiscountsPriceTextView.setText(String.format("优惠金额：%s", StringUtil.saveTwoDecimal(Math.abs(discount_pre_price - order.getPrice() - order.getMuoling_price()))));
        // 1：支付宝 2：微信 6：现金
        String payTypeStr = "";
        switch (order.getPayment()) {
            case 1:// 支付宝
                payTypeStr = "支付宝";
                break;
            case 2:// 微信
                payTypeStr = "微信";
                break;
            case 6:// 现金
                payTypeStr = "现金";
                break;
        }
        mPayTypeTextView.setText(String.format("支付方式：%s", payTypeStr));
        mTotalPriceTextView.setText(String.format(state == 1 ? "实付金额：%s" : "退款金额：%s", StringUtil.saveTwoDecimal(order.getPrice())));
        mOriginalPriceTextView.setText(String.format("合计：%s", StringUtil.saveTwoDecimal(discount_pre_price)));
        mOrderDesGoodsAdapter.clearAndAddList(orderDetails);
    }

    /**
     * 获取线上订单详情
     */
    private void getOnLineOrderDes() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("id", mOrderId);
        RequestManager.createRequest(ZLURLConstants.ORDER_DETAIL_URL, stringStringHashMap, new OnMyActivityRequestListener<OrderDesBean>(this) {

            @Override
            public void onSuccess(OrderDesBean bean) {
                data = bean.getData();
                setOnLineOrderData(data);
            }
        });
    }

    /**
     * 设置线上订单详情数据
     */
    private void setOnLineOrderData(OrderDesBean.DataBean data) {
        String state = data.getState();
        String stateStr = "";
        if ("2".equals(state)) {
            // 待发货
            stateStr = "待发货";
            mTuiPriceButton.setText("接单");
            mTuiPriceButton.setVisibility(View.VISIBLE);
        } else if ("3".equals(state)) {
            // 配送中（待确认状态，跑到了已完成状态）
            stateStr = "配送中";
            mTuiPriceButton.setText("确认送达");
            mTuiPriceButton.setVisibility(View.VISIBLE);
        } else if ("4".equals(state)) {
            // 4已完成(判读)
//            (finishtime-buytime) -(endtime -buytime)=finishtime-endtime
//            完成状态里面的，已耗时时间判读：(finishtime-addtime) -(endtime -addtime)>0 超时，<0正常耗时;;;简化：finishtime-endtime>0 超时，<0正常耗时
//            配送中的待确认状态：是完成状态4里面区分uidtime，非0：已完成；0：待确认;
            stateStr = "已完成";
        } else if ("6".equals(state)) {
            // 订单退款，细分 store_uid  0为:系统退款;
            if ("0".equals(data.getStore_uid())) {
                // 0为:系统退款;
                stateStr = "系统退款";
            } else {
                // 1正常：已退款
                stateStr = "已退款";
            }


        }
        String sendtime = data.getSendtime();
        if (!TextUtils.isEmpty(sendtime) && !"2".equals(state)) {
            mReceivableTimeTextView.setText(String.format("接单时间：%s", DateUtils.getYearMonthDay(NumberFormatUtils.toLong(sendtime) * 1000, "yyyy/MM/dd HH:mm")));
        }
        String send_username = data.getSend_username();
        if (!TextUtils.isEmpty(send_username)) {
            mPsInfoTextView.setText(String.format("配送人：%s/%s", data.getSend_username(), data.getSend_phone()));
            mPsInfoTextView.setVisibility(View.VISIBLE);
        }

        // 1：已处理 0：未处理
        isOperating = "1".equals(data.getNot_operating());
        mStoreNameTextView.setText(Spans.builder().text("订单状态：").color(0xff282828).text(stateStr).color(isOperating ? 0xff282828 : 0xffff725c).build());
        TextViewUtils.setImageResources(mContext, "2".equals(data.getDelivery()) ? R.mipmap.ic_mdzt : R.mipmap.ic_zlps, Gravity.RIGHT, mStoreNameTextView);

        // 合计金额
        double orderPrice = NumberFormatUtils.toDouble(data.getPrice()) + NumberFormatUtils.toDouble(data.getCoupon_price());
        // 自己打印封装数据
        mOrderItemBean = new OrderItemBean();
        String order_id = data.getId();// 订单id
        int stateInt = "6".equals(data.getState()) ? 2 : 1;// 流水类型： 1：消费 2：退款
        int payment = NumberFormatUtils.toInt(data.getPayment(), 1);// 支付类型：支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
        int order_type = 1;// 订单类型： 1：线上 2：线下
        double prices = NumberFormatUtils.toDouble(data.getPrices());// 原价，是未抹零、未优惠的价格
        double price = NumberFormatUtils.toDouble(data.getPrice());// 订单实际支付金额，是抹零之后的金额
        double pay_price = price;// 付款金额
        double zhaoling_price = 0;// 找零金额
        double muoling_price = 0;// 抹零金额
        long addtime = NumberFormatUtils.toLong(data.getBuytime());// 订单增加时间
        long syn_time = 0;// 同步时间
        String store_uid = data.getStore_uid();// 操作人员id
        String store_id = ZLUtils.getStoreId();// 店铺id
        int upload_state = 1;// 上传状态，0：未上传，1：已上传，2：无库存
        String auth_code = "";// 支付宝、微信支付的码
        double order_discount_pre_price = price + NumberFormatUtils.toDouble(data.getCoupon_price());// 未优惠（优惠前）金额
        String coupon = null;// 优惠券
        String members_code = null;// 会员码
        String source_order_id = "";// 原始订单id，退款订单关联原始订单表

        Order order = new Order(order_id, stateInt, payment, order_type, prices, price, pay_price, zhaoling_price, muoling_price, addtime, syn_time, store_uid, store_id, upload_state, auth_code, order_discount_pre_price, coupon, members_code, source_order_id);
        List<OrderDetail> list = new ArrayList<>();

        // 商品数量
        double totalNumber = 0;
        List<OrderDesBean.DataBean.DetailBean> detail = data.getDetail();
        for (int i = 0; i < detail.size(); i++) {
            OrderDesBean.DataBean.DetailBean detailBean = detail.get(i);
            totalNumber += NumberFormatUtils.toDouble(detailBean.getNum(), 0);
            String title = detailBean.getTitle();// 商品名
            double goods_prices = NumberFormatUtils.toDouble(detailBean.getPrices(), 0);// 商品原价
            double goods_price = NumberFormatUtils.toDouble(detailBean.getPrice(), 0);// 商品卖价
            String code = null;// 商品货号
            long goods_id = NumberFormatUtils.toLong(detailBean.getGoods_id(), 0);// 商品id
            double num = NumberFormatUtils.toDouble(detailBean.getNum());// 商品购买数量，或者重量
            double return_num = NumberFormatUtils.toDouble(detailBean.getNum());// 商品可退货总数
            int standard = 1;// 是否是标品:1：标品；2:非标品
            double discount_pre_price = 0;// 未优惠（优惠前）金额，打印小票不需要此字段
            int return_state = 0;// 退款状态:0:可退；非0不可退，线上订单，不可退款

            OrderDetail orderDetail = new OrderDetail(null,order_id, title, goods_prices, goods_price, code, goods_id, num, return_num, standard, discount_pre_price, return_state);
            list.add(orderDetail);
        }
        mOrderItemBean.setOrderDetails(list);
        mOrderItemBean.setOrder(order);

        mOrderNumberTextView.setText(String.format("收货人：%s", data.getAddress_name()));

        mTitleTextView.setText("6".equals(state) ? "线上退款" : "线上消费");
        mPrintOrderButton.setVisibility("6".equals(state) ? View.GONE : View.VISIBLE);

        String activity_type = data.getActivity_type();
        if ("1".equals(activity_type)) {
            // 预售订单
            mPosNumberTextView.setText(String.format("订单类型：%s", "预售订单"));
        } else if ("2".equals(activity_type)) {
            // 团购订单
            String is_groupon_team = data.getIs_groupon_team();
            mPosNumberTextView.setText(Spans.builder().text("订单类型：团购订单-").
                    text("0".equals(is_groupon_team) ? "未成团" : "已成团").color("0".equals(is_groupon_team) ? 0xffF5A623 : 0xff49C787).build());
            mTuiPriceButton.setVisibility("0".equals(is_groupon_team) ? View.GONE : View.VISIBLE);
        } else {
            // 普通订单
            mPosNumberTextView.setText(String.format("订单类型：%s", "普通订单"));
        }

        mCashierTextView.setText(String.format("电话：%s", data.getAddress_phone()));
        mPosPeopelNumberTextView.setText(String.format("订单号：%s", data.getId()));
        mTimeTextView.setText(String.format("下单时间：%s", DateUtils.getYearMonthDay(NumberFormatUtils.toLong(data.getBuytime()) * 1000, "yyyy/MM/dd HH:mm")));
        mPsAddressTextView.setText(String.format("配送地址：%s", data.getAddress_address()));
        mNumberTextView.setText(String.format("6".equals(data.getState()) ? "退货数量：%s" : "数量：%s", StringUtil.saveThreeDecimal(totalNumber)));
        mDiscountsPriceTextView.setText(String.format("优惠金额：%s", StringUtil.saveTwoDecimal(data.getCoupon_price())));
        //支付方式 1支付宝 2微信 3银联 4钱包 5货到付款
        // 支付信息
        String paymentStr = null;
        switch (data.getPayment()) {
            case "1":
                paymentStr = "支付宝";
                break;
            case "2":
                paymentStr = "微信";
                break;
            case "3":
                paymentStr = "银联";
                break;
            case "4":
                paymentStr = "钱包";
                break;
            case "5":
                paymentStr = "货到付款";
                break;
        }
        mPayTypeTextView.setText(String.format("支付方式：%s", paymentStr));
        mTotalPriceTextView.setText(String.format("6".equals(data.getState()) ? "退款金额：%s" : "实付金额：%s", StringUtil.saveTwoDecimal(data.getPrice())));
        mOriginalPriceTextView.setText(String.format("合计：%s", StringUtil.saveTwoDecimal(orderPrice)));
        mOrderDesGoodsAdapter.clearAndAddList(detail);
    }

}
