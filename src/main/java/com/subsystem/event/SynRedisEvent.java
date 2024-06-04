package com.subsystem.event;

import com.subsystem.module.SubSystemDefaultContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 数据同步事件
 */
@Getter
@Setter
public class SynRedisEvent extends ApplicationEvent {
    /**
     * 缓存key
     */
    String key;
    /**
     * 实时物模型数据（value）
     */
    String realTimeData;

    public SynRedisEvent(Object source, @NonNull SubSystemDefaultContext subSystemDefaultContext) {
        super(source);
        this.key = subSystemDefaultContext.getKey();
        this.realTimeData = subSystemDefaultContext.getRealTimeData();
    }
}
