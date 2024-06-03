package com.subsystem.module.linkage;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.event.LinkageEvent;
import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.module.alarm.AlarmModule;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.cleaning.DataCleaningModule;
import com.subsystem.module.mqtt.hanlder.MqttPublishGateway;
import com.subsystem.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.porp.BAProperties;
import com.subsystem.repository.RepositoryModule;
import com.subsystem.repository.mapping.AlarmInfo;
import com.subsystem.repository.mapping.DeviceAlarmType;
import com.subsystem.repository.mapping.DeviceInfo;
import com.subsystem.repository.mapping.DeviceLinkageRelationshipData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 联动模块
 */
@Component
@AllArgsConstructor
@Slf4j
public class LinkageModule {
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    MqttPublishGateway mqttPublishGateway;
    BAProperties baProperties;
    CaffeineCacheModule caffeineCacheModule;
    AlarmModule alarmModule;
    DataCleaningModule dataCleaningModule;
    RepositoryModule repositoryModule;
    //事件驱动模块
    ApplicationContext eventDrivenModule;
    private final static ConcurrentHashMap<String, ScheduledExecutorService> map = new ConcurrentHashMap();

    /**
     * 监听设备联动事件
     */
    @EventListener(classes = LinkageEvent.class)
    public void alarmEventListener(LinkageEvent linkageEvent) {
        SubSystemDefaultContext subSystemDefaultContext = linkageEvent.getSubSystemDefaultContext();
        LinkageInfo linkageInfo = subSystemDefaultContext.getLinkageInfo();
        boolean alarmOrAlarmCancel = true;
        if (!linkageInfo.isFirst()) {
            //更新实时数据
            updateRealTimeData(subSystemDefaultContext);
            //告警类型信息
            updateDeviceAlarmType(subSystemDefaultContext);

            DeviceAlarmType deviceAlarmType = subSystemDefaultContext.getDeviceAlarmType();
            if (null == deviceAlarmType) {
                alarmOrAlarmCancel = false;
            } else {
                //重新监测是否还告警
                alarmOrAlarmCancel = alarmModule.alarmOrAlarmCancel(subSystemDefaultContext);
            }
        }

        if (alarmOrAlarmCancel) {
            alarmHandle(subSystemDefaultContext);
        } else {
            alarmCancelHandle(linkageInfo);
        }
    }

    /**
     * 更新 deviceAlarmType 告警类型信息
     *
     * @param subSystemDefaultContext 上下文
     */
    private void updateDeviceAlarmType(SubSystemDefaultContext subSystemDefaultContext) {
        DeviceInfo deviceInfo = subSystemDefaultContext.getDeviceInfo();
        String deviceCode = deviceInfo.getDeviceCode();
        String alias = subSystemDefaultContext.getAlias();
        DeviceAlarmType deviceAlarmType = alarmModule.getDeviceAlarmType(deviceCode, alias);
        subSystemDefaultContext.setDeviceAlarmType(deviceAlarmType);
    }

    /**
     * 更新实时数据
     *
     * @param subSystemDefaultContext 上下文
     */
    private void updateRealTimeData(SubSystemDefaultContext subSystemDefaultContext) {
        String key = subSystemDefaultContext.getKey();
        String alias = subSystemDefaultContext.getAlias();
        String realTimeData = caffeineCacheModule.getSynchronizeRedisCacheValue(key);
        JSONObject realTimeDataObj = JSONObject.parseObject(realTimeData);
        subSystemDefaultContext.setValue(realTimeDataObj.get(alias));
        subSystemDefaultContext.setRealTimeData(realTimeData);
    }

    /**
     * 联动设备告警处理
     *
     * @param subSystemDefaultContext 子系统上下文
     */
    private void alarmHandle(SubSystemDefaultContext subSystemDefaultContext) {
        LinkageInfo linkageInfo = subSystemDefaultContext.getLinkageInfo();

        //去除第一次联动标识
        linkageInfo.setFirst(false);

        //开风机
        String linkageDeviceCode = linkageInfo.getLinkageDeviceCode();
        controlLinkageDevice(linkageDeviceCode, 1);

        //联动信息 入 库
        repositoryModule.saveLinkageInfo(subSystemDefaultContext);

        String triggerDeviceCode = linkageInfo.getTriggerDeviceCode();
        //结束延迟事件
        endThisEvent(triggerDeviceCode);
        //业务逻辑
        Runnable runAble = getRunAble(subSystemDefaultContext);
        //创建延迟任务
        ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
        this.map.put(linkageDeviceCode, scheduled);
        //延迟15分钟后处理业务逻辑
        scheduled.schedule(runAble, 15, TimeUnit.MINUTES);
        log.info("task#告警联动定时任务创建任务成功");
    }

    /**
     * 联动逻辑
     */
    private Runnable getRunAble(SubSystemDefaultContext subSystemDefaultContext) {
        return () -> {
            LinkageEvent linkageEvent = new LinkageEvent(this, subSystemDefaultContext);
            //触发一次联动事件
            eventDrivenModule.publishEvent(linkageEvent);
        };

    }


    /**
     * 结束这个事件
     */
    private void endThisEvent(String deviceCode) {
        if (this.map.containsKey(deviceCode)) {
            ScheduledExecutorService scheduled = this.map.get(deviceCode);
            scheduled.shutdown();
        }
    }


    /**
     * 检查任务是否已经关闭
     */
    private boolean checkShutdown(String linkageDeviceCode) {
        ScheduledExecutorService scheduledExecutorService = this.map.get(linkageDeviceCode);
        if (null == scheduledExecutorService || scheduledExecutorService.isShutdown()) {
            return true;
        }
        return false;
    }


    /**
     * 联动设备消警处理
     *
     * @param linkageInfo
     */
    private void alarmCancelHandle(LinkageInfo linkageInfo) {
        String linkageDeviceCode = linkageInfo.getLinkageDeviceCode();
        //关风机
        controlLinkageDevice(linkageDeviceCode, 0);

        String triggerDeviceCode = linkageInfo.getTriggerDeviceCode();
        //删除联动信息
        repositoryModule.deleteLinkageInfo(triggerDeviceCode);

        //结束事件
        endThisEvent(triggerDeviceCode);
        log.info("task#告警联动定时任务结束triggerDeviceCode:{}",triggerDeviceCode);
    }

    /**
     * 控制联动风机
     *
     * @param deviceCode 设备code
     * @param controlCmd 0关 1开
     */
    private void controlLinkageDevice(String deviceCode, Integer controlCmd) {
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Integer> message = new HashMap<>();
        //设备code转3方标识
        String tripartiteCode = subSystemStaticDataDefaultModule.getTripartiteCodeByDeviceCode(deviceCode);
        //应硬件系统厂家的要求，控制设备必须在设备名称后面加 _PTCMD
        message.put(tripartiteCode + "_PTCMD", controlCmd);
        metrics.put("metrics", message);
        mqttPublishGateway.sendToMqtt(baProperties.getTopic(), JSONUtil.toJsonStr(metrics));
    }

    /**
     * 联动处理
     */
    public void linkageHandle(SubSystemDefaultContext subSystemDefaultContext) {
        AlarmInfo alarmInfo = subSystemDefaultContext.getAlarmInfo();
        if (null == alarmInfo) return;
        DeviceInfo deviceInfo = subSystemDefaultContext.getDeviceInfo();
        String alias = subSystemDefaultContext.getAlias();
        String deviceCode = deviceInfo.getDeviceCode();
        DeviceLinkageRelationshipData data = subSystemStaticDataDefaultModule
                .getAllDeviceLinkageRelationship()
                .stream()
                .filter(v -> v.getDeviceCode().equals(deviceCode) && alias.equals(v.getThresholdAlias()))
                .findAny()
                .orElse(null);
        //是否是联动设备检查,是联动设备，且是阈值报警就开风机,并开启联动事件逻辑
        if (null == data) return;
        String linkageDeviceCode = data.getLinkageDeviceCode();
        //创建联动信息
        LinkageInfo linkageInfo = new LinkageInfo();
        linkageInfo.setTriggerDeviceCode(deviceCode);
        linkageInfo.setLinkageDeviceCode(linkageDeviceCode);
        linkageInfo.setFirst(true);
        subSystemDefaultContext.setLinkageInfo(linkageInfo);
    }
}
