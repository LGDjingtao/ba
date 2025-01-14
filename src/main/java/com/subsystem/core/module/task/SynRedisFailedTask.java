package com.subsystem.core.module.task;


import com.github.benmanes.caffeine.cache.Cache;
import com.subsystem.core.event.SynRedisEvent;
import com.subsystem.core.module.SubSystemDefaultContext;
import com.subsystem.core.module.cache.CaffeineCacheModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 定时把之前本地同步到redis失败的数据再次去尝试同步到redis
 */
@Component
@Slf4j
@AllArgsConstructor
public class SynRedisFailedTask extends ScheduleTask {
    //缓存模块
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
        SubSystemDefaultContext subSystemDefaultContext = new SubSystemDefaultContext();
        subSystemDefaultContext.setKey(key);
        subSystemDefaultContext.setRealTimeData(realTimeDataStr);
        SynRedisEvent synRedisEvent = new SynRedisEvent(this, subSystemDefaultContext);
        log.info("重新同步redis\nkey{}\nvalue:{}", key, realTimeDataStr);
        eventDrivenModule.publishEvent(synRedisEvent);
    }

}
