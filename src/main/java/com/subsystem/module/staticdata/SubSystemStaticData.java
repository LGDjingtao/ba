package com.subsystem.module.staticdata;

import com.subsystem.repository.mapping.DeviceAlarmType;
import com.subsystem.repository.mapping.DeviceInfo;

import java.util.List;
import java.util.Map;

/**
 * 子系统静态数据 存储
 */
public abstract class SubSystemStaticData {
    /*********************设备信息********************/
    protected static List<DeviceInfo> allDeviceInfo;
    protected static Map<String, DeviceInfo> deviceInfoByCode;
    protected static Map<String, DeviceInfo> deviceInfoBytripartiteCode;
    protected static Map<String, List<DeviceInfo>> deviceInfoByTypeCode;

    /*********************设备告警********************/
    public static List<DeviceAlarmType> allDeviceAlarmType;
    public static Map<String, List<DeviceAlarmType>> deviceAlarmTypeByType;
}
