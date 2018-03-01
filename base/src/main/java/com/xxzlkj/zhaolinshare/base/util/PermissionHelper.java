package com.xxzlkj.zhaolinshare.base.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.Arrays;
import java.util.List;

/**
 * @author deadline
 *         android >=M 的权限申请统一处理
 *         notice:
 *         很多手机对原生系统做了修改，比如小米4的6.0的shouldShowRequestPermissionRationale
 *         就一直返回false，而且在申请权限时，如果用户选择了拒绝，则不会再弹出申请权限对话框了, 因此有了
 *         void doAfterDenied(String... permission);
 */

public class PermissionHelper {

    private static final int REQUEST_PERMISSION_CODE = 1000;

    private Object mContext;

    private PermissionListener mListener;

    private List<String> mPermissionList;

    public PermissionHelper(@NonNull Object object) {
        checkCallingObjectSuitability(object);
        this.mContext = object;
    }


    /**
     * 权限授权申请
     *
     * @param hintMessage 要申请的权限的提示
     * @param listener    申请成功之后的callback
     * @param permissions 要申请的权限
     */
    public void requestPermissions(@NonNull CharSequence hintMessage, @Nullable PermissionListener listener, @NonNull final String... permissions) {
        if (listener != null) {
            mListener = listener;
        }
        mPermissionList = Arrays.asList(permissions);
        if (!hasPermissions(mContext, permissions)) {
            //没全部权限
            //需要向用户解释为什么申请这个权限
            boolean shouldShowRationale = false;
            for (String perm : permissions) {
                shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(mContext, perm);
            }
            if (shouldShowRationale) {
                // 展示提示
                showMessageOKCancel(hintMessage, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executePermissionsRequest(mContext, permissions, REQUEST_PERMISSION_CODE);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null) {
                            mListener.doAfterDenied(permissions);
                        }
                    }
                });
            } else {
                // 不展示提示
                executePermissionsRequest(mContext, permissions, REQUEST_PERMISSION_CODE);
            }
        } else if (mListener != null) { //有全部权限
            mListener.doAfterGrand(permissions);
        }
    }

    /**
     * 处理onRequestPermissionsResult
     */
    public void handleRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                boolean allGranted = true;// 所有的授予
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        // 有一个没被授予的就为false
                        allGranted = false;
                        break;
                    }
                }

                if (allGranted && mListener != null) {
                    // 所有的权限授予以后
                    mListener.doAfterGrand((String[]) mPermissionList.toArray());
                } else if (!allGranted && mListener != null) {
                    // 所有的权限有一个没授予以后
                    mListener.doAfterDenied((String[]) mPermissionList.toArray());
                    // 为了解决小米手机等，申请一次，拒绝后不再提示申请，所以去设置页面自己设置
                    showMessageOKCancel("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。", "去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startAppSettings(getActivity(mContext));
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                }
                break;
        }
    }

    /**
     * 判断是否具有某权限，规则：小于6.0具有，如果有个没有则返回false
     */
    private static boolean hasPermissions(@NonNull Object object, @NonNull String... perms) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            @SuppressWarnings("ConstantConditions")
            boolean hasPerm = (ContextCompat.checkSelfPermission(getActivity(object), perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }


    /**
     * 将要展示请求权限说明
     * 兼容fragment
     */
    @SuppressWarnings("SimplifiableIfStatement")
    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(@NonNull Object object, @NonNull String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    /**
     * 请求权限,
     * 兼容fragment
     */
    @TargetApi(23)
    private void executePermissionsRequest(@NonNull Object object, @NonNull String[] perms, int requestCode) {
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof android.support.v4.app.Fragment) {
            ((android.support.v4.app.Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    /**
     * 检查传递Context是否合法
     */
    private void checkCallingObjectSuitability(@Nullable Object object) {
        if (object == null) {
            throw new NullPointerException("Activity or Fragment should not be null");
        }

        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof android.support.v4.app.Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        if (!(isSupportFragment || isActivity || (isAppFragment && isNeedRequest()))) {
            if (isAppFragment) {
                throw new IllegalArgumentException("target sdk needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }

    /**
     * 获取activity
     */
    private static Activity getActivity(@NonNull Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    /**
     * 是否需要请求权限
     */
    private static boolean isNeedRequest() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @SuppressWarnings("ConstantConditions")
    private void showMessageOKCancel(CharSequence message, String okStr, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(getActivity(mContext))
                .setMessage(message)
                .setPositiveButton(okStr, okListener)
                .setNegativeButton("取消", cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }

    // 启动应用的设置
    @SuppressWarnings("ConstantConditions")
    public static void startAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }

    public interface PermissionListener {
        /**
         * 所有的权限授予之后
         *
         * @param permission 所有的权限
         */
        void doAfterGrand(String... permission);

        /**
         * 所有的权限有一个没授予以后
         *
         * @param permission 所有的权限
         */
        void doAfterDenied(String... permission);
    }
}