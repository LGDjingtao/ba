package com.subsystem.module.staticdata;

import com.subsystem.module.init.staticdata.SubSystemStaticDataInitDefaultModule;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
    public String getDeviceCodeByTripartiteCode(String tripartiteCode) {
        return deviceInfoBytripartiteCode.get(tripartiteCode).getDeviceCode();
    }

    /**
     * 获取三方标识，通过设备code
     *
     * @param deviceCode 设备code
     * @return 设备code
     */
    public String getTripartiteCodeByDeviceCode(String deviceCode) {
        return deviceInfoByCode.get(deviceCode).getDeviceTripartiteCode();
    }

    /**
     * 更新静态数据
     */
    @Override
    public void updateSubSystemStaticData() {
        subSystemStaticDataInitDefaultModule.init();
    }
}