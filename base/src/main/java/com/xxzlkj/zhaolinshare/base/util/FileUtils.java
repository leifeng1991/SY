package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/1/16 13:35
 */

public class FileUtils {
    /**
     * 获取app缓存路径
     *
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) && context.getExternalCacheDir() != null) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 检测Sdcard是否存在
     */
    public static boolean isExitsSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getStorageDir(Context context) {
        File file = Environment.getExternalStorageDirectory();
        if (file.exists())
            return file;
        else
            return context.getFilesDir();
    }
}
