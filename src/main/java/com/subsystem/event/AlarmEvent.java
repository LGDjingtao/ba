package com.subsystem.event;

import org.springframework.context.ApplicationEvent;

public class AlarmEvent extends ApplicationEvent {
    //事件信息
//    private String key;

    public AlarmEvent(Object source) {
        super(source);

    }
}
