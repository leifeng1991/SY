package com.xxzlkj.zhaolinshouyin.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.suke.widget.SwitchButton;
import com.xxzlkj.zhaolinshare.base.net.OnActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.OnMyActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.TextViewUtils;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.InputGoodsAdapter;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.db.InputGoodsBean;
import com.xxzlkj.zhaolinshouyin.db.Order;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.event.AddGoodsEvent;
import com.xxzlkj.zhaolinshouyin.event.CheckDiscountEvent;
import com.xxzlkj.zhaolinshouyin.event.FinishPayEvent;
import com.xxzlkj.zhaolinshouyin.event.RealDiscountMoneyEvent;
import com.xxzlkj.zhaolinshouyin.event.RefreshViceDisplayListEvent;
import com.xxzlkj.zhaolinshouyin.event.SelectionPositionEvent;
import com.xxzlkj.zhaolinshouyin.event.StopWorkEvent;
import com.xxzlkj.zhaolinshouyin.fragment.InputCodeFragment;
import com.xxzlkj.zhaolinshouyin.fragment.InputSelectFragment;
import com.xxzlkj.zhaolinshouyin.listener.OnPayListener;
import com.xxzlkj.zhaolinshouyin.model.AddOrderBean;
import com.xxzlkj.zhaolinshouyin.model.CheckDiscountBean;
import com.xxzlkj.zhaolinshouyin.model.OrderItemBean;
import com.xxzlkj.zhaolinshouyin.model.ThreeGoodsOrGoodsInfoBean;
import com.xxzlkj.zhaolinshouyin.utils.CashBoxUtils;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.PrintHelper;
import com.xxzlkj.zhaolinshouyin.utils.RecyclerViewHelperListener;
import com.xxzlkj.zhaolinshouyin.utils.WeightHelper;
import com.xxzlkj.zhaolinshouyin.utils.ZLCheckUtils;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;
import com.zrq.spanbuilder.Spans;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class ShouYinActivity extends ZLBaseActivity {
    private InputGoodsAdapter inputGoodsAdapter;
    private TextView btn_switch_input, btn_duizhang, btn_gua_dan, btn_return_goods, btn_weighing,
            btn_edit_delete, btn_edit_price, btn_edit_num, tv_input_code, tv_error_hint,
            tv_order_all_price_down, tv_order_all_num_down, tv_title_all_price, tv_title_prices, tv_title_id,
            btn_pay, tv_up_order_all_price,
            tv_pay_alipay, tv_pay_wechat, tv_order_all_price, tv_order_all_num, tv_up_order_zhaoling, tv_up_order_all_num, tv_up_order_pay_price;
    private RecyclerView inputGoodsRecyclerView;
    private SwitchButton switch_is_clear_list;
    private ImageView iv_down_page, iv_up_order, iv_up_page;
    private ViewGroup vg_button_left_layout, vg_bottom_button_layout, vg_hint_layout, vg_goods_button_layout,
            vg_pay_cash, vg_pay_wechat, vg_pay_alipay, vg_order_all_price_layout, vg_up_order_layout, vg_order_all_num_layout;
    private LinearLayout mInputCodeLayout;
    private InputCodeFragment.OnInputCodeListener onInputCodeListener;
    private String allPriceStr;
    private String allNumStr;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewHelperListener helperListener;
    private InputSelectFragment.OnGoodsClickListener onGoodsClickListener;
    private boolean isShowUpOrderInfo;
    private OnPayListener onPayListener;
    private boolean isAddGoodsBeforeClearList;
    private StringBuffer mStringBufferResult = new StringBuffer();// 扫码内容
    private PrintHelper printHelper = new PrintHelper();// 初始化打印
    private WeightHelper weightHelper = new WeightHelper();// 初始化称重
    private boolean requestPaying;// 是否是请求支付中
    private boolean isNetAvailable;
    private CheckDiscountBean.DataBean checkDiscountBeanData;
    private boolean requestCheckDiscounting;
    long[] mHits = new long[2];

    public static Intent newIntent(Context context) {
        return new Intent(context, ShouYinActivity.class);
    }

    @Override
    protected void loadViewLayout() {
        //设置全屏
        setContentView(R.layout.activity_shouyin);
    }

    @Override
    protected void findViewById() {
        EventBus.getDefault().register(this);
        vg_button_left_layout = getView(R.id.vg_button_left_layout);// 左边的按钮
        iv_up_order = getView(R.id.iv_up_order);// 左边的按钮
        // 上一个订单
        vg_up_order_layout = getView(R.id.vg_up_order_layout);// 上个订单布局
        tv_up_order_all_price = getView(R.id.tv_up_order_all_price);// 上个订单：总价
        tv_up_order_pay_price = getView(R.id.tv_up_order_pay_price);// 上个订单：付款
        tv_up_order_all_num = getView(R.id.tv_up_order_all_num);// 上个订单：数量
        tv_up_order_zhaoling = getView(R.id.tv_up_order_zhaoling);// 上个订单：找零
        // 商品数量：
        vg_order_all_num_layout = getView(R.id.vg_order_all_num_layout);// 商品数量布局
        tv_order_all_num = getView(R.id.tv_order_all_num);// 商品数量
        // 订单金额：
        vg_order_all_price_layout = getView(R.id.vg_order_all_price_layout);// 订单金额布局
        tv_order_all_price = getView(R.id.tv_order_all_price);// 订单金额
        // 按钮：
        vg_pay_alipay = getView(R.id.vg_pay_alipay);// 支付宝支付
        tv_pay_alipay = getView(R.id.tv_pay_alipay);// 支付宝支付
        vg_pay_wechat = getView(R.id.vg_pay_wechat);// 微信支付
        tv_pay_wechat = getView(R.id.tv_pay_wechat);// 微信支付
        vg_pay_cash = getView(R.id.vg_pay_cash);// 现金支付
        btn_pay = getView(R.id.btn_pay);// 结算按钮
        // 录入的商品列表
        tv_title_id = getView(R.id.tv_title_id);// 编号
        tv_title_prices = getView(R.id.tv_title_prices);// 原价
        tv_title_all_price = getView(R.id.tv_title_all_price);// 小计
        inputGoodsRecyclerView = getView(R.id.recyclerView);
        // 商品列表操作提示信息
        vg_goods_button_layout = getView(R.id.vg_goods_button_layout);// 商品按钮布局
        iv_up_page = getView(R.id.iv_up_page);// 上一页
        iv_down_page = getView(R.id.iv_down_page);// 下一页
        switch_is_clear_list = getView(R.id.switch_is_clear_list);// 切换是否删除
        tv_order_all_num_down = getView(R.id.tv_order_all_num_down);// 商品数量
        tv_order_all_price_down = getView(R.id.tv_order_all_price_down);// 订单金额
        // 错误提示
        vg_hint_layout = getView(R.id.vg_hint_layout);// 提示布局
        tv_error_hint = getView(R.id.tv_error_hint);// 错误提示
        tv_input_code = getView(R.id.tv_input_code);// 扫码输入的code
        mInputCodeLayout = getView(R.id.id_input_code_layout);// 扫码输入的code的布局
        // 底部按钮
        vg_bottom_button_layout = getView(R.id.vg_bottom_button_layout);// 底部按钮布局
        btn_edit_num = getView(R.id.btn_edit_num);// 修改数量
        btn_edit_price = getView(R.id.btn_edit_price);// 修改单价
        btn_edit_delete = getView(R.id.btn_edit_delete);// 删除选中

        btn_weighing = getView(R.id.btn_weighing);// 称重
        btn_return_goods = getView(R.id.btn_return_goods);// 退货
        btn_gua_dan = getView(R.id.btn_gua_dan);// 挂单
        btn_duizhang = getView(R.id.btn_duizhang);// 对账
        btn_switch_input = getView(R.id.btn_switch_input);// 切换录入

        // 初始化输入列表
        inputGoodsAdapter = new InputGoodsAdapter(this, R.layout.item_input_goods);
        linearLayoutManager = new LinearLayoutManager(mContext);
        inputGoodsRecyclerView.setLayoutManager(linearLayoutManager);
        helperListener = new RecyclerViewHelperListener(inputGoodsRecyclerView, linearLayoutManager);
        inputGoodsRecyclerView.addOnScrollListener(helperListener);
        closeDefaultAnimator();
        inputGoodsRecyclerView.setAdapter(inputGoodsAdapter);
    }

    @Override
    protected void setListener() {
        // 是否显示上一单信息
        vg_button_left_layout.setOnClickListener(v -> switchShowUpOrderInfo(!isShowUpOrderInfo));
        // 支付宝支付
        vg_pay_alipay.setOnClickListener(v -> showPayDialog(C.I.PAY_TYPE_ALIPAY));
        // 微信支付
        vg_pay_wechat.setOnClickListener(v -> showPayDialog(C.I.PAY_TYPE_WECHAT));
        // 现金支付
        vg_pay_cash.setOnClickListener(v -> showPayDialog(C.I.PAY_TYPE_CASH));
        // 结算
        btn_pay.setOnClickListener(v -> switchShowUpOrderInfo(false));
        // 上一页
        iv_up_page.setOnClickListener(v -> upPage());
        // 下一页
        iv_down_page.setOnClickListener(v -> downPage());
        // 切换提示
        switch_is_clear_list.setOnCheckedChangeListener((view, isChecked) -> setErrorHint(isChecked ? "结账后商品列表不清空" : "结账后商品列表清空"));
        // 修改选中数量
        btn_edit_num.setOnClickListener(v -> editSelectedNum());
        // 修改选中单价
        btn_edit_price.setOnClickListener(v -> editSelectedPrice());
        // 删除选中
        btn_edit_delete.setOnClickListener(v -> deleteSelected());
        // 称重
        btn_weighing.setOnClickListener(v -> weighing());
        // 退货
        btn_return_goods.setOnClickListener(v -> returnGoods());
        // 挂单
        btn_gua_dan.setOnClickListener(v -> entryOrder());
        // 对账
        btn_duizhang.setOnClickListener((View v) -> {
            // 打开钱箱
            CashBoxUtils.openDrawer();
            // 打开对账弹框
            startActivity(AcountCheckDialogActivity.newIntent(mContext, 1, "", 1, 1));
        });
        // 切换录入
        btn_switch_input.setOnClickListener(v -> switchInput());
        // 输入code完成监听
        onInputCodeListener = inputCode -> {
            if (!TextUtils.isEmpty(inputCode)) {
                inputGoodsByInputCode(inputCode);
            }
        };
        // 选择商品完成
        onGoodsClickListener = this::inputGoodsBySelectedGoods;
        // 订单列表item点击监听,设置选中
        inputGoodsAdapter.setOnItemClickListener((position, item) -> {
            // 设置副屏选中位置
            if (!isReturnGoodsIng())
                // 退货时不通知
                EventBus.getDefault().postSticky(new SelectionPositionEvent(position));
            inputGoodsAdapter.setSelectedPositionAndNotifySelected(position);
        });
        // 支付监听
        onPayListener = new OnPayListener() {
            @Override
            public void onPayFinish(Activity activity, int payState, int payType, double paidInMoney, double zhaolingMoney, double muolingMoney, String auth_code) {
                // 支付完成
                payFinish(activity, payState, payType, paidInMoney, zhaolingMoney, muolingMoney, auth_code);
            }

            @Override
            public void onCheckDiscount(boolean isNetAvailable, String coupon, int payment) {
                // 检查优惠
                checkDiscount(isNetAvailable, coupon, payment);
            }
        };
        // 切换全屏
        getView(R.id.vg_goods_title).setOnClickListener(v -> switchGoodsFullShowViewClick());
        // 称重完成，关闭称重，重新计算商品总价
        inputGoodsAdapter.setOnWeightingFinishListener(this::onWeightingFinish);
        // 搜索
        getView(R.id.id_input_code_layout).setOnClickListener(v -> startActivity(SearchGoodsActivity.newIntent(mContext, isHasWeightingGoods())));
    }

    @Override
    public void processLogic() {
        // 设置默认显示上一个订单信息
        switchShowUpOrderInfo(false);
        // 设置上一个订单的信息
        setUpOrderInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        printHelper.onResume();
    }

    @Override
    public void onBackHomeClick(View view) {
        // 商品列表未结账并且商品列表有商品
        if (!isAddGoodsBeforeClearList && inputGoodsAdapter.getList().size() > 0) {
            showGuaDanTipDialog();
            return;
        }
        super.onBackHomeClick(view);
    }

    @Override
    public void finish() {
        // 商品列表未结账并且商品列表有商品
        if (!isAddGoodsBeforeClearList && inputGoodsAdapter.getList().size() > 0) {
            showGuaDanTipDialog();
            return;
        }
        super.finish();
    }

    /**
     * 挂单提示对话框
     */
    private void showGuaDanTipDialog() {
        ZLDialogUtil.showRawDialogTwoButton(this, "有商品未支付，是否挂单？",
                "取消", (dialog, which) -> super.finish(), "确定", (dialog, which) ->
                        DaoUtils.insertOrReplaceGuaDan(inputGoodsAdapter.getList(), new DaoUtils.OnDaoResultListener<String>() {
                            @Override
                            public void onSuccess(String bean) {
                                ShouYinActivity.super.finish();
                            }

                            @Override
                            public void onFailed() {
                                ShouYinActivity.super.finish();
                            }
                        }));
    }

    /**
     * 扫码录入商品
     *
     * @param barCodeStr 条形码
     */
    private void inputGoodsByScanCode(String barCodeStr) {
        // 检查是否清空列表
        checkIsClearList();

        if (barCodeStr != null && barCodeStr.length() >= 17) {
            // 22 65001 00030 00184（2 + 5,6 + 5 + 5 = 17,18）
            int goodsCodeLength = barCodeStr.length() - 12;
            String goodsCode = barCodeStr.substring(2, goodsCodeLength + 2);// 货号
            String priceStr = barCodeStr.substring(goodsCodeLength + 2, goodsCodeLength + 7);// 单价
            String weightStr = barCodeStr.substring(goodsCodeLength + 7);// 重量,最后一位不要，小票上的规则

            double price = NumberFormatUtils.toLong(priceStr) / 100.0;// 单价（单位：元/kg，最大值为：999.99）
            double weight = NumberFormatUtils.toLong(weightStr) / 1000.0;// 重量（单位：kg，最大值为：99.999）
            double allPrice = NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(price * weight));// 总价（单位：元，保留两位小数）
            // 录入商品，通过码信息
            inputGoodsByCodeInfo(goodsCode, price, weight, allPrice);
        } else if (barCodeStr != null && barCodeStr.length() <= 6) {
            // 录入的非标品货号，通过货号
            inputGoodsByInputCode(barCodeStr);
        } else {
            // 录入商品，通过条形码
            inputGoodsByBarCode(barCodeStr);
        }
    }

    /**
     * 输入货号录入商品
     *
     * @param goodsCode 货号
     */
    private void inputGoodsByInputCode(String goodsCode) {
        // 检查是否清空列表
        checkIsClearList();

        DaoUtils.getGoodsByGoodsCode(this, goodsCode, new DaoUtils.OnDaoResultListener<Goods>() {
            @Override
            public void onSuccess(Goods bean) {
                // 添加一个商品，到录入的商品列表
                if (!ZLCheckUtils.checkHasNoCanBuyGoods(ShouYinActivity.this, bean)) {
                    // 商品可销售，不可销售已提示
                    addGoodsToListByGoods(bean);
                }
            }

            @Override
            public void onFailed() {
                showNoGoodsDialog(goodsCode);
            }
        });
    }

    /**
     * 选择录入商品
     *
     * @param goods 商品
     */
    private void inputGoodsBySelectedGoods(Goods goods) {
        // 检查是否清空列表
        checkIsClearList();
        if (!ZLCheckUtils.checkHasNoCanBuyGoods(ShouYinActivity.this, goods)) {
            // 商品可销售，不可销售已提示
            addGoodsToListByGoods(goods);
        }
    }

    /**
     * 录入商品，通过码信息
     *
     * @param goodsCode 货号
     * @param price     单价
     * @param weight    重量
     * @param allPrice  总价
     */
    private void inputGoodsByCodeInfo(String goodsCode, double price, double weight, double allPrice) {
        // 添加商品信息
        DaoUtils.getThreeGoodsOrGoodsInfoByGoodsCode(this, goodsCode, new DaoUtils.OnDaoResultListener<ThreeGoodsOrGoodsInfoBean>() {
            @Override
            public void onSuccess(ThreeGoodsOrGoodsInfoBean bean) {
                // 添加一个商品，到录入的商品列表
                if (!ZLCheckUtils.checkHasNoCanBuyGoodsByScanCode(ShouYinActivity.this, bean)) {
                    // 商品可销售，不可销售已提示
                    // 添加商品，信息优先用码的信息
                    addInputGoodsToList(new InputGoodsBean(null, null, bean.getStandard(), bean.getId(), goodsCode, bean.getTitle(), price, price, allPrice, weight, false));
                }
            }

            @Override
            public void onFailed() {
                showNoGoodsDialog(goodsCode);
            }
        });
    }

    /**
     * 录入商品，通过条形码
     *
     * @param barCodeStr 条形码
     */
    private void inputGoodsByBarCode(String barCodeStr) {
        // 查询本地条形码表，获取商品货号
        // 获取货号
        DaoUtils.getGoodsCodeByCarCode(this, barCodeStr, new DaoUtils.OnDaoResultListener<String>() {
            @Override
            public void onSuccess(String goodsCode) {
                // 获取商品货号成功，获取商品
                DaoUtils.getGoodsByGoodsCode(ShouYinActivity.this, goodsCode, new DaoUtils.OnDaoResultListener<Goods>() {
                    @Override
                    public void onSuccess(Goods bean) {
                        // 获取商品成功
                        // 添加一个商品，到录入的商品列表
                        if (!ZLCheckUtils.checkHasNoCanBuyGoods(ShouYinActivity.this, bean)) {
                            // 商品可销售，不可销售已提示
                            addGoodsToListByGoods(bean);
                        }
                    }

                    @Override
                    public void onFailed() {
                        // 获取商品失败，提示
                        showNoGoodsDialog(goodsCode);
                    }
                });
            }

            @Override
            public void onFailed() {
                // 获取商品货号失败，提示
                showNoGoodsDialog(barCodeStr);
            }
        });
    }

    /**
     * 添加一个商品，到录入的商品列表
     *
     * @param addGoods 要添加的商品
     */
    private void addGoodsToListByGoods(Goods addGoods) {
        // 添加商品
        String addGoodCode = addGoods.getCode();
        double addGoodsPrice = addGoods.getPrice();
        List<InputGoodsBean> list = inputGoodsAdapter.getList();
        if (!TextUtils.isEmpty(addGoodCode) && list.size() > 0 && list.get(list.size() - 1).getStandard() == 1
                && addGoodCode.equals(list.get(list.size() - 1).getCode()) && addGoodsPrice == list.get(list.size() - 1).getPrice()) {
            // 最后一个商品，是标品，并且与此商品货号相同，并且价格相同，数量加一
            InputGoodsBean lastInputGoodsBean = list.get(list.size() - 1);
            lastInputGoodsBean.setNum(lastInputGoodsBean.getNum() + 1);
            lastInputGoodsBean.setAllPrices(lastInputGoodsBean.getAllPrices() + lastInputGoodsBean.getPrice());
            // 通知
            inputGoodsAdapter.setSelectedPositionAndNotifySelected(inputGoodsAdapter.getItemCount() - 1);// 设置选中位置
            // 设置订单总数量，总金额
            setOrderAllNumAndAllPrice();
        } else {
            // 之前没有此商品，添加，标品数量为1，非标品数量为0
            double num = addGoods.getStandard() == 1 ? 1 : 0;// 商品数量，或者重量
            addInputGoodsToList(new InputGoodsBean(null, null, addGoods.getStandard(), addGoods.getId(), addGoods.getCode(), addGoods.getTitle(), addGoods.getPrice(), addGoods.getPrice(), addGoods.getPrice() * num, num, false));
        }
    }

    /**
     * 添加一个商品，到录入的商品列表
     */
    private void addInputGoodsToList(InputGoodsBean inputGoodsBean) {
        // 检测是否有未完成称重的商品
        if (checkIsHasNoFinishWeightGoods()) {
            setErrorHint("请完成称重商品");
            return;
        }

        // 设置称重中的商品为称重完成
        setWeightingGoodsFinished();
        // 添加商品
        inputGoodsAdapter.addItemAtLast(inputGoodsBean);
        // 设置选中
        inputGoodsAdapter.setSelectedPositionAndNotifySelected(inputGoodsAdapter.getItemCount() - 1);// 设置选中位置
        // 移动到底部
        rvMoveToBottom();
        // 设置订单总数量，总金额
        setOrderAllNumAndAllPrice();
    }

    /**
     * 检测优惠金额
     *
     * @param isNetAvailable 当前网络是否可用，可用，则一直只用此网络，否则一直使用本地
     * @param coupon         优惠券
     * @param payment        支付类型：支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
     */
    private void checkDiscount(boolean isNetAvailable, String coupon, int payment) {
        if (isReturnGoodsIng()) return;// 退款不检测优惠
        if (coupon == null) coupon = "";
        this.isNetAvailable = isNetAvailable;
        // 设置前屏显示
        BigDecimal payMoneyBigDecimal = new BigDecimal(StringUtil.saveTwoDecimal(allPriceStr)).setScale(1, BigDecimal.ROUND_DOWN);
        double payMoney = NumberFormatUtils.toDouble(payMoneyBigDecimal.toString());// 抹零之后的金额
        EventBus.getDefault().postSticky(new RealDiscountMoneyEvent(payMoney, 0));// 通知副屏处理

        if (isNetAvailable) {
            // 有网络，全部使用网络生成
            // 生成订单信息
            OrderItemBean orderItemBean = new OrderItemBean();
            // 封装订单
            User loginUser = ZhaoLinApplication.getInstance().getLoginUserDoLogin(this);
            String store_id = ZLUtils.getStoreIdDoInit(this);
            if (loginUser == null || TextUtils.isEmpty(store_id)) {
                return;
            }
            orderItemBean.setOrder(new Order("", 0, payment, 2, 0, 0, 0, 0, 0, 0, 0, loginUser.getUid(), store_id, 0, "", 0, coupon, "", ""));
            // 封装清单
            List<OrderDetail> orderDetails = new ArrayList<>();
            List<InputGoodsBean> list = inputGoodsAdapter.getList();
            for (InputGoodsBean inputGoodsBean : list) {
                orderDetails.add(new OrderDetail(null, null, inputGoodsBean.getTitle(), inputGoodsBean.getPrices(), inputGoodsBean.getPrice(), inputGoodsBean.getCode(), inputGoodsBean.getId(), inputGoodsBean.getNum(), inputGoodsBean.getNum(), inputGoodsBean.getStandard(), inputGoodsBean.getPrice(), 0));
            }
            orderItemBean.setOrderDetails(orderDetails);

            HashMap<String, String> map = new HashMap<>();
            map.put(C.P.JSON, new Gson().toJson(orderItemBean));
            RequestManager.createRequest(ZLURLConstants.CHECK_ORDER_URL, map, new OnMyActivityRequestListener<CheckDiscountBean>(this) {
                @Override
                public void handlerStart() {
                    super.handlerStart();
                    requestCheckDiscounting = true;// 请求检测优惠
                }

                @Override
                public void handlerEnd() {
                    super.handlerEnd();
                    requestCheckDiscounting = false;// 请求检测优惠
                }

                @Override
                public void onSuccess(CheckDiscountBean bean) {
                    checkDiscountBeanData = bean.getData();
                    EventBus.getDefault().postSticky(new CheckDiscountEvent(bean));// 通知收银弹框处理
                    EventBus.getDefault().postSticky(new RealDiscountMoneyEvent(checkDiscountBeanData.getPrice(), checkDiscountBeanData.getDiscount_price()));// 通知副屏处理
                }
            });
        } else {
            // 没网，优惠券不处理，会员处理，会员目前没有做
            if (!TextUtils.isEmpty(coupon)) {
                // 使用了优惠券，提示不能使用优惠券
                ToastManager.showShortToast(mContext, "无网络，不能使用优惠券");
            }
        }
    }

    /**
     * 支付完成
     *
     * @param payState      支付状态: 1：支付 2：退款
     * @param payType       支付类型: 1：支付宝 2：微信 6：现金
     * @param paidInMoney   实收金额
     * @param zhaolingMoney 找零金额
     * @param muolingMoney  抹零金额
     * @param authCode      （选传）微信、支付宝支付时的码,，支付宝、微信支付必传
     */
    private void payFinish(Activity dialogActivity, int payState, int payType, double paidInMoney, double zhaolingMoney, double muolingMoney, String authCode) {
        // 封装成一个订单、清单集合，用于请求接口、生成订单
        if (TextUtils.isEmpty(authCode)) authCode = "0";// 支付码，必传
        double rawPrice = Math.abs(NumberFormatUtils.toDouble(allPriceStr));// 原价，未加工的价格
        double allPrice = rawPrice - muolingMoney;// 订单实际金额
        User loginUser = ZhaoLinApplication.getInstance().getLoginUserDoLogin(this);
        String storeIdStr = ZLUtils.getStoreIdDoInit(this);
        if (loginUser == null || TextUtils.isEmpty(storeIdStr)) {
            return;
        }
        // 生成订单信息
        OrderItemBean orderItemBean = new OrderItemBean();
        // 封装订单（默认本地，网络的由后台封装好后返回）
        long currentTimeMillis = System.currentTimeMillis();
        String order_id = DateUtils.getYearMonthDay(currentTimeMillis, "yyyyMMddHHmmss") + String.valueOf((int) ((Math.random() * 9 + 1) * 100000));// 随即生成的订单号,格式：20170321100850_704856
        int state = payState;// 流水类型： 1：消费 2：退款
        int payment = payType;// 支付类型：支付方式 1支付宝 2微信 3银联 4钱包 5货到付款 6现金支付
        int order_type = 2;// 订单类型： 1：线上 2：线下
        double prices = rawPrice;// 原价，是未抹零、未优惠的价格
        double price = allPrice;// 订单实际支付金额，是抹零之后的金额  TODO 后期加会员
        double pay_price = paidInMoney;// 付款金额
        double zhaoling_price = zhaolingMoney;// 找零金额
        double muoling_price = muolingMoney;// 抹零金额
        long addtime = currentTimeMillis / 1000;// 订单增加时间
        long syn_time = 0;// 同步时间
        String store_uid = loginUser.getUid();// 操作人员id
        String store_id = storeIdStr;// 店铺id
        int upload_state = 0;// 上传状态，0：未上传，1：已上传，2：无库存
        String auth_code = authCode;// 支付宝、微信支付的码
        double discount_pre_price = price;// 未优惠（优惠前）金额，因为没加会员，所以相等实际支付金额
        String coupon = "";// 优惠券
        String members_code = "";// 会员码 TODO 后期加会员
        String source_order_id = "";// 原始订单id，退款订单关联原始订单表

        if (isNetAvailable) {
            // 全部网络，必须等优惠检测完成后，才能请求生成订单
            if (requestCheckDiscounting) {
                // 检测优惠中，返回
                ToastManager.showShortToast(mContext, "检测是否优惠中，请稍后再试");
                return;
            } else {
                // 检测优惠完成
                if (checkDiscountBeanData != null) {
                    // 有优惠，添加优惠券
                    coupon = checkDiscountBeanData.getCoupon();// 优惠券
                }
            }
        } else {
            // 全部本地，数据已由上面默认处理
        }
        orderItemBean.setOrder(new Order(order_id, state, payment, order_type, prices, price, pay_price, zhaoling_price, muoling_price, addtime, syn_time, store_uid, store_id, upload_state, auth_code, discount_pre_price, coupon, members_code, source_order_id));
        // 封装清单
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<InputGoodsBean> list = inputGoodsAdapter.getList();
        for (InputGoodsBean inputGoodsBean : list) {
            orderDetails.add(new OrderDetail(null, order_id, inputGoodsBean.getTitle(), inputGoodsBean.getPrices(), inputGoodsBean.getPrice(), inputGoodsBean.getCode(), inputGoodsBean.getId(), inputGoodsBean.getNum(), inputGoodsBean.getNum(), inputGoodsBean.getStandard(), inputGoodsBean.getPrice(), 0));
        }
        orderItemBean.setOrderDetails(orderDetails);

        // 操作订单，支付、退款，订单状态已处理
        if (payState == C.I.PAY_STATE_PAY)
            // 支付
            handlerPay(dialogActivity, payType, orderItemBean);
        else if (payState == C.I.PAY_STATE_RETURN)
            // 退款，判断是否有未上传的订单
            DaoUtils.checkPayOrderHasNoUpload(this, hasNoUploadPayOrder -> handlerReturn(dialogActivity, hasNoUploadPayOrder, payType, orderItemBean));
    }

    /**
     * 处理支付
     */
    private void handlerPay(Activity dialogActivity, int payType, OrderItemBean mOrderItemBean) {
        // 请求支付中，不能再提交请求
        if (requestPaying) {
            // 请求支付中，不能再提交请求
            ToastManager.showShortToast(mContext, "请求支付中，不能再次提交支付");
            return;
        }

        if (isNetAvailable) {
            // 有网，全部网络
            HashMap<String, String> map = new HashMap<>();
            map.put(C.P.JSON, new Gson().toJson(mOrderItemBean));
            RequestManager.createRequest(ZLURLConstants.ADD_ORDER_URL, map, new OnMyActivityRequestListener<AddOrderBean>(this) {
                @Override
                public void handlerStart() {
                    super.handlerStart();
                    requestPaying = true;// 请求支付中
                }

                @Override
                public void handlerEnd() {
                    super.handlerEnd();
                    requestPaying = false;// 请求支付中
                }

                @Override
                public void onSuccess(AddOrderBean bean) {
                    // 任何支付方式，只有200才是成功，其它提示
                    // 本地数据库，添加订单，并且处理成功后逻辑
                    addOrderLocalAndHandlerSuccess(bean.getData(), dialogActivity);
                }

            });
        } else {
            // 无网，走本地的
            if (ZLCheckUtils.checkHasNoCanBuyGoodsByPay(this, mOrderItemBean)) {
                // 有不可销售商品
                dialogActivity.finish();
                return;
            }
            // 现金支付，失败走本地，三方支付，失败提示原因
            if (payType == C.I.PAY_TYPE_CASH)
                // 本地数据库，添加订单，并且处理成功后逻辑
                addOrderLocalAndHandlerSuccess(mOrderItemBean, dialogActivity);
            else
                // 失败，提示
                ToastManager.showShortToast(mContext, "没有网络,只支持现金支付");
        }
    }

    /**
     * 处理退款，检查支付状态的订单是否有未上传的状态，有的话走本地退款
     */
    private void handlerReturn(Activity dialogActivity, Boolean hasNoUploadPayOrder, int payType, OrderItemBean orderItemBean) {
        if (!hasNoUploadPayOrder) {
            // 没有未上传的订单，把订单信息请求给后台
            HashMap<String, String> map = new HashMap<>();
            map.put(C.P.JSON, new Gson().toJson(orderItemBean));
            RequestManager.createRequest(ZLURLConstants.ADD_RETURN_ORDER_URL, map, new OnActivityRequestListener<AddOrderBean>(this) {
                @Override
                public void handlerSuccess(AddOrderBean bean) {
                    // 后台生成订单成功，本地保存
                    if (payType == C.I.PAY_TYPE_CASH) {
                        // 现金支付，成功走网络，失败走本地，都会生成订单
                        OrderItemBean addOrderItemBean;
                        if ("200".equals(bean.getCode())) {
                            // 成功，已封装好
                            addOrderItemBean = bean.getData();
                        } else {
                            // 400等原因，状态未上传
                            orderItemBean.getOrder().setUpload_state(0);
                            addOrderItemBean = orderItemBean;
                        }
                        // 本地数据库，添加订单，并且处理成功后逻辑
                        addOrderLocalAndHandlerSuccess(addOrderItemBean, dialogActivity);
                    } else {
                        // 三方支付，成功走网络，失败不处理
                        if ("200".equals(bean.getCode())) {
                            // 成功，已封装好
                            // 本地数据库，添加订单，并且处理成功后逻辑
                            addOrderLocalAndHandlerSuccess(bean.getData(), dialogActivity);
                        } else {
                            // 失败，显示失败信息
                            ToastManager.showShortToast(mContext, bean.getMessage());
                        }
                    }
                }

                @Override
                public void handlerError(int errorCode, String errorMessage) {
                    // 现金支付，失败走本地，三方支付，失败提示原因
                    if (payType == C.I.PAY_TYPE_CASH)
                        // 本地数据库，添加订单，并且处理成功后逻辑
                        addOrderLocalAndHandlerSuccess(orderItemBean, dialogActivity);
                    else
                        // 失败，提示
                        ToastManager.showErrorToast(mContext, errorCode, errorMessage);
                }

            });
        } else {
            ToastManager.showShortToast(mContext, "本地有未上传的消费订单，不能退款");
        }
    }

    /**
     * 本地数据库，添加订单，并且处理成功后逻辑
     */
    private void addOrderLocalAndHandlerSuccess(OrderItemBean addOrderItemBean, Activity dialogActivity) {
        DaoUtils.addOrderByOrderItemBean(ShouYinActivity.this, addOrderItemBean, new DaoUtils.OnDaoResultListener<String>() {

            @Override
            public void onSuccess(String bean) {
                // 清空，本页面所有数据
                dialogActivity.finish();
                // 设置上一单信息
                setUpOrderInfo();
                if (!switch_is_clear_list.isChecked()) {
                    // 关闭代表清除
                    // 清空列表
                    inputGoodsAdapter.clear();
                    // 清空总额
                    setOrderAllNumAndAllPrice();
                } else {
                    // 不打开代表不清除
                    isAddGoodsBeforeClearList = true;
                }
                // 打印小票
                printHelper.printGoodsList(ShouYinActivity.this, addOrderItemBean);
                // 支付完成 退货时不通知
                if (!isReturnGoodsIng()) {
                    // 支付完成通知
                    EventBus.getDefault().postSticky(new FinishPayEvent());
//                    EventBus.getDefault().postSticky(new ClosePayEvent(true));
                }
            }

            @Override
            public void onFailed() {
                ToastManager.showShortToast(mContext, "添加订单出错");
            }
        });
    }

    /**
     * 设置订单总数量，总金额
     */
    private void setOrderAllNumAndAllPrice() {
        List<InputGoodsBean> list = inputGoodsAdapter.getList();
        double allPrice = 0;
        double allNum = 0;
        boolean returnGoodsing = isReturnGoodsIng();
        for (InputGoodsBean inputGoodsBean : list) {
            if (inputGoodsBean.isWeighing())
                // 称重中的商品，不计算
                continue;
            // 设置价格
            if (returnGoodsing)
                // 退货中，金额显示－XXX
                allPrice -= inputGoodsBean.getAllPrices();
            else
                // 购买中，金额显示XXX
                allPrice += inputGoodsBean.getAllPrices();
            // 设置数量
            if (inputGoodsBean.getStandard() == 1) {//  标品:1 非标:2
                // 标品，数量加num
                allNum += inputGoodsBean.getNum();
            } else {
                // 非标品，数量加1
                allNum += 1;
            }
        }
        // 总数量
        allNumStr = StringUtil.saveInt(allNum);
        tv_order_all_num.setText(allNumStr);
        tv_order_all_num_down.setText(allNumStr);
        // 总金额
        allPriceStr = StringUtil.saveTwoDecimal(allPrice);
        if (allPriceStr == null)
            allPriceStr = "";
        tv_order_all_price.setText(Spans.builder().text("￥").size(24).text(allPriceStr).build());
        tv_order_all_price_down.setText(Spans.builder().text("￥").size(16).text(allPriceStr).build());
        // 刷新副屏幕列表显示
        sendRefreshViceDisplayListEvent();
    }


    /**
     * 展示没有商品的dialog
     *
     * @param goodsCode 货号,或条形码号
     */
    private void showNoGoodsDialog(String goodsCode) {
        ZLDialogUtil.showNoGoodsDialog(this, goodsCode, "商品库无此商品！请检查货号/条码是否正确");
    }


    /**
     * 下一页
     */
    private void downPage() {
        int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        helperListener.scrollToPosition(lastCompletelyVisibleItemPosition + 1);
    }

    /**
     * 上一页
     */
    private void upPage() {
        int firstCompletelyVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int i = lastCompletelyVisibleItemPosition - firstCompletelyVisibleItemPosition;
        linearLayoutManager.scrollToPositionWithOffset(firstCompletelyVisibleItemPosition - i > 0 ? firstCompletelyVisibleItemPosition - i - 1 : 0, 0);
    }

    /**
     * 修改选中数量
     */
    private void editSelectedNum() {
        int selectedPosition = inputGoodsAdapter.getSelectedPosition();
        if (selectedPosition < inputGoodsAdapter.getItemCount()) {
            // 操作正常
            // 要修改的数据
            InputGoodsBean inputGoodsBean = inputGoodsAdapter.getList().get(selectedPosition);
            if (DaoUtils.isThreeGoods(inputGoodsBean.getCode())) {
                // 三方商品，不能修改数量
                setErrorHint("三方商品，不能修改数量");
                return;
            }
            ZLDialogUtil.showNumberKeyboard(this, 2, inputGoodsBean.getStandard() == 1, inputCode -> {
                // 修改数据
                inputGoodsBean.setNum(NumberFormatUtils.toDouble(StringUtil.saveThreeDecimal(inputCode)));
                inputGoodsBean.setAllPrices(NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(inputGoodsBean.getPrice() * inputGoodsBean.getNum())));// 设置总价
                // 通知
                inputGoodsAdapter.notifyItemChanged(selectedPosition);
                // 设置订单总数量，总金额
                setOrderAllNumAndAllPrice();
            });
        }
    }

    /**
     * 修改选中价格
     */
    private void editSelectedPrice() {
        int selectedPosition = inputGoodsAdapter.getSelectedPosition();
        if (selectedPosition < inputGoodsAdapter.getItemCount()) {
            // 操作正常
            // 要修改的数据
            InputGoodsBean inputGoodsBean = inputGoodsAdapter.getList().get(selectedPosition);
            if (DaoUtils.isThreeGoods(inputGoodsBean.getCode())) {
                // 三方商品，不能修改价格
                setErrorHint("三方商品，不能修改价格");
                return;
            }
            //    标品:1 非标:2
            if (inputGoodsBean.getStandard() == 2 || isReturnGoodsIng()) {
                // 非标品、退货中，可修改价格
                ZLDialogUtil.showNumberKeyboard(this, 1, inputGoodsBean.getStandard() == 1, inputCode -> {
                    // 修改数据
                    inputGoodsBean.setPrice(NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(inputCode)));// 设置单价
                    inputGoodsBean.setAllPrices(NumberFormatUtils.toDouble(StringUtil.saveTwoDecimal(inputGoodsBean.getPrice() * inputGoodsBean.getNum())));// 设置总价
                    // 通知
                    inputGoodsAdapter.notifyItemChanged(selectedPosition);
                    // 设置订单总数量，总金额
                    setOrderAllNumAndAllPrice();
                });
            } else {
                // 标品，不能修改价格
                setErrorHint("标准品不可修改价格");
            }
        }
    }

    /**
     * 删除选中
     */
    private void deleteSelected() {
        int selectedPosition = inputGoodsAdapter.getSelectedPosition();
        if (inputGoodsAdapter.getItemCount() > 0 && inputGoodsAdapter.getItemCount() > selectedPosition) {
            // 有条目可删除
            // 如果删除的是称重的商品，则关闭称重
            if (inputGoodsAdapter.getList().get(selectedPosition).getIsWeighing()) {
                // 删除的是称重的商品，则关闭称重
                weightHelper.closeWeight();
            }
            // 删除
            inputGoodsAdapter.deleteItem(selectedPosition);
            // 通知选中
            int lastPosition = inputGoodsAdapter.getItemCount() - 1;
            if (selectedPosition > lastPosition && lastPosition >= 0) {
                // 选中的大于最后一个，选中的为最后一个
                inputGoodsAdapter.setSelectedPositionAndNotifySelected(lastPosition);
            }
            // 重新设置订单总数量，总金额
            setOrderAllNumAndAllPrice();
        }
    }

    /**
     * 称重
     */
    private void weighing() {
        // 获取选择商品，判断是否是非标品，是，可以称重，否，提示不可称重
        int selectedPosition = inputGoodsAdapter.getSelectedPosition();
        if (inputGoodsAdapter.getItemCount() > 0 && inputGoodsAdapter.getItemCount() > selectedPosition) {
            InputGoodsBean selectedInputGoodsBean = inputGoodsAdapter.getList().get(selectedPosition);
            if (DaoUtils.isThreeGoods(selectedInputGoodsBean.getCode())) {
                // 三方商品，不能修改价格
                setErrorHint("三方商品，不能称重");
                return;
            }
            if (selectedInputGoodsBean.getStandard() == 2) {
                // 选中的是非标品，设置称重中，称重修改数量
                selectedInputGoodsBean.setWeighing(true);
                inputGoodsAdapter.notifyItemChanged(selectedPosition);
                // 设置称重改变监听
                weightHelper.openWeight(weightInfo -> {
                    // 有数据，并且最后一个为称重商品，获取最后一条数据，然后修改重量，通知
                    selectedInputGoodsBean.setNum(NumberFormatUtils.toDouble(StringUtil.saveThreeDecimal(weightInfo.netWeight)));// 设置数量
                    selectedInputGoodsBean.setAllPrices(selectedInputGoodsBean.getNum() * selectedInputGoodsBean.getPrice());// 设置小计
                    inputGoodsAdapter.notifyItemChanged(selectedPosition);
                });
            } else {
                // 选中的是标品，提示
                setErrorHint("标准品不可称重");
            }
        } else
            setErrorHint("请选择称重商品");
    }

    /**
     * 退货
     */
    private void returnGoods() {
        // 订单退货，跳到订单管理
        startActivity(OrderListActivity.newIntent(mContext, 2));
//        String buttonStr = btn_return_goods.getText().toString().trim();
//        if ("退货".equals(buttonStr)) {
//            // 退货，选择退货方式
//            ZLDialogUtil.showSelectSalesTypeDialog(this, position -> {
//                if (position == 1) {
//                    // 单品退货，清空商品列表、价格、支付宝、微信支付按钮不可点击
//                    // 清空列表
//                    inputGoodsAdapter.clear();
//                    // 清空总额
//                    setOrderAllNumAndAllPrice();
//                    // 设置微信、支付宝按钮
//                    setAlipayAndWechatPayEnable(true);
//                    // 修改状态
//                    btn_return_goods.setText("取消");
//                } else if (position == 2) {
//                    // 订单退货，跳到订单管理
//                    startActivity(OrderListActivity.newIntent(mContext, 2));
//                }
//            });
//        } else if ("取消".equals(buttonStr)) {
//            // 取消，弹框确认
//            ZLDialogUtil.showConfigReturnGoodsDialog(this, () -> {
//                // 清空列表
//                inputGoodsAdapter.clear();
//                // 清空总额
//                setOrderAllNumAndAllPrice();
//                // 设置微信、支付宝按钮
//                setAlipayAndWechatPayEnable(false);
//                // 修改状态
//                btn_return_goods.setText("退货");
//            });
//        }
    }

    /**
     * 挂单
     */
    private void entryOrder() {
        if (isHasWeightingGoods()) {
            // 有称重中的商品
            setErrorHint("请确认称重完成");
        } else {
            List<InputGoodsBean> list = inputGoodsAdapter.getList();
            // 创建一个新的集合 把list添加进去
            List<InputGoodsBean> newList = new ArrayList<>();
            newList.addAll(list);
            // 清空商品
            inputGoodsAdapter.clear();
            // 设置订单总数量，总金额
            setOrderAllNumAndAllPrice();
            // 挂单弹框
            ZLDialogUtil.showEntryOrdersDialog(this, newList, inputGoodsBeanList -> {
                // 提取成功，重置列表、价格
                inputGoodsAdapter.setSelectedPosition(0);
                inputGoodsAdapter.clearAndAddList(ZLCheckUtils.entryOrderFilterHasCanBuyGoods(ShouYinActivity.this, inputGoodsBeanList));
                // 设置订单总数量，总金额
                setOrderAllNumAndAllPrice();
            });
        }
    }

    /**
     * 设置微信、支付宝支付，不能点击的样式
     *
     * @param isShowGray 是否是展示灰色
     */
    @SuppressLint("RtlHardcoded")
    private void setAlipayAndWechatPayEnable(boolean isShowGray) {
        // 支付宝支付
        tv_pay_alipay.setTextColor(ContextCompat.getColor(mContext, isShowGray ? R.color.gray_e3e3e3 : R.color.black_282828));
        TextViewUtils.setImageResources(mContext, isShowGray ? R.mipmap.ic_pay_alipay_gray : R.mipmap.ic_pay_alipay, Gravity.LEFT, tv_pay_alipay);
        // 微信支付
        tv_pay_wechat.setTextColor(ContextCompat.getColor(mContext, isShowGray ? R.color.gray_e3e3e3 : R.color.black_282828));
        TextViewUtils.setImageResources(mContext, isShowGray ? R.mipmap.ic_pay_wechat_gray : R.mipmap.ic_pay_wechat, Gravity.LEFT, tv_pay_wechat);
    }

    /**
     * 是否是退货中
     *
     * @return true：退货中
     */
    public boolean isReturnGoodsIng() {
        return "取消".equals(btn_return_goods.getText().toString().trim());
    }

    /**
     * 切换录入
     */
    private void switchInput() {
        // 切换到小的布局
        switchSelectGoodsLayoutShow(false);

        String buttonStr = btn_switch_input.getText().toString().trim();
        if ("扫码".equals(buttonStr)) {
            // 当前为扫码，切到录入
            btn_switch_input.setText("录入");
            mInputCodeLayout.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_input_content, InputCodeFragment.newInstance(onInputCodeListener, this)).commitAllowingStateLoss();
        } else if ("录入".equals(buttonStr)) {
            // 当前为录入，切到检索
            btn_switch_input.setText("检索");
            mInputCodeLayout.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_input_content, InputSelectFragment.newInstance(3, false, onGoodsClickListener)).commitAllowingStateLoss();
        } else if ("检索".equals(buttonStr)) {
            // 当前为检索，切到扫码
            btn_switch_input.setText("扫码");
            mInputCodeLayout.setVisibility(View.VISIBLE);
            ((ViewGroup) getView(R.id.fl_input_content)).removeAllViews();
        }
    }

    /**
     * 切换商品列表全屏显示的View点击
     */
    private void switchGoodsFullShowViewClick() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 300)) {// 1000代表多击事件的限定时间
            // 处理多击事件的代码
            switchGoodsFullShow();
        }
    }

    /**
     * 切换商品列表全屏显示
     */
    private void switchGoodsFullShow() {
        if (vg_bottom_button_layout.getVisibility() == View.VISIBLE) {
            // 底部按钮显示，代表没有开启全屏模式，现在开启全屏模式
            vg_goods_button_layout.setVisibility(View.GONE);// 列表底部按钮布局
            vg_hint_layout.setVisibility(View.GONE);// 提示布局
            vg_bottom_button_layout.setVisibility(View.GONE);// 底部按钮布局
            // 切换到扫码页面
            ((ViewGroup) getView(R.id.fl_input_content)).removeAllViews();
        } else {
            // 底部按钮没显示，代表开启全屏模式，现在关闭
            vg_goods_button_layout.setVisibility(View.VISIBLE);// 列表底部按钮布局
            vg_hint_layout.setVisibility(View.VISIBLE);// 提示布局
            vg_bottom_button_layout.setVisibility(View.VISIBLE);// 底部按钮布局
            // 回到原来的状态
            String buttonStr = btn_switch_input.getText().toString().trim();
            if ("扫码".equals(buttonStr)) {
                // 当前为扫码，切到上一状态:检索
                btn_switch_input.setText("检索");
            } else if ("录入".equals(buttonStr)) {
                // 当前为录入，切到上一状态:扫码
                btn_switch_input.setText("扫码");
            } else if ("检索".equals(buttonStr)) {
                // 当前为检索，切到上一状态:录入
                btn_switch_input.setText("录入");
            }
            switchInput();
        }
    }

    /**
     * 切换选择商品布局展示
     *
     * @param isShowBigLayout 是否展示大布局，true，展示大布局，false，展示小布局
     */
    public void switchSelectGoodsLayoutShow(boolean isShowBigLayout) {
        if ((tv_title_id.getVisibility() != View.VISIBLE) == isShowBigLayout) {
            // 已经显示了，还要显示，return；已经隐藏了，还有隐藏，return；
            return;
        }
        // 设置内容头
        tv_title_id.setVisibility(isShowBigLayout ? View.GONE : View.VISIBLE);// 编号
        tv_title_prices.setVisibility(isShowBigLayout ? View.GONE : View.VISIBLE);// 原价
        tv_title_all_price.setVisibility(isShowBigLayout ? View.GONE : View.VISIBLE);// 小计
        // 设置内容
        inputGoodsAdapter.setShowBigLayoutAndNotify(isShowBigLayout);
        // 设置底部按钮
        vg_goods_button_layout.setVisibility(isShowBigLayout ? View.GONE : View.VISIBLE);// 列表底部按钮布局
    }

    /**
     * @param payType 支付类型  1：支付宝 2：微信 6：现金
     */
    private void showPayDialog(int payType) {
        if (inputGoodsAdapter.getItemCount() <= 0) {
            // 没有商品，提示
            setErrorHint("请录入商品");
        } else if (checkIsHasNoFinishWeightGoods()) {
            // 检测是否有未完成称重的商品
            setErrorHint("请完成称重商品");
        } else if (isHasWeightingGoods()) {
            // 有称重中的商品
            setErrorHint("请确认称重完成");
        } else if (isReturnGoodsIng() && (payType == C.I.PAY_TYPE_ALIPAY || payType == C.I.PAY_TYPE_WECHAT)) {
            // 退货中，点击了支付宝、或微信
            setErrorHint("暂不支持支付宝、微信退款");
        } else {
            // 有商品，并且，没有称重中的状态
            // 展示支付
            int payState = isReturnGoodsIng() ? C.I.PAY_STATE_RETURN : C.I.PAY_STATE_PAY;// 支付状态   1：支付 2：退款
            startActivity(PayDialogActivity.newIntent(mContext, payState, payType, NumberFormatUtils.toDouble(allPriceStr), onPayListener));
//            overridePendingTransition(0, 0);
        }
    }

    /**
     * 检查是否清空列表
     */
    private void checkIsClearList() {
        // 判断是否需要清空列表
        if (isAddGoodsBeforeClearList) {
            // 增加商品前，清空列表数据
            // 清空列表
            inputGoodsAdapter.clear();
            // 清空总额
            setOrderAllNumAndAllPrice();
            // 归原
            isAddGoodsBeforeClearList = false;
        }
    }

    /**
     * 检测是否有未完成称重的商品
     */
    private boolean checkIsHasNoFinishWeightGoods() {
        List<InputGoodsBean> list = inputGoodsAdapter.getList();
        if (list != null && list.size() > 0) {
            InputGoodsBean lastInputGoodsBean = list.get(list.size() - 1);// 最后一个
            if (lastInputGoodsBean.getNum() <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置上一个订单的信息
     */
    private void setUpOrderInfo() {
        DaoUtils.getLastOrder(this, new DaoUtils.OnDaoResultListener<Order>() {
            @Override
            public void onSuccess(Order bean) {
                tv_up_order_all_price.setText(String.format("合计：%s", StringUtil.saveTwoDecimal(bean.getPrice())));// 上个订单：总价
                tv_up_order_pay_price.setText(String.format("付款：%s", StringUtil.saveTwoDecimal(bean.getPay_price())));// 上个订单：付款
                tv_up_order_zhaoling.setText(String.format("找零：%s", StringUtil.saveTwoDecimal(bean.getZhaoling_price())));// 上个订单：找零
                // 根据订单id，获取清单数量
                DaoUtils.getOrderDetailNumByOrderId(ShouYinActivity.this, bean.getOrder_id(), new DaoUtils.OnDaoResultListener<Integer>() {
                    @Override
                    public void onSuccess(Integer bean) {
                        tv_up_order_all_num.setText(String.format("数量：%s", String.valueOf(bean)));// 上个订单：数量
                    }

                    @Override
                    public void onFailed() {
                        tv_up_order_all_num.setText(String.format("数量：%s", "0"));// 上个订单：数量
                    }
                });
            }

            @Override
            public void onFailed() {
                tv_up_order_all_price.setText(String.format("合计：%s", "0.00"));// 上个订单：总价
                tv_up_order_pay_price.setText(String.format("付款：%s", "0.00"));// 上个订单：付款
                tv_up_order_all_num.setText(String.format("数量：%s", "0"));// 上个订单：数量
                tv_up_order_zhaoling.setText(String.format("找零：%s", "0.00"));// 上个订单：找零
            }
        });
    }

    /**
     * 切换显示上一单信息
     *
     * @param isShowUpOrderInfo 是否显示上一单信息
     */
    private void switchShowUpOrderInfo(boolean isShowUpOrderInfo) {
        this.isShowUpOrderInfo = isShowUpOrderInfo;
        iv_up_order.setBackgroundResource(isShowUpOrderInfo ? R.drawable.shape_up_order_button_left : R.drawable.shape_up_order_button_right);
        iv_up_order.setImageResource(isShowUpOrderInfo ? R.mipmap.ic_arrow_left : R.mipmap.ic_arrow_right);
        vg_up_order_layout.setVisibility(isShowUpOrderInfo ? View.VISIBLE : View.GONE);// 上一订单布局
        vg_pay_alipay.setVisibility(isShowUpOrderInfo ? View.GONE : View.VISIBLE);// 支付宝支付
        vg_pay_wechat.setVisibility(isShowUpOrderInfo ? View.GONE : View.VISIBLE);// 微信支付
        vg_pay_cash.setVisibility(isShowUpOrderInfo ? View.GONE : View.VISIBLE);// 现金支付
        btn_pay.setVisibility(isShowUpOrderInfo ? View.VISIBLE : View.GONE);// 结算按钮
    }

    /**
     * 设置称重完成
     */
    public void onWeightingFinish() {
        // 关闭称重
        weightHelper.closeWeight();
        // 重新计算总价
        setOrderAllNumAndAllPrice();
    }

    /**
     * 录入的商品里面是否有称重中商品
     */
    public boolean isHasWeightingGoods() {
        List<InputGoodsBean> list = inputGoodsAdapter.getList();
        if (list != null && list.size() > 0) {
            for (InputGoodsBean inputGoodsBean : list) {
                if (inputGoodsBean.isWeighing())
                    return true;// 称重中商品，返回true
            }
        }
        return false;
    }

    /**
     * 设置称重中商品为称重完成
     */
    private void setWeightingGoodsFinished() {
        List<InputGoodsBean> list = inputGoodsAdapter.getList();
        if (list.size() > 0) {
            for (InputGoodsBean inputGoodsBean : list) {
                inputGoodsBean.setWeighing(false);
            }
            // 通知列表刷新显示
            inputGoodsAdapter.notifyDataSetChanged();
            // 设置订单称重完成
            onWeightingFinish();
        }
    }

    /**
     * 发送刷新副屏显示
     */
    private void sendRefreshViceDisplayListEvent() {
        if (!isReturnGoodsIng())
            EventBus.getDefault().postSticky(new RefreshViceDisplayListEvent(allNumStr, allPriceStr, inputGoodsAdapter.getList(), inputGoodsAdapter.getSelectedPosition()));
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
                inputGoodsByScanCode(string);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printHelper.onDestroy();
        weightHelper.closeWeight();
        // 离开收银界面通知副屏停止工作
        EventBus.getDefault().postSticky(new StopWorkEvent());
        EventBus.getDefault().unregister(this);
    }


    /**
     * 设置错误提示
     */
    private void setErrorHint(String errorHint) {
        tv_error_hint.setText(String.format("操作说明：%s", errorHint));
        ToastManager.showShortToast(mContext, errorHint);
    }

    /**
     * 关闭默认局部刷新动画
     */
    public void closeDefaultAnimator() {
        inputGoodsRecyclerView.getItemAnimator().setAddDuration(0);
        inputGoodsRecyclerView.getItemAnimator().setChangeDuration(0);
        inputGoodsRecyclerView.getItemAnimator().setMoveDuration(0);
        inputGoodsRecyclerView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) inputGoodsRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    /**
     * 设置滚动到底部
     */
    public void rvMoveToBottom() {
        inputGoodsRecyclerView.smoothScrollToPosition(inputGoodsAdapter.getItemCount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addGoods(AddGoodsEvent event) {
        Goods goods = event.getGoods();
        if (goods != null)
            inputGoodsBySelectedGoods(goods);
    }
}
