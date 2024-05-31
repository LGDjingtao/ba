package com.subsystem.repository;


import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.repository.mapping.SyncFailedData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RepositoryModule {
    SyncFailedDataRepository syncFailedDataRepository;
    CaffeineCacheModule caffeineCacheModule;

    /**
     * 更新最新数据到数据库 且 更新缓存
     * @param key 物模型数据key
     * @param realTimeData 实时物模型数据
     */
    public SyncFailedData saveSyncFailedData(String key, String realTimeData) {
        SyncFailedData syncFailedData = new SyncFailedData();
        try {
            syncFailedData.setKey(key);
            syncFailedData.setValue(realTimeData);
            syncFailedData = syncFailedDataRepository.saveAndFlush(syncFailedData);

        } catch (Exception e) {
            /**
             * 数据库落库出问题 已经是无力回天的事情了
             * 我们打个日志 记录一下问题数据 这已经是最后一到保障了 至少日志还有数据 这也算是恢复重要数据的依据
             */
            log.error("落库失败!\nkey:{}\nvalue:{},", key, realTimeData, e);
            return syncFailedData;
        }
        //落库成功后才更新缓存 落库失败更新缓存没意义
        caffeineCacheModule.setSynRedisFailedCacheValue(key, realTimeData);
        return syncFailedData;
    }

    /**
     * @param key 物模型数据key
     */
    public void deleteSyncFailedDataByKey(String key) {
        syncFailedDataRepository.deleteById(key);
    }
}
