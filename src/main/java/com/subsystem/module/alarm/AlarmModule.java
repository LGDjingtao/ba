package com.subsystem.module.alarm;

import com.subsystem.event.AlarmEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AlarmModule {
    /**
     * 监听告警事件
     *
     * @param alarmEvent 告警事件
     */
    @EventListener(classes = AlarmEvent.class)
    private void alarmEventListener(AlarmEvent alarmEvent) {

    }
}
