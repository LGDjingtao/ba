package com.subsystem.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class SynRedisEvent extends ApplicationEvent {
    //缓存key
    private String key;

    public SynRedisEvent(Object source, String key) {
        super(source);
        this.key = key;
    }
}
