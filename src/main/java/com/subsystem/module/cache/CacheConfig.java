package com.subsystem.module.cache;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.subsystem.common.Constants;
import com.subsystem.event.LinkageEvent;
import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.module.linkage.LinkageInfo;
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
        list.add(new CaffeineCache(Constants.SYN_REDIS,
                Caffeine.newBuilder()
                        .initialCapacity(2000)
                        .maximumSize(10000)
                        .removalListener((key, value, cause) -> {
                            log.info("SYN_REDIS失效");
                            log.info("key:{}",key);
                            log.info("value:{}",value);
                            log.info("cause:{}",cause);
                        })
                        .build(loadCache -> loadCache)));
        list.add(new CaffeineCache(Constants.LOCAL,
                Caffeine.newBuilder()
                        .initialCapacity(50)
                        .maximumSize(10000)
                        //.expireAfterAccess(Constants.EXPIRES_15_DAYS, TimeUnit.SECONDS)
                        .expireAfterWrite(20000, TimeUnit.SECONDS)
                        //缓存失效通知
                        .removalListener((key, value, cause) -> {
                            //todo 清理通知 key,value ==> 键值对   cause ==> 清理原因
                            //todo 通过事件通知 告警服务去告警
                        })
                        .build(loadCache -> loadCache)));
        list.add(new CaffeineCache(Constants.SYN_REDIS_FAILED,
                Caffeine.newBuilder()
                        .initialCapacity(20)
                        .maximumSize(10000)
                        .build(loadCache -> loadCache)));
        list.add(new CaffeineCache(Constants.LINKAGE,
                Caffeine.newBuilder()
                        .initialCapacity(20)
                        .maximumSize(100)
                        //联动缓存15分钟失效
                        .expireAfterWrite(Constants.EXPIRES_15_MIN, TimeUnit.SECONDS)
                        .removalListener((key, value, cause) -> {
                            //todo 替换不触发
                            //失效后通知联动检测任务再次检测
                            SubSystemDefaultContext subSystemDefaultContext = JSONObject.parseObject((String) value, SubSystemDefaultContext.class);
                            LinkageInfo linkageInfo = subSystemDefaultContext.getLinkageInfo();
                            linkageInfo.setFirst(false);
                            LinkageEvent linkageEvent = new LinkageEvent(this, subSystemDefaultContext);
                            eventDrivenModule.publishEvent(linkageEvent);
                        })
                        .build(loadCache -> loadCache)));
        cacheManager.setCaches(list);
        return cacheManager;
    }

}

