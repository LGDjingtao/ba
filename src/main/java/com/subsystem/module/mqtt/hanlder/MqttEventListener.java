package com.subsystem.module.mqtt.hanlder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.event.MqttMessageDeliveredEvent;
import org.springframework.integration.mqtt.event.MqttMessageSentEvent;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;
import org.springframework.stereotype.Component;

/**
 * Mqtt 事件监听器
 */
@Component
@Slf4j
public class MqttEventListener {

    /**
     * 连接失败的事件通知
     */
    @EventListener(classes = MqttConnectionFailedEvent.class)
    public void listenerAction(MqttConnectionFailedEvent event) {
        log.info("连接失败的事件通知");
    }

    /**
     * 已发送的事件通知
     */
    @EventListener(classes = MqttMessageSentEvent.class)
    public void listenerAction(MqttMessageSentEvent event) {
        log.info("已发送的事件通知:[{}]", event.toString());
    }

    /**
     * 已传输完成的事件通知
     * 1. QOS=0, 发送消息后会即可此事件回调，因为不需要等代回执
     * 2. QOS=1, 发送消息后会等待ACK回执，ACK回执后会进行事件通知
     * 3. QOS=2, 发送消息后会等待PubRECV回执，知道收到PubRECV后进行些事件通知
     */
    @EventListener(classes = MqttMessageDeliveredEvent.class)
    public void listenerAction(MqttMessageDeliveredEvent event) {
        log.info("已传输完成的事件通知:[{}]", event.toString());
    }

    /**
     * 消息订阅的事件通知
     */
    @EventListener(classes = MqttSubscribedEvent.class)
    public void listenerAction(MqttSubscribedEvent event) {
        log.info("消息订阅的事件通知");
        log.info("成功订阐的主题: 消息:[{}]", event.toString());
    }

}
