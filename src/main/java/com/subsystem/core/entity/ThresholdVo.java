package com.subsystem.core.entity;

import lombok.Data;

/**
 * 阈值vo
 */
@Data
public class ThresholdVo {
    /**
     * 标识符
     */
    String paramModelCode;
    /**
     * 最大阈值
     */
    String maxValue;
    /**
     * 最小阈值
     */
    String minValue;
}
