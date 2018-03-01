package com.xxzlkj.zhaolinshouyin.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.base.BaseAdapter;
import com.xxzlkj.zhaolinshare.base.base.BaseViewHolder;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.activity.AcountCheckDialogActivity;
import com.xxzlkj.zhaolinshouyin.activity.HistoryStatementDialogActivity;
import com.xxzlkj.zhaolinshouyin.db.Account;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.zrq.spanbuilder.Spans;

import java.util.Locale;

/**
 * 描述:
 *
 * @author leifeng
 *         2017/12/19 14:50
 */


public class HistoryStatementAdapter extends BaseAdapter<Account> {
    private Activity activity;

    public HistoryStatementAdapter(Context context, Activity activity, int itemId) {
        super(context, itemId);
        this.activity = activity;
    }

    @Override
    public void convert(BaseViewHolder holder, int position, Account itemBean) {
        TextView mLineNumberTextView = holder.getView(R.id.id_line_number);// 行号
        TextView mDateTextView = holder.getView(R.id.id_date);// 日期
        TextView mStartTimeTextView = holder.getView(R.id.id_start_time);// 开始时间
        TextView mEndTimeTextView = holder.getView(R.id.id_end_time);// 结束时间
        TextView mCashierTextView = holder.getView(R.id.id_cashier);// 收银员
        TextView mCashTotalrTextView = holder.getView(R.id.id_cash_total);// 现金总额
        TextView mTotalAmountTextView = holder.getView(R.id.id_total_amount);// 全部总额
        TextView mTotalOrdersTextView = holder.getView(R.id.id_total_orders);// 总订单数
        // 设置数据
        mLineNumberTextView.setText(String.format(Locale.CHINESE, "%d", position + 1));
        mDateTextView.setText(DateUtils.getYearMonthDay(itemBean.getEndtime() * 1000, "yyyy/MM/dd"));
        mStartTimeTextView.setText(DateUtils.getYearMonthDay(itemBean.getStarttime() * 1000, "HH:mm"));
        mEndTimeTextView.setText(DateUtils.getYearMonthDay(itemBean.getEndtime() * 1000, "HH:mm"));
        // 获取收银员信息
        DaoUtils.getUserByUserId(activity, itemBean.getUid(), new DaoUtils.OnDaoResultListener<User>() {
            @Override
            public void onSuccess(User bean) {
                mCashierTextView.setText(bean.getName());
            }

            @Override
            public void onFailed() {
                mCashierTextView.setText("");
            }
        });
        mCashTotalrTextView.setText(StringUtil.saveTwoDecimal(itemBean.getCash_price()));
        mTotalAmountTextView.setText(StringUtil.saveTwoDecimal(itemBean.getAll_price()));
        mTotalOrdersTextView.setText(Spans.builder().text(String.format(Locale.CHINESE, "%d", itemBean.getOrder_num())).color(0xff0074FC).underLine().color(0xff0074FC).build());

        mTotalOrdersTextView.setOnClickListener(v -> {
            // 跳转到对账详情
            activity.startActivity(AcountCheckDialogActivity.newIntent(mContext, 2, itemBean.getUid(), itemBean.getStarttime(), itemBean.getEndtime()));
        });
    }
}
