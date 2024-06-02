package com.subsystem.repository.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Created by TangXiangLin on 2023-07-31 11:29
 * 告警事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ALARM_EVENT_VO")
public class AlarmInfo {

    @Id
    @Column(name = "ALARM_ID")
    private String alarmid;

    /**
     * 告警分类, 例如: 物品位移告警、重点人员监控高警、热成像告警、设备故障告警
     */
    //@NotNull(message = "告警分类不能为空")
    @Column(name = "ALARM_EVENT_VO")
    private String alarmCategory;

    /**
     * 告警级别 0:Ⅳ级（一般）(蓝色)、1:Ⅲ级（较重）(黄色)、2:Ⅱ级（严重）(橙色)、3:Ⅰ级（特别严重）(红色)
     */
    //@NotNull(message = "告警级别不能为空")
    //@Range(min = 0, max = 4, message = "告警级别参数不合法. 0:一般、1:较重、2:严重、3:特别严重")
    @Column(name = "LEVEL")
    private Integer level;

    /**
     * 告警时间 格式: yyyy-MM-dd HH:mm:ss
     */
    //@NotNull(message = "告警时间参数不能为空")
    @Column(name = "ALARM_TIME")
    private String alarmTime;

    /**
     * 转换的告警时间
     */
    @Column(name = "ALARM_LOCAL_TIME")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmLocalTime;

    /**
     * 告警设备ID
     */
    @Column(name = "ALARM_DEVICE_ID")
    private String alarmDeviceId;

    @Column(name = "ALARM_SUBSYSTEM_NAME")
    /** 告警设备所属子系统名称 */
    private String alarmSubsystemName;

    /**
     * 告警设备类型
     */
    @Column(name = "ALARM_DEVICE_TYPE")
    private String alarmDeviceType;

    /**
     * 告警位置
     */
    @Column(name = "ALARM_LOCATION")
    private String alarmLocation;

    /**
     * 告警内容描述
     */
    @Column(name = "ALARM_CONTENT")
    private String alarmContent;

    /**
     * 告警处置状态 0:待处理 1:已处理
     */
   // @NotNull(message = "告警处置状态参数不能为空")
    @Column(name = "ALARM_DISPOSAL_STATUS")
    private Integer alarmDisposalStatus;

    /**
     * 告警处理人
     */
    @Column(name = "ALARM_DISPOSAL_PERSON")
    private String alarmDisposalPerson;

    /**
     * 告警处理时间 格式: yyyy-MM-dd HH:mm:ss
     */
    @Column(name = "ALARM_DISPOSAL_TIME")
    private LocalDateTime alarmDisposalTime;

    /**
     * 转换的告警处理时间
     */
    @Column(name = "ALARM_LOCAT_DISPOSAL_TIME")
    private String alarmLocatDisposalTime;

    /**
     * 告警处置结果
     */
    @Column(name = "ALARM_DISPOSAL_RESULT")
    private String alarmDisposalResult;

    /**
     * 事件扩展信息
     */
    @Column(name = "DATA")
    private String data;

    /**
     * 报警推送状态 0-未推送 1-推送中 2-已推送
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 是否告警
     */
    private Boolean isAlarm;
}
