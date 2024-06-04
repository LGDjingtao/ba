package com.subsystem.module.init.linkage;


import com.alibaba.fastjson.JSONObject;
import com.subsystem.event.LinkageEvent;
import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.repository.RepositoryModule;
import com.subsystem.repository.mapping.LinkageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 初始化联动任务
 */
@Component
@AllArgsConstructor
@Slf4j
public class LinkageInitModule {
    //事件驱动模块
    ApplicationContext eventDrivenModule;
    //数据库模块
    RepositoryModule repositoryModule;

    @EventListener(classes = ApplicationReadyEvent.class)
    public void alarmEventListener(ApplicationReadyEvent applicationReadyEvent) {
        List<LinkageInfo> allLinkageInfo = repositoryModule.getAllLinkageInfo();
        for (LinkageInfo linkageInfo : allLinkageInfo) {
            String subSystemContext = linkageInfo.getSubSystemContext();
            SubSystemDefaultContext subSystemDefaultContext = JSONObject.parseObject(subSystemContext,SubSystemDefaultContext.class);
            LinkageEvent linkageEvent = new LinkageEvent(this,subSystemDefaultContext);
            eventDrivenModule.publishEvent(linkageEvent);
        }
    }
}
