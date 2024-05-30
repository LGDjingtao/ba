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

    //15天
    public static final int EXPIRES_15_DAYS = 60 * 60 * 24 * 15;

    //同步缓存
    public static final String SYN_REDIS = "SYN_REDIS";
    //本地缓存
    public static final String LOCAL = "LOCAL";
    //同步失败缓存
    public static final String SYN_REDIS_FAILED = "SYN_FAILED";

    /** ############################ cache ################################## */


    /**
     * ############################ redis ##################################
     */

    //物模型key的前缀
    public static final String PREFIX_FOR_OBJECT_MODEL_KEY = "device_report_";

    /**
     * ############################ redis ##################################
     */

    public static final String EMPTY_JSON_OBJ = new JSONObject().toJSONString();

    public static final String SPECIAL_FIELDS_FALSE = "0";

    public static final String SPECIAL_FIELDS_TRUE = "1";

    /**
     * ############################ mqtt ##################################
     */
    public static final String MQTT_RECEIVEDTOPIC = "mqtt_receivedTopic";

    /** ############################ mqtt ################################## */

}
