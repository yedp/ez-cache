/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.yedp.ez.cache.core.cache;

import com.alibaba.fastjson.JSON;

import com.github.yedp.ez.cache.core.stats.CacheStats;
import com.github.yedp.ez.cache.core.stats.StatsEnum;
import com.github.yedp.ez.cache.core.support.NullValue;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.concurrent.Callable;


/**
 * @author yedp
 * @date 2021-01-30 17:40:47
 * @comment Cache 接口的抽象实现类，对公共的方法做了一写实现，如是否允许存NULL值
 * <p>如果允许为NULL值，则需要在内部将NULL替换成{@link NullValue#INSTANCE} 对象
 **/
public abstract class AbstractValueAdaptingCache implements ICache, IStats {

    /**
     * 缓存名称
     */
    private final String name;

    /**
     * 是否开启统计功能
     */
    private boolean statsSwitch;
    /**
     * 缓存统计类
     */
    private CacheStats cacheStats = new CacheStats();

    /**
     * 通过构造方法设置缓存配置
     *
     * @param statsSwitch 是否开启监控统计
     * @param name        缓存名称
     */
    protected AbstractValueAdaptingCache(boolean statsSwitch, String name) {
        Assert.notNull(name, "缓存名称不能为NULL");
        this.setStatsSwitch(statsSwitch);
        this.name = name;
    }

    /**
     * 获取是否允许存NULL值
     *
     * @return true:允许，false:不允许
     */
    public abstract boolean isAllowNullValues();

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Convert the given value from the internal store to a user value
     * returned from the get method (adapting {@code null}).
     *
     * @param storeValue the store value
     * @return the value to return to the user
     */
    protected Object fromStoreValue(Object storeValue) {
        if (isAllowNullValues() && storeValue instanceof NullValue) {
            return null;
        }
        return storeValue;
    }

    /**
     * Convert the given user value, as passed into the put method,
     * to a value in the internal store (adapting {@code null}).
     *
     * @param userValue the given user value
     * @return the value to store
     */
    protected Object toStoreValue(Object userValue) {
        if (isAllowNullValues() && userValue == null) {
            return NullValue.INSTANCE;
        }
        return userValue;
    }


    /**
     * {@link #get(String, Class, Callable)} 方法加载缓存值的包装异常
     */
    public class LoaderCacheValueException extends RuntimeException {

        private final Object key;

        public LoaderCacheValueException(Object key, Throwable ex) {
            super(String.format("加载key为 %s 的缓存数据,执行被缓存方法异常", JSON.toJSONString(key)), ex);
            this.key = key;
        }

        public Object getKey() {
            return this.key;
        }
    }

    protected void statsAddByRs(Object object) {
        if (object == null || object instanceof NullValue) {
            statsAdd(StatsEnum.MISS);
        }
        statsAdd(StatsEnum.HIT);
    }

    protected void statsAdd(StatsEnum statsEnum) {
        if (!this.getStatusSwitch()) {
            return;
        }
        cacheStats.addRequestCount(1);
        if (StatsEnum.HIT.equals(statsEnum)) {
            cacheStats.addHitCount(1);
        } else if (StatsEnum.LOAD.equals(statsEnum)) {
            cacheStats.addLoadCount(1);
        }
    }

    /**
     * 获取统计信息
     *
     * @return CacheStats
     */
    @Override
    public CacheStats getCacheStats() {
        return cacheStats;
    }

    @Override
    public void setStatsSwitch(boolean status) {
        if (this.statsSwitch && status) {
            return;
        }
        this.statsSwitch = status;
        if (status) {
            cacheStats.clear();
        }
    }

    /**
     * 获取是否开启统计
     *
     * @return true：开启统计，false：关闭统计
     */
    @Override
    public boolean getStatusSwitch() {
        return statsSwitch;
    }
}
