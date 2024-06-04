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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final static ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledExecutorService>> map = new ConcurrentHashMap();

    /**
     * 监听设备联动事件
     */
    @EventListener(classes = LinkageEvent.class)
    public void alarmEventListener(LinkageEvent linkageEvent) {
        SubSystemDefaultContext subSystemDefaultContext = linkageEvent.getSubSystemDefaultContext();
        LinkageInfo linkageInfo = subSystemDefaultContext.getLinkageInfo();
        //是否是第一次联动
        boolean first = linkageInfo.isFirst();
        //是告警 还是消警
        boolean alarmOrAlarmCancel = subSystemDefaultContext.getAlarmOrAlarmCancel();
        //不是第一次联动 就需要重新获取告警信息
        if (!first) alarmOrAlarmCancel = isAlarmOrAlarmCancel(subSystemDefaultContext);
        //告警处理
        if (alarmOrAlarmCancel) alarmHandle(subSystemDefaultContext);
        //第一次联动不走消警逻辑
        if (!alarmOrAlarmCancel && !first) alarmCancelHandle(linkageInfo);
    }

    /**
     * 重新判断是告警还是消警
     *
     * @param subSystemDefaultContext
     * @return
     */
    private boolean isAlarmOrAlarmCancel(SubSystemDefaultContext subSystemDefaultContext) {
        boolean alarmOrAlarmCancel;
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
        return alarmOrAlarmCancel;
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
        endThisEvent(linkageDeviceCode, triggerDeviceCode);
        log.info("task[告警]#联动定时任务结束\nlinkageDeviceCode:{}\ntriggerDeviceCode:{}", linkageDeviceCode, triggerDeviceCode);
        //业务逻辑
        Runnable runAble = getRunAble(subSystemDefaultContext);
        //创建延迟任务
        ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
        ConcurrentHashMap<String, ScheduledExecutorService> triggerDeviceCodeMap = this.map.get(linkageDeviceCode);
        if (null == triggerDeviceCodeMap) {
            triggerDeviceCodeMap = new ConcurrentHashMap<>();
            this.map.put(linkageDeviceCode, triggerDeviceCodeMap);
        }
        triggerDeviceCodeMap.put(triggerDeviceCode, scheduled);
        //延迟LinkageTaskTime分钟后处理业务逻辑
        scheduled.schedule(runAble, baProperties.getLinkageTaskTime(), TimeUnit.MINUTES);
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
    private void endThisEvent(String linkageDeviceCode, String triggerDeviceCode) {
        if (this.map.containsKey(linkageDeviceCode)) {
            ConcurrentHashMap<String, ScheduledExecutorService> triggerDeviceCodeMap = this.map.get(linkageDeviceCode);
            if (triggerDeviceCodeMap.containsKey(triggerDeviceCode)) {
                ScheduledExecutorService scheduled = triggerDeviceCodeMap.get(triggerDeviceCode);
                scheduled.shutdown();
                triggerDeviceCodeMap.remove(triggerDeviceCode);
            }
        }
    }


    /**
     * 检查任务是否已经存在
     */
    private boolean checkExistTask(String linkageDeviceCode) {
        if (this.map.containsKey(linkageDeviceCode)) {
            ConcurrentHashMap<String, ScheduledExecutorService> triggerDeviceCodeMap = this.map.get(linkageDeviceCode);
            if (!triggerDeviceCodeMap.isEmpty()) return true;
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
        String triggerDeviceCode = linkageInfo.getTriggerDeviceCode();
        //删除联动信息
        repositoryModule.deleteLinkageInfo(triggerDeviceCode);
        //结束事件
        endThisEvent(linkageDeviceCode, triggerDeviceCode);
        log.info("task[消警]#联动定时任务结束\nlinkageDeviceCode:{}\ntriggerDeviceCode:{}", linkageDeviceCode, triggerDeviceCode);

        //联动任务结束 后 需要看改设备身上还有没有绑定联动任务 一个联动任务都没有才关设备
        if (checkExistTask( linkageDeviceCode)) return;
        log.info("task[消警]#关闭设备");
        controlLinkageDevice(linkageDeviceCode, 0);
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
