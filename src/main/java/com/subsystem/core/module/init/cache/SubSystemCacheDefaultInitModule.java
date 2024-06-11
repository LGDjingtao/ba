package com.subsystem.core.module.init.cache;

import com.subsystem.core.common.Constants;
import com.subsystem.core.module.cache.CaffeineCacheModule;
import com.subsystem.core.module.init.InitModule;
import com.subsystem.core.module.redis.StringRedisModule;
import com.subsystem.core.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.core.repository.RepositoryModule;
import com.subsystem.core.repository.mapping.SyncFailedData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 初始化缓存模块的数据
 */
@Component
@AllArgsConstructor
@Slf4j
public class SubSystemCacheDefaultInitModule implements InitModule {
    /**
     * 静态数据模块
     */
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    /**
     * redis模块
     */
    StringRedisModule stringRedisModule;
    /**
     * 缓存模块
     */
    CaffeineCacheModule caffeineCacheModule;
    /**
     * 数据库模块
     */
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
        Stream.iterate(0, n -> n + 1)
                .limit(modelKeys.size())
                .forEach(index -> {
                    String key = modelKeys.get(index);
                    String realTimeData = (String) filterData.get(index);
                    caffeineCacheModule.setInitSynchronizeRedisCacheValue(key, realTimeData);
                });
        log.info("初始化同步缓存 end");
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


    @Override
    public void init() throws Exception {

    }
}
