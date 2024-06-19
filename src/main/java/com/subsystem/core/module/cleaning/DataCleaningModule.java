package com.subsystem.core.module.cleaning;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.core.common.Constants;
import com.subsystem.core.module.SubSystemDefaultContext;
import com.subsystem.core.module.alarm.AlarmModule;
import com.subsystem.core.module.cache.CaffeineCacheModule;
import com.subsystem.core.module.staticdata.SubSystemStaticDataModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.text.DecimalFormat;

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
     * 数据清洗
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
    private  String mergeData(SubSystemDefaultContext subSystemDefaultContext, String targetCacheData) {
        JSONObject realTimeDataObj = JSON.parseObject(targetCacheData);
        realTimeDataObj.put(subSystemDefaultContext.getAlias(), subSystemDefaultContext.getValue());
        specialTreatment(realTimeDataObj, subSystemDefaultContext);
        String realTimeData = realTimeDataObj.toJSONString();
        return realTimeData;
    }



    /**
     * 特殊处理
     * <p>
     * 例如3相表 EAP值需要相加
     */
    private void specialTreatment(JSONObject physicalModel, SubSystemDefaultContext subSystemDefaultContext) {
        String alias = subSystemDefaultContext.getAlias();
        String key = subSystemDefaultContext.getKey();
        try {
            if (alias.equals(Constants.EPA1) || alias.equals(Constants.EPA2) || alias.equals(Constants.EPA3)) {
                String EPA1Value = physicalModel.getString(Constants.EPA1);
                Double temp = 0d;
                if (null != EPA1Value) {
                    Double aDouble = Double.valueOf(EPA1Value);
                    temp += aDouble;
                }
                String EPA2Value = physicalModel.getString(Constants.EPA2);
                if (null != EPA2Value) {
                    Double aDouble = Double.valueOf(EPA2Value);
                    temp += aDouble;
                }
                String EPA3Value = physicalModel.getString(Constants.EPA3);
                if (null != EPA3Value) {
                    Double aDouble = Double.valueOf(EPA3Value);
                    temp += aDouble;
                }
                DecimalFormat df = new DecimalFormat("#.##");
                String formattedNumber = df.format(temp);
                physicalModel.put(Constants.ALL_EPA, formattedNumber);
            }
        } catch (Exception e) {
            log.error("三相电表{}数值相加失败！", key);
        }
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
            String cacheObj = targetObj.getString(alias);
            String outObj = subSystemDefaultContext.getValue();
            return ObjectUtils.nullSafeEquals(cacheObj, outObj);
        }
        return false;
    }


}
