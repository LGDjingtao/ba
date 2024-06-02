package com.subsystem;

import com.subsystem.module.mqtt.prop.MqttProperties;
import com.subsystem.porp.BAProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({MqttProperties.class, BAProperties.class})
public class SubSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubSystemApplication.class, args);
    }
}
