package com.subsystem.core.module.task;


import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.core.entity.ResultBean;
import com.subsystem.core.feign.AlarmCenterFeign;
import com.subsystem.core.repository.RepositoryModule;
import com.subsystem.core.repository.mapping.AlarmInfo;
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

    @Scheduled(cron = "0 0/1 * * * ? ")
    @Override
    public void run() {
        //log.info("定时推送 告警事件推送失败的事件");
        List<AlarmInfo> alarmFiledInfo = repositoryModule.findAlarmFiledInfo();
        alarmFiledInfo.forEach(this::pushAlarmData);
    }

    /**
     * 推送告警数据
     *
     * @param alarmInfo 告警数据
     */
    private void pushAlarmData(AlarmInfo alarmInfo) {
        try {
            String alarmid = alarmInfo.getAlarmid();
            alarmInfo.setAlarmid(null);
            ResultBean receive = alarmCenterFeign.receive(alarmInfo);
            int code = receive.getCode();
            if (!NumberUtil.equals(code, 200)) {
                log.error("推送告警信息接口报错，code：{}", code);
                return;
            }
            repositoryModule.deleteAlarmFiledInfoById(alarmid);
        } catch (Exception e) {
            log.info("失败告警信息重新推送失败:{}", JSONObject.toJSONString(alarmInfo), e);
        }

    }


}
