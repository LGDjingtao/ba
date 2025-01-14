package com.subsystem.core.module.staticdata;

import com.subsystem.core.repository.mapping.DeviceAlarmType;
import com.subsystem.core.repository.mapping.DeviceFaultType;
import com.subsystem.core.repository.mapping.DeviceInfo;
import com.subsystem.core.repository.mapping.DeviceLinkageRelationshipData;

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

    /*********************设备故障********************/

    public static List<DeviceFaultType> allDeviceFaultType;
    public static Map<String, List<DeviceFaultType>> deviceFaultTypeByType;

    /*********************设备联动********************/
    public static List<DeviceLinkageRelationshipData> allDeviceLinkageRelationship;
}
