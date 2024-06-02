package com.subsystem.entity;

import lombok.Data;

import java.util.Date;

@Data
public class RealTimeData {
    //设备code
    private String deviceCode;
    //三方标识
    private String tripartiteCode;
    //物模型别名
    private String alias;
    //实时值
    private Object value;
    //时间
    private Date timestamp;
    //缓存key
    private String key;
    //实时物模型数据
    private String realTimeData;
}
