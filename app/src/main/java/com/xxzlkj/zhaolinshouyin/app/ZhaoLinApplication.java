package com.xxzlkj.zhaolinshouyin.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xxzlkj.zhaolinshare.base.app.BaseApplication;
import com.xxzlkj.zhaolinshare.base.config.BaseGlobalParams;
import com.xxzlkj.zhaolinshare.base.listener.OnLoginUserListener;
import com.xxzlkj.zhaolinshare.base.util.CrashHandler;
import com.xxzlkj.zhaolinshare.base.util.LogUtil;
import com.xxzlkj.zhaolinshouyin.activity.LoginActivity;
import com.xxzlkj.zhaolinshouyin.db.DaoMaster;
import com.xxzlkj.zhaolinshouyin.db.DaoSession;
import com.xxzlkj.zhaolinshouyin.db.Params;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.FloatWindowUtils;
import com.xxzlkj.zhaolinshouyin.utils.GreenDaoContext;
import com.xxzlkj.zhaolinshouyin.utils.MigrationHelper;
import com.xxzlkj.zhaolinshouyin.utils.MyOpenHelper;

import io.rong.imlib.RongIMClient;


/**
 * 描述:application
 *
 * @author zhangrq
 *         2017/3/6 14:53
 */

public class ZhaoLinApplication extends BaseApplication implements OnLoginUserListener {
    private static ZhaoLinApplication instance;

    private User loginUser;
    public int screenWidth;
    public int screenHeight;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 初始化一次
        initOnce();
        // 初始化GreenDao
        initGreenDao();
        // 初始化融云
        initRongYun();
        // 初始化订单推送弹框
        FloatWindowUtils.initOrderPush(this);
    }

    public static ZhaoLinApplication getInstance() {
        return instance;
    }

    /**
     * 初始化一次
     */
    private void initOnce() {
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    measureInit();// 测量屏幕宽高
                    new CrashHandler(getApplicationContext(), "zhaoLinLog");// 设置捕获日志
                }
            }.start();
        }
    }

    /**
     * 初始化GreenDao
     */
    private void initGreenDao() {
        MigrationHelper.DEBUG = BaseGlobalParams.isDebug; //如果你想查看日志信息，请将DEBUG设置为true
        MyOpenHelper helper = new MyOpenHelper(new GreenDaoContext(getApplicationContext()), "shouyin.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    /**
     * 初始化融云
     */
    private void initRongYun() {
        String curProcessName = getCurProcessName(getApplicationContext());
        boolean isCurrentProcess = getApplicationInfo().packageName.equals(curProcessName);
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (isCurrentProcess || "io.rong.push".equals(curProcessName)) {
            RongIMClient.init(this);
            //监听接收到的消息
            RongIMClient.setOnReceiveMessageListener(new MyOnReceiveMessageListener(getApplicationContext()));
            // 监听链接状态变化
            RongIMClient.setConnectionStatusListener(new MyConnectionStatusListener(getApplicationContext()));
        }
    }

    /**
     * 连接融云
     */
    public void connectRongIM() {
        // 链接融云
        Params params = DaoUtils.getParams();
        if (params != null && !TextUtils.isEmpty(params.getRong_token())) {
            // 用户不为空，并且融云token不为空，链接
            connectRongIM(params.getRong_token());
        }
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    public void connectRongIM(String token) {
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    // IOS 说token永久生效的，所以不用处理
                    LogUtil.i("UserLoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtil.i("UserLoginActivity", "--onSuccess---" + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.i("UserLoginActivity", "--onError" + errorCode);
                }
            });
        }
    }

    // 获取登录用户
    public User getLoginUser() {
        return loginUser;
    }

    public User getLoginUserDoLogin(Activity activity) {
        if (loginUser == null) {
            // 用户没登录，并且未保存本地（退出登录效果）,重新登录
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        }
        return loginUser;
    }

    /**
     * 设置成功登录用户
     */
    public void setSuccessLoginUser(User loginUser) {
        // user保存
        this.loginUser = loginUser;
    }

    /**
     * 设置退出登录
     */
    public void setExitLoginUser() {
        // user清空
        this.loginUser = null;
        // 设置订单框隐藏
        FloatWindowUtils.setOrderPushHide();
    }

    /**
     * 获取屏幕宽度并设置
     */
    private void measureInit() {
        WindowManager wm = (WindowManager) getBaseContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public String getLoginUserId() {
        return loginUser == null ? "0" : String.valueOf(loginUser.getUid());
    }
}