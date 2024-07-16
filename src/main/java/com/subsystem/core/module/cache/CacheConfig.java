package com.subsystem.core.module.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.subsystem.core.common.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
@AllArgsConstructor
public class CacheConfig {
    @Resource
    ApplicationContext eventDrivenModule;


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
        list.add(new CaffeineCache(Constants.SYN_REDIS,
                Caffeine.newBuilder()
                        .initialCapacity(Constants.SYN_REDIS_INITIALCAPACITY)//后面可以考虑将这些值放入配置文件
                        .maximumSize(Constants.DEFAULT_MAXIMUMSIZE)
                        .removalListener((key, value, cause) -> {
                            log.debug("SYN_REDIS失效");
                            log.debug("key:{}", key);
                            log.debug("value:{}", value);
                            log.debug("cause:{}", cause);
                        })
                        .build()));
        list.add(new CaffeineCache(Constants.LOCAL,
                Caffeine.newBuilder()
                        .initialCapacity(Constants.LOCAL_INITIALCAPACITY)
                        .maximumSize(Constants.DEFAULT_MAXIMUMSIZE)
                        .expireAfterWrite(Constants.ONLINE_CACHE_EXPIRES, TimeUnit.SECONDS)
                        .build()));
        list.add(new CaffeineCache(Constants.SYN_REDIS_FAILED,
                Caffeine.newBuilder()
                        .initialCapacity(Constants.SYN_REDIS_FAILED_INITIALCAPACITY)
                        .maximumSize(Constants.DEFAULT_MAXIMUMSIZE)
                        .build()));
        list.add(new CaffeineCache(Constants.THRESHOLD_CACHE,
                Caffeine.newBuilder()
                        .initialCapacity(Constants.THRESHOLD_CACHE_INITIALCAPACITY)
                        .maximumSize(Constants.DEFAULT_MAXIMUMSIZE)
                        .removalListener((key, value, cause) -> {
                            log.debug("THRESHOLD_CACHE失效");
                            log.debug("key:{}", key);
                            log.debug("value:{}", value);
                            log.debug("cause:{}", cause);
                        })
                        .expireAfterWrite(Constants.THRESHOLD_CACHE_EXPIRES, TimeUnit.SECONDS)
                        .build()));
        cacheManager.setCaches(list);
        return cacheManager;
    }



    /*
      创建基于Caffeine的Cache Manager
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

}

