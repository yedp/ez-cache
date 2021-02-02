package com.github.yedp.ez.cache.core;

import com.alibaba.fastjson.JSON;
import com.github.yedp.ez.cache.core.cache.ICache;
import com.github.yedp.ez.cache.core.cache.IStats;
import com.github.yedp.ez.cache.core.cache.caffeine.CaffeineCache;
import com.github.yedp.ez.cache.core.setting.CaffeineCacheSetting;
import com.github.yedp.ez.cache.core.support.ExpireMode;
import org.junit.Before;
import org.junit.Test;


import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author yedp
 * @date 2021/1/3017:52
 * @comment
 **/
public class CaffeineCacheTest {
    ICache cache = null;

    @Before
    public void before() {
        CaffeineCacheSetting setting = new CaffeineCacheSetting();
        setting.setExpireMode(ExpireMode.WRITE);
        setting.setExpireTime(1000);
        setting.setMaximumSize(100);
        setting.setTimeUnit(TimeUnit.SECONDS);
        cache = new CaffeineCache("test", setting, true);
    }

    @Test
    public void testPut() {
        String key1 = "testKey1";
        String value1 = "testValue1";
        cache.put(key1, value1);
        IStats stats = (IStats) cache;
        System.out.println(key1 + ":" + cache.get(key1) + JSON.toJSONString(stats.getCacheStats()));

        String key2 = "testKey2";
        System.out.println(key2 + ":" + cache.get(key2, String.class) + JSON.toJSONString(stats.getCacheStats()));

        String key3 = "testKey3";
        System.out.println(key3 + ":" + cache.get(key3, Long.class, new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return System.currentTimeMillis();
            }
        }) + JSON.toJSONString(stats.getCacheStats()));


        System.out.println(key3 + ":" + cache.get(key3, Long.class) + JSON.toJSONString(stats.getCacheStats()));

    }
}
