package com.subsystem.core.module.staticdata;

import com.subsystem.core.common.Constants;
import com.subsystem.core.module.init.staticdata.SubSystemStaticDataInitDefaultModule;
import com.subsystem.core.repository.mapping.DeviceAlarmType;
import com.subsystem.core.repository.mapping.DeviceFaultType;
import com.subsystem.core.repository.mapping.DeviceInfo;
import com.subsystem.core.repository.mapping.DeviceLinkageRelationshipData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 子系统静态数据组件
 * 里面封装各种获取子系统静态数据的方法
 * 外部组件或者接口 获取静态数据 都从这个组件的方法里面获取
 */
@Slf4j
@Component
@AllArgsConstructor
public class SubSystemStaticDataDefaultModule extends SubSystemStaticData implements SubSystemStaticDataModule {

    SubSystemStaticDataInitDefaultModule subSystemStaticDataInitDefaultModule;

    /**
     * 获取设备code，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return 设备code
     */
    @Override
    public String getDeviceCodeByTripartiteCode(String tripartiteCode) throws Exception {
        DeviceInfo deviceInfo = getDeviceInfoByTripartiteCode(tripartiteCode);
        Optional.ofNullable(deviceInfo).orElseThrow(() -> {
            log.error("传入的三方标识:{}获取不到缓存key", tripartiteCode);
            return new Exception("获取不到缓存key");
        });
        return deviceInfo.getDeviceCode();
    }

    /**
     * 获取设备信息，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return 设备code
     */
    @Override
    public DeviceInfo getDeviceInfoByTripartiteCode(String tripartiteCode) {
        return deviceInfoBytripartiteCode.get(tripartiteCode);
    }

    /**
     * 获取设备物模型存redis的key，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return redis key
     */
    @Override
    public String getDeviceCodeRedisKeyByTripartiteCode(String tripartiteCode) throws Exception {
        String deviceCodeByTripartiteCode = getDeviceCodeByTripartiteCode(tripartiteCode);
        return Constants.PREFIX_FOR_OBJECT_MODEL_KEY + deviceCodeByTripartiteCode;

    }


    /**
     * 获取三方标识，通过设备code
     *
     * @param deviceCode 设备code
     * @return 设备code
     */
    @Override
    public String getTripartiteCodeByDeviceCode(String deviceCode) {
        return deviceInfoByCode.get(deviceCode).getDeviceTripartiteCode();
    }

    /**
     * 获取所有三方标识
     */
    @Override
    public List<String> getAllTripartiteCode() {
        return allDeviceInfo.stream().map(DeviceInfo::getDeviceTripartiteCode).collect(Collectors.toList());
    }

    /**
     * 获取所有设备编码（资产编码）
     */
    @Override
    public List<String> getAllDeviceCode() {
        return allDeviceInfo.stream().map(DeviceInfo::getDeviceCode).collect(Collectors.toList());
    }

    /**
     * 获取所有设备物模型的key
     */
    @Override
    public List<String> getAllModelKeys() {
        return getAllDeviceCode().stream().map(v -> Constants.PREFIX_FOR_OBJECT_MODEL_KEY + v).collect(Collectors.toList());
    }

    /**
     * 更新静态数据
     */
    @Override
    public void updateSubSystemStaticData() {
        subSystemStaticDataInitDefaultModule.init();
    }

    /**
     * 通过设备code 获取改设备的所有告警类型
     *
     * @param deviceCode 设备code
     * @return 告警类型数据
     */
    public List<DeviceAlarmType> getDeviceAlarmTypeByDeviceCode(String deviceCode) {
        DeviceInfo deviceInfo = deviceInfoByCode.get(deviceCode);
        String deviceTypeCode = deviceInfo.getDeviceTypeCode();
        return deviceAlarmTypeByType.get(deviceTypeCode);
    }

    /**
     * 通过设备code 获取改设备的所有故障类型
     *
     * @param deviceCode 设备code
     * @return 故障类型数据
     */
    public List<DeviceFaultType> getDeviceFaultTypeByDeviceCode(String deviceCode) {
        DeviceInfo deviceInfo = deviceInfoByCode.get(deviceCode);
        String deviceTypeCode = deviceInfo.getDeviceTypeCode();
        return deviceFaultTypeByType.get(deviceTypeCode);
    }

    /**
     * 获取所有联动设备关联信息
     *
     * @return 联动设备关联信息
     */
    public List<DeviceLinkageRelationshipData> getAllDeviceLinkageRelationship() {
        return allDeviceLinkageRelationship;
    }

    /**
     * 获取所有该设备类似的设备信息
     *
     * @param deviceType 设备类型
     * @return
     */
    public List<DeviceInfo> getDeviceInfoByTypeCode(String deviceType) {
        return deviceInfoByTypeCode.get(deviceType);
    }

}