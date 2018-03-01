package com.xxzlkj.zhaolinshare.base.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.PhotoUploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


/**
 * 描述:
 *
 * @author zhangrq
 *         2017/3/29 9:11
 */
public class UpLoadImageUtils {

    private static final String BUCKET = "zhaolin";
    private static final String SAMPLE = "sample";
    private static final String APPID = "10029121";
    private static final String QCLOUDPHOTO = "qcloudphoto";
    private static UploadManager uploadManager;

    private static UploadManager getUploadManager(Context context) {
        if (uploadManager == null) {
            uploadManager = new UploadManager(context, APPID, Const.FileType.Photo, QCLOUDPHOTO);
        }
        return uploadManager;
    }

    /**
     * 上传缩放图片，里面进行了压缩
     */
    public static void upLoadSingleImageInMainThread(final Activity activity, final String filePath, final OnUploadListener onUploadListener) {
        // 获取签名
        PicassoUtils.getImageSignatureInMainThread(activity.getApplicationContext(), new PicassoUtils.OnGetImageSignatureListener() {
            @Override
            public void onSuccess(final String sign) {
                // 回调在主线程
                // 压缩图片
//                Bitmap bitmapFormUri = BitmapUtils.decodeSampledBitmap(filePath, sample);
//                byte[] bytes = BitmapUtils.Bitmap2Bytes(bitmapFormUri);
//                bitmapFormUri.recycle();
                Context context = activity.getApplicationContext();
                String output = context.getFilesDir().getAbsolutePath();
                File file = new File(filePath);
                LogUtil.i("原始大小" + file.length());
                Luban.with(context)
                        .load(file)                          // 传人要压缩的图片列表
                        .ignoreBy(100)                                  // 忽略不压缩图片的大小
                        .setTargetDir(output)                        // 设置压缩后文件存储位置
                        .setCompressListener(new OnCompressListener() { //设置回调
                            @Override
                            public void onStart() {
                                // 压缩开始前调用，可以在方法内启动 loading UI
                            }

                            @Override
                            public void onSuccess(File file) {
                                // 压缩成功后调用，返回压缩后的图片文件
                                LogUtil.i("压缩后的大小" + file.length());
                                // 上传图片
                                UpLoadImageUtils.upLoadImage(activity, file.getAbsolutePath(), null, sign, onUploadListener);
                            }

                            @Override
                            public void onError(Throwable e) {
                                // 当压缩过程出现问题时调用
                                onUploadListener.onUploadFailed(-999, "图片压缩错误" + filePath);
                            }
                        }).launch();    //启动压缩

            }

            @Override
            public void onError(final int errorCode, final String errorMsg) {
                // 回调在主线程
                if (onUploadListener != null)
                    onUploadListener.onUploadFailed(errorCode, errorMsg);

            }
        });

    }

    /**
     * 上传多张图片,里面进行了压缩
     */
    public static void upLoadMultiImageInMainThread(final Activity activity, final List<String> paths, final OnMultiUploadListener onUploadListener) {
        final ArrayList<String> urls = new ArrayList<>();
        final SparseArray<String> urlMap = new SparseArray<>();
        if (paths.size() == 0 && onUploadListener != null) {
            onUploadListener.onUploadSucceed(urls, "");
            return;
        }
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            int finalI = i;
            upLoadSingleImageInMainThread(activity, path, new OnUploadListener() {
                @Override
                public void onUploadSucceed(String filePath, String url) {
                    // 回调在主线程
//                    urls.add(finalI, url);
                    urlMap.put(finalI, url);
                    LogUtil.i("上传图片成功", "第" + urlMap.size() + "张图片地址：" + url);
                    if (urlMap.size() == paths.size() && onUploadListener != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < urlMap.size(); i++) {
                            // 拼接
                            if (i != 0) {
                                sb.append(",");
                            }
                            sb.append(urlMap.get(i));
                            // 排序
                            urls.add(urlMap.get(i));
                        }
                        final String simg = sb.toString();

                        onUploadListener.onUploadSucceed(urls, simg);
                    }
                }

                @Override
                public void onUploadFailed(int errorCode, String errorMsg) {
                    // 回调在主线程

                    LogUtil.i("errorMsg" + errorMsg);
                    if (onUploadListener != null) {
                        onUploadListener.onUploadFailed(errorCode, errorMsg);
                    }
                }
            });
        }

    }

    /**
     * filePath、、bytes二选一
     * 回调在主线程
     */
    @SuppressWarnings("SameParameterValue")
    private static void upLoadImage(final Activity activity, String filePath, byte[] bytes, String photoSign, final OnUploadListener onUploadListener) {
        // 实例化Photo业务上传管理类
        IUploadTaskListener iUploadTaskListener = new IUploadTaskListener() {

            @Override
            public void onUploadSucceed(final FileInfo result) {
                // 回调在子线程
                activity.runOnUiThread(() -> {
                    // 回调在主线程
                    if (onUploadListener != null)
                        onUploadListener.onUploadSucceed(filePath, result.url);

                });
            }

            @Override
            public void onUploadStateChange(ITask.TaskState state) {
            }

            @Override
            public void onUploadProgress(long totalSize, long sendSize) {
//                        long p = (long) ((sendSize * 100) / (totalSize * 1.0f));
//                        Log.i("Demo", "上传进度: " + p + "%");
            }

            @Override
            public void onUploadFailed(final int errorCode, final String errorMsg) {
                // 回调在子线程
                activity.runOnUiThread(() -> {
                    // 回调在主线程
                    if (onUploadListener != null)
                        onUploadListener.onUploadFailed(errorCode, errorMsg);
                });

            }
        };
        PhotoUploadTask task;
        if (TextUtils.isEmpty(filePath))
            task = new PhotoUploadTask(bytes, iUploadTaskListener);
        else
            task = new PhotoUploadTask(filePath, iUploadTaskListener);
        task.setBucket(BUCKET);  // 设置Bucket(命名空间)
        task.setFileId(SAMPLE + System.currentTimeMillis()); // 可以为图片自定义FileID(可选)

        task.setAuth(photoSign);
        getUploadManager(activity.getApplicationContext()).upload(task);  // 开始上传
    }


    public interface OnUploadListener {
        void onUploadSucceed(String filePath, String url);

        void onUploadFailed(int errorCode, String errorMsg);
    }

    public interface OnMultiUploadListener {
        /**
         * @param urls 上传后的地址有序的集合
         * @param simg 用，拼接好的地址
         */
        void onUploadSucceed(List<String> urls, String simg);

        void onUploadFailed(int errorCode, String errorMsg);
    }
}
