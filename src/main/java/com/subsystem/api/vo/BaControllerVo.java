package com.subsystem.api.vo;


import lombok.Data;

/**
 * BA子系统控制信息VO
 */
@Data
public class BaControllerVo {
    /**
     * 设备code web端为回路设备id，模型端为控制箱设备id
     */
    private String deviceCode;
    /**
     * 控制指令
     */
    private Integer control;
}
