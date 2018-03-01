package com.xxzlkj.zhaolinshare.base.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

/**
 * 描述:apk下载的工具类
 */
public class ApkDownloadUtils {

    /**
     * @param downloadFolderPath 下载apk的目录
     * @param downloadFileName   下载apk的名，尾缀为apk
     */
    public static long downloadEnqueue(Context context, String uriString, String downloadFolderPath, String downloadFileName, String description) {

        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(uriString);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // 设置下载路径和文件名
        request.setDestinationInExternalPublicDir(downloadFolderPath, downloadFileName);
        request.setDescription(description);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");

        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        return dManager.enqueue(request);
    }
}


