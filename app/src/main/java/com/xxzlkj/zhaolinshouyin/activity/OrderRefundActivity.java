package com.xxzlkj.zhaolinshouyin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xxzlkj.zhaolinshare.base.MyRecyclerView;
import com.xxzlkj.zhaolinshare.base.net.OnMyActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.OrderRefundGoodsAdapter;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.model.AddOrderBean;
import com.xxzlkj.zhaolinshouyin.model.OrderItemBean;
import com.xxzlkj.zhaolinshouyin.model.OrderRefundGoods;
import com.xxzlkj.zhaolinshouyin.model.ReturnOrder;
import com.xxzlkj.zhaolinshouyin.model.ReturnOrderDetail;
import com.xxzlkj.zhaolinshouyin.model.ReturnOrderItemBean;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.PrintHelper;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;
import com.zrq.spanbuilder.Spans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 描述:订单退款
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class OrderRefundActivity extends ZLBaseActivity {
    public static final String ORDER_ID = "order_id";
    private Button mUpPageButton;
    private LinearLayout mNoRefundGoodsLayout;
    private MyRecyclerView mReturnableRecyclerView, mNoRefundRecyclerView;
    private ImageView mAllCheckedImageView;
    private TextView mTotalPriceTextView, mSureTextView;
    private RadioGroup mRadioGroup;
    private RadioButton mOtherRadioButton;
    private OrderRefundGoodsAdapter mReturnableAdapter, mNoRefundAdapter;
    private String mOrderId;
    private boolean isAllChecked;
    private OrderItemBean orderItemBean;
    private double rawPrice;
    private Dialog dialog;
    private PrintHelper printHelper = new PrintHelper();// 初始化打印

    /**
     * @param orderId 订单id(必传)
     */
    public static Intent newIntent(Context context, String orderId) {
        Intent intent = new Intent(context, OrderRefundActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        return intent;
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_order_refund);
    }

    @Override
    protected void findViewById() {
        mUpPageButton = getView(R.id.id_up_page);// 上一页
        mNoRefundGoodsLayout = getView(R.id.id_no_refund_goods_layout);// 没有可退商品展示布局
        mReturnableRecyclerView = getView(R.id.id_recycler_view_1);// 可退列表
        mReturnableRecyclerView.setPullRefreshAndLoadingMoreEnabled(false, false);
        mReturnableRecyclerView.getxRecyclerView().setNestedScrollingEnabled(false);
        mReturnableRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mReturnableAdapter = new OrderRefundGoodsAdapter(mContext, this, true, R.layout.item_order_refund_goods_list);
        mReturnableRecyclerView.setAdapter(mReturnableAdapter);
        mNoRefundRecyclerView = getView(R.id.id_recycler_view_2);// 不可退列表
        mNoRefundRecyclerView.setPullRefreshAndLoadingMoreEnabled(false, false);
        mNoRefundRecyclerView.getxRecyclerView().setNestedScrollingEnabled(false);
        mNoRefundRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mNoRefundAdapter = new OrderRefundGoodsAdapter(mContext, this, false, R.layout.item_order_refund_goods_list);
        mReturnableRecyclerView.addHeaderView(getHearView(true));
        mNoRefundRecyclerView.addHeaderView(getHearView(false));
        mNoRefundRecyclerView.setAdapter(mNoRefundAdapter);
        mAllCheckedImageView = getView(R.id.id_all_checked);// 全选
        mTotalPriceTextView = getView(R.id.id_total_price);// 总计
        mSureTextView = getView(R.id.id_sure);// 确定
        mRadioGroup = getView(R.id.id_radio_group);// 单选
        mOtherRadioButton = getView(R.id.id_other_rb);// 支付宝/微信

    }

    @Override
    public void setListener() {
        // 上一页
        mUpPageButton.setOnClickListener(v -> finish());
        // 确定
        mSureTextView.setOnClickListener(v -> {
            if (orderItemBean != null) {
                List<OrderRefundGoods> checkedGoodsList = mReturnableAdapter.getCheckedGoodsList();
                if (checkedGoodsList.size() > 0) {
                    // 有选中商品
                    double price = rawPrice;
                    double muoling_price = 0.00;
                    int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
                    // 只有现金退款抹零，其余都不进行抹零
                    if (checkedRadioButtonId == R.id.id_cash_rb) {
                        // 现金退款进行抹零
                        BigDecimal payMoneyBigDecimal = new BigDecimal(StringUtil.saveTwoDecimal(rawPrice)).setScale(1, BigDecimal.ROUND_DOWN);
                        // 抹零后的实付金额值
                        price = NumberFormatUtils.toDouble(payMoneyBigDecimal.toString());
                        // 抹零金额
                        muoling_price = NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(rawPrice - price));
                    }
                    double finalPrice = NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(price));
                    double finalMuoling_price = muoling_price;
                    // 确认退款弹框
                    dialog = ZLDialogUtil.showOrderRefundDesDialog(OrderRefundActivity.this, checkedGoodsList,
                            price, checkedRadioButtonId == R.id.id_cash_rb ? 6 : orderItemBean.getOrder().getPayment(), () -> submitOrderRefund(dialog, finalPrice, finalMuoling_price));
                } else {
                    // 无选中商品
                    ToastManager.showShortToast(mContext, "请选择退款商品");
                }
            } else
                ToastManager.showShortToast(mContext, "未获取到订单信息，请稍后再试");

        });
        // 全选监听事件
        mAllCheckedImageView.setOnClickListener(v -> {
            if (mReturnableAdapter.getItemCount() > 0) {
                isAllChecked = !isAllChecked;
                List<OrderRefundGoods> list = mReturnableAdapter.getList();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setChecked(isAllChecked);
                }
                mAllCheckedImageView.setImageResource(isAllChecked ? R.mipmap.ic_checked : R.mipmap.ic_check_normal);
                // 计算并设置总计
                mReturnableAdapter.setTotalPrice();
                mReturnableAdapter.notifyDataSetChanged();
            }

        });
        // 可以退款列表选中监听事件
        mReturnableAdapter.setOnIsAllCheckedListener(isAllChecked -> mAllCheckedImageView.setImageResource(isAllChecked ? R.mipmap.ic_checked : R.mipmap.ic_check_normal));
        // 可以退款列表计算总价监听事件
        mReturnableAdapter.OnTotalPriceListener(totalPrice -> {
            this.rawPrice = totalPrice;
            mTotalPriceTextView.setText(String.format("总计：%s", StringUtil.saveTwoDecimal(totalPrice)));
        });

    }


    @Override
    public void processLogic() {
        mOrderId = getIntent().getStringExtra(ORDER_ID);
        // 刷新数据
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        printHelper.onResume();
    }

    /**
     * 提交订单退款
     */
    private void submitOrderRefund(Dialog dialog, double price, double muoling_price) {
        List<OrderRefundGoods> checkedGoodsList = mReturnableAdapter.getCheckedGoodsList();
        if (orderItemBean != null && orderItemBean.getOrder() != null && checkedGoodsList.size() > 0) {
            // 数据ok，检测订单是否上传，未上传，本地处理，上传，请求后台，失败本地
//             upload_state = 0;// 上传状态，0：未上传，1：已上传，2：无库存
            if (orderItemBean.getOrder().getUpload_state() == 1) {
                // 已上传订单，网络退款，失败走本地
                handleOrderRefundByNet(dialog, orderItemBean.getOrder(), checkedGoodsList, price, muoling_price);
            } else {
                // 未上传，或上传失败订单，本地处理
                handleOrderRefundByLocal(dialog, orderItemBean.getOrder(), checkedGoodsList, price, muoling_price);
            }
        }
    }


    /**
     * 本地处理退款订单
     *
     * @param rawOrder         原始订单
     * @param checkedGoodsList 选择的退款列表
     * @param price            退款金额（抹零之后的）
     * @param muoling_price    抹零金额
     */
    private void handleOrderRefundByLocal(Dialog dialog, Order rawOrder, List<OrderRefundGoods> checkedGoodsList, double price, double muoling_price) {
        // 封装退款订单、清单信息
        User loginUser = ZhaoLinApplication.getInstance().getLoginUserDoLogin(this);
        if (loginUser == null || rawOrder == null || checkedGoodsList == null)
            return;
        OrderItemBean orderItemBean = new OrderItemBean();
        // 封装订单
        long currentTimeMillis = System.currentTimeMillis();
        String order_id = DateUtils.getYearMonthDay(currentTimeMillis, "yyyyMMddHHmmss") + String.valueOf((int) ((Math.random() * 9 + 1) * 100000));// 随即生成的订单号,格式：20170321100850_704856
        int state = 2;// 流水类型： 1：消费 2：退款
        int payment = 6;// 支付类型：支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付；本地处理，只有现金退款
        int order_type = 2;// 订单类型： 1：线上 2：线下
        double prices = price + muoling_price;// 原价，是未抹零、未优惠的价格
        double pay_price = price;// 付款金额
        double zhaoling_price = 0;// 找零金额
        long addtime = currentTimeMillis / 1000;// 订单增加时间
        long syn_time = 0;// 同步时间
        String store_uid = loginUser.getUid();// 操作人员id
        String store_id = ZLUtils.getStoreId();// 店铺id
        int upload_state = 0;// 上传状态，0：未上传，1：已上传，2：无库存
        String auth_code = "";// 支付宝、微信支付的码
        double discount_pre_price = price;// 未优惠（优惠前）金额
        String coupon = "";// 优惠券
        String members_code = "";// 会员码
        String source_order_id = rawOrder.getOrder_id();// 原始订单id，退款订单关联原始订单表

        orderItemBean.setOrder(new Order(order_id, state, payment, order_type, prices, price, pay_price, zhaoling_price, muoling_price, addtime, syn_time, store_uid, store_id, upload_state, auth_code, discount_pre_price, coupon, members_code, source_order_id));
        // 封装清单
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderRefundGoods orderRefundGoods : checkedGoodsList) {
            OrderDetail orderDetail = orderRefundGoods.getOrderDetail();
            orderDetails.add(new OrderDetail(null, order_id, orderDetail.getTitle(), orderDetail.getPrices(), orderDetail.getPrice(), orderDetail.getCode(), orderDetail.getGoods_id(), orderRefundGoods.getRefundNumber(), 0, orderDetail.getStandard(), orderDetail.getPrice(), 0));
        }
        orderItemBean.setOrderDetails(orderDetails);
        // 封装生成订单信息
        addOrderRefundAndHandlerSuccess(dialog, orderItemBean);
    }

    /**
     * 网络处理退款订单
     *
     * @param rawOrder         原始订单
     * @param checkedGoodsList 选择的退款列表
     * @param price            退款金额（抹零之后的）
     * @param muoling_price    抹零金额
     */
    private void handleOrderRefundByNet(Dialog dialog, Order rawOrder, List<OrderRefundGoods> checkedGoodsList, double price, double muoling_price) {
        // 封装请求退款信息
        User loginUser = ZhaoLinApplication.getInstance().getLoginUserDoLogin(this);
        if (loginUser == null || rawOrder == null || checkedGoodsList == null)
            return;
        ReturnOrderItemBean returnOrderItemBean = new ReturnOrderItemBean();
        // 封装订单
        String order_id = rawOrder.getOrder_id();// 订单id
        int return_type = mRadioGroup.getCheckedRadioButtonId() == R.id.id_cash_rb ? 1 : 2;// 退款类型：1：现金退款，2：原路返回
        String store_uid = loginUser.getUid();// 操作人员id
        String store_id = ZLUtils.getStoreId();// 店铺id
        ReturnOrder returnOrder = new ReturnOrder(order_id, return_type, price, muoling_price, store_uid, store_id);
        returnOrderItemBean.setOrder(returnOrder);
        // 封装清单
        List<ReturnOrderDetail> orderDetails = new ArrayList<>();
        for (OrderRefundGoods orderRefundGoods : checkedGoodsList) {
            OrderDetail orderDetail = orderRefundGoods.getOrderDetail();
            ReturnOrderDetail returnOrderDetail = new ReturnOrderDetail(orderDetail.getPrice(), orderDetail.getCode(), orderDetail.getGoods_id(), orderRefundGoods.getRefundNumber());
            orderDetails.add(returnOrderDetail);
        }
        returnOrderItemBean.setOrderDetails(orderDetails);
        // 请求网络
        HashMap<String, String> map = new HashMap<>();
        map.put(C.P.JSON, new Gson().toJson(returnOrderItemBean));
        RequestManager.createRequest(ZLURLConstants.ORDER_RETURN_URL, map, new OnMyActivityRequestListener<AddOrderBean>(this) {

            @Override
            public void onSuccess(AddOrderBean bean) {
                // 本地数据库，添加订单退款，并且处理成功后逻辑
                addOrderRefundAndHandlerSuccess(dialog, bean.getData());
            }

            @Override
            public void onFailed(boolean isError, String code, String message) {
//                super.onFailed(isError, code, message);
                // 失败，本地生成
                if (return_type == 1 || rawOrder.getPayment() == 6) {
                    // 现金退款失败，走本地
                    handleOrderRefundByLocal(dialog, rawOrder, checkedGoodsList, price, muoling_price);
                } else {
                    // 三方退款失败，提示
                    String paymentHint = "";// 支付类型：支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
                    switch (rawOrder.getPayment()) {
                        case 1:
                            paymentHint = "支付宝";
                            break;
                        case 2:
                            paymentHint = "微信";
                            break;
                    }
                    ToastManager.showShortToast(mContext, paymentHint + "退款失败，请使用现金退款");
                }
            }
        });
    }

    /**
     * 本地数据库，添加订单，并且处理成功后逻辑
     */
    private void addOrderRefundAndHandlerSuccess(Dialog dialog, OrderItemBean returnOrderItemBean) {
        DaoUtils.addOrderByOrderItemBean(this, returnOrderItemBean, new DaoUtils.OnDaoResultListener<String>() {

            @Override
            public void onSuccess(String bean) {
                // 退款订单添加成功，修改原始清单数量，销毁页面，刷新本页面
                DaoUtils.updateOrderDetailReturnNumByReturnOrderItemBean(OrderRefundActivity.this, returnOrderItemBean, new DaoUtils.OnDaoResultListener<String>() {
                    @Override
                    public void onSuccess(String bean) {
                        // 清空，本页面所有数据
                        dialog.dismiss();
                        // 打印小票
                        printHelper.printGoodsList(OrderRefundActivity.this, returnOrderItemBean);
                        // 刷新
                        refreshData();
                    }

                    @Override
                    public void onFailed() {
                        ToastManager.showShortToast(mContext, "修改清单可退货数量出错");
                    }
                });
            }

            @Override
            public void onFailed() {
                ToastManager.showShortToast(mContext, "添加退款订单出错");
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
        // 归零
        mTotalPriceTextView.setText(String.format("总计：%s", "0.00"));
        // 线下订单详情
        getLocalOrderDes();
    }

    /**
     * 获取线下订单详情
     */
    private void getLocalOrderDes() {
        DaoUtils.getOrderDetail(this, mOrderId, new DaoUtils.OnDaoResultListener<OrderItemBean>() {
            @Override
            public void onSuccess(OrderItemBean bean) {
                orderItemBean = bean;
                // 归原
//                mCashRadioButton.setChecked(true);
                isAllChecked = false;
                mAllCheckedImageView.setImageResource(R.mipmap.ic_check_normal);
                // 支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
                int payment = orderItemBean.getOrder().getPayment();

                mOtherRadioButton.setVisibility((payment == 1 || payment == 2) ? View.VISIBLE : View.GONE);
                if (payment == 1)
                    // 支付宝
                    mOtherRadioButton.setText(Spans.builder().text("支付宝").text("（原路退回）").color(0xffA0A0A0).build());
                if (payment == 2)
                    // 微信
                    mOtherRadioButton.setText(Spans.builder().text("微信").text("（原路退回）").color(0xffA0A0A0).build());
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
        List<OrderDetail> orderDetails = bean.getOrderDetails();
        // 可退列表
        List<OrderRefundGoods> mReturnableList = new ArrayList<>();
        // 不可退列表
        List<OrderRefundGoods> mNoRefundList = new ArrayList<>();
        // 遍历获取可退和不可退商品列表
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail orderDetail = orderDetails.get(i);
            // 构建bean
            OrderRefundGoods orderRefundGoods = new OrderRefundGoods();
            orderRefundGoods.setOrderDetail(orderDetail);
            orderRefundGoods.setRefundNumber(orderDetail.getReturn_num());
            // return_state 退款状态:0:可退；非0不可退
            if (orderDetail.getReturn_state() == 0 && orderDetail.getReturn_num() > 0) {
                // 可退货列表：可退并且退货数量大于0，分配退货数量
                double num = orderDetail.getNum();
                double return_num = orderDetail.getReturn_num();
                if (num == return_num) {
                    // 一件没退，全部是可退货列表
                    mReturnableList.add(orderRefundGoods);
                } else {
                    // 退了一半，两个列表都添加
                    mReturnableList.add(orderRefundGoods);// 可退货列表
                    mNoRefundList.add(orderRefundGoods);// 不可退货列表
                }
            } else {
                // 不可退货列表
                mNoRefundList.add(orderRefundGoods);
            }
        }
        // 设置可退列表、不可退列表、没有可退商品展示布局的显示和隐藏
        mNoRefundGoodsLayout.setVisibility(mReturnableList.size() > 0 ? View.GONE : View.VISIBLE);
        mReturnableRecyclerView.setVisibility(mReturnableList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoRefundRecyclerView.setVisibility(mNoRefundList.size() > 0 ? View.VISIBLE : View.GONE);
        // 设置可退列表、不可退列表数据
        mReturnableAdapter.clearAndAddList(mReturnableList);
        mNoRefundAdapter.clearAndAddList(mNoRefundList);

    }

    /**
     * 头
     */
    private View getHearView(boolean isCanRefund) {
        View mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.item_order_refund_goods_list, null);// 头
        mHeaderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView mNonRefundableGoodsTextView = mHeaderView.findViewById(R.id.id_Non_refundable_goods);// 不可退商品字样
        TextView mRefundNumberTextView = mHeaderView.findViewById(R.id.id_refund_number);// 可退货总数/不可退货总数
        TextView mRefundTitleTextView = mHeaderView.findViewById(R.id.id_refund_title);// 不可退商品字样
        TextView mCheckAllTextView = mHeaderView.findViewById(R.id.id_check_all);// 选择
        TextView mSubtotalTextView = mHeaderView.findViewById(R.id.id_subtotal);// 小计
        mNonRefundableGoodsTextView.setVisibility(isCanRefund ? View.GONE : View.VISIBLE);
        mRefundNumberTextView.setText(isCanRefund ? "可退货总数" : "不可退货总数");
        mRefundTitleTextView.setText(isCanRefund ? "本次退款数量" : "不可退款原因");
        mSubtotalTextView.setText(isCanRefund ? "小计" : "");
        mRefundTitleTextView.setVisibility(View.VISIBLE);
        mCheckAllTextView.setVisibility(View.VISIBLE);
        return mHeaderView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printHelper.onDestroy();
    }
}
