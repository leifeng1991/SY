package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 描述: 版本更新的管理者
 *
 * @author zhangrq
 *         2016/9/19 14:39
 */
public class UpdateAppManager {

    private static final int DOWN_START = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_FINISH = 2;
    private static final int DOWN_EXCEPTION = 3;
    private static String apkName = "downloadApk.apk";
    private static OnDownloadApkListener onDownloadApkListener;
    private final Context context;
    private Handler handler = new UpdateAppHandler();

    private static class UpdateAppHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_START:
                    // 开始
                    if (onDownloadApkListener != null)
                        onDownloadApkListener.onStart();
                    break;
                case DOWN_UPDATE:
                    // 更新
                    int progress = msg.arg1;
                    if (onDownloadApkListener != null)
                        onDownloadApkListener.onUpdate(progress);
                    break;
                case DOWN_EXCEPTION:
                    // 异常
                    Exception e = (Exception) msg.obj;
                    if (onDownloadApkListener != null)
                        onDownloadApkListener.onException(e);
                    break;
                case DOWN_FINISH:
                    // 完成
                    File file = (File) msg.obj;
                    if (onDownloadApkListener != null)
                        onDownloadApkListener.onFinish(file);
                    break;
            }
        }
    }

    public UpdateAppManager(Context context, String apkName) {
        this.context = context;
        if (apkName.endsWith(".apk")) {
            apkName = apkName.substring(0, apkName.indexOf(".apk"));
        }
        UpdateAppManager.apkName = apkName + ".apk";
    }

    /**
     * 安装程序
     *
     * @param authority 例如：BuildConfig.APPLICATION_ID + ".fileProvider"
     * @param apkFile   安装包
     */
    public static void install(Context context, String authority, File apkFile) {
        if (!apkFile.exists())
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
            uri = FileProvider.getUriForFile(context, authority, apkFile);
        } else {
            uri = Uri.fromFile(apkFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 不能给上面那个添加，否则报错
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 下载Apk
     */
    public void downApkByOkHttp(String url, OnDownloadApkListener onUpdateApkListener) {
        UpdateAppManager.onDownloadApkListener = onUpdateApkListener;
        // 子线程访问网络
        try {
            new OkHttpClient().newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    sendException(e);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    // 发送开始
                    sendStart();

                    downApkAndUpdateProgress(response.body().contentLength(), response.body().byteStream());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            sendException(e);
        }
    }


    /**
     * 更新下载的进度
     */
    public void downApkAndUpdateProgress(long contentLength, InputStream inputStream) {
        int downloadedSize = 0;
        byte buf[] = new byte[1024 * 1024];
        int numBytesRead;
        // 保存的路径
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory(), apkName);
        } else {
            file = new File(context.getCacheDir(), apkName);
        }
        BufferedOutputStream fos = null;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(file));
            do {
                numBytesRead = inputStream.read(buf);
                if (numBytesRead > 0) {
                    fos.write(buf, 0, numBytesRead);// 写流
                    downloadedSize += numBytesRead;
                    // 当前进度值
                    int progress = (int) (((float) downloadedSize / contentLength) * 100);
                    // 更新进度
                    sendUpdate(progress);
                }
            } while (numBytesRead > 0);
            // 上面执行完了，即下载完成了
            sendFinish(file);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            sendException(e);
        } finally {
            // 关流
            try {
                if (inputStream != null)
                    inputStream.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                sendException(e);
            }
        }
    }

    private void sendFinish(File file) {
        Message obtain = Message.obtain();
        obtain.what = DOWN_FINISH;
        obtain.obj = file;
        handler.sendMessage(obtain);
    }

    /**
     * 发送异常
     */
    private void sendException(Exception e) {
        Message obtain = Message.obtain();
        obtain.what = DOWN_EXCEPTION;
        obtain.obj = e;
        handler.sendMessage(obtain);
    }

    /**
     * 发送更新
     */
    private void sendUpdate(int progress) {
        Message obtain = Message.obtain();
        obtain.what = DOWN_UPDATE;
        obtain.arg1 = progress;
        handler.sendMessage(obtain);
    }

    /**
     * 发送开始
     */
    private void sendStart() {
        handler.sendEmptyMessage(DOWN_START);
    }

    public interface OnDownloadApkListener {
        void onException(Exception e);

        void onStart();

        void onUpdate(int progress);

        void onFinish(File file);
    }
}

