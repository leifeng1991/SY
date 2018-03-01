package com.xxzlkj.zhaolinshouyin.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.NotificationCompat;

import com.xxzlkj.zhaolinshare.base.net.OnBaseRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.PermissionHelper;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshare.base.util.UpdateAppManager;
import com.xxzlkj.zhaolinshouyin.BuildConfig;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.app.ZhaoLinApplication;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.User;
import com.xxzlkj.zhaolinshouyin.model.LoginUpBean;

import java.io.File;
import java.util.HashMap;

/**
 * 描述:更新
 *
 * @author zhangrq
 *         2017/4/26 16:28
 */
public class ZLUpdateUtils {

    public static final int UPDATE_NOTIFY_ID = 123;

    /**
     * 检查更新,这个需要写入权限
     */
    public static void checkUpdateInMainThread(final Activity activity, final PermissionHelper permissionHelper) {
        checkIsVersionUpdate(new VersionUpdateListener() {
            @Override
            public void update(final LoginUpBean.DataBean dataBean, boolean isForcedUpdate) {
                ZLDialogUtil.showUpdateDialog(activity,
                        isForcedUpdate,
                        dataBean.getTitle(),
                        dataBean.getDesc(),
                        () -> requestPermission(activity, permissionHelper, dataBean.getUrl()));
            }

            @Override
            public void notUpdate() {
//                if (activity instanceof SettingActivity) {
//                    ToastManager.showShortToast(activity, "已是最新版本");
//                }
            }
        });
    }

    /**
     * 是否有最新版本
     */
    public static void checkIsVersionUpdate(final VersionUpdateListener listener) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        User loginUser = ZhaoLinApplication.getInstance().getLoginUser();
//        String registrationID = JPushInterface.getRegistrationID(ZhaoLinApplication.getInstance().getApplicationContext());
        stringStringHashMap.put(C.P.UID, loginUser == null ? "" : loginUser.getUid());
        stringStringHashMap.put(C.P.STORE_ID, ZLUtils.getStoreId());
        RequestManager.createRequest(ZLURLConstants.REQUEST_LOGIN_UP, stringStringHashMap, new OnBaseRequestListener<LoginUpBean>() {

            @Override
            public void handlerSuccess(LoginUpBean bean) {
                if ("200".equals(bean.getCode())) {
                    // 200	表示成功
                    //        state	0不更新 1可选更新 2强制更新
                    final LoginUpBean.DataBean data = bean.getData();
                    String state = data.getState();
                    if ("1".equals(state) || "2".equals(state)) {
                        // 更新
                        listener.update(data, "2".equals(state));
                    } else {
                        // 不更新
                        listener.notUpdate();
                    }
                }
            }

            @Override
            public void handlerError(int errorCode, String errorMessage) {

            }
        });
    }

    private static void requestPermission(final Activity activity, PermissionHelper permissionHelper, final String url) {
        permissionHelper.requestPermissions("请授予[写入]权限，否则无法下载本apk到本地", new PermissionHelper.PermissionListener() {
            @Override
            public void doAfterGrand(String... permission) {
                // 请求权限成功
                updateDown(activity, url);
            }

            @Override
            public void doAfterDenied(String... permission) {

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static void updateDown(final Activity activity, final String url) {
//        是否是wifi 是：直接更新  否：提示更新
        final Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            downApk(context, url);
//            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
//                // WIFI,直接下载
//                downApk(context, url);
//            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
//                // 移动网络
//                ZLDialogUtil.showRawDialog(activity, "当前是移动网络  是否更新", () -> downApk(context, url));
//            }
        }
    }

    private static void downApk(final Context context, String url) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("兆邻更新下载")
                .setContentText("正在下载")
                .setProgress(100, 0, false);
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(UPDATE_NOTIFY_ID, builder.build());

        UpdateAppManager updateAppManager = new UpdateAppManager(context, "zhaolin");
        updateAppManager.downApkByOkHttp(url, new UpdateAppManager.OnDownloadApkListener() {
            public int oldProgress;

            @Override
            public void onException(Exception e) {
                ToastManager.showShortToast(context, "下载安装包失败");
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onUpdate(int progress) {
                if (progress == oldProgress)// 现在的进度与原来的相同，不处理
                    return;
                builder.setProgress(100, progress, false)
                        .setContentText("下载" + progress + "%");
                manager.notify(UPDATE_NOTIFY_ID, builder.build());
                oldProgress = progress;
            }

            @Override
            public void onFinish(File file) {
                //下载完成
                manager.cancel(UPDATE_NOTIFY_ID);//设置关闭通知栏

                UpdateAppManager.install(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            }
        });

    }

    public interface VersionUpdateListener {
        /**
         * @param isForcedUpdate 是否强制更新
         */
        void update(LoginUpBean.DataBean dataBean, boolean isForcedUpdate);

        void notUpdate();
    }
}
