package com.xxzlkj.zhaolinshouyin.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xxzlkj.zhaolinshare.base.MyRecyclerView;
import com.xxzlkj.zhaolinshare.base.util.DateUtils;
import com.xxzlkj.zhaolinshare.base.util.LogUtil;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.HistoryStatementAdapter;
import com.xxzlkj.zhaolinshouyin.db.Account;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 描述:历史对账单
 *
 * @author zhangrq
 *         2017/1/7 13:16
 */

public class HistoryStatementDialogActivity extends Activity {
    private Button mBackButton;
    private TextView mStartDateTextView, mEndTimeTextView, mCashierTextView;
    private LinearLayout mSelectCashierLayout;
    private MyRecyclerView mRecyclerView;
    private HistoryStatementAdapter adapter;
    private long mStartTime = -1;
    private long mEndTime = -1;
    private int page = 1;
    private List<String> list;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HistoryStatementDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadViewLayout();
        findView();
        setListener();
        processLogic();
    }

    protected void loadViewLayout() {
        setContentView(R.layout.dialog_history_statement);
    }

    protected void findView() {
        mBackButton = findViewById(R.id.id_back);// 返回
        mStartDateTextView = findViewById(R.id.id_start_date);// 开始日期
        mEndTimeTextView = findViewById(R.id.id_end_time);// 结束日期
        mCashierTextView = findViewById(R.id.id_cashier);// 收银员
        mSelectCashierLayout = findViewById(R.id.id_select_cashier);// 选择收银员
        mRecyclerView = findViewById(R.id.id_recycler_view);// 列表
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HistoryStatementDialogActivity.this));
        adapter = new HistoryStatementAdapter(HistoryStatementDialogActivity.this, this, R.layout.adapter_history_statement_list_item);
        mRecyclerView.setAdapter(adapter);

    }

    protected void setListener() {
        // 刷新和加载
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getAccountList();
            }

            @Override
            public void onLoadMore() {
                page = adapter.getItemCount() / 20 + 1;
                getAccountList();
            }
        });
        // 返回
        mBackButton.setOnClickListener(v -> finish());
        // 选择开始时间
        mStartDateTextView.setOnClickListener(v -> showSelectDateDialog(true));
        // 选择结束时间
        mEndTimeTextView.setOnClickListener(v -> showSelectDateDialog(false));
        // 选择收银员
        mSelectCashierLayout.setOnClickListener(v -> ZLDialogUtil.showSelectCashierDialog(HistoryStatementDialogActivity.this, (uids, names) -> {
            list = uids;
            mCashierTextView.setText(names);
            page = 1;
            getAccountList();
        }));

    }

    protected void processLogic() {
        getAccountList();
    }


    /**
     * @param isStartTime true:开始时间 false:结束时间
     */
    private void showSelectDateDialog(boolean isStartTime) {
        // 当前日期
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        // 日期选择
        DatePickerDialog endDialog = new DatePickerDialog(HistoryStatementDialogActivity.this, (view, year, month, dayOfMonth) -> {
            month++;
            String format = String.format(Locale.CHINESE, "%d-%d-%d", year, month, dayOfMonth);
            if (isStartTime) {
                // 开始时间
                mStartTime = DateUtils.getTimeInMillis("yyyy-MM-dd", format) / 1000;
                mStartDateTextView.setText(format);
                LogUtil.e("time" + DateUtils.getYearMonthDayHourMinuteSeconds2(mStartTime));
            } else {
                // 结束时间
                mEndTime = DateUtils.getTimeInMillis("yyyy-MM-dd", format) / 1000;
                mEndTimeTextView.setText(format);
                LogUtil.e("time" + DateUtils.getYearMonthDayHourMinuteSeconds2(mEndTime));
            }

            page = 1;
            getAccountList();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        endDialog.show();
        // 取消监听
        endDialog.setOnCancelListener(dialog -> {
            if (isStartTime) {
                // 开始时间
                mStartTime = -1;
                mStartDateTextView.setText("开始日期");
            } else {
                // 结束时间
                mEndTime = -1;
                mEndTimeTextView.setText("结束日期");
            }

        });
    }

    /**
     * 获取对账列表
     */
    private void getAccountList() {
        DaoUtils.getAccountList(this, mStartTime, mEndTime, page, list, new DaoUtils.OnDaoResultListListener<Account>() {
            @Override
            public void onSuccess(List<Account> list) {
                mRecyclerView.handlerSuccessHasRefreshAndLoad(adapter, page == 1, list);
            }

            @Override
            public void onFailed() {
                mRecyclerView.handlerError(adapter, page == 1);
            }
        });
    }
}
