package com.xxzlkj.zhaolinshare.base.util;

import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;

/**
 * 描述:
 *
 * @author leifeng
 *         2018/1/9 10:24
 */


public class CacheGlideUrl extends GlideUrl{
    private String mUrl;

    public CacheGlideUrl(String url) {
        super(url);
        mUrl = url;
    }

    @Override
    public String getCacheKey() {
        return checkQnUrl() && !TextUtils.isEmpty(getMyCacheKey()) ? getMyCacheKey() : super.getCacheKey();
    }

    private String getMyCacheKey() {
        String cacheKey = null;
        int index = mUrl.indexOf("?");
        if (index != -1) {
            cacheKey = mUrl.substring(0, index);
        }
        return cacheKey;
    }

    public boolean checkQnUrl() {
        if (!TextUtils.isEmpty(mUrl) && (mUrl.contains("?"))) {
            return true;
        }
        return false;
    }
}
