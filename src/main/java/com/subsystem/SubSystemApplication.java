package com.subsystem;

import com.subsystem.core.module.mqtt.prop.MqttProperties;
import com.subsystem.core.porp.BAProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({MqttProperties.class, BAProperties.class})
public class SubSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubSystemApplication.class, args);
    }
}
