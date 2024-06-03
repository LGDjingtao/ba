package com.subsystem.event;

import com.subsystem.repository.mapping.AlarmInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 告警事件
 */
@Getter
@Setter
public class AlarmEvent extends ApplicationEvent {
    //告警信息
    private AlarmInfo alarmInfo;

    public AlarmEvent(Object source, AlarmInfo alarmInfo) {
        super(source);
        this.alarmInfo = alarmInfo;
    }
}
