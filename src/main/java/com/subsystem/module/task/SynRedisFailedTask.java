package com.subsystem.module.task;


import com.github.benmanes.caffeine.cache.Cache;
import com.subsystem.entity.RealTimeData;
import com.subsystem.event.SynRedisEvent;
import com.subsystem.module.cache.CaffeineCacheModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
@AllArgsConstructor
public class SynRedisFailedTask extends ScheduleTask {
    CaffeineCacheModule caffeineCacheModule;
    //事件驱动模块
    ApplicationContext eventDrivenModule;

    @Scheduled(cron = "0 0/1 * * * ? ")
    @Override
    public void run() {
        Cache<Object, Object> synRedisFailedCache = caffeineCacheModule.getSynRedisFailedCache();
        ConcurrentMap<@NonNull Object, @NonNull Object> map = synRedisFailedCache.asMap();
        if (map.isEmpty()) return;
        map.entrySet().stream().forEach(this::publishEvent);
    }

    public void publishEvent(Map.Entry<Object, Object> entry) {
        String key = (String) entry.getKey();
        String realTimeDataStr = (String) entry.getValue();
        RealTimeData realTimeData = new RealTimeData();
        realTimeData.setKey(key);
        realTimeData.setRealTimeData(realTimeDataStr);
        SynRedisEvent synRedisEvent = new SynRedisEvent(this, realTimeData);
        eventDrivenModule.publishEvent(synRedisEvent);
    }

}