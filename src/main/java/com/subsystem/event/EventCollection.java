package com.subsystem.event;

import com.subsystem.entity.RealTimeData;
import com.subsystem.repository.mapping.AlarmInfo;
import lombok.Data;
import lombok.NonNull;

@Data
public class EventCollection {
    private SynRedisEvent synRedisEvent;
    private AlarmEvent alarmEvent;

    public void createSynRedisEvent(RealTimeData realTimeData) {
        if (null == realTimeData) return;
        synRedisEvent = new SynRedisEvent(this, realTimeData);
    }

    public void createAlarmEvent(AlarmInfo alarmInfo) {
        if (null == alarmInfo) return;
        alarmEvent = new AlarmEvent(this, alarmInfo);
    }

}
