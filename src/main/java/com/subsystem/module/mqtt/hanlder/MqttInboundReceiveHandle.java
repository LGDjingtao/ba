package com.subsystem.module.mqtt.hanlder;

import com.subsystem.assembly.SubSystemServiceCoreAssembly;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * 入站消息处理器
 */
@Component
@Slf4j
@AllArgsConstructor
public class MqttInboundReceiveHandle implements MessageHandler {
    SubSystemServiceCoreAssembly subSystemServiceCoreAssembly;

    @Override
    @ServiceActivator(inputChannel = "inBoundChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        //将消息转发给子系统业务组装模块
        subSystemServiceCoreAssembly.serviceAssemblyEntrance(message);
    }

}
