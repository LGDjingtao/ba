package com.subsystem.module.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
//    /**
//     * Caffeine配置说明：
//     * initialCapacity=[integer]: 初始的缓存空间大小
//     * maximumSize=[long]: 缓存的最大条数
//     * maximumWeight=[long]: 缓存的最大权重
//     * expireAfterAccess=[duration]: 最后一次写入或访问后经过固定时间过期
//     * expireAfterWrite=[duration]: 最后一次写入后经过固定时间过期
//     * refreshAfterWrite=[duration]: 创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存
//     * weakKeys: 打开key的弱引用
//     * weakValues：打开value的弱引用
//     * softValues：打开value的软引用
//     * recordStats：开发统计功能
//     * 注意：
//     * expireAfterWrite和expireAfterAccess同事存在时，以expireAfterWrite为准。
//     * maximumSize和maximumWeight不可以同时使用
//     * weakValues和softValues不可以同时使用
//     */
//    @Bean
//    public CacheManager cacheManager() {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//        List<CaffeineCache> list = new ArrayList<>();
//        //循环添加枚举类中自定义的缓存，可以自定义
//        for (CacheEnum cacheEnum : CacheEnum.values()) {
//            list.add(new CaffeineCache(cacheEnum.getName(),
//                    Caffeine.newBuilder()
//                            .initialCapacity(50)
//                            .maximumSize(1000)
//                            .expireAfterAccess(cacheEnum.getExpires(), TimeUnit.SECONDS)
//                            .build()));
//        }
//        cacheManager.setCaches(list);
//        return cacheManager;
//    }

    /**
     * 创建基于Caffeine的Cache Manager
     *
     * @return
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        ArrayList<CaffeineCache> caches = new ArrayList<>();
        Map<String, Object> map = getCacheType();
        for (String name : map.keySet()) {
            caches.add(new CaffeineCache(name, (Cache<Object, Object>) map.get(name)));
        }
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * 初始化自定义缓存策略
     *
     * @return
     */
    private static Map<String, Object> getCacheType() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("name1", Caffeine.newBuilder().recordStats()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .maximumSize(100)
                .build());
        map.put("name2", Caffeine.newBuilder().recordStats()
                .expireAfterWrite(50, TimeUnit.SECONDS)
                .maximumSize(50)
                .build());
        return map;
    }

}

