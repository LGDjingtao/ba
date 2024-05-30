package com.subsystem.module.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.subsystem.Constants;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@AllArgsConstructor
public class CacheConfig {

    /**
     * 创建基于Caffeine的Cache Manager
     */
//    @Bean(name = CacheConstants.LOCAL)
//    public CacheManager localCacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        cacheManager.setCaffeine(getLocalCacheType());
//        return cacheManager;
//    }
//
//    private Caffeine<Object, Object> getLocalCacheType() {
//        return Caffeine.newBuilder().recordStats()
//                .initialCapacity(2000)
//                .maximumSize(10000)
//                .expireAfterWrite(CacheConstants.EXPIRES_10_MIN, TimeUnit.SECONDS);
//    }


    /**
     * Caffeine配置说明：
     * initialCapacity=[integer]: 初始的缓存空间大小
     * maximumSize=[long]: 缓存的最大条数
     * maximumWeight=[long]: 缓存的最大权重
     * expireAfterAccess=[duration]: 最后一次写入或访问后经过固定时间过期
     * expireAfterWrite=[duration]: 最后一次写入后经过固定时间过期
     * refreshAfterWrite=[duration]: 创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存
     * weakKeys: 打开key的弱引用
     * weakValues：打开value的弱引用
     * softValues：打开value的软引用
     * recordStats：开发统计功能
     * 注意：
     * expireAfterWrite和expireAfterAccess同事存在时，以expireAfterWrite为准。
     * maximumSize和maximumWeight不可以同时使用
     * weakValues和softValues不可以同时使用
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> list = new ArrayList<>();
        list.add(new CaffeineCache(Constants.SYNCHRONIZE_REDIS,
                Caffeine.newBuilder()
                        .initialCapacity(50)
                        .maximumSize(1000)
                        .expireAfterWrite(3, TimeUnit.SECONDS)
                        .build(loadCache-> loadCache)));
        list.add(new CaffeineCache(Constants.LOCAL,
                Caffeine.newBuilder()
                        .initialCapacity(50)
                        .maximumSize(1000)
                        .expireAfterAccess(5, TimeUnit.SECONDS)
                        .build(loadCache-> loadCache)));
        cacheManager.setCaches(list);
        return cacheManager;
    }

}

