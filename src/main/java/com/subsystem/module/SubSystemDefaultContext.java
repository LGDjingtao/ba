package com.subsystem.module;

import com.subsystem.module.linkage.LinkageInfo;
import com.subsystem.repository.mapping.AlarmInfo;
import com.subsystem.repository.mapping.DeviceAlarmType;
import com.subsystem.repository.mapping.DeviceInfo;
import lombok.Data;

import java.util.Date;

/**
 * 子系统上下文，保存子系统上下文数据
 */
@Data
public class SubSystemDefaultContext {
    /**
     * 设备信息
     */
    private DeviceInfo deviceInfo;
    /**
     * 告警信息
     */
    private AlarmInfo alarmInfo;
    /**
     * 告警还是消警 true-告警 false-消警
     */
    private Boolean alarmOrAlarmCancel;
    /**
     * 联动信息
     */
    private LinkageInfo linkageInfo;
    /**
     * 物模型别名
     */
    private String alias;
    /**
     * 实时物模型值
     */
    private Object value;
    /**
     * 接受到数据时间
     */
    private Date timestamp;
    /**
     * 缓存key
     */
    private String key;
    /**
     * 实时物模型所有数据
     */
    private String realTimeData;
    /**
     * 告警类型信息
     */
    private DeviceAlarmType deviceAlarmType;
}
