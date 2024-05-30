package com.subsystem.module.cache;


import com.github.benmanes.caffeine.cache.LoadingCache;
import com.subsystem.common.Constants;
import com.subsystem.module.redis.StringRedisModule;
import com.subsystem.repository.RepositoryModule;
import com.subsystem.repository.SyncFailedDataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CaffeineCacheModule {
    CacheManager cacheManager;
    StringRedisModule redisModule;
    SyncFailedDataRepository syncFailedDataRepository;
    CaffeineCacheModule caffeineCacheModule;
    RepositoryModule repositoryModule;

    /**
     * 根据缓存类型获取对应的缓存
     * 这里根据业务需要 获取LoadingCache，因为这个缓存实现了 批量存入操作
     */
    public LoadingCache getLoadingCache(String cacheType) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(cacheType);
        return (LoadingCache) cache.getNativeCache();
    }


    /**
     * 通过key 获取同步缓存
     */
    @Cacheable(cacheNames = Constants.SYN_REDIS, key = "#key")
    public String getSynchronizeRedisCacheValue(String key) {
        log.warn("在SYNCHRONIZE_REDIS 缓存中没有key为{}的value", key);
        return Constants.EMPTY_JSON_OBJ;
    }

    /**
     * 更新同步缓存
     * 同步缓存同步到redis
     * 更新同步失败缓存
     */
    @CachePut(cacheNames = Constants.SYN_REDIS, key = "#key")
    public String setSynchronizeRedisCacheValue(String key, String realTimeData) {
        synRealTimeDataToDataBase(key, realTimeData);
        synRedis(key, realTimeData);

        return realTimeData;
    }

    /**
     * 同步最新的数据到数据库
     *
     * @param key          物模型数据key
     * @param realTimeData 实时物模型数据
     */
    private void synRealTimeDataToDataBase(String key, String realTimeData) {
        //查询 key 是否有同步失败的缓存里面
        String synRedisFailedCache = (String) caffeineCacheModule.getSynRedisFailedCache(key);
        //如果不在缓存里面 就不做处理
        if (Constants.EMPTY_JSON_OBJ.equals(synRedisFailedCache)) return;
        //在缓存里面 就更新最新的数据到数据库
        repositoryModule.saveSyncFailedData(key, realTimeData);
    }

    /**
     * 同步数据到redis
     *
     * @param key          物模型数据key
     * @param realTimeData 实时物模型数据
     */
    private void synRedis(String key, String realTimeData) {
        try {
            redisModule.set(key, realTimeData);
        } catch (Exception e) {
            log.error("同步到redis失败\n设备:{}\n数据:{}\n", key, realTimeData, e);
            redisExceptionHandle(key, realTimeData);
        }
    }

    /**
     * 同步redis时出现异常的处理
     *
     * @param key          物模型数据key
     * @param realTimeData 实时物模型数据
     */
    private void redisExceptionHandle(String key, String realTimeData) {
        /**
         * 这个时候将出现本地和redis数据不一致情况
         * 但是我们保证本地缓存一定是最新的  方便后续告警业务正常进行 不会因为redis通信异常而不去告警
         * 再把同步失败的设备key存入mysql
         */
        //失败数据落库
        repositoryModule.saveSyncFailedData(key, realTimeData);

    }

    /**
     * 通过key获取本地缓存
     */
    @Cacheable(cacheNames = Constants.LOCAL, key = "#key")
    public Object getLocalCacheValue(String key) {
        return Constants.EMPTY_JSON_OBJ;
    }

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
    @Cacheable(cacheNames = Constants.SYN_REDIS_FAILED, key = "#key")
    public Object getSynRedisFailedCache(String key) {
        return Constants.EMPTY_JSON_OBJ;
    }

    /**
     * 更新 同步失败缓存
     *
     * @param key          物模型数据数据key
     * @param realTimeData 实时物模型数据
     */
    @CachePut(cacheNames = Constants.SYN_REDIS_FAILED, key = "#key")
    public String setSynRedisFailedCache(String key, String realTimeData) {
        return realTimeData;
    }
}
