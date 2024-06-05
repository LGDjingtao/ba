package com.subsystem.api.repository;


import com.subsystem.api.vo.ElectricitySafetyAreaExtend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 用电安全区域数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ELECTRICITY_SAFETY_AREA")
public class ElectricitySafetyAreaData extends ElectricitySafetyAreaExtend {
    @Id
    @Column(name = "ID")
    Integer id;

    /**
     * 楼栋Code
     */
    @Column(name = "BUILDING_CODE")
    String buildingCode;

    /**
     * 楼栋名称
     */
    @Column(name = "BUILDING_NAME")
    String buildingName;

    /**
     * 楼层
     */
    @Column(name = "FLOOR")
    String floor;

    /**
     * 三方标识
     */
    @Column(name = "DEVICE_TRIPARTITE_CODE")
    String deviceTripartiteCode;


}
