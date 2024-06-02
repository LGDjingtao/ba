package com.subsystem.porp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ba配置数据
 */
@Data
@ConfigurationProperties("ba")
public class BAProperties {
    String topic;//推送的topic
    String alarmCenterUrl;//报警推送
    String thresholdUrl;//阈值获取接口
}
