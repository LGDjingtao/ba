package com.subsystem.core.module.staticdata;

import com.subsystem.core.repository.mapping.DeviceAlarmType;
import com.subsystem.core.repository.mapping.DeviceFaultType;
import com.subsystem.core.repository.mapping.DeviceInfo;

import java.util.List;

/**
 * 子系统静态数据组件接口
 */
public interface SubSystemStaticDataModule {

    /**
     * 获取设备code，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return 设备code
     */
    String getDeviceCodeByTripartiteCode(String tripartiteCode) throws Exception;

    /**
     * 获取设备信息，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return 设备信息
     */
    DeviceInfo getDeviceInfoByTripartiteCode(String tripartiteCode);

    /**
     * 获取设备物模型存redis的key，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return redis key
     */
    String getDeviceCodeRedisKeyByTripartiteCode(String tripartiteCode) throws Exception;

    /**
     * 获取三方标识，通过设备code
     *
     * @param deviceCode 设备code
     * @return 设备code
     */
    String getTripartiteCodeByDeviceCode(String deviceCode);

    /**
     * 提供更新静态缓存的方法
     * 使得更改数据库后可以热更数据
     */
    void updateSubSystemStaticData();


    /**
     * 获取所有三方标识
     */
    List<String> getAllTripartiteCode();


    /**
     * 获取所有设备编码（资产编码）
     */
    List<String> getAllDeviceCode();


    /**
     * 获取所有设备物模型的key
     */
    List<String> getAllModelKeys();

    /**
     * 通过设备code 获取改设备的所有告警类型
     *
     * @param deviceCode 设备code
     * @return 告警类型数据
     */
    List<DeviceAlarmType> getDeviceAlarmTypeByDeviceCode(String deviceCode);

    /**
     * 通过设备code 获取改设备的所有故障类型
     *
     * @param deviceCode 设备code
     * @return 故障类型数据
     */
    List<DeviceFaultType> getDeviceFaultTypeByDeviceCode(String deviceCode);
}
