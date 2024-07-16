package com.subsystem.core.module.task;


import com.subsystem.core.feign.AlarmCenterFeign;
import com.subsystem.core.module.cache.CaffeineCacheModule;
import com.subsystem.core.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.core.repository.RepositoryModule;
import com.subsystem.core.repository.mapping.AlarmInfo;
import com.subsystem.core.repository.mapping.DeviceAlarmType;
import com.subsystem.core.repository.mapping.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 离线感知
 */
@Component
@Slf4j
@AllArgsConstructor
public class OfflinePerceptionTask extends ScheduleTask {
    //缓存模块
    CaffeineCacheModule caffeineCacheModule;
    //静态数据模块
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    //事件驱动
    ApplicationContext eventDrivenModule;
    //告警远程调用
    AlarmCenterFeign alarmCenterFeign;
    //数据库模块
    RepositoryModule repositoryModule;

    @PostConstruct
    @Scheduled(cron = "0 0/5 * * * ? ")
    @Override
    public void run() {
        List<String> allTripartiteCode = subSystemStaticDataDefaultModule.getAllTripartiteCode();
        for (String tripartite : allTripartiteCode) {

            Object localCache = caffeineCacheModule.getLocalCache(tripartite);
            //触发离线告警感知
            try {
                DeviceInfo deviceInfo = subSystemStaticDataDefaultModule.getDeviceInfoByTripartiteCode(tripartite);
                String deviceCode = deviceInfo.getDeviceCode();
                boolean haveRecord = alarmCenterFeign.hasAlarmRecord(deviceCode, "长时间未收到设备信息告警");
                if (haveRecord && null != localCache)
                    alarmCenterFeign.markAlarmAsProcessed(deviceCode, "长时间未收到设备信息告警");
                if (!haveRecord && null == localCache){
                    AlarmInfo alarmInfo = new AlarmInfo();
                    LocalDateTime now = LocalDateTime.now();
                    // 定义日期时间格式化器，按照需要的格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    // 格式化LocalDateTime对象为字符串
                    String formattedDateTime = now.format(formatter);
                    alarmInfo.setAlarmLocalTime(now);
                    alarmInfo.setAlarmTime(formattedDateTime);
                    //设备code
                    alarmInfo.setAlarmDeviceId(deviceCode);
                    //待处理状态 默认0
                    alarmInfo.setAlarmDisposalStatus(0);
                    alarmInfo.setAlarmLocation(deviceInfo.getDeviceLocationName());
                    alarmInfo.setAlarmDeviceType(deviceInfo.getDeviceTypeName());
                    alarmInfo.setAlarmCategory("长时间未收到设备信息告警");
                    alarmInfo.setAlarmContent("长时间未收到设备信息告警");
                    alarmInfo.setLevel(1);
                    List<DeviceAlarmType> deviceAlarmTypeByDeviceCode = subSystemStaticDataDefaultModule.getDeviceAlarmTypeByDeviceCode(deviceCode);
                    DeviceAlarmType deviceAlarmType = deviceAlarmTypeByDeviceCode.get(0);
                    alarmInfo.setAlarmSubsystemName(deviceAlarmType.getSysTypeName());
                    alarmCenterFeign.receive(alarmInfo);
                }
            } catch (Exception e) {
                log.error("离线告警感知 rpc调用失败！");
            }
        }
    }
}
