package com.subsystem.core.module.mqtt.config;


import com.subsystem.core.module.mqtt.prop.MqttProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

/**
 * 出站消息配置
 */
@Configuration
@IntegrationComponentScan
@Slf4j
@AllArgsConstructor
public class MqttOutboundConfig {

    private final MqttProperties mqttProperties;

    /**
     * 出站管道
     */
    @Bean(name = "outBoundChannel")
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT消息处理器(生产者)
     */
    @Bean
    @ServiceActivator(inputChannel = "outBoundChannel")
    public MqttPahoMessageHandler mqttOutbound(
            @Qualifier("mqttPahoClientFactory") MqttPahoClientFactory mqttPahoClientFactory) {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(this.mqttProperties.getOutboundClientId(), mqttPahoClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(this.mqttProperties.getDefaultTopic());
        messageHandler.setAsyncEvents(true);   // 消息发送和传输完成以后，发送异步的通知回调
        messageHandler.setConverter(new DefaultPahoMessageConverter()); // 编解码
        return messageHandler;
    }

}
