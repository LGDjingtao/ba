package com.subsystem.repository.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 设备故障类型
 * 用于判断设备是否需要处理故障信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "DEVICE_FAULT_TYPE")
public class DeviceFaultType {
    @Id
    @Column(name = "ID")
    Integer id;

    //设备类型code
    @Column(name = "DEVICE_TYPE")
    String deviceType;

    //故障别名
    @Column(name = "DEVICE_FAULT_ALIAS")
    String deviceFaultAlias;

    //故障新别名
    @Column(name = "DEVICE_FAULT_NEW_ALIAS")
    String deviceFaultNewAlias;

    //故障信息
    @Column(name = "DEVICE_FAULT_MESSAGE")
    String deviceFaultMessage;

    //故障信息别名
    @Column(name = "DEVICE_FAULT_MESSAGE_ALIAS")
    String deviceFaultMessageAlias;

}
