package com.subsystem.entity;

import lombok.Data;

/**
 * 阈值vo
 */
@Data
public class ThresholdVo {
    //标识符
    String paramModelCode;
    String maxValue;
    String minValue;
}
