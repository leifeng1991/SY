package com.xxzlkj.zhaolinshouyin;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.NetStateManager;
import com.xxzlkj.zhaolinshare.base.util.PermissionHelper;
import com.xxzlkj.zhaolinshare.base.util.PicassoUtils;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.activity.ChangePasswordActivity;
import com.xxzlkj.zhaolinshouyin.activity.GoodsListActivity;
import com.xxzlkj.zhaolinshouyin.activity.GoodsWeightActivity;
import com.xxzlkj.zhaolinshouyin.activity.LoginActivity;
import com.xxzlkj.zhaolinshouyin.activity.OrderListActivity;
import com.xxzlkj.zhaolinshouyin.activity.ShouYinActivity;
import com.xxzlkj.zhaolinshouyin.activity.ZLBaseActivity;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.event.ReceiveOrderPushEvent;
import com.xxzlkj.zhaolinshouyin.utils.CountDownTimerUtils;
import com.xxzlkj.zhaolinshouyin.utils.FloatWindowUtils;
import com.xxzlkj.zhaolinshouyin.utils.PrintBarCodeHelper;
import com.xxzlkj.zhaolinshouyin.utils.SynOrderLooper;
import com.xxzlkj.zhaolinshouyin.utils.ZLDialogUtil;
import com.xxzlkj.zhaolinshouyin.utils.ZLLooper;
import com.xxzlkj.zhaolinshouyin.utils.ZLUpdateUtils;
import com.xxzlkj.zhaolinshouyin.utils.ZLUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/2 17:33
 */
public class MainActivity extends ZLBaseActivity {
    private TextView mEquipmentNumberTextView, mTimeTextView;
    private LinearLayout mSalesCashierLayout, mCommodityMmanagementLayout, mOrderManagementLayout, mGoodsWeighingLayout;
    private Button mLogOutButton, mChangePasswordButton;
    private PermissionHelper permissionHelper;
    private PrintBarCodeHelper printBarCodeHelper;

    public static Intent newIntent(Context mContext) {
        return new Intent(mContext, MainActivity.class);
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void findViewById() {
        mEquipmentNumberTextView = getView(R.id.id_equipment_number);// 设备编号
        mTimeTextView = getView(R.id.id_time);// 时间
        mSalesCashierLayout = getView(R.id.id_sales_cashier);// 销售收银
        mCommodityMmanagementLayout = getView(R.id.id_commodity_management);// 商品管理
        mOrderManagementLayout = getView(R.id.id_order_management);// 订单管理
        mGoodsWeighingLayout = getView(R.id.id_goods_weighing);// 商品称重
        mLogOutButton = getView(R.id.id_log_out);// 退出登录
        mChangePasswordButton = getView(R.id.id_change_password);// 修改密码
    }

    @Override
    protected void setListener() {
        EventBus.getDefault().register(this);
        // 销售收银
        mSalesCashierLayout.setOnClickListener(v -> startActivity(ShouYinActivity.newIntent(mContext)));
        // 商品管理
        mCommodityMmanagementLayout.setOnClickListener(v -> startActivity(GoodsListActivity.newIntent(mContext, false)));
        // 订单管理
        mOrderManagementLayout.setOnClickListener(v -> startActivity(OrderListActivity.newIntent(mContext, 2)));
        // 商品称重
        mGoodsWeighingLayout.setOnClickListener(v -> startActivity(GoodsWeightActivity.newIntent(mContext)));
        // 修改密码
        mChangePasswordButton.setOnClickListener(v -> startActivity(ChangePasswordActivity.newIntent(mContext)));
        // 退出登录
        mLogOutButton.setOnClickListener(v -> ZLDialogUtil.showRawDialog(this, "确定要退出登录？", () -> {
            // 退出登录
            ZhaoLinApplication.getInstance().setExitLoginUser();
            startActivity(LoginActivity.newIntent(mContext));
            finish();
        }));
        // 选择打印设备
        getView(R.id.btn_select_print_devices).setOnClickListener(v -> PrintBarCodeHelper.showSelectUsbTitleDialogNoConnect(this));
    }

    @Override
    protected void processLogic() {
        // 设置值
        mEquipmentNumberTextView.setText(String.format("设备编号：%s   版本号：%s", ZLUtils.getDevicesNum(),BuildConfig.VERSION_NAME));
        CountDownTimerUtils.addCountDownTimer(mTimeTextView);// 倒计时
        // 初始化应用
        ZhaoLinApplication.getInstance().connectRongIM();// 连接融云
        NetStateManager.getInstance().registerNetStateReceiver(mContext);// 网络状态
        PicassoUtils.startLooperToken(mContext);// 开始轮询获取image token
        ZLLooper.startLooper(this);// 开始轮询同步数据
        SynOrderLooper.synUploadOrder(this);// 开始轮询同步订单
        permissionHelper = new PermissionHelper(this);
        ZLUpdateUtils.checkUpdateInMainThread(this, permissionHelper);// 检查更新
        printBarCodeHelper = PrintBarCodeHelper.getInstance(this);// 绑定打印服务
        printBarCodeHelper.bindService();
        FloatWindowUtils.getNoOperatingNum(this);// 获取未打印的数量
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        NetStateManager.getInstance().unregisterNetStateReceiver(mContext);// 网络状态
        CountDownTimerUtils.removeCountDownTimer(mTimeTextView);// 倒计时
        ZLLooper.removeLooper();// 移除
        printBarCodeHelper.unbindService();// 解除打印绑定服务
    }

    // 数组长度 N 就代表几击事件
    long[] mHits = new long[2];

    // 判断是否退出app
    private void exitApp() {
        // 数组向左移位操作
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {// 1000代表多击事件的限定时间
            // 处理多击事件的代码
            this.finish();
        } else {
            ToastManager.showShortToast(mContext, "再按一次退出");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveOrderPush(ReceiveOrderPushEvent event) {
        if (event != null) {
            FloatWindowUtils.setOrderPushShowOrHide(mContext, event.getUnPrintOrderNum());
        }
    }
}
