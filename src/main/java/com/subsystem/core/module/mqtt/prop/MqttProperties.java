package com.subsystem.core.module.mqtt.prop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("mqtt")
public class MqttProperties {
    /**
     * 地址
     */
    private String hostUrl;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 网关向平台推送数据订阅者客户端id
     */
    private String inboundClientIdBA;
    /**
     * 网关向平台推送数据订阅主题
     */
    private String inboundGatewayTopicBA;

    /**
     * 网关向平台推送数据订阅者客户端id
     */
    private String inboundClientIdNY;
    /**
     * 网关向平台推送数据订阅主题
     */
    private String inboundGatewayTopicNY;

    /**
     * 网关向平台推送数据订阅者客户端id
     */
    private String inboundClientIdDL;
    /**
     * 网关向平台推送数据订阅主题
     */
    private String inboundGatewayTopicDL;

    /**
     * 网关向平台推送数据订阅者客户端id
     */
    private String inboundClientIdHJ;
    /**
     * 网关向平台推送数据订阅主题
     */
    private String inboundGatewayTopicHJ;


    /**
     * 网关向平台推送数据订阅者客户端id
     */
    private String inboundClientIdIAQ;
    /**
     * 网关向平台推送数据订阅主题
     */
    private String inboundGatewayTopicIAQ;


    /**
     * 平台向网关推送数据订阅者客户端id
     */
    private String outboundClientId;
    /**
     * 平台向网关推送数据订阅主题
     */
    private String[] outboundPlatformTopics;
    /**
     * 默认网关
     */
    private String defaultTopic;
    /**
     * 超时时间
     */
    private int timeout;
    /**
     * 心跳(秒)
     */
    private int keepalive;
    /**
     * mqtt开关
     */
    private boolean enabled;
}
