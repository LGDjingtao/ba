package com.subsystem.module.mqtt.config;


import com.subsystem.module.mqtt.prop.MqttProperties;
import com.subsystem.module.mqtt.hanlder.MqttInboundReceiveHandle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;


/**
 * 入站消息配置
 */
@Configuration
@IntegrationComponentScan
@Slf4j
@AllArgsConstructor
public class MqttInboundConfig {

    private final MqttProperties mqttProperties;
    private final MqttInboundReceiveHandle mqttInboundReceiveHandle;


    /**
     * 入站管道
     */
    @Bean(name = "inBoundChannel")
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    /**
     * 入站管道适配器
     */
    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter(
            @Qualifier("mqttPahoClientFactory") MqttPahoClientFactory mqttPahoClientFactory,
            @Qualifier("inBoundChannel") MessageChannel messageChannel
    ) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                this.mqttProperties.getInboundClientId(),
                mqttPahoClientFactory,
                this.mqttProperties.getInboundGatewayTopics());
        adapter.setCompletionTimeout(this.mqttProperties.getTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());  // 编解码
        adapter.setQos(2);
        adapter.setOutputChannel(messageChannel);
        return adapter;
    }

    /**
     * 入站消息处理器
     */
    @Bean
    @ServiceActivator(inputChannel = "inBoundChannel")
    public MessageHandler handler() {
        return (message) -> mqttInboundReceiveHandle.handleMessage(message);
    }

}
