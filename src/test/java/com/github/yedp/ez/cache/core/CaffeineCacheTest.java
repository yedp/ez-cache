package com.github.yedp.ez.cache.core;

import com.github.yedp.ez.cache.core.cache.Cache;
import com.github.yedp.ez.cache.core.cache.caffeine.CaffeineCache;
import com.github.yedp.ez.cache.core.setting.FirstCacheSetting;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.PreDestroy;

/**
 * @author yedp
 * @date 2021/1/3017:52
 * @comment
 **/
public class CaffeineCacheTest {
    Cache cache = null;

    @Before
    public void before(){
        FirstCacheSetting setting = new FirstCacheSetting();
        cache = new CaffeineCache("test",setting,false);
    }
    @Test
    public void testPut(){
        String key = "testKey";
        String value = "testValue";
        cache.put(key,value);
    }
}
