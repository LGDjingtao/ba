package com.subsystem.module.staticdata;

import com.subsystem.common.Constants;
import com.subsystem.module.init.staticdata.SubSystemStaticDataInitDefaultModule;
import com.subsystem.repository.mapping.DeviceInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 子系统静态数据组件
 * 里面封装各种获取子系统静态数据的方法
 * 外部组件或者接口 获取静态数据 都从这个组件的方法里面获取
 */
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
    public String getDeviceCodeByTripartiteCode(String tripartiteCode) {
        return deviceInfoBytripartiteCode.get(tripartiteCode).getDeviceCode();
    }

    /**
     * 获取设备物模型存redis的key，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return redis key
     */
    @Override
    public String getDeviceCodeRedisKeyByTripartiteCode(String tripartiteCode) {
        return Constants.PREFIX_FOR_OBJECT_MODEL_KEY + getDeviceCodeByTripartiteCode(tripartiteCode);

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
}