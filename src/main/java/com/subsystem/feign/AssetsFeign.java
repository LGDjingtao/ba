package com.subsystem.feign;

import com.subsystem.entity.ResultBean;
import com.subsystem.entity.SimpleReturnBo;
import com.subsystem.entity.ThresholdVo;
import com.subsystem.repository.mapping.AlarmInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;


@FeignClient(name = "sn-assets")
public interface AssetsFeign {

    /**
     * 接收告警事件
     */
    @GetMapping(value = "/asset/param/model/code/page", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean<List<ThresholdVo>> receive(@RequestParam String deviceCode);
}
