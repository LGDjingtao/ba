package com.subsystem.event;

import com.subsystem.entity.RealTimeData;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class SynRedisEvent extends ApplicationEvent {

    //实时数据
    RealTimeData realTimeData;


    public SynRedisEvent(Object source, @NonNull RealTimeData realTimeData) {
        super(source);
        this.realTimeData = realTimeData;
    }
}
