package com.github.yedp.ez.core.cache;

import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author daoping.ye
 * @create 2020/8/18 10:56
 * @desc
 */
public class GuavaCache implements ICache, InitializingBean {


    @Override
    public List listLocalCache() {
        return null;
    }

    @Override
    public Object getLocalCache() {
        return null;
    }

    @Override
    public boolean clearLocalCache() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
