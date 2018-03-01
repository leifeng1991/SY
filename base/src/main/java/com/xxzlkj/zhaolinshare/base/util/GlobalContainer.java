package com.xxzlkj.zhaolinshare.base.util;

import java.util.HashMap;

/**
 * 应用全局容器
 *
 * @author zhangrq
 */
public class GlobalContainer {

    private HashMap<String, Object> container = new HashMap<>();// 用来存放一些参数的容器

    private final static GlobalContainer instance = new GlobalContainer();

    private GlobalContainer() {

    }

    public static GlobalContainer getInstance() {
        return instance;
    }

    public void putParam(String key, Object value) {
        container.put(key, value);
    }

    /**
     * 根据Key从容器中取出其对应的Value
     *
     * @param key   存放的Key
     * @param clazz 存放的类型
     */
    @SuppressWarnings("unchecked")
    public <T> T getParam(String key, Class<T> clazz) {
        return (T) container.get(key);
    }

    /**
     * 从容器中取出   存放的对应key 的value   并从容器里删除掉
     *
     * @param key   存放的Key
     * @param clazz 存放的类型
     */
    @SuppressWarnings("unchecked")
    public <T> T getParamAndRemove(String key, Class<T> clazz) {
        T object = (T) container.get(key);
        remove(key);
        return object;

    }

    /**
     * 移除某个key
     */
    public void remove(String key) {
        container.remove(key);
    }

    /**
     * 清除所有
     */
    public void clear() {
        container.clear();
    }
}
