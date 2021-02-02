package com.github.yedp.ez.cache.core.cache.caffeine;

import com.alibaba.fastjson.JSON;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.yedp.ez.cache.core.cache.AbstractValueAdaptingCache;
import com.github.yedp.ez.cache.core.setting.CaffeineCacheSetting;
import com.github.yedp.ez.cache.core.stats.StatsEnum;
import com.github.yedp.ez.cache.core.support.ExpireMode;
import com.github.yedp.ez.cache.core.support.NullValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author yedp
 * @date 2021-02-01 09:07:35
 * @comment 基于Caffeine实现的缓存
 **/
public class CaffeineCache extends AbstractValueAdaptingCache {
    protected static final Logger logger = LoggerFactory.getLogger(CaffeineCache.class);

    /**
     * 缓存对象
     */
    private final Cache<Object, Object> cache;

    /**
     * 使用name和{@link CaffeineCacheSetting}创建一个 {@link CaffeineCache} 实例
     *
     * @param name                 缓存名称
     * @param caffeineCacheSetting 一级缓存配置 {@link CaffeineCacheSetting}
     * @param stats                是否开启统计模式
     */
    public CaffeineCache(String name, CaffeineCacheSetting caffeineCacheSetting, boolean stats) {
        super(stats, name);
        this.cache = getCache(caffeineCacheSetting);
    }

    @Override
    public Cache<Object, Object> getNativeCache() {
        return this.cache;
    }


    @Override
    public String get(String key) {
        return this.get(key, String.class);
    }

    @Override
    public <T> T get(String key, Class<T> resultType) {
        if (logger.isDebugEnabled()) {
            logger.debug("caffeine缓存 key={} 获取缓存", JSON.toJSONString(key));
        }

        T result = null;
        if (this.cache instanceof LoadingCache) {
            result = (T) ((LoadingCache<Object, Object>) this.cache).get(key);
        }
        result = (T) cache.getIfPresent(key);
        super.statsAddByRs(result);
        return result;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> resultType, Callable<T> valueLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug("caffeine缓存 key={} 获取缓存， 如果没有命中就走库加载缓存", JSON.toJSONString(key));
        }

        Object result = this.cache.get(key, k -> loaderValue(key, valueLoader));
        // 如果不允许存NULL值 直接删除NULL值缓存
        boolean isEvict = !isAllowNullValues() && (result == null || result instanceof NullValue);
        if (isEvict) {
            evict(key);
        }
        super.statsAddByRs(result);
        return (T) fromStoreValue(result);
    }


    @Override
    public void put(String key, Object value) {
        // 允许存NULL值
        if (isAllowNullValues()) {
            if (logger.isDebugEnabled()) {
                logger.debug("caffeine缓存 key={} put缓存，缓存值：{}", JSON.toJSONString(key), JSON.toJSONString(value));
            }
            this.cache.put(key, toStoreValue(value));
            return;
        }

        // 不允许存NULL值
        if (value != null && !(value instanceof NullValue)) {
            if (logger.isDebugEnabled()) {
                logger.debug("caffeine缓存 key={} put缓存，缓存值：{}", JSON.toJSONString(key), JSON.toJSONString(value));
            }
            this.cache.put(key, toStoreValue(value));
            return;
        }
        logger.debug("缓存值为NULL并且不允许存NULL值，不缓存数据");
    }

    @Override
    public String putIfAbsent(String key, String value) {
        return this.putIfAbsent(key, value, String.class);
    }

    @Override
    public <T> T putIfAbsent(String key, Object value, Class<T> resultType) {
        if (logger.isDebugEnabled()) {
            logger.debug("caffeine缓存 key={} putIfAbsent 缓存，缓存值：{}", JSON.toJSONString(key), JSON.toJSONString(value));
        }
        boolean flag = !isAllowNullValues() && (value == null || value instanceof NullValue);
        if (flag) {
            return null;
        }
        Object result = this.cache.get(key, k -> toStoreValue(value));
        return (T) fromStoreValue(result);
    }

    @Override
    public void evict(String key) {
        if (logger.isDebugEnabled()) {
            logger.debug("caffeine缓存 key={} 清除缓存", JSON.toJSONString(key));
        }
        this.cache.invalidate(key);
    }

    @Override
    public void clear() {
        logger.debug("caffeine缓存 key={} 清空缓存");
        this.cache.invalidateAll();
    }

    /**
     * 加载数据
     */
    private <T> Object loaderValue(Object key, Callable<T> valueLoader) {
        long start = System.currentTimeMillis();
        try {
            T t = valueLoader.call();
            if (logger.isDebugEnabled()) {
                logger.debug("caffeine缓存 key={} 从库加载缓存", JSON.toJSONString(key), JSON.toJSONString(t));
            }
            super.statsAdd(StatsEnum.LOAD);
            return toStoreValue(t);
        } catch (Exception e) {
            throw new LoaderCacheValueException(key, e);
        }

    }

    /**
     * 根据配置获取本地缓存对象
     *
     * @param caffeineCacheSetting 一级缓存配置
     * @return {@link Cache}
     */
    private static Cache<Object, Object> getCache(CaffeineCacheSetting caffeineCacheSetting) {
        // 根据配置创建Caffeine builder
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder.initialCapacity(caffeineCacheSetting.getInitialCapacity());
        builder.maximumSize(caffeineCacheSetting.getMaximumSize());
        if (ExpireMode.WRITE.equals(caffeineCacheSetting.getExpireMode())) {
            builder.expireAfterWrite(caffeineCacheSetting.getExpireTime(), caffeineCacheSetting.getTimeUnit());
        } else if (ExpireMode.ACCESS.equals(caffeineCacheSetting.getExpireMode())) {
            builder.expireAfterAccess(caffeineCacheSetting.getExpireTime(), caffeineCacheSetting.getTimeUnit());
        }
        // 根据Caffeine builder创建 Cache 对象
        return builder.build();
    }

    @Override
    public boolean isAllowNullValues() {
        return false;
    }

    @Override
    public long estimatedSize() {
        return cache.estimatedSize();
    }


}
