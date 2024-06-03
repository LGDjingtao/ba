package com.subsystem.feign;

import com.subsystem.entity.ResultBean;
import com.subsystem.entity.SimpleReturnBo;
import com.subsystem.repository.mapping.AlarmInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@FeignClient(name = "sn-alarm-center")
public interface AlarmCenterFeign {

    /** 接收告警事件 */
    @PostMapping(value = "/alarm/receive/event", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean receive(@Valid @RequestBody AlarmInfo vo);
}
