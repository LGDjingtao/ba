package com.subsystem.core.event;

import com.subsystem.core.module.SubSystemDefaultContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 联动事件
 */
@Getter
@Setter
public class LinkageEvent extends ApplicationEvent {
    /**
     * 子系统上下文
     */
    private SubSystemDefaultContext subSystemDefaultContext;

    public LinkageEvent(Object source, SubSystemDefaultContext subSystemDefaultContext) {
        super(source);
        this.subSystemDefaultContext = subSystemDefaultContext;
    }

}