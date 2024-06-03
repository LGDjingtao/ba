package com.subsystem.module.init.staticdata;

import com.subsystem.module.init.InitModule;
import com.subsystem.module.staticdata.SubSystemStaticData;
import com.subsystem.repository.DeviceAlarmTypeRepository;
import com.subsystem.repository.DeviceFaultTypeRepository;
import com.subsystem.repository.DeviceInfoRepository;
import com.subsystem.repository.DeviceLinkageRelationshipRepository;
import com.subsystem.repository.mapping.DeviceAlarmType;
import com.subsystem.repository.mapping.DeviceFaultType;
import com.subsystem.repository.mapping.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

/**
 * 子系统静态数据初始化逻辑组件
 */
@Component
@AllArgsConstructor
@Slf4j
public class SubSystemStaticDataInitDefaultModule extends SubSystemStaticData implements InitModule {
    DeviceInfoRepository deviceInfoRepository;
    DeviceAlarmTypeRepository deviceAlarmTypeRepository;
    DeviceFaultTypeRepository deviceFaultTypeRepository;
    DeviceLinkageRelationshipRepository deviceLinkageRelationshipRepository;

    @Override
    @PostConstruct
    public void init() {
        initDeviceInfo();
        initDeviceAlarmType();
        initDeviceFaultType();
        initDeviceLinkage();
    }

    /**
     * 初始化设备信息
     */
    private void initDeviceInfo() {
        log.info("Static######### start 初始化设备信息");
        this.allDeviceInfo = deviceInfoRepository.findAll();
        log.info("Static######### in progress 初始化设备信息  ####  设备总数{}", allDeviceInfo.size());
        this.deviceInfoByCode = this.allDeviceInfo.stream().collect(Collectors.toMap(DeviceInfo::getDeviceCode, deviceInfo -> deviceInfo));
        this.deviceInfoBytripartiteCode = this.allDeviceInfo.stream().collect(Collectors.toMap(DeviceInfo::getDeviceTripartiteCode, deviceInfo -> deviceInfo));
        this.deviceInfoByTypeCode = this.allDeviceInfo.stream().collect(Collectors.groupingBy(DeviceInfo::getDeviceTypeCode));
        log.info("Static######### end 初始化设备信息");
    }

    /**
     * 初始化设备告警类型
     */
    private void initDeviceAlarmType() {
        log.info("Static######### 初始化设备告警类型");
        this.allDeviceAlarmType = deviceAlarmTypeRepository.findAll();
        this.deviceAlarmTypeByType = this.allDeviceAlarmType.stream().collect(Collectors.groupingBy(DeviceAlarmType::getDeviceType));
    }

    /**
     * 初始化设备故障类型
     */
    private void initDeviceFaultType() {
        log.info("Static######### 初始化设备故障类型");
        this.allDeviceFaultType = deviceFaultTypeRepository.findAll();
        this.deviceFaultTypeByType = this.allDeviceFaultType.stream().collect(Collectors.groupingBy(DeviceFaultType::getDeviceType));
    }

    /**
     * 初始化设备联动
     */
    private void initDeviceLinkage() {
        log.info("Static######### 初始化设备联动");
        this.allDeviceLinkageRelationship = deviceLinkageRelationshipRepository.findAll();
    }
}
