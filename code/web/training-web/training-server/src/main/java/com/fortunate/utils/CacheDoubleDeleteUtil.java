package com.fortunate.utils;

import jakarta.annotation.PreDestroy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class CacheDoubleDeleteUtil {

    private final  RedisTemplate<String, ?> redisTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public CacheDoubleDeleteUtil( RedisTemplate<String, ?> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 执行双删：立即删 + 延迟删
     */
    public void doubleDelete(String pattern, long delayMillis) {
        // 第一次删除
        clearCache(pattern);
        // 延迟第二次删除
        scheduler.schedule(() -> clearCache(pattern), delayMillis, TimeUnit.MILLISECONDS);
    }

    private void clearCache(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    //线程池关闭
    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}