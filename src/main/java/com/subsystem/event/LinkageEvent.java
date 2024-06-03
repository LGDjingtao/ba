package com.subsystem.event;

import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.module.linkage.LinkageInfo;
import com.subsystem.repository.mapping.AlarmInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 联动事件
 */
@Getter
@Setter
public class LinkageEvent extends ApplicationEvent {
    private SubSystemDefaultContext subSystemDefaultContext;

    public LinkageEvent(Object source, SubSystemDefaultContext subSystemDefaultContext) {
        super(source);
        this.subSystemDefaultContext = subSystemDefaultContext;
    }

}