package com.github.yedp.ez.cache.core.cache;

import com.github.yedp.ez.cache.core.stats.CacheStats;

/**
 * @author yedp
 * @date 2021-01-30 16:47:56
 * @comment 缓存统计相关接口
 **/
public interface IStats {
    /**
     * 设置统计开关
     *
     * @param status true-打开；false-关闭
     */
    void setStatsSwitch(boolean status);

    /**
     * 获取统计开关状态
     *
     * @return
     */
    boolean getStatusSwitch();

    /**
     * 获取统计信息
     *
     * @return {@link CacheStats}
     */
    CacheStats getCacheStats();
}
