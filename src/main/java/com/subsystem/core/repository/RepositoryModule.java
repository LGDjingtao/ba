package com.subsystem.core.repository;


import com.alibaba.fastjson.JSONObject;
import com.subsystem.core.common.Constants;
import com.subsystem.core.module.SubSystemDefaultContext;
import com.subsystem.core.repository.mapping.AlarmInfo;
import com.subsystem.core.repository.mapping.LinkageInfo;
import com.subsystem.core.repository.mapping.SyncFailedData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据库数据获取模块
 */
@Component
@AllArgsConstructor
@Slf4j
public class RepositoryModule {
    SyncFailedDataRepository syncFailedDataRepository;
    LinkageInfoRepository linkageInfoRepository;
    AlarmInfoRepository alarmInfoRepository;

    /**
     * 更新异常数据到数据库
     *
     * @param key          物模型数据key
     * @param realTimeData 实时物模型数据
     */
    public SyncFailedData saveSyncFailedData(String key, String realTimeData) throws Exception {
        SyncFailedData syncFailedData = new SyncFailedData();
        try {
            syncFailedData.setKey(key);
            syncFailedData.setValue(realTimeData);
            syncFailedData.setUpdateTime(new DateTime().toString(Constants.Time_Format));
            syncFailedData = syncFailedDataRepository.saveAndFlush(syncFailedData);
        } catch (Exception e) {
            /**
             * 数据库落库出问题 已经是无力回天的事情了
             * 我们打个日志 记录一下问题数据 这已经是最后一到保障了 至少日志还有数据 这也算是恢复重要数据的依据
             */
            log.error("落库失败!\nkey:{}\nvalue:{},", key, realTimeData, e);
            throw new Exception("落库失败");
        }
        return syncFailedData;
    }

    /**
     * 删除同步失败缓存
     *
     * @param key 物模型数据key
     */
    public void deleteSyncFailedDataByKey(String key) {
        syncFailedDataRepository.deleteById(key);
    }

    /**
     * 获取所有同步失败缓存
     *
     * @return
     */
    public List<SyncFailedData> findAllSyncFailedData() {
        return syncFailedDataRepository.findAll();
    }

    /**
     * 获取所有联动信息
     *
     * @return
     */
    public List<LinkageInfo> getAllLinkageInfo() {
        return linkageInfoRepository.findAll();
    }

    /**
     * 保存联动信息
     *
     * @param subSystemDefaultContext
     */
    public void saveLinkageInfo(SubSystemDefaultContext subSystemDefaultContext) {
        String key = subSystemDefaultContext.getLinkageInfo().getTriggerDeviceCode();
        String context = JSONObject.toJSONString(subSystemDefaultContext);
        com.subsystem.core.repository.mapping.LinkageInfo linkageInfo = new LinkageInfo();
        linkageInfo.setKey(key);
        linkageInfo.setSubSystemContext(context);
        //数据库落盘
        linkageInfoRepository.save(linkageInfo);
    }


    /**
     * 删除联动信息
     *
     * @param key key
     */
    public void deleteLinkageInfo(String key) {
        if (linkageInfoRepository.existsById(key)) {
            linkageInfoRepository.deleteById(key);
        }
    }

    /**
     * 存入告警信息
     */
    public void saveAlarmFiledInfo(AlarmInfo alarmInfo) {
        alarmInfoRepository.save(alarmInfo);
    }

    /**
     * 存入获取失败的信息 每次只查询50条
     *
     * @return
     */
    public List<AlarmInfo> findAlarmFiledInfo() {
        Sort sort = Sort.by(Sort.Order.desc("alarmTime"));
        PageRequest pageRequest = PageRequest.of(0, 50, sort);
        Page<AlarmInfo> all = alarmInfoRepository.findAll(pageRequest);
        return all.getContent();
    }

    /**
     * 删除获取失败的信息
     *
     * @return
     */
    public void deleteAlarmFiledInfoById(String id) {
        alarmInfoRepository.deleteById(id);
    }
}
