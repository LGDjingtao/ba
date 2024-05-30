package com.subsystem.assembly;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.common.Constants;
import com.subsystem.common.SpecialFieldsEnum;
import com.subsystem.entity.Metric;
import com.subsystem.entity.MqttPayload;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.cleaning.DataCleaningModule;
import com.subsystem.module.staticdata.SubSystemStaticDataModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Optional;


/**
 * 子系统业务组装
 */
@Slf4j
@Component
@AllArgsConstructor
public class SubSystemServiceCoreAssembly {
    //静态数据模块
    SubSystemStaticDataModule subSystemStaticDataModule;
    //缓存模块
    CaffeineCacheModule caffeineCacheModule;
    //数据清洗模块
    DataCleaningModule dataCleaningModule;

    //业务组装入口
    public void serviceAssemblyEntrance(Message<?> message) throws Exception {

        //获取三方标识
        String tripartiteCode = getTripartiteCodeByMessage(message);

        //新数据刷新本地缓存 目的是为了刷新设备信息过期时间 用来感知设备是否离线
        caffeineCacheModule.setLocalCache(tripartiteCode);

        //数据转换
        Metric metric = dataConversion(message);

        //数据清洗
        dataCleaningModule.dataCleaning(metric, tripartiteCode);

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
     *
     * @param metric
     */
    private static void specialFieldsConversion(Metric metric) {
        String alias = metric.getAlias();
        Object value = metric.getValue();
        if (null == value) return;
        boolean empty = Arrays.stream(SpecialFieldsEnum.values()).map(SpecialFieldsEnum::name).filter(v -> v.equals(alias)).findAny().isEmpty();
        if (empty) return;
        if (value.equals(false))
            metric.setValue(Constants.SPECIAL_FIELDS_FALSE);
        if (value.equals(true))
            metric.setValue(Constants.SPECIAL_FIELDS_TRUE);
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
