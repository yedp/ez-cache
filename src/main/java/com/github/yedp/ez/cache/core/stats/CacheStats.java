package com.github.yedp.ez.cache.core.stats;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author yedp
 * @date 2021-02-01 18:25:10
 * @comment 缓存统计信息实体类
 **/
public final class CacheStats implements Serializable {
    /**
     * 请求缓存总数
     */
    private LongAdder requestCount = new LongAdder();
    /**
     * 加载数据总数
     */
    private LongAdder loadCount = new LongAdder();
    /**
     * 命中数
     */
    private LongAdder hitCount = new LongAdder();


    /**
     * 自增请求缓存总数
     *
     * @param add 自增数量
     */
    public void addRequestCount(long add) {
        requestCount.add(add);
    }

    /**
     * 加载缓存总数
     *
     * @param add 自增数量
     */
    public void addLoadCount(long add) {
        loadCount.add(add);
    }

    /**
     * 命中数增加
     *
     * @param add 自增数量
     */
    public void addHitCount(long add) {
        hitCount.add(add);
    }


    public LongAdder getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(LongAdder requestCount) {
        this.requestCount = requestCount;
    }

    public LongAdder getHitCount() {
        return hitCount;
    }

    public void setHitCount(LongAdder hitCount) {
        this.hitCount = hitCount;
    }

    public LongAdder getLoadCount() {
        return loadCount;
    }

    public void setLoadCount(LongAdder loadCount) {
        this.loadCount = loadCount;
    }

    public void clear() {
        requestCount.reset();
        hitCount.reset();
        loadCount.reset();
    }

}