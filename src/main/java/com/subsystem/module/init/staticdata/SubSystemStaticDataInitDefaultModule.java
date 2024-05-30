package com.subsystem.module.init.staticdata;

import com.subsystem.module.init.InitModule;
import com.subsystem.repository.DeviceInfoRepository;
import com.subsystem.repository.mapping.DeviceInfo;
import com.subsystem.module.staticdata.SubSystemStaticData;
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

    @Override
    @PostConstruct
    public void init() {
        initDeviceInfo();
    }

    /**
     * 初始化设备信息
     */
    private void initDeviceInfo() {
        log.info("######### start 初始化设备信息");
        this.allDeviceInfo = deviceInfoRepository.findAll();
        log.info("######### in progress 初始化设备信息  ####  设备总数{}", allDeviceInfo.size());
        this.deviceInfoByCode = this.allDeviceInfo.stream().collect(Collectors.toMap(DeviceInfo::getDeviceCode, deviceInfo -> deviceInfo));
        this.deviceInfoBytripartiteCode = this.allDeviceInfo.stream().collect(Collectors.toMap(DeviceInfo::getDeviceTripartiteCode, deviceInfo -> deviceInfo));
        this.deviceInfoByTypeCode = this.allDeviceInfo.stream().collect(Collectors.groupingBy(DeviceInfo::getDeviceTypeCode));
        log.info("######### end 初始化设备信息");
    }
}
