package com.subsystem.event;

import com.subsystem.module.SubSystemDefaultContext;
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
    private SubSystemDefaultContext subSystemDefaultContext;

    public AlarmEvent(Object source, SubSystemDefaultContext subSystemDefaultContext) {
        super(source);
        this.subSystemDefaultContext = subSystemDefaultContext;
    }
}
