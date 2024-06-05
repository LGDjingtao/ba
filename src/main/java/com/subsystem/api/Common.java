package com.subsystem.api;

public class Common {
    private Common() {

    }

    /**
     * 设备物理模型前缀
     */
    public static String DEVICE_REPORT = "device_report_";

    /**
     * 双集水井设备代号
     */
    public static String SJSJ = "SJSJ";

    /**
     * 单集水井设备代号
     */
    public static String DJSJ = "DJSJ";

    /**
     * 压力传感器设备代号
     */
    public static String YLCGQ = "YLCGQ";

    /**
     * 灌溉压力传感器设备代号
     */
    public static String GGYLCGQ = "GGYLCGQ";

    /**
     * 灌溉水表
     */
    public static String GGSB = "GGSB";

    /**
     * 生活水表
     */
    public static String SHSB = "SHSB";

    /**
     * 智能单相电表
     */
    public static String ZNDX = "ZNDX";

    /**
     * 智能三相电表
     */
    public static String ZNSX = "ZNSX";

    /**
     * 单相多户电表
     */
    public static String DXDH = "DXDH";

    /**
     * 水浸传感器
     */
    public static String SJ = "SJ";

    /**
     * 温湿度传感器
     */
    public static String WS = "WS";

    /**
     * 一氧化碳传感器
     */
    public static String CO = "CO";

    /**
     * 气体多合一传感器
     */
    public static String DHY = "DHY";

    /**
     * 消防压力传感器
     */
    public static String XFYLCGQ = "XFYLCGQ";

    /**
     * 在线状态标识
     */
    public static String ONLINE = "ONLINE";

    /**
     * 告警状态标识
     */
    public static String ALARM = "ALARM";

    /**
     * 24小时一氧化碳浓度变化 redis key
     */
    public static String CO_24HOURDATA = "24hourdata:co";

    /**
     * 24小时主进出水管压力变化 redis key
     */
    public static String MAINPIPEPRESSURE_24HOURDATA = "24hourdata:mainpipepressure";

    /**
     * 24小时水管压力变化 redis key
     */
    public static String PIPEPRESSURE_24HOURDATA = "24hourdata:pipepressure";


    /**
     * 压力传感器value暂定value代号
     */
    public static String PIPEPRESSURE_VALUE_KEY = "value";

    /**
     * 地下排风系统
     */
    public static String SYSTYPE_DXPF = "排风系统";

    /**
     * 报警数据标记
     */
    public static String ALARM_DATA_MARK = "mark_";

    /**
     * 时间格式
     */
    public static String Time_Format = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式
     */
    public static String Time_Format_Test = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 故障物模型信息
     */
    public static String FAULT_MSG = "FAULT_MSG";

    /**
     * 告警物模型信息
     */
    public static String ALARM_MSG = "ALARM_MSG";


    /**
     * 电器火灾探测器
     */
    public static String DQHZ = "DQHZ";

    /**
     * 双数风机
     */
    public static String SPF = "SPF";

    /**
     * 单数风机
     */
    public static String DPF = "DPF";


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


    /**
     * 电力火灾探测器 温度报警状态
     */
    public static String FIRE_PROBE_TEMPERATURE_STATUS = "T_AL";

    /**
     * 电力火灾探测器 剩余电流报警状态
     */
    public static String FIRE_PROBE_CURRENT_STATUS = "I_AL";

    /**
     * 电力火灾探测器 温度值
     */
    public static String FIRE_PROBE_TEMPERATURE_VALUE = "T_";


    /**
     * 电力火灾探测器 电流值值
     */
    public static String FIRE_PROBE_CURRENT_VALUE = "A_";


    /**
     * 电力火灾探测器 温度探针名
     */
    public static String FIRE_PROBE_NAME = "温度探针";

    /**
     * 电力火灾探测器 电柜号
     */
    public static String CABINET_NUMBER = "电柜号#";


    /**
     * 电力火灾探测器 电表号
     */
    public static String ELECTRICITYMETER = "电表#";

    /**
     * 电力火灾探测器 默认值 ， 如果出现这个值 说明没有该探针
     */
    public static String FIRE_PROBE_DEFAULT_VALUE = "3276.7";

    /**
     * 温度单位
     */
    public static String TEMPERATURE_UNIT = "°C";

    /**
     * 电流单位
     */
    public static String CURRENT_UNIT = "mA";

    /**
     * 设备code key
     */
    public static String DEVICE_CODE = "deviceCode";

    /**
     * 电表设备别名
     */
    public static String PMP_ALIAS = "PMP_ALIAS";

    /**
     * 三方标识
     */
    public static String TRIPARTITE_CODE = "TRIPARTITE_CODE";

    /**
     * 近一小时水流量
     */
    public static String NEARLY_1_HOUR_DISCHARGE = "NEARLY_1_HOUR_DISCHARGE";
}
