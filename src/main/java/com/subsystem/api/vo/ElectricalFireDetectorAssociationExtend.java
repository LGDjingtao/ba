package com.subsystem.api.vo;

import lombok.Data;

/**
 * 电器火灾探测器 扩展数据
 */
@Data
public class ElectricalFireDetectorAssociationExtend {
    /**
     * 楼栋Code
     */
    String buildingCode;

    /**
     * 楼栋名称
     */
    String buildingName;

    /**
     * 楼层
     */
    String floor;

    /**
     * 设备类型code
     */
    String deviceTypeCode;

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
