package com.subsystem.api.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 楼栋 - 用电监测 - 楼栋楼层告警
 */
@Data
public class BuildingMonitoringAlarmVo {
    /**
     * 楼栋
     */
    String buildingCode;
    /**
     * 楼层
     */
    List<String> floor = new ArrayList<>();
}
