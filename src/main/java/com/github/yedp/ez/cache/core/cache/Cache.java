package com.github.yedp.ez.cache.core.cache;

import java.util.concurrent.Callable;

/**
 *
 *@author  yedp
 *@date    2021-01-30 16:47:56
 *@comment 
 *
 **/
public interface Cache {
    /**
     * 返回缓存名称
     *
     * @return String
     */
    String getName();

    /**
     * 返回真实Cache对象
     *
     * @return Object
     */
    Object getNativeCache();

    /**
     * 根据KEY返回缓存中对应的值，并将其返回类型转换成对应类型，如果对应key不存在返回NULL
     *
     * @param key        缓存key
     * @param resultType 返回值类型
     * @param <T>        Object
     * @return 缓存key对应的值
     */
    <T> T get(String key, Class<T> resultType);

    /**
     * 根据KEY返回缓存中对应的值，并将其返回类型转换成对应类型，如果对应key不存在则调用valueLoader加载数据
     *
     * @param key         缓存key
     * @param resultType  返回值类型
     * @param valueLoader 加载缓存的回调方法
     * @param <T>         Object
     * @return 缓存key对应的值
     */
    <T> T get(String key, Class<T> resultType, Callable<T> valueLoader);

    /**
     * 将对应key-value放到缓存，如果key原来有值就直接覆盖
     *
     * @param key   缓存key
     * @param value 缓存的值
     */
    void put(String key, Object value);

    /**
     * 如果缓存key没有对应的值就将值put到缓存，如果有就直接返回原有的值
     *
     * @param key        缓存key
     * @param value      缓存key对应的值
     * @param resultType 返回值类型
     * @param <T> T
     * @return 因为值本身可能为NULL，或者缓存key本来就没有对应值的时候也为NULL，
     * 所以如果返回NULL就表示已经将key-value键值对放到了缓存中
     */
    <T> T putIfAbsent(String key, Object value, Class<T> resultType);

    /**
     * 在缓存中删除对应的key
     *
     * @param key 缓存key
     */
    void evict(String key);

    /**
     * 清楚缓存
     */
    void clear();


    /**
     * 缓存预估size
     *
     * @return 预估大小
     */
    default long estimatedSize() {
        return 0;
    }
}
