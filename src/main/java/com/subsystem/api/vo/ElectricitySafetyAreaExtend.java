package com.subsystem.api.vo;

import lombok.Data;

@Data
public class ElectricitySafetyAreaExtend {
    /**
     * 设备类型code
     */
    String deviceTypeCode;

    /**
     * 设备类型名称
     */
    String deviceTypeName;;

    /**
     * 设备位置名称
     */
    String deviceLocationName;

    /**
     * 设备位置code
     */
    String deviceLocationCode;

    /**
     * 设备code
     */
    String deviceCode;
}
