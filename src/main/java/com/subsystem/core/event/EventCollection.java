package com.subsystem.core.event;

import com.subsystem.core.module.SubSystemDefaultContext;
import com.subsystem.core.module.linkage.LinkageInfo;
import com.subsystem.core.repository.mapping.AlarmInfo;
import lombok.Data;

/**
 * 事件集和
 */
@Data
public class EventCollection {
    private SynRedisEvent synRedisEvent;
    private AlarmEvent alarmEvent;
    private LinkageEvent linkageEvent;

    public void createSynRedisEvent(SubSystemDefaultContext subSystemDefaultContext) {
        String realTimeData = subSystemDefaultContext.getRealTimeData();
        if (null == realTimeData) return;
        synRedisEvent = new SynRedisEvent(this, subSystemDefaultContext);
    }

    public void createAlarmEvent(SubSystemDefaultContext subSystemDefaultContext) {
        AlarmInfo alarmInfo = subSystemDefaultContext.getAlarmInfo();
        if (null == alarmInfo) return;
        alarmEvent = new AlarmEvent(this, subSystemDefaultContext);
    }

    public void createLinkageEvent(SubSystemDefaultContext subSystemDefaultContext) {
        LinkageInfo linkageInfo = subSystemDefaultContext.getLinkageInfo();
        if (null == linkageInfo) return;
        linkageEvent = new LinkageEvent(this, subSystemDefaultContext);
    }

}
