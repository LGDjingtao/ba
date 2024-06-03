package com.subsystem.module.linkage;

import lombok.Data;

/**
 * 联动信息
 */
@Data
public class LinkageInfo {
    //触发设备code
    private String triggerDeviceCode;

    //联动设备code
    private String linkageDeviceCode;

    //是否是首次推送联动事件 第一次就不用检测是否告警
    private boolean isFirst;

}
