package com.subsystem.core.feign;

import com.subsystem.core.entity.ResultBean;
import com.subsystem.core.repository.mapping.AlarmInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * 告警服务接口
 */
@FeignClient(name = "sn-alarm-center")
public interface AlarmCenterFeign {

    /**
     * 接收告警事件
     */
    @PostMapping(value = "/alarm/receive/event", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean receive(@Valid @RequestBody AlarmInfo vo);
    /**
     * 是否有告警记录
     */
    @GetMapping("/alarm/hasAlarmHistoryRecord")
    boolean hasAlarmRecord(@RequestParam String alarmDeviceId, @RequestParam String alarmCategory);

    /**
     * 消警
     */
    @GetMapping("/alarm/markAlarmAsProcessed")
    ResultBean<Integer> markAlarmAsProcessed(@RequestParam String alarmDeviceId, @RequestParam String alarmCategory);
}
