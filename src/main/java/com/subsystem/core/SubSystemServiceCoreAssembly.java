package com.subsystem.core;


import com.alibaba.fastjson.JSONObject;
import com.subsystem.core.common.Constants;
import com.subsystem.core.common.SpecialFieldsEnum;
import com.subsystem.core.entity.Metric;
import com.subsystem.core.entity.MqttPayload;
import com.subsystem.core.event.EventCollection;
import com.subsystem.core.module.SubSystemDefaultContext;
import com.subsystem.core.module.alarm.AlarmModule;
import com.subsystem.core.module.cache.CaffeineCacheModule;
import com.subsystem.core.module.cleaning.DataCleaningModule;
import com.subsystem.core.module.linkage.LinkageModule;
import com.subsystem.core.module.staticdata.SubSystemStaticDataModule;
import com.subsystem.core.repository.mapping.DeviceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;


/**
 * 子系统业务组装
 */
@Slf4j
@Component
public class SubSystemServiceCoreAssembly {
    @Resource
    CacheManager cacheManager;
    //缓存模块
    @Lazy
    @Resource
    CaffeineCacheModule caffeineCacheModule;
    //数据清洗模块
    @Lazy
    @Resource
    DataCleaningModule dataCleaningModule;
    //事件驱动模块
    @Resource
    ApplicationContext eventDrivenModule;
    //告警模块
    @Resource
    AlarmModule alarmModule;
    //联动模块
    @Resource
    LinkageModule linkageModule;
    //静态数据模块
    @Resource
    SubSystemStaticDataModule subSystemStaticDataModule;

    /**
     * 业务逻辑组装入口
     */
    public void serviceAssemblyEntrance(Message<?> message) throws Exception {
        //获取三方标识
        String tripartiteCode = getTripartiteCodeByMessage(message);

        //推送命令不参与逻辑处理 ，这是由于控制命令也会被接收 所以做这一层过滤
        if (Constants.COMMAND.equals(tripartiteCode)) return;

        //新数据刷新本地缓存 目的是为了刷新设备信息过期时间 用来感知设备是否离线（暂时需求还没提 预感会有这一块需求）
        caffeineCacheModule.setLocalCache(tripartiteCode);

        //数据转换
        Metric metric = dataConversion(message);

        //创建子系统上下文
        SubSystemDefaultContext subSystemDefaultContext = createSubSystemDefaultContext(metric, tripartiteCode);

        //数据清洗
        dataCleaningModule.dataCleaning(subSystemDefaultContext);

        //告警故障处理
        alarmModule.alarmAndFaultHandle(subSystemDefaultContext);

        //触发设备联动
        linkageModule.linkageHandle(subSystemDefaultContext);

        // 用前面步骤整理好的数据来 数据上报
        dataReporting(subSystemDefaultContext);
    }

    /**
     * 数据上报
     */
    private void dataReporting(SubSystemDefaultContext subSystemDefaultContext) {
        //创建各类事件
        EventCollection eventCollection = new EventCollection();
        eventCollection.createSynRedisEvent(subSystemDefaultContext);
        eventCollection.createAlarmEvent(subSystemDefaultContext);
        eventCollection.createLinkageEvent(subSystemDefaultContext);
        //推送数据同步事件
        Optional.ofNullable(eventCollection.getSynRedisEvent()).ifPresent(SynRedisEvent -> eventDrivenModule.publishEvent(SynRedisEvent));
        //推送告/消警事件
        Optional.ofNullable(eventCollection.getAlarmEvent()).ifPresent(AlarmEvent -> eventDrivenModule.publishEvent(AlarmEvent));
        //推送设备联动事件
        Optional.ofNullable(eventCollection.getLinkageEvent()).ifPresent(LinkageEvent -> eventDrivenModule.publishEvent(LinkageEvent));
    }

    /**
     * 创建子系统上下文
     *
     * @param metric         设备信息
     * @param tripartiteCode 三方标识
     * @return 子系统上下文
     */
    private SubSystemDefaultContext createSubSystemDefaultContext(Metric metric, String tripartiteCode) {
        //设备信息
        DeviceInfo deviceInfo = subSystemStaticDataModule.getDeviceInfoByTripartiteCode(tripartiteCode);
        String deviceCode = deviceInfo.getDeviceCode();
        //获取缓存key
        String key = getKey(deviceCode);
        //设置基础上下文信息
        SubSystemDefaultContext subSystemDefaultContext = new SubSystemDefaultContext();
        subSystemDefaultContext.setKey(key);
        subSystemDefaultContext.setDeviceInfo(deviceInfo);
        subSystemDefaultContext.setAlias(metric.getAlias());
        subSystemDefaultContext.setValue(metric.getValue().toString());
        subSystemDefaultContext.setTimestamp(metric.getTimestamp());
        return subSystemDefaultContext;
    }

    /**
     * 获取缓存key
     *
     * @param deviceCode 设备code
     * @return 缓存key
     */
    private String getKey(String deviceCode) {
        return Constants.PREFIX_FOR_OBJECT_MODEL_KEY + deviceCode;
    }


    /**
     * 通过接收到的topic 截取设备第三方标识
     */
    private static String getTripartiteCodeByMessage(Message<?> message) {
        //拿到请求头
        MessageHeaders headers = message.getHeaders();
        //获取请求topic
        String mqttReceivedTopic = (String) headers.get(Constants.MQTT_RECEIVEDTOPIC);
        //截取三方标识
        String tripartiteCode = mqttReceivedTopic.substring(mqttReceivedTopic.lastIndexOf("/") + 1);
        return tripartiteCode;
    }

    /**
     * 特殊数据转换 对于在线离线，故障，告警 推送值兼容
     * false->"0" true->"1"
     */
    private static void specialFieldsConversion(Metric metric) {
        String alias = metric.getAlias();
        String value = metric.getValue().toString();
        if (null == value) return;
        boolean empty = Arrays.stream(SpecialFieldsEnum.values()).map(SpecialFieldsEnum::name).filter(v -> v.equals(alias)).findAny().isEmpty();
        if (empty) return;
        if (value.equals(Constants.SPECIAL_FIELDS_FALSE))
            metric.setValue(Constants.SPECIAL_FIELDS_0);
        if (value.equals(Constants.SPECIAL_FIELDS_TRUE))
            metric.setValue(Constants.SPECIAL_FIELDS_1);
    }


    /**
     * mqtt消息转换成实体信息
     *
     * @param message 消息
     */
    private Metric dataConversion(Message<?> message) throws Exception {
        Metric metric = null;
        try {
            String payload = (String) message.getPayload();
            MqttPayload mqttPayload = JSONObject.parseObject(payload, MqttPayload.class);
            metric = mqttPayload.getMetrics().get(0);
        } catch (Exception e) {
            log.error("解析payload异常#payload:{}", message.getPayload());
            throw new Exception("解析消息异常");
        }
        Optional.ofNullable(metric).orElseThrow(() -> new Exception("传入信息无效"));

        //特殊数据转换
        specialFieldsConversion(metric);

        return metric;
    }
}
