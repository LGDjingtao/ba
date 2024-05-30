package com.subsystem.module.cleaning;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.entity.Metric;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.staticdata.SubSystemStaticDataModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

/**
 * 数据清洗模块
 */
@Slf4j
@Component
@AllArgsConstructor
public class DataCleaningModule {
    //静态数据模块
    SubSystemStaticDataModule subSystemStaticDataModule;
    //缓存模块
    CaffeineCacheModule caffeineCacheModule;

    public void dataCleaning(Metric metric, String tripartiteCode) throws Exception {
        //获取缓存key
        String redisKey = subSystemStaticDataModule.getDeviceCodeRedisKeyByTripartiteCode(tripartiteCode);
        Optional.ofNullable(redisKey).orElseThrow(() -> {
            log.error("数据清洗的时，传入的三方标识:{}获取不到缓存key", tripartiteCode);
            return new Exception("获取不到缓存key");
        });

        //查询同步缓存
        String targetCacheData = caffeineCacheModule.getSynchronizeRedisCacheValue(redisKey);

        //比对缓存 相同数据不进行业务处理
        if (caCheComparison(metric, targetCacheData)) return;

        JSONObject realTimeDataObj = JSON.parseObject(targetCacheData);
        realTimeDataObj.put(metric.getAlias(),metric.getValue());
        String realTimeData = realTimeDataObj.toJSONString();
        //修改同步缓存
        caffeineCacheModule.setSynchronizeRedisCacheValue(redisKey,realTimeData);

    }

    /**
     * @param metric          接收到的外部信息
     * @param targetCacheData 从本地同步缓存拿到的数据
     * @return 是否一样
     */
    private static Boolean caCheComparison(Metric metric, String targetCacheData) {
        JSONObject targetObj = JSON.parseObject(targetCacheData);
        String alias = metric.getAlias();
        if (targetObj.containsKey(alias)) {
            Object cacheObj = targetObj.get(alias);
            Object outObj = metric.getValue();
            return ObjectUtils.nullSafeEquals(cacheObj, outObj);
        }
        return false;
    }
}
