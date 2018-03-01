package com.xxzlkj.zhaolinshare.base.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：张文亮
 * 时间：2016年08月06日    08:06
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 存储 错误日志的 路径
     */
    private String logPath;
    private Context context;
    private OnCrashListener onCrashListener;

    /**
     * 不需要包含 SD 根目录
     */
    public CrashHandler(Context context) {
        this(context, "本地日志" + File.separator + "cash.log");
    }

    /**
     * 不需要包含 SD 根目录
     */
    public CrashHandler(Context context, OnCrashListener onCrashListener) {
        this(context, "本地日志" + File.separator + "cash.log", onCrashListener);
    }

    /**
     * 不需要包含 SD 根目录
     */
    public CrashHandler(Context context, String logPath) {
        this(context, logPath, null);
    }

    /**
     * 不需要包含 SD 根目录
     */
    private CrashHandler(Context context, String logPath, OnCrashListener onCrashListener) {
        if (!Environment.isExternalStorageEmulated()) {
            return;
        }
        this.logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + logPath + File.separator;
        this.context = context;
        this.onCrashListener = onCrashListener;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void setOnCrashListener(OnCrashListener onCrashListener) {
        this.onCrashListener = onCrashListener;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();

        long currentTime = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTime));
        String phoneInfo = getPhoneInfo();
        String cashMessage = time + "\n" + phoneInfo + ("\n" + getStackTraceString(ex));
        if (onCrashListener != null) {
            onCrashListener.onCrash(cashMessage);
        }
        saveFile(cashMessage, false);
        LogUtil.e(cashMessage);

        Process.killProcess(Process.myPid());

    }

    private String getPhoneInfo() {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return (" versionName : " + packageInfo.versionName) +
                    "\n versionCode : " + packageInfo.versionCode +
                    "\n OS  version : " + Build.VERSION.RELEASE +
                    "\n 制造商 : " + Build.MANUFACTURER +
                    "\n手机型号 : " + Build.MODEL +
                    "\n cpu架构 : " + Build.CPU_ABI + "  " + Build.CPU_ABI2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 保存文件
     *
     * @param append true-追加|false-清空
     */
    private boolean saveFile(String result, boolean append) {
        FileOutputStream output = null;
        try {
            File file = new File(logPath + "log" + System.currentTimeMillis() + ".txt");
            String parentStr = file.getParent();
            boolean createSDCardDir = createSDCardDir(parentStr);
            if (!createSDCardDir) {
                return false;
            }
            output = new FileOutputStream(new File(logPath + "log" + System.currentTimeMillis() + ".txt"), append);
            output.write(result.getBytes());
            output.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                LogUtil.e(e);
            }
        }
        return false;
    }

    private boolean createSDCardDir(String newPath) {
        //得到一个路径，内容是sdcard的文件夹路径和名字
        //LogUtil.e("newPath = "+newPath);
        File path1 = new File(newPath);
        //若不存在，创建目录，可以在应用启动的时候创建
        return path1.exists() || path1.mkdir();
    }

    private String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    interface OnCrashListener {
        void onCrash(String error);
    }
}

