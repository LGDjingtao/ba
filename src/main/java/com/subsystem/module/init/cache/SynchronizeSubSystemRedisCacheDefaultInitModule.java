package com.subsystem.module.init.cache;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.subsystem.common.Constants;
import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.redis.StringRedisModule;
import com.subsystem.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.repository.RepositoryModule;
import com.subsystem.repository.mapping.LinkageInfo;
import com.subsystem.repository.mapping.SyncFailedData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 初始化的时候同步子系统redis缓存到本地缓存
 */
@Component
@AllArgsConstructor
@Slf4j
public class SynchronizeSubSystemRedisCacheDefaultInitModule {
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    StringRedisModule stringRedisModule;
    CaffeineCacheModule caffeineCacheModule;
    RepositoryModule repositoryModule;
    CacheManager cacheManager;

    @EventListener(classes = ApplicationPreparedEvent.class)
    public void init(ApplicationPreparedEvent event) throws Exception {
        initSnyCache();
        initSyncFailedCache();
    }


    /**
     * 初始化同步缓存
     */
    private void initSnyCache() {
        log.info("初始化同步缓存");
        //获取全部物模型的redis key
        List<String> modelKeys = subSystemStaticDataDefaultModule.getAllModelKeys();
        //批量从redis管道获取数据
        List<Object> baseData = stringRedisModule.batchGetByPipelined(modelKeys);
        //处理空数据
        List<Object> filterData = baseData.stream().map(v -> null == v ? Constants.EMPTY_JSON_OBJ : v).collect(Collectors.toList());
        //聚合成key - value 存入本地缓存
        Map<String, Object> result = Stream.iterate(0, n -> n + 1)
                .limit(modelKeys.size())
                .collect(Collectors.toMap(modelKeys::get, filterData::get));
        LoadingCache cache = caffeineCacheModule.getSynRedisCache();
        cache.putAll(result);
    }

    /**
     * 初始化同步失败缓存
     */
    private void initSyncFailedCache() throws Exception {
        log.info("初始化同步失败缓存");
        List<SyncFailedData> allSyncFailedData = repositoryModule.findAllSyncFailedData();
        for (SyncFailedData allSyncFailedDatum : allSyncFailedData) {
            String key = allSyncFailedDatum.getKey();
            String value = allSyncFailedDatum.getValue();
            caffeineCacheModule.setSynRedisFailedCacheValue(key, value);
            //把失败数据同步到redis
            caffeineCacheModule.setSynchronizeRedisCacheValue(key, value);
        }
    }


}
