package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.db.Goods;
import com.xxzlkj.zhaolinshouyin.fragment.InputSelectFragment;
import com.xxzlkj.zhaolinshouyin.listener.OnConnectDevicesListener;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.NumberKeyboardUtil;
import com.xxzlkj.zhaolinshouyin.utils.PrintBarCodeHelper;
import com.xxzlkj.zhaolinshouyin.utils.WeightHelper;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;

import java.util.Locale;

/**
 * 描述:商品称重
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class GoodsWeightActivity extends ZLBaseActivity {
    private TextView mProductNameTextView, mUnitPriceTextView, mWeightTextView, mTotalPriceTextView, mPrintNumberTextView;
    private EditText mEditText;
    private Button mChangePriceButton, mPrintGoodsCodeButton, mPrintBarcodeButton, mCommonGoodsButton;
    private LinearLayout mCardView;
    private StringBuffer mStringBufferResult = new StringBuffer();// 扫码内容
    private WeightHelper weightHelper = new WeightHelper();// 初始化称重
    private Goods goods;
    // 打印小票数量 默认一张
    private int printNumber = 1;

    public static Intent newIntent(Context context) {
        return new Intent(context, GoodsWeightActivity.class);
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_goods_weight);
    }

    @Override
    protected void findViewById() {
        mProductNameTextView = getView(R.id.id_product_name);// 货品名称
        mUnitPriceTextView = getView(R.id.id_unit_price);// 单价
        mWeightTextView = getView(R.id.id_weight);// 重量
        mTotalPriceTextView = getView(R.id.id_total_price);// 总价
        mEditText = getView(R.id.id_edit_text);// 输入商品货号
        mChangePriceButton = getView(R.id.id_change_price);// 今日改价
        mPrintBarcodeButton = getView(R.id.id_print_barcode);// 打印条
        mPrintGoodsCodeButton = getView(R.id.id_print_goods_code);// 打印商品
        mCardView = getView(R.id.id_price_card_view);// 改价
        mPrintNumberTextView = getView(R.id.id_print_number);// 打印数量
        mCommonGoodsButton = getView(R.id.id_common_goods);// 常用商品
    }

    @Override
    public void setListener() {
        // 数字键盘点击事件
        NumberKeyboardUtil.setClick(getWindow().getDecorView(), mEditText, 2, (inputCode, clickButton) -> {
            if (TextUtils.isEmpty(inputCode)) {
                ToastManager.showShortToast(mContext, "请输入商品货号");
            } else {
                // 确定
                inputGoodsByInputCode(inputCode);
            }
            mEditText.setText("");

        }, null);
        // 今日改价
        mChangePriceButton.setOnClickListener(v -> ToastManager.showShortToast(mContext, "暂未开放"));
        // 打印条码
        mPrintBarcodeButton.setOnClickListener(v -> {
            if (goods != null) {
                // 打印
                double goodsWeight = NumberFormatUtils.toDouble(mWeightTextView.getText().toString().trim());// 重量
                if (goodsWeight == 0) {
                    // 提示
                    ToastManager.showShortToast(mContext, "请称重商品");
                } else {
                    // 打印
                    // 打开打印链接
                    PrintBarCodeHelper printBarCodeHelper = PrintBarCodeHelper.getInstance(this);
                    printBarCodeHelper.autoConnectPrintDevices(this, new OnConnectDevicesListener() {
                        @Override
                        public void onSuccess() {
                            // 链接打印成功，打印
                            double goodsPrice = NumberFormatUtils.toDouble(mUnitPriceTextView.getText().toString().trim());// 单价
                            printBarCodeHelper.printBarCode(mContext, goods.getCode(), goods.getTitle(), goodsPrice, goodsWeight, printNumber, () -> {
                                // 打印结束，关闭打印，重新链接下称重
                                printBarCodeHelper.disconnectConnectPrintDevices();
                                // 打开称重改变监听
                                openWeightChangeListener();
                            });
                        }

                        @Override
                        public void onFailed() {
                            ToastManager.showShortToast(mContext, "链接打印设备失败，请重新选择");
                        }
                    });
                }
            } else {
                // 提示
                ToastManager.showShortToast(mContext, "请录入商品");
            }
        });
        // 打印商品
        mPrintGoodsCodeButton.setOnClickListener(v -> {
            if (goods != null) {
                // 打印
                // 打开打印链接
                PrintBarCodeHelper printBarCodeHelper = PrintBarCodeHelper.getInstance(this);
                printBarCodeHelper.autoConnectPrintDevices(this, new OnConnectDevicesListener() {
                    @Override
                    public void onSuccess() {
                        // 链接打印成功，打印，打印结束，关闭打印
                        printBarCodeHelper.printGoodsCode(mContext, goods.getCode(), goods.getTitle(), printNumber, printBarCodeHelper::disconnectConnectPrintDevices);
                    }

                    @Override
                    public void onFailed() {
                        ToastManager.showShortToast(mContext, "链接打印设备失败，请重新选择");
                    }
                });
            } else {
                // 提示
                ToastManager.showShortToast(mContext, "请录入商品");
            }
        });
        // 编辑商品价格
        mCardView.setOnClickListener(v -> {
            if (goods != null) {
                // 有商品
                editSelectedPrice();
            } else {
                // 无商品 提示
                ToastManager.showShortToast(mContext, "请选择要改价的商品");
            }
        });
        // 打印数量
        mPrintNumberTextView.setOnClickListener(v -> ZLDialogUtil.showNumberKeyboard(GoodsWeightActivity.this, 3, true, inputCode -> {
            int toInt = NumberFormatUtils.toInt(inputCode, 0);
            if (toInt > 0) {
                // 最大20张，超过重置为20
                printNumber = toInt > 20 ? 20 : toInt;
                // 重置默认打印数量
                mPrintNumberTextView.setText(String.format(Locale.CHINA, "%d", printNumber));
            }
        }));
        // 选择要链接的打印设备
        getView(R.id.btn_select_print_devices).setOnClickListener(v -> PrintBarCodeHelper.showSelectUsbTitleDialogNoConnect(this));
        // 常用商品
        mCommonGoodsButton.setOnClickListener(v -> {
            // 跳转到常用商品列表
            startActivity(CommonGoodsListActivity.newIntent(mContext));
        });
    }

    @Override
    public void processLogic() {
        // 设置网络状态，刷新的时候重新获取状态
        boolean netIsAvailable = NetStateManager.isAvailable(mContext);
        setNetStateHint(netIsAvailable);
        // 商品分类和商品列表
        getSupportFragmentManager().beginTransaction().replace(R.id.id_frame_layout, InputSelectFragment.newInstance(6, true, this::inputGoodsBySelectedGoods)).commitAllowingStateLoss();
        // 打开称重改变监听
        openWeightChangeListener();
    }

    /**
     * 打开称重改变监听
     */
    private void openWeightChangeListener() {
        // 设置称重改变监听
        weightHelper.openWeight(weightInfo -> {
                    String unitPriceStr = mUnitPriceTextView.getText().toString().trim();
                    if (TextUtils.isEmpty(unitPriceStr)) unitPriceStr = "0";
                    // 设置商品重量
                    mWeightTextView.setText(StringUtil.saveThreeDecimal(weightInfo.netWeight));
                    // 计算商品总价
                    double v = NumberFormatUtils.toDouble(unitPriceStr, 0) * weightInfo.netWeight;
                    mTotalPriceTextView.setText(StringUtil.saveTwoDecimal(v));
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weightHelper.closeWeight();
    }

    /**
     * 设置商品信息
     */
    private void setGoodsInfo(Goods goods) {
        this.goods = goods;
        // 是否是标品:1：标品；2:非标品
        if (goods.getStandard() == 1) {
            // 1：标品
            ToastManager.showShortToast(mContext, "标品不可称重");
        } else {
            // 2:非标品
            mProductNameTextView.setText(goods.getTitle());
            mUnitPriceTextView.setText(StringUtil.saveTwoDecimal(goods.getPrice()));
            // 设置商品重量
            String weightStr = mWeightTextView.getText().toString().trim();
            double weight = TextUtils.isEmpty(weightStr) ? 0 : NumberFormatUtils.toDouble(weightStr);
            mWeightTextView.setText(StringUtil.saveThreeDecimal(weight));
            // 计算商品总价
            mTotalPriceTextView.setText(StringUtil.saveTwoDecimal(weight * goods.getPrice()));
        }

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

    /**
     * 扫码录入商品
     *
     * @param barCodeStr 条形码
     */
    private void inputGoodsByScanCode(String barCodeStr) {
        // 录入商品，通过条形码
        inputGoodsByBarCode(barCodeStr);
    }

    /**
     * 输入货号录入商品
     *
     * @param goodsCode 货号
     */
    private void inputGoodsByInputCode(String goodsCode) {

        DaoUtils.getGoodsByGoodsCode(this, goodsCode, new DaoUtils.OnDaoResultListener<Goods>() {
            @Override
            public void onSuccess(Goods bean) {
                // 添加一个商品，到录入的商品列表
                setGoodsInfo(bean);
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
        setGoodsInfo(goods);
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
                DaoUtils.getGoodsByGoodsCode(GoodsWeightActivity.this, goodsCode, new DaoUtils.OnDaoResultListener<Goods>() {
                    @Override
                    public void onSuccess(Goods bean) {
                        // 获取商品成功
                        // 添加一个商品，到录入的商品列表
                        setGoodsInfo(bean);
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
     * 展示没有商品的dialog
     *
     * @param goodsCode 货号,或条形码号
     */
    private void showNoGoodsDialog(String goodsCode) {
        ZLDialogUtil.showNoGoodsDialog(this, goodsCode, "商品库无此商品！请检查货号/条码是否正确");
    }

    /**
     * 修改选中价格
     */
    private void editSelectedPrice() {
        // 操作正常
        ZLDialogUtil.showNumberKeyboard(this, 1, false, inputCode -> {
            if (!TextUtils.isEmpty(inputCode)) {
                // 价格不为空
                mUnitPriceTextView.setText(String.format("%s", inputCode));
                double v = NumberFormatUtils.toDouble(inputCode) * NumberFormatUtils.toDouble(mWeightTextView.getText().toString());
                // 重置总价
                mTotalPriceTextView.setText(StringUtil.saveTwoDecimal(v));
            }

        });
    }

}
