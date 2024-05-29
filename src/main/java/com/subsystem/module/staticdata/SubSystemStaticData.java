package com.subsystem.module.staticdata;

import com.subsystem.repository.mapping.DeviceInfo;

import java.util.List;
import java.util.Map;

/**
 * 子系统静态数据 存储
 */
public abstract class SubSystemStaticData {
    protected static List<DeviceInfo> allDeviceInfo;
    protected static Map<String, DeviceInfo> deviceInfoByCode;
    protected static Map<String, DeviceInfo> deviceInfoBytripartiteCode;
    protected static Map<String, List<DeviceInfo>> deviceInfoByTypeCode;
}
