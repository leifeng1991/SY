package com.xxzlkj.zhaolinshare.base.net;

import android.support.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangrq
 */
public class RequestManager {
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "proxy_method thread #"
                    + this.mCount.getAndIncrement());
        }
    };
    private static ExecutorService mExecutorService;

    private RequestManager() {

    }

    public static <T> void createRequest(String urlString, Map<String, String> requestParams,
                                         OnRequestListener<T> listener) {
        // 线程提交任务
        RequestHandler<T> requestHandler = new RequestHandler<>(listener);
        RequestRunnable oMethodProxy = new RequestRunnable(urlString, requestParams, requestHandler);
        getExecutor().execute(oMethodProxy);
    }

    public static <T> void createRequest(String urlString, OnRequestListener<T> listener) {
        createRequest(urlString, null, listener);
    }

    private static ExecutorService getExecutor() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newFixedThreadPool(5, sThreadFactory);
        }
        return mExecutorService;
    }
}
