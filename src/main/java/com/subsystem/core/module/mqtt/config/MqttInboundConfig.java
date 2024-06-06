package com.subsystem.core.module.mqtt.config;


import com.subsystem.core.module.mqtt.hanlder.MqttInboundReceiveHandle;
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
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;


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
    @Bean(name = "inBoundChannel_BA")
    public MessageChannel mqttInboundChannelBA() {
        return new DirectChannel();
    }


    /**
     * BA管道适配器
     */
    @Bean("mqttPahoMessageDrivenChannelAdapter_BA" )
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterBA(
            @Qualifier("mqttPahoClientFactory") MqttPahoClientFactory mqttPahoClientFactory,
            @Qualifier("inBoundChannel_BA") MessageChannel messageChannel
    ) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                this.mqttProperties.getInboundClientIdBA(),
                mqttPahoClientFactory,
                this.mqttProperties.getInboundGatewayTopicBA());
        adapter.setCompletionTimeout(this.mqttProperties.getTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());  // 编解码
        adapter.setQos(2);
        adapter.setOutputChannel(messageChannel);
        return adapter;
    }

    /**
     * BA入站消息处理器
     */
    @Bean
    @ServiceActivator(inputChannel = "inBoundChannel_BA")
    public MessageHandler handlerBA() {
        return (message) -> mqttInboundReceiveHandle.handleMessage(message);
    }



    /**
     * 入站管道
     */
    @Bean(name = "inBoundChannel_NY")
    public MessageChannel mqttInboundChannelNY() {
        return new DirectChannel();
    }

    /**
     * 入站管道适配器
     */
    @Bean("mqttPahoMessageDrivenChannelAdapter_NY" )
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterNY(
            @Qualifier("mqttPahoClientFactory") MqttPahoClientFactory mqttPahoClientFactory,
            @Qualifier("inBoundChannel_NY") MessageChannel messageChannel
    ) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                this.mqttProperties.getInboundClientIdNY(),
                mqttPahoClientFactory,
                this.mqttProperties.getInboundGatewayTopicNY());
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
    @ServiceActivator(inputChannel = "inBoundChannel_NY")
    public MessageHandler handlerNY() {
        return (message) -> mqttInboundReceiveHandle.handleMessage(message);
    }


    /**
     * 入站管道
     */
    @Bean(name = "inBoundChannel_DL")
    public MessageChannel mqttInboundChannelDL() {
        return new DirectChannel();
    }

    /**
     * 入站管道适配器
     */
    @Bean("mqttPahoMessageDrivenChannelAdapter_DL" )
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapterDL(
            @Qualifier("mqttPahoClientFactory") MqttPahoClientFactory mqttPahoClientFactory,
            @Qualifier("inBoundChannel_DL") MessageChannel messageChannel
    ) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                this.mqttProperties.getInboundClientIdDL(),
                mqttPahoClientFactory,
                this.mqttProperties.getInboundGatewayTopicDL());
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
    @ServiceActivator(inputChannel = "inBoundChannel_DL")
    public MessageHandler handlerDL() {
        return (message) -> mqttInboundReceiveHandle.handleMessage(message);
    }

}
