package com.subsystem.module.cleaning;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.module.alarm.AlarmModule;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.staticdata.SubSystemStaticDataModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 数据清洗模块
 */
@Slf4j
@Component
@AllArgsConstructor
public class DataCleaningModule {
    /**
     * 数据模块
     */
    SubSystemStaticDataModule subSystemStaticDataModule;
    /**
     * 缓存模块
     */
    CaffeineCacheModule caffeineCacheModule;
    /**
     * 告警模块
     */
    AlarmModule alarmModule;
    /**
     * 缓存管理 （测试看内存用）
     */
    CacheManager cacheManager;

    /**
     * @return 合并后的最新数据
     */
    public void dataCleaning(SubSystemDefaultContext subSystemDefaultContext) {
        //查询本地老缓存
        String key = subSystemDefaultContext.getKey();
        String targetCacheData = caffeineCacheModule.getSynchronizeRedisCacheValue(key);
        //比对缓存 相同数据不进行业务处理
        if (caCheComparison(subSystemDefaultContext, targetCacheData)) return;
        //合并 数据 得到最新数据
        String realTimeDataStr = mergeData(subSystemDefaultContext, targetCacheData);
        //最新数据放入上下文
        subSystemDefaultContext.setRealTimeData(realTimeDataStr);
    }


    /**
     * 合并 数据
     *
     * @param subSystemDefaultContext 子系统上下文
     * @param targetCacheData         老缓存
     * @return 新缓存数据
     */
    private static String mergeData(SubSystemDefaultContext subSystemDefaultContext, String targetCacheData) {
        JSONObject realTimeDataObj = JSON.parseObject(targetCacheData);
        realTimeDataObj.put(subSystemDefaultContext.getAlias(), subSystemDefaultContext.getValue());
        String realTimeData = realTimeDataObj.toJSONString();
        return realTimeData;
    }

    /**
     * @param subSystemDefaultContext 子系统上下文
     * @param targetCacheData         从本地同步缓存拿到的数据
     * @return 是否一样
     */
    private static Boolean caCheComparison(SubSystemDefaultContext subSystemDefaultContext, String targetCacheData) {
        JSONObject targetObj = JSON.parseObject(targetCacheData);
        String alias = subSystemDefaultContext.getAlias();
        if (targetObj.containsKey(alias)) {
            Object cacheObj = targetObj.get(alias);
            Object outObj = subSystemDefaultContext.getValue();
            return ObjectUtils.nullSafeEquals(cacheObj, outObj);
        }
        return false;
    }


}
