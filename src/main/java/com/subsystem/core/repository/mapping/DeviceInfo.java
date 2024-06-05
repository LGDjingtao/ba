package com.subsystem.core.repository.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * 设备信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "DEVICE_INFO")
public class DeviceInfo {
    @Id
    @Column(name = "ID")
    Integer id;
    //设备类型名称
    @Column(name = "DEVICE_TYPE_NAME")
    String deviceTypeName;
    //设备类型code
    @Column(name = "DEVICE_TYPE_CODE")
    String deviceTypeCode;
    //设备code
    @Column(name = "DEVICE_CODE")
    String deviceCode;
    //设备三方标识
    @Column(name = "DEVICE_TRIPARTITE_CODE")
    String deviceTripartiteCode;
    //设备位置名称
    @Column(name = "DEVICE_LOCATION_NAME")
    String deviceLocationName;
    //设备位置code
    @Column(name = "DEVICE_LOCATION_CODE")
    String deviceLocationCode;
}