package com.subsystem.api;


import com.subsystem.api.repository.ElectricalFireDetectorAssociationData;
import com.subsystem.api.repository.ElectricalFireDetectorAssociationDataRepository;
import com.subsystem.api.repository.ElectricitySafetyAreaData;
import com.subsystem.api.repository.ElectricitySafetyAreaDataRepository;
import com.subsystem.core.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.core.repository.mapping.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
@DependsOn("subSystemStaticDataInitDefaultModule")
public class InitDeviceInfo {
    /**
     * 静态数据模块
     */
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    /**
     * 电器火灾探测器关联 电柜 探针 电表 信息
     */
    ElectricalFireDetectorAssociationDataRepository electricalFireDetectorAssociationDataRepository;
    /**
     * 用电安全区域数据
     */
    ElectricitySafetyAreaDataRepository electricitySafetyAreaDataRepository;

    public static List<ElectricalFireDetectorAssociationData> allElectricalFireAssociationData;
    public static Map<String, List<ElectricalFireDetectorAssociationData>> electricalFireAssociationByCode;
    public static List<ElectricitySafetyAreaData> allElectricitySafetyAreaData;
    public static Map<String, List<ElectricitySafetyAreaData>> electricitySafetyAreaByCode;
    public static Map<String, List<ElectricitySafetyAreaData>> electricitySafetyAreaByTripartiteCode;
    public static Map<String, List<ElectricitySafetyAreaData>> electricitySafetyAreaByBuildingCode;

    @PostConstruct
    public void init() {

        log.info("################## START : {}### 从数据库获取用电安全静态数据....", DateTime.now().toString(Common.Time_Format));
        this.allElectricalFireAssociationData = electricalFireDetectorAssociationDataRepository.findAll();
        this.allElectricitySafetyAreaData = electricitySafetyAreaDataRepository.findAll();
        log.info("################## END : {}### 从数据库获取用电安全静态数据....", DateTime.now().toString(Common.Time_Format));
        /**
         *扩展静态数据
         * 1. 防止表格数据冗余
         * 2. 预防录入冗余数据时 人为录入错误 采用程序进行扩展
         * 3. 数据预处理 提升程序效率
         */
        extendedStaticData();
    }

    private void extendedStaticData() {
        log.info("################## START : {}### 电安全扩展静态数据....", DateTime.now().toString(Common.Time_Format));
        //扩展用电安全区域数据
        extendedElectricitySafetyAreaData();
        //扩展用电安全区域数据
        extendElectricalFireDetectorAssociationData();
        log.info("################## END : {}### 电安全扩展静态数据....", DateTime.now().toString(Common.Time_Format));
    }

    private void extendedElectricitySafetyAreaData() {
        for (ElectricitySafetyAreaData electricitySafetyAreaDatum : allElectricitySafetyAreaData) {
            String deviceTripartiteCode = electricitySafetyAreaDatum.getDeviceTripartiteCode();
            DeviceInfo deviceInfo = subSystemStaticDataDefaultModule.getDeviceInfoByTripartiteCode(deviceTripartiteCode);
            electricitySafetyAreaDatum.setDeviceTypeCode(deviceInfo.getDeviceTypeCode());
            electricitySafetyAreaDatum.setDeviceLocationName(deviceInfo.getDeviceLocationName());
            electricitySafetyAreaDatum.setDeviceLocationCode(deviceInfo.getDeviceLocationCode());
            electricitySafetyAreaDatum.setDeviceTypeCode(deviceInfo.getDeviceTypeCode());
            electricitySafetyAreaDatum.setDeviceCode(deviceInfo.getDeviceCode());
            electricitySafetyAreaDatum.setDeviceTypeName(deviceInfo.getDeviceTypeName());
        }

        electricitySafetyAreaByCode = allElectricitySafetyAreaData.stream().collect(Collectors.groupingBy(ElectricitySafetyAreaData::getDeviceCode));
        electricitySafetyAreaByTripartiteCode = allElectricitySafetyAreaData.stream().collect(Collectors.groupingBy(ElectricitySafetyAreaData::getDeviceTripartiteCode));
        electricitySafetyAreaByBuildingCode = allElectricitySafetyAreaData.stream().collect(Collectors.groupingBy(ElectricitySafetyAreaData::getBuildingCode));
    }

    private static void extendElectricalFireDetectorAssociationData() {
        for (ElectricalFireDetectorAssociationData electricalFireDetectorAssociationData : allElectricalFireAssociationData) {
            String deviceTripartiteCode = electricalFireDetectorAssociationData.getDeviceTripartiteCode();
            List<ElectricitySafetyAreaData> electricitySafetyAreaDatas = electricitySafetyAreaByTripartiteCode.get(deviceTripartiteCode);
            ElectricitySafetyAreaData electricitySafetyAreaData = electricitySafetyAreaDatas.get(0);
            electricalFireDetectorAssociationData.setBuildingCode(electricitySafetyAreaData.getBuildingCode());
            electricalFireDetectorAssociationData.setBuildingName(electricitySafetyAreaData.getBuildingName());
            electricalFireDetectorAssociationData.setFloor(electricitySafetyAreaData.getFloor());
            electricalFireDetectorAssociationData.setDeviceTypeCode(electricitySafetyAreaData.getDeviceTypeCode());
            electricalFireDetectorAssociationData.setDeviceLocationName(electricitySafetyAreaData.getDeviceLocationName());
            electricalFireDetectorAssociationData.setDeviceLocationCode(electricitySafetyAreaData.getDeviceLocationCode());
            electricalFireDetectorAssociationData.setDeviceCode(electricitySafetyAreaData.getDeviceCode());
        }
        electricalFireAssociationByCode = allElectricalFireAssociationData.stream().collect(Collectors.groupingBy(ElectricalFireDetectorAssociationData::getDeviceCode));
    }

}
