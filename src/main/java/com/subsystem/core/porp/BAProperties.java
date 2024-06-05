package com.subsystem.core.porp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ba配置数据
 */
@Data
@ConfigurationProperties("ba")
public class BAProperties {
    String topic;//推送的topic
    Integer linkageTaskTime;//联动延迟任务时间
}
