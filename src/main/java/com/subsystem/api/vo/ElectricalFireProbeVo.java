package com.subsystem.api.vo;

import lombok.Data;

/**
 * 电器火灾探针
 */
@Data
public class ElectricalFireProbeVo {
    /**
     * 探针名称
     */
    String name;
    /**
     * 温度TC
     */
    String tc;
    /**
     * 剩余电流IR
     */
    String ir;
    /**
     * 温度TC 状态 0-无告警 1-告警
     */
    String tcStatus;
    /**
     * 剩余电流IR 状态 0-无告警 1-告警
     */
    String irStatus;
}

