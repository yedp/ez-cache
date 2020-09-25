import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @Author daoping.ye
 * @create 2020/9/24 10:32
 * @desc
 */
public class GuavaCacheTest {
    private static final Logger logger = LoggerFactory.getLogger(GuavaCacheTest.class);
    private static final Long MAX_SIZE = 2000L;
    private static Cache<String, Object> cache = null;

    @Test
    public void testCacheExpired() throws InterruptedException, IOException {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(80000, TimeUnit.SECONDS)
                .expireAfterAccess(20000, TimeUnit.SECONDS)
                .maximumSize(MAX_SIZE)
                .recordStats().build();
        String key = "testKey";
        String value = "testValue";

        for (int i = 0; i < 10; i++) {
            cache.put(key, value + i);
            logger.info("key:{}; value:{}", key, cache.getIfPresent(key));
            Thread.sleep( 1000);
        }
    }
    @Test
    public void test() {
        System.out.println(204950766 >>> 30 & 3);
    }
}
