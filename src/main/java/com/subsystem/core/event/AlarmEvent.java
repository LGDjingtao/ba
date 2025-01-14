package com.subsystem.core.event;

import com.subsystem.core.module.SubSystemDefaultContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 告警事件
 */
@Getter
@Setter
public class AlarmEvent extends ApplicationEvent {
    /**
     * 子系统上下文
     */
    private SubSystemDefaultContext subSystemDefaultContext;

    public AlarmEvent(Object source, SubSystemDefaultContext subSystemDefaultContext) {
        super(source);
        this.subSystemDefaultContext = subSystemDefaultContext;
    }
}
