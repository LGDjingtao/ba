package com.subsystem.api.repository;


import com.subsystem.api.vo.ElectricalFireDetectorAssociationExtend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 电器火灾探测器关联 电柜 探针 电表 信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ELECTRICAL_FIRE_DETECTOR_ASSOCIATION")
public class ElectricalFireDetectorAssociationData extends ElectricalFireDetectorAssociationExtend {
    @Id
    @Column(name = "ID")
    Integer id;

    /**
     * 设备三方标识
     */
    @Column(name = "DEVICE_TRIPARTITE_CODE")
    String deviceTripartiteCode;

    /**
     * 电柜号
     */
    @Column(name = "ELECTRIC_CABINET_CODE")
    String ElectricCabinetCode;

    /**
     * 电器火灾探测器设备编号(现场按顺序编的，不是资产编号)
     */
    @Column(name = "DEVICE_NUMBER")
    String deviceNumber;

    /**
     * 电器火灾探测器探针编号（1-16）
     */
    @Column(name = "PROBE_NUMBER")
    String probeNumber;

    /**
     * 电器火灾探测器探针名称（对接端点）
     */
    @Column(name = "PROBE_NAME")
    String probeName;

    /**
     * 电器火灾探测器探针绑定远正电表code
     */
    @Column(name = "ELECTRICITY_METER_CODE")
    String electricityMeterCode;

}
