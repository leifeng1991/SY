package com.xxzlkj.zhaolinshouyin.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.xxzlkj.zhaolinshouyin.BuildConfig;

import java.io.File;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/22 14:24
 */

public class GreenDaoContext extends ContextWrapper {

    private final Context context;

    public GreenDaoContext(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象
     */
    @Override
    public File getDatabasePath(String dbName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // sd 卡存在
            File databaseFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + (BuildConfig.isTest ? "databaseTest" : "database"));
            if (!databaseFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                databaseFile.mkdir();
            }
            return new File(databaseFile, dbName);
        } else {
            // sd卡不存在
            return context.getDatabasePath(dbName);
        }
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int,
     * android.database.sqlite.SQLiteDatabase.CursorFactory,
     * android.database.DatabaseErrorHandler)
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

}
