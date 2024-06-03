package com.subsystem.module.linkage;

import cn.hutool.json.JSONUtil;
import com.subsystem.event.LinkageEvent;
import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.module.alarm.AlarmModule;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.cleaning.DataCleaningModule;
import com.subsystem.module.mqtt.hanlder.MqttPublishGateway;
import com.subsystem.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.porp.BAProperties;
import com.subsystem.repository.RepositoryModule;
import com.subsystem.repository.mapping.DeviceInfo;
import com.subsystem.repository.mapping.DeviceLinkageRelationshipData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 监听设备联动事件
     */
    @EventListener(classes = LinkageEvent.class)
    public void alarmEventListener(LinkageEvent linkageEvent) {
        SubSystemDefaultContext subSystemDefaultContext = linkageEvent.getSubSystemDefaultContext();
        LinkageInfo linkageInfo = subSystemDefaultContext.getLinkageInfo();
        boolean alarmOrAlarmCancel = true;
        if (!linkageInfo.isFirst()) {
            //重新监测是否还告警
            alarmOrAlarmCancel = alarmModule.alarmOrAlarmCancel(subSystemDefaultContext);
        }
        if (alarmOrAlarmCancel) {
            alarmHandle(subSystemDefaultContext);
        } else {
            alarmCancelHandle(linkageInfo);
        }
    }

    /**
     * 联动设备告警处理
     *
     * @param subSystemDefaultContext 子系统上下文
     */
    private void alarmHandle(SubSystemDefaultContext subSystemDefaultContext) {
        LinkageInfo linkageInfo = subSystemDefaultContext.getLinkageInfo();
        String linkageDeviceCode = linkageInfo.getLinkageDeviceCode();
        //开风机
        controlLinkageDevice(linkageDeviceCode, 1);
        //保存联动信息
        caffeineCacheModule.setLinkagCacheValue(subSystemDefaultContext);
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
        //删除联动信息
        repositoryModule.deleteLinkageInfo(linkageDeviceCode);
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
