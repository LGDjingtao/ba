package com.subsystem.repository.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 设备联动关系数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "DEVICE_LINKAGE_RELATIONSHIP")
public class DeviceLinkageRelationshipData {
    @Id
    @Column(name = "ID")
    Integer id;
    /**
     * 设备 code
     */
    @Column(name = "DEVICE_CODE")
    String deviceCode;

    /**
     * 联动设备code
     */
    @Column(name = "LINKAGE_DEVICE_CODE")
    String linkageDeviceCode;

    /**
     * 阈值别名
     * 用来标识设备有哪几种阈值是需要触发联动设备的
     */
    @Column(name = "THRESHOLD_ALIAS")
    String thresholdAlias;
}
