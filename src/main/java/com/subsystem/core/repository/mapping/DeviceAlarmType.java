package com.subsystem.core.repository.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 设备报警类型
 * 用于判断设备是否需要报警
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "DEVICE_ALARM_TYPE")
public class DeviceAlarmType {
    @Id
    @Column(name = "ID")
    Integer id;

    //设备类型
    @Column(name = "DEVICE_TYPE")
    String deviceType;

    //报警别名
    @Column(name = "ALARM_ALIAS")
    String alarmAlias;

    //报警新别名
    @Column(name = "ALARM_NEW_ALIAS")
    String alarmNewAlias;

    //报警消息别名
    @Column(name = "ALARM_MESSAGE_ALIAS")
    String alarmMessageAlias;

    //报警消息
    @Column(name = "ALARM_MESSAGE")
    String alarmMessage;

    //报警策略
    @Column(name = "ALARM_STRATEGY")
    String alarmStrategy;

    //报警阈值
    @Column(name = "ALARM_STRATEGY_VALUE")
    String alarmStrategyValue;

    //阈值别名
    @Column(name = "STRATEGY_ALIAS")
    String strategyAlias;

    //设备名称
    @Column(name = "DEVICE_NAME")
    String deviceName;

    //报警内容
    @Column(name = "ALARM_CONTENT")
    String alarmContent;

    //报警等级
    @Column(name = "ALARM_LEVEL")
    String alarmLevel;

    //子系统类型code
    @Column(name = "SYS_TYPE_CODE")
    String sysTypeCode;

    //子系统类型名称
    @Column(name = "SYS_TYPE_NAME")
    String sysTypeName;

    /**
     * 告警描述，现在需要拼接楼栋楼层
     * 不同设备需要不同处理方式
     */
    public String getAlarmMessage(String deviceCode) {
        if (null == deviceCode) return alarmMessage;
        //检测设备类型 是普通设备还是电表 还是 电器火灾探测器

        //普通类型
        String result = alarmMessage;

        //电表类型
        //todo ssss
//        List<ElectricitySafetyAreaData> electricitySafetyAreaData = InitDeviceInfo.electricitySafetyAreaByCode.get(deviceCode);
//        if (null != electricitySafetyAreaData && electricitySafetyAreaData.size() != 0) {
//            ElectricitySafetyAreaData data = electricitySafetyAreaData.get(0);
//            String value = InitDLData.getValue(data);
//            return value + "#" + alarmMessage;
//        }
//
//        //电器火灾类型
//        List<ElectricalFireDetectorAssociationData> electricalFireDetectorAssociationData = InitDeviceInfo.electricalFireAssociationByCode.get(deviceCode);
//        if (null != electricalFireDetectorAssociationData && electricalFireDetectorAssociationData.size() != 0) {
//            ElectricalFireDetectorAssociationData data = electricalFireDetectorAssociationData.get(0);
//            String value = BIMServiceImpl.createFireProbeName(data);
//            return value + "#" + alarmMessage;
//        }
        return result;
    }
}
