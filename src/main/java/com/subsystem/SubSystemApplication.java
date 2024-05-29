package com.subsystem;

import com.subsystem.module.mqtt.prop.MqttProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties({MqttProperties.class})
public class SubSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubSystemApplication.class, args);
    }
}
