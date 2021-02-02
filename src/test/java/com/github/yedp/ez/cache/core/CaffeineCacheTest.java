package com.github.yedp.ez.cache.core;

import com.alibaba.fastjson.JSON;
import com.github.yedp.ez.cache.core.cache.ICache;
import com.github.yedp.ez.cache.core.cache.caffeine.CaffeineCache;
import com.github.yedp.ez.cache.core.setting.CaffeineCacheSetting;
import com.github.yedp.ez.cache.core.support.ExpireMode;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author yedp
 * @date 2021/1/3017:52
 * @comment
 **/
public class CaffeineCacheTest {
    ICache cache = null;

    @Before
    public void before(){
        CaffeineCacheSetting setting = new CaffeineCacheSetting();
        setting.setExpireMode(ExpireMode.WRITE);
        setting.setExpireTime(1000);
        setting.setMaximumSize(100);
        setting.setTimeUnit(TimeUnit.SECONDS);
        cache = new CaffeineCache("test",setting,true);
    }
    @Test
    public void testPut(){
        String key = "testKey";
        String value = "testValue";
        cache.put(key,value);
        System.out.println(cache.get(key));
        System.out.println(JSON.toJSONString(cache));
    }
}
