package com.subsystem.controller;

import com.subsystem.entity.ResultBean;
import com.subsystem.module.staticdata.SubSystemStaticDataDefaultModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/subsystem")
@AllArgsConstructor
public class SubSystemStaticDataController {
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;

    /**
     * 更新子系统静态数据
     */
    @GetMapping(value = "/update/staticdata")
    public ResultBean updateSubSystemStaticData() {
        subSystemStaticDataDefaultModule.updateSubSystemStaticData();
        return ResultBean.success("更新子系统静态数据成功");
    }
}
