package com.subsystem.module.mqtt.prop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("mqtt")
public class MqttProperties {
    /** 地址 */
    private String hostUrl;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
    /** 网关向平台推送数据订阅者客户端id */
    private String inboundClientId;
    /** 网关向平台推送数据订阅主题 */
    private String[] inboundGatewayTopics;
    /** 平台向网关推送数据订阅者客户端id */
    private String outboundClientId;
    /** 平台向网关推送数据订阅主题 */
    private String[] outboundPlatformTopics;
    /** 默认网关 */
    private String defaultTopic;
    /** 超时时间 */
    private int timeout;
    /** 心跳(秒) */
    private int keepalive;
    /** mqtt开关 */
    private boolean enabled;
}
