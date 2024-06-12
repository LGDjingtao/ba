package com.subsystem.core.common;

import com.alibaba.fastjson.JSONObject;

public class Constants {
    /** ############################ cache ################################## */
    /**
     * 默认过期时间（配置类中我使用的时间单位是秒，所以这里如 3*60 为3分钟）
     */
    public static final int DEFAULT_EXPIRES = 3 * 60;
    public static final int EXPIRES_5_MIN = 5 * 60;
    public static final int EXPIRES_10_MIN = 10 * 60;
    public static final int EXPIRES_15_MIN = 15 * 60;
    public static final int EXPIRES_15_DAYS = 60 * 60 * 24 * 15;
    /**
     * 默认最大缓存个数
     */
    public static final int DEFAULT_MAXIMUMSIZE = 10000;
    /**
     * 阈值缓存失效时间
     */
    public static final int THRESHOLD_CACHE_EXPIRES = 30;
    /**
     * 同步缓存
     */
    public static final String SYN_REDIS = "SYN_REDIS";
    /**
     * 同步缓存 初始化缓存个数
     */
    public static final int SYN_REDIS_INITIALCAPACITY = 2000;
    /**
     * 本地缓存
     */
    public static final String LOCAL = "LOCAL";
    /**
     * 本地缓存 初始化缓存个数
     */
    public static final int LOCAL_INITIALCAPACITY = 50;
    /**
     * 同步失败缓存
     */
    public static final String SYN_REDIS_FAILED = "SYN_FAILED";
    /**
     * 同步失败缓存 初始化缓存个数
     */
    public static final int SYN_REDIS_FAILED_INITIALCAPACITY = 20;
    /**
     * 阈值缓存
     */
    public static final String THRESHOLD_CACHE = "THRESHOLD";
    /**
     * 阈值缓存 初始化缓存个数
     */
    public static final int THRESHOLD_CACHE_INITIALCAPACITY = 20;

    /**
     * 联动
     */
    public static final String LINKAGE = "LINKAGE";
    /**
     * 推送给硬件的命令
     */
    public static final String COMMAND = "command";

    /** ############################ cache ################################## */


    /** ############################ redis ################################## */
    /**
     * 物模型缓存在redis key的前缀
     */
    public static final String PREFIX_FOR_OBJECT_MODEL_KEY = "device_report_";

    /** ############################ redis ################################## */


    /** ############################ mqtt ################################## */
    /**
     * 请求topic 框架固定别名
     */
    public static final String MQTT_RECEIVEDTOPIC = "mqtt_receivedTopic";

    /** ############################ mqtt ################################## */

    /**
     * 10分钟才告警一次
     */
    public static final int ALARM_10_MIN = 10 * 60 * 1000;

    /**
     * 特殊数据
     */
    public static final String EMPTY_JSON_OBJ = new JSONObject().toJSONString();
    /**
     * 特殊数据
     */
    public static final String SPECIAL_FIELDS_0 = "0";
    /**
     * 特殊数据
     */
    public static final String SPECIAL_FIELDS_1 = "1";
    /**
     * 特殊数据
     */
    public static final String SPECIAL_FIELDS_FALSE = "false";
    /**
     * 特殊数据
     */
    public static final String SPECIAL_FIELDS_TRUE = "true";
    /**
     * 特殊数据
     */
    public static String THRESHOLD = "阈值";
    /**
     * 设备code
     */
    public static String DEVICE_CODE = "deviceCode";
    /**
     * 故障物模型信息
     */
    public static String FAULT_MSG = "FAULT_MSG";
    /**
     * 告警物模型信息
     */
    public static String ALARM_MSG = "ALARM_MSG";
    /**
     * 时间格式
     */
    public static String Time_Format = "yyyy-MM-dd HH:mm:ss";
    /**
     * 1分钟 毫秒为单位
     */
    public static final long ONE_MINS = 1000 * 60;

    /**
     * 总三相电
     */
    public static String ALL_EPA = "EPA";

    /**
     * 三相电1
     */
    public static String EPA1 = "EPA1";

    /**
     * 三相电2
     */
    public static String EPA2 = "EPA2";

    /**
     * 三相电3
     */
    public static String EPA3 = "EPA3";

}
