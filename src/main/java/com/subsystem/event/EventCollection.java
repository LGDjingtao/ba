package com.subsystem.event;

import lombok.Data;

@Data
public class EventCollection {
    private SynRedisEvent synRedisEvent;
    private AlarmEvent alarmEvent;
}
