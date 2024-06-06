package com.subsystem.core.feign;

import com.subsystem.core.common.Constants;
import com.subsystem.core.entity.ResultBean;
import com.subsystem.core.entity.ThresholdVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 资产服务接口
 */
@FeignClient(name = "sn-assets")
public interface AssetsFeign {

    /**
     * 接收告警事件
     */
    @GetMapping(value = "/asset/param/model/code/page", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean<List<ThresholdVo>> receive(@RequestParam String deviceCode);
}
