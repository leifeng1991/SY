package com.xxzlkj.zhaolinshare.base.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

/**
 * 拨打电话的工具类
 *
 * @author zhangrq
 */
public class CallPhoneUtils {
    /**
     * 打电话的，弹框提示
     *
     * @param activity    当前页面的activity
     * @param phoneNumber 手机号
     */
    public static void callPhoneDialog(final Activity activity, final String phoneNumber) {
        Dialog dialog = new AlertDialog.Builder(activity)
                .setMessage("是否拨打：" + phoneNumber)
                .setNegativeButton("取消", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("确定", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Uri uri = Uri.parse("tel:" + phoneNumber);
                        // Intent intent = new Intent(Intent.ACTION_DIAL,
                        // uri);
                        // Intent intent = new Intent(Intent.ACTION_CALL, Uri
                        // .parse("tel:" + phoneNumber));
                        // activity.startActivity(intent);
                        callPhone(activity, phoneNumber);

                    }
                }).setTitle("拨打电话").create();
        dialog.show();
    }

    public static void callPhone(Activity activity, String phoneNumber) {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(intent);
    }
}
