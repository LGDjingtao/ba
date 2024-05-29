package com.subsystem.assembly;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.entity.MqttPayload;
import com.subsystem.module.staticdata.SubSystemStaticDataModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * 子系统业务组装
 */
@Slf4j
@Component
@AllArgsConstructor
public class SubSystemServiceCoreAssembly {
    //静态数据模块
    SubSystemStaticDataModule subSystemStaticDataModule;

    //业务组装入口
    public void serviceAssemblyEntrance(Message<?> message) {
        //数据转换
        MqttPayload mqttPayload = dataConversion(message);
        if (null == mqttPayload) return;
        //数据清洗

    }

    /**
     * mqtt消息转换成实体信息
     *
     * @param message 消息
     */
    private MqttPayload dataConversion(Message<?> message) {
        MqttPayload mqttPayload = null;
        try {
            String payload = (String) message.getPayload();
            mqttPayload = JSONObject.parseObject(payload, MqttPayload.class);
        } catch (JSONException e) {
            log.error("解析消息异常:{}", message.getPayload(), e);
        }
        return mqttPayload;
    }

}
