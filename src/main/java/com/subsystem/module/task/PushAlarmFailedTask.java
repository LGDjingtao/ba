package com.subsystem.module.task;


import cn.hutool.core.util.NumberUtil;
import com.subsystem.entity.ResultBean;
import com.subsystem.feign.AlarmCenterFeign;
import com.subsystem.repository.RepositoryModule;
import com.subsystem.repository.mapping.AlarmInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时推送 告警事件推送失败的事件
 */
@Component
@Slf4j
@AllArgsConstructor
public class PushAlarmFailedTask extends ScheduleTask {

    RepositoryModule repositoryModule;
    AlarmCenterFeign alarmCenterFeign;

    @Scheduled(cron = "0/1 0 * * * ? ")
    @Override
    public void run() {
        log.info("定时推送 告警事件推送失败的事件");
        List<AlarmInfo> alarmFiledInfo = repositoryModule.findAlarmFiledInfo();
        alarmFiledInfo.forEach(this::pushAlarmData);
    }

    /**
     * 推送告警数据
     *
     * @param alarmInfo 告警数据
     */
    private void pushAlarmData(AlarmInfo alarmInfo) {
        String alarmid = alarmInfo.getAlarmid();
        alarmInfo.setAlarmid(null);
        ResultBean receive = alarmCenterFeign.receive(alarmInfo);
        int code = receive.getCode();
        if (!NumberUtil.equals(code, 200)) {
            log.error("推送告警信息接口报错，code：{}", code);
            return;
        }
        repositoryModule.deleteAlarmFiledInfoById(alarmid);
    }


}
