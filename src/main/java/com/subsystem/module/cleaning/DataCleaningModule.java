package com.subsystem.module.cleaning;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.common.Constants;
import com.subsystem.entity.Metric;
import com.subsystem.entity.RealTimeData;
import com.subsystem.event.AlarmEvent;
import com.subsystem.event.EventCollection;
import com.subsystem.event.SynRedisEvent;
import com.subsystem.module.alarm.AlarmModule;
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
    //告警模块
    AlarmModule alarmModule;

    /**
     *
     * @param metric 新的mqtt消息
     * @param tripartiteCode 三方标识
     * @return 合并后的最新数据
     */
    public RealTimeData dataCleaning(Metric metric, String tripartiteCode) throws Exception {
        String deviceCode = subSystemStaticDataModule.getDeviceCodeByTripartiteCode(tripartiteCode);
        //获取缓存key
        String key = getKey(deviceCode);
        //查询同步缓存
        String targetCacheData = caffeineCacheModule.getSynchronizeRedisCacheValue(key);
        //比对缓存 相同数据不进行业务处理
        if (caCheComparison(metric, targetCacheData)) return null;
        //合并 数据
        String realTimeDataStr = mergeData(metric, targetCacheData);
        //创建同步事件
        RealTimeData realTimeData = new RealTimeData();
        realTimeData.setKey(key);
        realTimeData.setRealTimeData(realTimeDataStr);
        realTimeData.setDeviceCode(deviceCode);
        realTimeData.setTripartiteCode(tripartiteCode);
        realTimeData.setAlias(metric.getAlias());
        realTimeData.setValue(metric.getValue());
        realTimeData.setTimestamp(metric.getTimestamp());
        return realTimeData;
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

    /**
     * @param deviceCode 设备code
     * @return 缓存key
     * @throws Exception
     */
    private String getKey(String deviceCode) {
        return Constants.PREFIX_FOR_OBJECT_MODEL_KEY + deviceCode;
    }
}
