package com.subsystem.api.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用电感知设备Vo
 */
@Data
public class ElectricityPerceptionEquipmentVo {
    /**
     * 楼栋code
     */
    String buildingCode;
    /**
     * 用电感知设备详细信息
     */
    List<ElectricityPerceptionEquipment> electricityPerceptionEquipmentList = new ArrayList<>();



    @Data
    public static class ElectricityPerceptionEquipment{
        /**
         * 设备code
         */
        String deviceCode;
        /**
         * 是否异常
         */
        Boolean isAbnormal;
        /**
         * 设备名称
         */
        String deviceName;
    }
}


