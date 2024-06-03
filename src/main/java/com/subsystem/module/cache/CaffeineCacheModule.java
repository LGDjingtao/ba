package com.subsystem.module.cache;


import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.subsystem.common.Constants;
import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.event.SynRedisEvent;
import com.subsystem.module.redis.StringRedisModule;
import com.subsystem.repository.RepositoryModule;
import com.subsystem.repository.mapping.LinkageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CaffeineCacheModule {

    @Resource
    CacheManager cacheManager;
    @Resource
    StringRedisModule redisModule;
    @Lazy
    @Resource
    CaffeineCacheModule caffeineCacheModule;
    @Resource
    RepositoryModule repositoryModule;


    /**
     * 通过key 获取同步缓存
     */
    @Cacheable(cacheNames = Constants.SYN_REDIS, key = "#key")
    public String setInitSynchronizeRedisCacheValue(String key, String realTimeData) {
        return realTimeData;
    }

    /**
     * 通过key 获取同步缓存
     */
    @Cacheable(cacheNames = Constants.SYN_REDIS, key = "#key")
    public String getSynchronizeRedisCacheValue(String key) {
        return Constants.EMPTY_JSON_OBJ;
    }

    /**
     * 更新同步缓存
     * 同步缓存同步到redis
     * 更新同步失败缓存
     */
    @CachePut(cacheNames = Constants.SYN_REDIS, key = "#key")
    public String setSynchronizeRedisCacheValue(String key, String realTimeData) throws Exception {
        synExceptionData(key, realTimeData);
        try {
            double random = Math.random();
            if (random > 0.5) throw new Exception("测试");
            redisModule.set(key, realTimeData);
        } catch (Exception e) {
            log.error("同步到redis失败\n设备:{}\n数据:{}\n", key, realTimeData, e);
            saveExceptionData(key, realTimeData);
        }
        //同步成功 后 删除mysql和缓存
        rmSynRedisFailedCacheValue(key);
        return realTimeData;
    }

    /**
     * 同步最新的异常数据到数据库
     *
     * @param key          物模型数据key
     * @param realTimeData 实时物模型数据
     */
    private void synExceptionData(String key, String realTimeData) throws Exception {
        //查询 key 是否是异常数据
        String synRedisFailedCache = caffeineCacheModule.getSynRedisFailedCacheValue(key);
        //如果不在缓存里面 就不做处理
        if (null == synRedisFailedCache) return;
        //在异常缓存里面 就更新最新的异常数据到数据库
        saveExceptionData(key, realTimeData);
    }

    /**
     * redis同步事件
     *
     * @param synRedisEvent redis同步事件
     */
    @EventListener(classes = SynRedisEvent.class)
    public void synRedisEventListener(SynRedisEvent synRedisEvent) {
        String key = synRedisEvent.getKey();
        String realTimeData = synRedisEvent.getRealTimeData();
        //修改同步缓存
        try {
            caffeineCacheModule.setSynchronizeRedisCacheValue(key, realTimeData);
        } catch (Exception e) {
            log.error("redis同步事件失败\nkey:{}\nvalue:{}\n", key, realTimeData, e);
        }

    }

    /**
     * 保存和更新异常数据
     *
     * @param key          物模型数据key
     * @param realTimeData 实时物模型数据
     */
    private void saveExceptionData(String key, String realTimeData) throws Exception {
        //异常数据落库
        repositoryModule.saveSyncFailedData(key, realTimeData);
        //落库成功后才更新缓存 保证数据一致性 落库失败更新缓存没意义
        caffeineCacheModule.setSynRedisFailedCacheValue(key, realTimeData);
    }

//    /**
//     * 通过key获取本地缓存
//     */
//    @Cacheable(cacheNames = Constants.LOCAL, key = "#key")
//    public Object getLocalCacheValue(String key) {
//        return Constants.EMPTY_JSON_OBJ;
//    }

    /**
     * 更新 本地缓存
     */
    @CachePut(cacheNames = Constants.LOCAL, key = "#key")
    public Object setLocalCache(String key) {
        //todo 本地缓存内容设计
        return key;
    }


    /**
     * 通过key 获取 同步失败缓存
     */
    public Cache<Object, Object> getSynRedisFailedCache() {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(Constants.SYN_REDIS_FAILED);
        return cache.getNativeCache();
    }

    /**
     * 通过key 获取 同步失败缓存
     */
    @Cacheable(cacheNames = Constants.SYN_REDIS_FAILED, key = "#key", unless = "#result == null")
    public String getSynRedisFailedCacheValue(String key) {
        return null;
    }

    /**
     * 更新 同步失败缓存
     *
     * @param key          物模型数据数据key
     * @param realTimeData 实时物模型数据
     */
    @CachePut(cacheNames = Constants.SYN_REDIS_FAILED, key = "#key")
    public String setSynRedisFailedCacheValue(String key, String realTimeData) {
        return realTimeData;
    }

    /**
     * 删除缓存
     *
     * @param key 物模型数据数据key
     */
    @CacheEvict(cacheNames = Constants.SYN_REDIS_FAILED, key = "#key", condition = "#key")
    public void rmSynRedisFailedCacheValue(String key) {
        String synRedisFailedCacheValue = caffeineCacheModule.getSynRedisFailedCacheValue(key);
        if (null == synRedisFailedCacheValue) return;
        repositoryModule.deleteSyncFailedDataByKey(key);
    }


}
