package com.subsystem.module.mqtt.config;


import com.subsystem.module.mqtt.prop.MqttProperties;
import com.subsystem.module.mqtt.hanlder.MqttInboundReceiveHandle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

/**
 * MQTT配置类
 */
@Configuration
@IntegrationComponentScan
@Slf4j
@AllArgsConstructor
public class MqttConfig {

    private final MqttProperties mqttProperties;


    /**
     * MQTT连接器
     */
    @Bean(name = "mqttConnectOptions")
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions m = new MqttConnectOptions();
        m.setServerURIs(new String[]{this.mqttProperties.getHostUrl()});
        m.setUserName(this.mqttProperties.getUsername());
        m.setPassword(this.mqttProperties.getPassword().toCharArray());
        m.setCleanSession(true);       // 设置是否清空session,false:表示服务器会保留客户端的连接记录，true:每次以新身份登录
        m.setConnectionTimeout(10);    // 设置超时时间、单位(秒)
        m.setAutomaticReconnect(true); // 设置自动连接
        m.setKeepAliveInterval(10);    // 设置会话心跳时间、单位(秒)
        return m;
    }

    /**
     * MQTT工厂
     */
    @Bean(name = "mqttPahoClientFactory")
    public MqttPahoClientFactory mqttPahoClientFactory(@Qualifier("mqttConnectOptions") MqttConnectOptions mqttConnectOptions) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }


}
