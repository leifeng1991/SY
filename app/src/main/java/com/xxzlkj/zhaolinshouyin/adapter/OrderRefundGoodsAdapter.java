package com.xxzlkj.zhaolinshouyin.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.NumberFormatUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.activity.OrderRefundActivity;
import com.xxzlkj.zhaolinshouyin.db.OrderDetail;
import com.xxzlkj.zhaolinshouyin.model.OrderRefundGoods;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/2/5 11:28
 */


public class OrderRefundGoodsAdapter extends BaseAdapter<OrderRefundGoods> {
    private boolean isCanRefund;// true:可退款列表 false：不可退款列表
    private OnTotalPriceListener onTotalPriceListener;
    private OnIsAllCheckedListener onIsAllCheckedListener;
    private OrderRefundActivity activity;

    /**
     * @param isCanRefund true:可以退款 false:不可以退款
     */
    public OrderRefundGoodsAdapter(Context context, OrderRefundActivity activity, boolean isCanRefund, int itemId) {
        super(context, itemId);
        this.isCanRefund = isCanRefund;
        this.activity = activity;
    }

    @Override
    public void convert(BaseViewHolder holder, int position, OrderRefundGoods itemBean) {
        LinearLayout mLinearLayout = holder.getView(R.id.id_layout);
        LinearLayout mCheckAllLinearLayout = holder.getView(R.id.id_check_all_layout);
        ImageView imageView = holder.getView(R.id.id_check_image);// 选中
        TextView mLineNumberTextView = holder.getView(R.id.id_line_number);// 行号
        TextView mGoodsNameTextView = holder.getView(R.id.id_goods_name);// 商品名称
        TextView mBuyPriceTextView = holder.getView(R.id.id_buy_price);// 购买单价
        TextView mBuyNumberTextView = holder.getView(R.id.id_buy_number);// 购买总数
        TextView mRefundNumberTextView = holder.getView(R.id.id_refund_number);// 可退货总数
        LinearLayout mRefundLayout = holder.getView(R.id.id_refund_layout);
        TextView mSubtractTextView = holder.getView(R.id.id_subtract);// 减
        TextView mCurrentRefundNumberTextView = holder.getView(R.id.id_current_refund_number);// 退货数量(标品)
        TextView mRefundTitleTextView = holder.getView(R.id.id_refund_title);// 退货数量（非标品）
        TextView mAddTextView = holder.getView(R.id.id_add);// 加
        TextView mSubtotalTextView = holder.getView(R.id.id_subtotal);// 小计
        // 设置数据
        mLinearLayout.setBackgroundColor((position + 1) % 2 == 0 ? 0xffF7F8FC : 0xffffffff);
        OrderDetail orderDetail = itemBean.getOrderDetail();
        // 是否是标品:1：标品；2:非标品
        int standard = orderDetail.getStandard();
        imageView.setVisibility(View.VISIBLE);
        mRefundTitleTextView.setVisibility(isCanRefund ? (standard == 1 ? View.GONE : View.VISIBLE) : View.VISIBLE);
        mRefundLayout.setVisibility(isCanRefund ? standard == 1 ? View.VISIBLE : View.GONE : View.GONE);
        Boolean aBoolean = itemBean.isChecked();
        imageView.setImageResource(isCanRefund ? aBoolean ? R.mipmap.ic_checked : R.mipmap.ic_check_normal : R.drawable.shape_rectangle_e3e3e3_2);
        mLineNumberTextView.setText(String.format(Locale.CHINA, "%d", position + 1));
        mGoodsNameTextView.setText(orderDetail.getTitle());
        double price = orderDetail.getPrice();
        mBuyPriceTextView.setText(StringUtil.saveTwoDecimal(price));
        double num = orderDetail.getNum();
        mBuyNumberTextView.setText(StringUtil.saveThreeDecimal(num));
        double return_num = orderDetail.getReturn_num();
        mRefundNumberTextView.setText(StringUtil.saveThreeDecimal(isCanRefund ? return_num : num - return_num));
        double refundNumber = itemBean.getRefundNumber();
        mCurrentRefundNumberTextView.setText(StringUtil.saveZeroDecimal(refundNumber));
        // TODO 后期改
        mRefundTitleTextView.setText(isCanRefund ? StringUtil.saveThreeDecimal(return_num) : "已退货");
        double subtotalPrice = price * refundNumber;
        mSubtotalTextView.setText(isCanRefund ? StringUtil.saveTwoDecimal(subtotalPrice) : "");
        // 选中
        mCheckAllLinearLayout.setOnClickListener(v -> {
            itemBean.setChecked(!aBoolean);
            if (onIsAllCheckedListener != null)
                onIsAllCheckedListener.isAllChecked(isAllChecked());
            setTotalPrice();
            notifyDataSetChanged();

        });
        // 减
        mSubtractTextView.setOnClickListener(v -> {
            int number = NumberFormatUtils.toInt(mCurrentRefundNumberTextView.getText().toString());
            if (number == 1) {
                // 最小退货数量为1
                ToastManager.showShortToast(mContext, "已经到最小值");
            } else {
                number--;
                itemBean.setRefundNumber(number);
            }
            itemBean.setChecked(true);
            notifyDataSetChanged();
            // 选中：重新计算总计
            if (itemBean.isChecked())
                setTotalPrice();
        });
        // 加
        mAddTextView.setOnClickListener(v -> {
            int number = NumberFormatUtils.toInt(mCurrentRefundNumberTextView.getText().toString());
            if (number >= return_num) {
                // 最大退货数量为return_num
                ToastManager.showShortToast(mContext, "已经到最大值");
            } else {
                number++;
                number = number > return_num ? (int) return_num : number;
                itemBean.setRefundNumber(number);
            }
            itemBean.setChecked(true);
            notifyDataSetChanged();
            // 选中：重新计算总计
            if (itemBean.isChecked())
                setTotalPrice();
        });
        // 修改数量
        mCurrentRefundNumberTextView.setOnClickListener(v -> ZLDialogUtil.showNumberKeyboard(activity, 2, true, inputCode -> {
            int number = TextUtils.isEmpty(inputCode) ? 1 : NumberFormatUtils.toInt(inputCode);
            if (number > return_num) {
                // 超过可退数量
                ToastManager.showShortToast(mContext, "超过最大值，修改失败");
            } else if (number <= 0) {
                // 等于零
                ToastManager.showShortToast(mContext, "修改失败");
            } else {
                // 大于零 小于等于最大数量
                itemBean.setRefundNumber(number);
            }
            itemBean.setChecked(true);
            notifyDataSetChanged();
            // 选中：重新计算总计
            if (itemBean.isChecked())
                setTotalPrice();
        }));
    }

    /**
     * 是否全选
     *
     * @return true:全选 false:没全选
     */
    private boolean isAllChecked() {
        List<OrderRefundGoods> list = getList();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Boolean mBoolean = list.get(i).isChecked();
                if (!mBoolean) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 计算总计
     *
     * @return 返回总价价格
     */
    private double calculateTotalPrice() {
        double totalPrice = 0;
        List<OrderRefundGoods> list = getList();
        if (list.size() > 0) {
            for (OrderRefundGoods orderRefundGoods : list) {
                OrderDetail orderDetail = orderRefundGoods.getOrderDetail();
                if (orderRefundGoods.isChecked()) {
                    // 选中
                    double refundNumber = orderRefundGoods.getRefundNumber();
                    totalPrice += refundNumber * orderDetail.getPrice();
                }

            }
        }
        return totalPrice;
    }

    /**
     * 获取被选中商品列表
     *
     * @return 返回选中商品列表
     */
    public List<OrderRefundGoods> getCheckedGoodsList() {
        List<OrderRefundGoods> list = getList();
        List<OrderRefundGoods> newList = new ArrayList<>();
        if (list.size() > 0) {
            for (OrderRefundGoods orderRefundGoods : list) {
                if (orderRefundGoods.isChecked()) {
                    // 选中
                    newList.add(orderRefundGoods);
                }
            }
        }
        return newList;
    }

    public void setTotalPrice() {
        if (onTotalPriceListener != null)
            onTotalPriceListener.onTotalPrice(calculateTotalPrice());
    }

    public void OnTotalPriceListener(OnTotalPriceListener onTotalPriceListener) {
        this.onTotalPriceListener = onTotalPriceListener;
    }

    public void setOnIsAllCheckedListener(OnIsAllCheckedListener onIsAllCheckedListener) {
        this.onIsAllCheckedListener = onIsAllCheckedListener;
    }

    public interface OnTotalPriceListener {
        void onTotalPrice(double totalPrice);
    }

    public interface OnIsAllCheckedListener {
        void isAllChecked(boolean isAllChecked);
    }
}
