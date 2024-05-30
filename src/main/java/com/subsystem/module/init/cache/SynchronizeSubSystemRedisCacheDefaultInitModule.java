package com.subsystem.module.init.cache;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.subsystem.Constants;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.init.InitModule;
import com.subsystem.module.redis.StringRedisModule;
import com.subsystem.module.staticdata.SubSystemStaticDataDefaultModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * 同步子系统redis缓存
 */
@Component
@AllArgsConstructor
@Slf4j
@DependsOn("subSystemStaticDataInitDefaultModule")
public class SynchronizeSubSystemRedisCacheDefaultInitModule implements InitModule {
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    StringRedisModule stringRedisModule;
    CaffeineCacheModule caffeineCacheModule;

    @Override
    @PostConstruct
    public void init() {
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
        LoadingCache cache = caffeineCacheModule.getLoadingCache(Constants.SYNCHRONIZE_REDIS);
        cache.putAll(result);
    }
}
