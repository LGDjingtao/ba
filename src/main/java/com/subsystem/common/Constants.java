package com.subsystem.common;

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
    //15天
    public static final int EXPIRES_15_DAYS = 60 * 60 * 24 * 15;

    //同步缓存
    public static final String SYN_REDIS = "SYN_REDIS";
    //本地缓存
    public static final String LOCAL = "LOCAL";
    //同步失败缓存
    public static final String SYN_REDIS_FAILED = "SYN_FAILED";
    //联动
    public static final String LINKAGE = "LINKAGE";
    //推送给硬件的命令
    public static final String COMMAND = "command";

    /** ############################ cache ################################## */


    /**
     * ############################ redis ##################################
     */

    //物模型key的前缀
    public static final String PREFIX_FOR_OBJECT_MODEL_KEY = "device_report_";

    /**
     * ############################ redis ##################################
     */
    //特殊数据
    public static final String EMPTY_JSON_OBJ = new JSONObject().toJSONString();
    //特殊数据
    public static final String SPECIAL_FIELDS_FALSE = "0";
    //特殊数据
    public static final String SPECIAL_FIELDS_TRUE = "1";

    //设备code key
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
     * ############################ mqtt ##################################
     */
    public static final String MQTT_RECEIVEDTOPIC = "mqtt_receivedTopic";

    /** ############################ mqtt ################################## */

}
