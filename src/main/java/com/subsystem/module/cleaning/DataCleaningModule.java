package com.subsystem.module.cleaning;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.entity.Metric;
import com.subsystem.event.EventCollection;
import com.subsystem.event.SynRedisEvent;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.staticdata.SubSystemStaticDataModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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

    public EventCollection dataCleaning(Metric metric, String key) {
        EventCollection eventCollection = new EventCollection();
        //查询同步缓存
        String targetCacheData = caffeineCacheModule.getSynchronizeRedisCacheValue(key);
        //比对缓存 相同数据不进行业务处理
        if (caCheComparison(metric, targetCacheData)) return eventCollection;
        //合并 数据
        String realTimeData = mergeData(metric, targetCacheData);
        //修改同步缓存
        caffeineCacheModule.setSynchronizeRedisCacheValue(key, realTimeData);
        //创建同步事件
        SynRedisEvent synRedisEvent = new SynRedisEvent(null, key);
        eventCollection.setSynRedisEvent(synRedisEvent);


        return eventCollection;
    }

    /**
     * 合并 数据
     *
     * @param metric          新数据
     * @param targetCacheData 老缓存
     * @return 新缓存数据
     */
    private static String mergeData(Metric metric, String targetCacheData) {
        JSONObject realTimeDataObj = JSON.parseObject(targetCacheData);
        realTimeDataObj.put(metric.getAlias(), metric.getValue());
        String realTimeData = realTimeDataObj.toJSONString();
        return realTimeData;
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
