package com.subsystem.core.module.alarm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.core.common.Constants;
import com.subsystem.core.entity.ResultBean;
import com.subsystem.core.entity.ThresholdVo;
import com.subsystem.core.event.AlarmEvent;
import com.subsystem.core.feign.AlarmCenterFeign;
import com.subsystem.core.feign.AssetsFeign;
import com.subsystem.core.module.SubSystemDefaultContext;
import com.subsystem.core.module.cache.CaffeineCacheModule;
import com.subsystem.core.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.core.repository.RepositoryModule;
import com.subsystem.core.repository.mapping.AlarmInfo;
import com.subsystem.core.repository.mapping.DeviceAlarmType;
import com.subsystem.core.repository.mapping.DeviceFaultType;
import com.subsystem.core.repository.mapping.DeviceInfo;
import com.subsystem.core.strategy.AlarmStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class AlarmModule {
    /**
     * 静态数据模块
     */
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    /**
     * 资产服务接口
     */
    AssetsFeign assetsFeign;
    /**
     * 告警服务接口
     */
    AlarmCenterFeign alarmCenterFeign;
    /**
     * 数据库模块
     */
    RepositoryModule repositoryModule;
    /**
     * 缓存模块
     */
    CaffeineCacheModule caffeineCacheModule;

    /**
     * 告警<设备code，告警别名，上次告警时间>
     */
    private final static ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> alarmMark = new ConcurrentHashMap();

    /**
     * 监听告警事件
     *
     * @param alarmEvent 告警事件
     */
    @EventListener(classes = AlarmEvent.class)
    public void alarmEventListener(AlarmEvent alarmEvent) {
        SubSystemDefaultContext subSystemDefaultContext = alarmEvent.getSubSystemDefaultContext();
        initAlarmRecord(subSystemDefaultContext);
        Boolean alarmOrAlarmCancel = subSystemDefaultContext.getAlarmOrAlarmCancel();
        //消警情况广西这个项目不做处理
        if (!alarmOrAlarmCancel) return;
        //判断是不是重复告警 true-重复告警 false-非重复告警
        if (isRepeatedAlarm(subSystemDefaultContext)) return;

        AlarmInfo alarmInfo = subSystemDefaultContext.getAlarmInfo();
        //若是告警不考虑顺序问题 这个可以异步推送
        ResultBean receive = null;
        try {
            receive = alarmCenterFeign.receive(alarmInfo);
            int code = receive.getCode();
            if (!NumberUtil.equals(code, 200)) {
                log.error("推送告警信息接口报错，code：{}", code);
                throw new Exception("推送告警信息接口报错");
            }
        } catch (Exception e) {
            //保存推送失败的告警信息 然后定时推送
            alarmInfo.setAlarmid(IdUtil.randomUUID());
            repositoryModule.saveAlarmFiledInfo(alarmInfo);
            log.error("rpc调用报错", e);
            return;
        }
        log.info("推送告警信息成功{}", JSONObject.toJSONString(alarmInfo));
    }

    /**
     * 判断是不是重复告警 true-重复告警 false-非重复告警
     *
     * @return
     */
    private boolean isRepeatedAlarm(SubSystemDefaultContext subSystemDefaultContext) {
        String deviceCode = subSystemDefaultContext.getDeviceInfo().getDeviceCode();
        String alias = subSystemDefaultContext.getAlias();
        ConcurrentHashMap<String, Long> alarmMarkMap = alarmMark.get(deviceCode);
        Long lastAlarmTimeStamp = alarmMarkMap.get(alias);
        if (!lastAlarmTimeStamp.equals(Long.MIN_VALUE)) return true;
        long realTimeStamp = new org.joda.time.DateTime().getMillis();
        //连续告警小于1分钟 不告警这个按需求加
        //long timeDifference = realTimeStamp - lastAlarmTimeStamp;
        //if (Constants.ONE_MINS < timeDifference) return true;
        //非重复告警 记录告警时间戳
        alarmMarkMap.put(alias, realTimeStamp);
        return false;
    }

    /**
     * 初始化记录告警数据
     *
     * @param subSystemDefaultContext 上下文
     */
    private static void initAlarmRecord(SubSystemDefaultContext subSystemDefaultContext) {
        Boolean alarmOrAlarmCancel = subSystemDefaultContext.getAlarmOrAlarmCancel();
        String deviceCode = subSystemDefaultContext.getDeviceInfo().getDeviceCode();
        String alias = subSystemDefaultContext.getAlias();
        //如果缓存没有该设备告警数据就初始化一个该设备缓存
        alarmMark.computeIfAbsent(deviceCode, DEVICECODE -> {
            ConcurrentHashMap<String, Long> node = new ConcurrentHashMap<>();
            node.put(alias, Long.MIN_VALUE);
            return node;
        });
        //如果有该设备缓存 消警情况-重置该设备这个物模型的告警时间
        alarmMark.computeIfPresent(deviceCode, (DEVICECODE, NODE) -> {
            if (!alarmOrAlarmCancel) NODE.put(alias, Long.MIN_VALUE);
            return NODE;
        });
    }


    /**
     * 告警和故障处理
     *
     * @param subSystemDefaultContext 上下文
     * @return 告警信息
     */
    public void alarmAndFaultHandle(SubSystemDefaultContext subSystemDefaultContext) {
        String realTimeData = subSystemDefaultContext.getRealTimeData();
        //没有最新数据 不参与告警和故障处理
        if (null == realTimeData) return;
        //故障处理
        faultHandle(subSystemDefaultContext);
        //判断是否是告警数据
        boolean isAlarmData = isAlarmData(subSystemDefaultContext);
        if (!isAlarmData) return;
        //告警/消警处理
        alarmHandle(subSystemDefaultContext);
        //构建告警信息
        AlarmInfo alarmInfo = buildAlarmInfo(subSystemDefaultContext);
        //告警信息放入上下文
        subSystemDefaultContext.setAlarmInfo(alarmInfo);
    }

    /**
     * 构建告警信息
     */
    private AlarmInfo buildAlarmInfo(SubSystemDefaultContext subSystemDefaultContext) {
        DeviceInfo deviceInfo = subSystemDefaultContext.getDeviceInfo();
        DeviceAlarmType deviceAlarmType = subSystemDefaultContext.getDeviceAlarmType();
        String deviceCode = deviceInfo.getDeviceCode();

        AlarmInfo alarmInfo = new AlarmInfo();
        // 设置告警时间
        setAlarmTime(alarmInfo, subSystemDefaultContext);
        // 设置设备code 和 待处理状态
        setDeviceCodeAndDisposalStatus(alarmInfo, deviceCode);
        // 设置告警位置  告警设备类型  告警分类 告警内容描述 告警级别 告警设备所属子系统名称
        setOtherAlarmInfo(alarmInfo, deviceInfo, deviceAlarmType, subSystemDefaultContext);
        return alarmInfo;
    }

    /**
     * 告警处理
     *
     * @param subSystemDefaultContext 实时数据
     */
    private void alarmHandle(SubSystemDefaultContext subSystemDefaultContext) {
        //告警还是消警
        boolean alarmOrAlarmCancel = alarmOrAlarmCancel(subSystemDefaultContext);
        //告警还是消警存入上下文
        subSystemDefaultContext.setAlarmOrAlarmCancel(alarmOrAlarmCancel);

        DeviceInfo deviceInfo = subSystemDefaultContext.getDeviceInfo();
        String deviceCode = deviceInfo.getDeviceCode();
        String realTimeDataStr = subSystemDefaultContext.getRealTimeData();

        //获取告警描述信息
        DeviceAlarmType deviceAlarmType = subSystemDefaultContext.getDeviceAlarmType();
        //设备告警信息描述
        String deviceAlarmMessage = deviceAlarmType.getAlarmMessage(deviceCode);


        //解析出最新物模型数据
        JSONObject physicalModel = JSONObject.parseObject(realTimeDataStr);
        //每个设备物模型都有ALARM_MSG字段 ，保存当前设备处于告警时的告警描述信息 ，多个描述信息使用 “,” 分割
        String deviceAlarmMessageCaChe = physicalModel.getString(Constants.ALARM_MSG);
        if (StringUtils.isEmpty(deviceAlarmMessageCaChe)) deviceAlarmMessageCaChe = "";
        String[] split = deviceAlarmMessageCaChe.split(",");
        List<String> splitList = Arrays.stream(split).map(String::trim).collect(Collectors.toList());

        //告警情况
        if (alarmOrAlarmCancel) {
            if (!splitList.contains(deviceAlarmMessage)) {
                splitList.add(deviceAlarmMessage);
                splitList.remove("");
                String strip = StringUtils.strip(splitList.toString(), "[]");
                //加入告警障信息物模型
                physicalModel.put(deviceAlarmType.getAlarmMessageAlias(), strip);
                physicalModel.put(deviceAlarmType.getAlarmNewAlias(), "1");
            }
        }

        //消警情况
        if (!alarmOrAlarmCancel) {
            if (splitList.contains(deviceAlarmMessage)) {
                splitList.remove(deviceAlarmMessage);
                splitList.remove("");
                String strip = StringUtils.strip(splitList.toString(), "[]");
                //加入告警信息物模型
                physicalModel.put(deviceAlarmType.getAlarmMessageAlias(), strip);
                String trim = strip.trim();
                if (null == trim || "".equals(trim))
                    physicalModel.put(deviceAlarmType.getAlarmNewAlias(), "0");
            }
        }
        subSystemDefaultContext.setRealTimeData(physicalModel.toJSONString());
    }


    /**
     * 设备故障处理
     */
    private void faultHandle(SubSystemDefaultContext subSystemDefaultContext) {
        //判断这个信息是否是故障信息
        DeviceFaultType deviceFaultType = getDeviceFaultType(subSystemDefaultContext);
        if (null == deviceFaultType) return;
        //获取故障描述信息
        String deviceFaultMessage = deviceFaultType.getDeviceFaultMessage();
        String value = subSystemDefaultContext.getValue();
        JSONObject physicalModel = JSONObject.parseObject(subSystemDefaultContext.getRealTimeData());
        String devicefaultMessageCaChe = physicalModel.getString(Constants.FAULT_MSG);
        if (StringUtils.isEmpty(devicefaultMessageCaChe)) devicefaultMessageCaChe = "";
        String[] split = devicefaultMessageCaChe.split(",");
        List<String> splitList = Arrays.stream(split).map(String::trim).collect(Collectors.toList());

        //仅处理故障消息
        if ("1".equals(value)) {//是故障情况
            if (!splitList.contains(deviceFaultMessage)) {
                splitList.add(deviceFaultMessage);
                splitList.remove("");
                String strip = StringUtils.strip(splitList.toString(), "[]");
                //加入故障信息物模型
                physicalModel.put(deviceFaultType.getDeviceFaultMessageAlias(), strip);
                physicalModel.put(deviceFaultType.getDeviceFaultNewAlias(), "1");
            }

        }
        if ("0".equals(value)) {//非故障情况
            if (splitList.contains(deviceFaultMessage)) {
                splitList.remove(deviceFaultMessage);
                splitList.remove("");
                String strip = StringUtils.strip(splitList.toString(), "[]");
                //加入故障信息物模型
                physicalModel.put(deviceFaultType.getDeviceFaultMessageAlias(), strip);
                String trim = strip.trim();
                if (null == trim || "".equals(trim))
                    physicalModel.put(deviceFaultType.getDeviceFaultNewAlias(), "0");
            }
        }
        subSystemDefaultContext.setRealTimeData(physicalModel.toJSONString());
    }

    /**
     * 获取故障信息
     *
     * @param subSystemDefaultContext 实时数据
     */
    private DeviceFaultType getDeviceFaultType(SubSystemDefaultContext subSystemDefaultContext) {
        //数据检查
        DeviceInfo deviceInfo = subSystemDefaultContext.getDeviceInfo();
        String deviceCode = deviceInfo.getDeviceCode();
        String alias = subSystemDefaultContext.getAlias();
        List<DeviceFaultType> deviceFaultTypes = subSystemStaticDataDefaultModule.getDeviceFaultTypeByDeviceCode(deviceCode);
        if (null == deviceFaultTypes) return null;
        return deviceFaultTypes
                .stream()
                .filter(faultType -> faultType.getDeviceFaultAlias().equals(alias))
                .findFirst()
                .orElse(null);

    }

    /**
     * 判断是报警 还是 消警
     * true - > 告警
     * false - > 消警
     */
    public boolean alarmOrAlarmCancel(SubSystemDefaultContext subSystemDefaultContext) {
        DeviceInfo deviceInfo = subSystemDefaultContext.getDeviceInfo();
        String deviceCode = deviceInfo.getDeviceCode();
        DeviceAlarmType deviceAlarmType = subSystemDefaultContext.getDeviceAlarmType();
        String value = subSystemDefaultContext.getValue();
        //报警策略
        String alarmStrategy = deviceAlarmType.getAlarmStrategy();
        //报警阈值
        String alarmStrategyValue = getAlarmStrategyValue(deviceCode, deviceAlarmType, alarmStrategy);
        //是告警 还是 消警
        return AlarmStrategy.strategicJudgment(alarmStrategy, alarmStrategyValue, value);
    }

    /**
     * 是否是告警数据
     *
     * @param subSystemDefaultContext
     * @return true-告警数据 false-非告警数据
     */
    public boolean isAlarmData(SubSystemDefaultContext subSystemDefaultContext) {
        String alias = subSystemDefaultContext.getAlias();
        DeviceInfo deviceInfo = subSystemDefaultContext.getDeviceInfo();
        String deviceCode = deviceInfo.getDeviceCode();
        DeviceAlarmType deviceAlarmType = getDeviceAlarmType(deviceCode, alias);
        if (null == deviceAlarmType) return false;
        subSystemDefaultContext.setDeviceAlarmType(deviceAlarmType);
        return true;
    }

    /**
     * 获取对应的告警类型信息
     *
     * @param deviceCode 设备code
     * @param alias      物模型某一个属性别名
     * @return 告警类型信息
     */
    public DeviceAlarmType getDeviceAlarmType(String deviceCode, String alias) {
        List<DeviceAlarmType> deviceAlarmTypes = subSystemStaticDataDefaultModule.getDeviceAlarmTypeByDeviceCode(deviceCode);
        if (null == deviceAlarmTypes) return null;
        return deviceAlarmTypes
                .stream()
                .filter(deviceAlarmType -> deviceAlarmType.getAlarmAlias().equals(alias))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取阈值
     *
     * @param deviceCode      设备code
     * @param deviceAlarmType 设备告警类型信息
     * @param alarmStrategy   告警策略
     * @return 阈值
     */
    private String getAlarmStrategyValue(String deviceCode, DeviceAlarmType deviceAlarmType, String alarmStrategy) {
        //预先给一个默认阈值，拿不到就使用默认阈值
        String alarmStrategyValue = deviceAlarmType.getAlarmStrategyValue();
        if (!AlarmStrategy.isNumericalType(alarmStrategy)) return alarmStrategyValue;
        //从平台拿阈值
        alarmStrategyValue = getAlarmStrategyValue(deviceCode, deviceAlarmType, alarmStrategy, alarmStrategyValue);
        return alarmStrategyValue;
    }

    private String getAlarmStrategyValue(String deviceCode, DeviceAlarmType deviceAlarmType, String alarmStrategy, String alarmStrategyValue) {
        try {
            ResultBean<List<ThresholdVo>> receive = caffeineCacheModule.getThreshold(deviceCode);
            int code = receive.getCode();
            if (!NumberUtil.equals(code, 0)) {
                log.error("获取阈值接口报错，code：{}", code);
                throw new Exception();
            }
            List<ThresholdVo> thresholdVos = receive.getData();
            //匹配对应告警
            String strategyAlias = deviceAlarmType.getStrategyAlias();
            ThresholdVo thresholdVo = thresholdVos.stream().filter(v -> strategyAlias.equals(v.getParamModelCode())).findFirst().orElse(null);
            if (thresholdVos == null || thresholdVos.isEmpty()) {
            } else if (alarmStrategy.equals("1")) {
                alarmStrategyValue = thresholdVo.getMaxValue();
            } else if (alarmStrategy.equals("2")) {
                alarmStrategyValue = thresholdVo.getMinValue();
            }
        } catch (Exception e) {
            log.error("rpc请求失败::设备{}将使用默认阈值：{}", deviceCode, alarmStrategyValue);
        }
        return alarmStrategyValue;
    }

    /**
     * 设置告警时间
     *
     * @param vo                      告警信息
     * @param subSystemDefaultContext 实时数据
     */
    private static void setAlarmTime(AlarmInfo vo, SubSystemDefaultContext subSystemDefaultContext) {
        Date timestamp = subSystemDefaultContext.getTimestamp();
        DateTime dateTime = new DateTime(timestamp);
        Instant instant = timestamp.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime().withNano(0);
        vo.setAlarmLocalTime(localDateTime);
        vo.setAlarmTime(dateTime.toString(Constants.Time_Format));
    }

    /**
     * 设置设备code 和 待处理状态
     *
     * @param deviceCode 设备code
     */
    private static void setDeviceCodeAndDisposalStatus(AlarmInfo alarmInfo, String deviceCode) {
        //设备code
        alarmInfo.setAlarmDeviceId(deviceCode);
        //待处理状态 默认0
        alarmInfo.setAlarmDisposalStatus(0);
    }

    /**
     * 设置 告警位置  告警设备类型  告警分类 告警内容描述 告警级别 告警设备所属子系统名称
     */
    private static void setOtherAlarmInfo(AlarmInfo alarmInfo, DeviceInfo deviceInfo, DeviceAlarmType deviceAlarmType, SubSystemDefaultContext subSystemDefaultContext) {
        alarmInfo.setAlarmLocation(deviceInfo.getDeviceLocationName());
        alarmInfo.setAlarmDeviceType(deviceInfo.getDeviceTypeName());
        String value = subSystemDefaultContext.getValue();
        String alarmMessage = deviceAlarmType.getAlarmMessage(deviceInfo.getDeviceCode());
        StringBuilder stringBuilder = new StringBuilder(alarmMessage);
        String alarmContent = replacementInfo(stringBuilder, Constants.THRESHOLD, value);
        alarmInfo.setAlarmCategory(deviceAlarmType.getAlarmContent());
        alarmInfo.setAlarmContent(alarmContent);
        alarmInfo.setLevel(Integer.valueOf(deviceAlarmType.getAlarmLevel()));
        alarmInfo.setAlarmSubsystemName(deviceAlarmType.getSysTypeName());
    }

    /**
     * 在某字符前添加字段 且第一次出现的地方
     *
     * @param stringBuilder ：原字符串
     * @param keyword       ：字符
     * @param before        ：在字符前需要插入的字段
     * @return
     */
    public static String replacementInfo(StringBuilder stringBuilder, String keyword, String before) {
        //字符第一次出现的位置
        int index = stringBuilder.indexOf(keyword);
        if (-1 == index) return stringBuilder.toString();
        stringBuilder.insert(index, before);
        return stringBuilder.toString();
    }

}
