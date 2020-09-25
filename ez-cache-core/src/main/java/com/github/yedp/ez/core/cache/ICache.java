package com.github.yedp.ez.core.cache;

import java.util.Collections;
import java.util.List;

/**
 * @Author daoping.ye
 * @create 2020/8/18 10:38
 * @desc
 */
public interface ICache<T> {
    /**
     * 默认缓存时间5分钟
     */
    int DEFAULT_CACHE_TIMEOUT = 5;

    /**
     * 默认缓存存储大小
     */
    int DEFAULT_MAXIMUM_SIZE = 1;

    /**
     * 获取本地缓存list
     */
    default List<T> listLocalCache() {
        return Collections.emptyList();
    }

    /**
     * 获取本地缓存
     */
    default T getLocalCache() {
        return null;
    }

    /**
     * 清除本地缓存
     */
    default boolean clearLocalCache() {
        return false;
    }
}
