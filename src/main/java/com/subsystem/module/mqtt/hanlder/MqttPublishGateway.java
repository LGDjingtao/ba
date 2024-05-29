package com.subsystem.module.mqtt.hanlder;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * 出站消息接口
 */
@Service
@MessagingGateway(defaultRequestChannel = "outBoundChannel")
public interface MqttPublishGateway {

    /**
     * 发送信息到MQTT服务器
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, byte[] payload);

    /**
     * 发送信息到MQTT服务器
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

    /**
     * 发送信息到MQTT服务器
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, Object payload);

    /**
     * 发送信息到MQTT服务器
     * QOS: 对消息处理的几种机制
     * 0 表示的是订阅者没收到消息不会再发送，消息可以会丢失。
     * 1 表示尝试重试，一直到接收到消息，但这种情况可能导致订阅者收到多次重复消息。
     * 2 表示尝试重试，一直到接收消息，但是确保只会消息一次。
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
}
