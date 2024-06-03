package com.subsystem.module.alarm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.subsystem.common.Constants;
import com.subsystem.entity.SimpleReturnBo;
import com.subsystem.feign.AlarmCenterFeign;
import com.subsystem.module.SubSystemDefaultContext;
import com.subsystem.entity.ResultBean;
import com.subsystem.entity.ThresholdVo;
import com.subsystem.event.AlarmEvent;
import com.subsystem.feign.AssetsFeign;
import com.subsystem.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.repository.mapping.AlarmInfo;
import com.subsystem.repository.mapping.DeviceAlarmType;
import com.subsystem.repository.mapping.DeviceFaultType;
import com.subsystem.repository.mapping.DeviceInfo;
import com.subsystem.strategy.AlarmStrategy;
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
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class AlarmModule {

    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    AssetsFeign assetsFeign;
    AlarmCenterFeign alarmCenterFeign;
    /**
     * 监听告警事件
     *
     * @param alarmEvent 告警事件
     */
    @EventListener(classes = AlarmEvent.class)
    public void alarmEventListener(AlarmEvent alarmEvent) {
        ResultBean receive = alarmCenterFeign.receive(alarmEvent.getAlarmInfo());
        int code = receive.getCode();
        if (!NumberUtil.equals(code, 200)) {
            log.error("推送告警信息接口报错，code：{}", code);
        }
    }


    /**
     * @param subSystemDefaultContext 实时数据
     * @return 告警信息
     */
    public void alarmAndFaultHandle(SubSystemDefaultContext subSystemDefaultContext) {
        String realTimeData = subSystemDefaultContext.getRealTimeData();
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


        DeviceInfo deviceInfo = subSystemDefaultContext.getDeviceInfo();
        String deviceCode = deviceInfo.getDeviceCode();
        String realTimeDataStr = subSystemDefaultContext.getRealTimeData();

        JSONObject physicalModel = JSONObject.parseObject(realTimeDataStr);
        //获取告警描述信息
        DeviceAlarmType deviceAlarmType = subSystemDefaultContext.getDeviceAlarmType();
        String deviceAlarmMessage = deviceAlarmType.getAlarmMessage(deviceCode);

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
        Object value = subSystemDefaultContext.getValue();
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
        Object obj = subSystemDefaultContext.getValue();
        String value = JSONObject.toJSONString(obj) ;
        //报警策略
        String alarmStrategy = deviceAlarmType.getAlarmStrategy();
        //报警阈值
        String alarmStrategyValue = getAlarmStrategyValue(deviceCode, deviceAlarmType, alarmStrategy);
        //是告警 还是 消警
        return AlarmStrategy.strategicJudgment(alarmStrategy, alarmStrategyValue, value);
    }

    private boolean isAlarmData(SubSystemDefaultContext subSystemDefaultContext) {
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
     * @param alias
     * @return
     */
    private DeviceAlarmType getDeviceAlarmType(String deviceCode, String alias) {
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
     * @return
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
//        try {
//            ResultBean<List<ThresholdVo>> receive = assetsFeign.receive(deviceCode);
//            int code = receive.getCode();
//            if (!NumberUtil.equals(code, 0)) {
//                log.error("获取阈值接口报错，code：{}", code);
//                throw new Exception();
//            }
//            List<ThresholdVo> thresholdVos = receive.getData();
//            //匹配对应告警
//            String strategyAlias = deviceAlarmType.getStrategyAlias();
//            ThresholdVo thresholdVo = thresholdVos.stream().filter(v -> strategyAlias.equals(v.getParamModelCode())).findFirst().orElse(null);
//            if (thresholdVos == null || thresholdVos.isEmpty()) {
//            } else if (alarmStrategy.equals("1")) {
//                alarmStrategyValue = thresholdVo.getMaxValue();
//            } else if (alarmStrategy.equals("2")) {
//                alarmStrategyValue = thresholdVo.getMinValue();
//            }
//        } catch (Exception e) {
//            log.error("使用默认阈值：{}", alarmStrategyValue);
//        }
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
        Object value = subSystemDefaultContext.getValue();
        //todo 阈值 还需要设计
        String threshold = "";
        try {
            if (null != value) {
                if (!Boolean.FALSE.equals(value) && !Boolean.TRUE.equals(value)) {
                    threshold = JSON.toJSONString(value);
                }
            }
        } catch (Exception e) {
            log.error("阈值解析失败");
        }
        alarmInfo.setAlarmCategory(deviceAlarmType.getAlarmContent());
        alarmInfo.setAlarmContent(deviceAlarmType.getAlarmMessage(deviceInfo.getDeviceCode()) + threshold);
        alarmInfo.setLevel(Integer.valueOf(deviceAlarmType.getAlarmLevel()));
        alarmInfo.setAlarmSubsystemName(deviceAlarmType.getSysTypeName());
    }

}
