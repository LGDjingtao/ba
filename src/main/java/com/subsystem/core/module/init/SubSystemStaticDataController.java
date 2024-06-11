package com.subsystem.core.module.init;

import com.subsystem.core.entity.ResultBean;
import com.subsystem.core.module.staticdata.SubSystemStaticDataDefaultModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供数据数据更新操作接口
 */
@Slf4j
@RestController
@RequestMapping(value = "/subsystem")
@AllArgsConstructor
public class SubSystemStaticDataController {
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;

    /**
     * 更新子系统静态数据接口
     */
    @GetMapping(value = "/update/staticdata")
    public ResultBean updateSubSystemStaticData() {
        subSystemStaticDataDefaultModule.updateSubSystemStaticData();
        return ResultBean.success("更新子系统静态数据成功");
    }
}
