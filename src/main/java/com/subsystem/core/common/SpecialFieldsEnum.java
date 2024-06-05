package com.subsystem.core.common;

/**
 * 特殊字段枚举
 * 在线离线
 * 故障
 * 告警
 */
public enum SpecialFieldsEnum {
    /**
     * 在线离线 1-在线 0离线
     */
    ONLINE,
    /**
     * 告警 1-告警 0-无告警
     */
    ALARM,
    /**
     * 故障 1-故障 0-无故障
     */
    FAULT;
}
