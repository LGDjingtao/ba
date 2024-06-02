package com.subsystem.module.alarm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.subsystem.common.Constants;
import com.subsystem.entity.RealTimeData;
import com.subsystem.event.AlarmEvent;
import com.subsystem.entity.ThresholdVo;
import com.subsystem.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.porp.BAProperties;
import com.subsystem.repository.mapping.AlarmInfo;
import com.subsystem.repository.mapping.DeviceAlarmType;
import com.subsystem.strategy.AlarmStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class AlarmModule {
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    BAProperties baProperties;

    /**
     * 监听告警事件
     *
     * @param alarmEvent 告警事件
     */
    @EventListener(classes = AlarmEvent.class)
    private void alarmEventListener(AlarmEvent alarmEvent) {

    }


    /**
     * @param realTimeData 实时数据
     * @return 告警信息
     */
    public AlarmInfo handleAlarm(RealTimeData realTimeData) {
        if (null == realTimeData) return null;
        AlarmInfo alarmInfo = determineAlarm(realTimeData);
        if (!isAlarm) return null;
        //组装告警信息
        assembleAlarmInfo(realTimeData);
        //触发设备联动
        equipmentLinkage(metric, deviceCode);
    }

    /**
     * 判断是否是报警
     */
    private AlarmInfo determineAlarm(RealTimeData realTimeData) {
        String alias = realTimeData.getAlias();
        String value = realTimeData.getValue().toString();
        String deviceCode = realTimeData.getDeviceCode();
        DeviceAlarmType deviceAlarmType = getDeviceAlarmType(deviceCode, alias);
        if (null == deviceAlarmType) return null;
        //报警别名
        String alarmStrategy = deviceAlarmType.getAlarmStrategy();
        //报警阈值
        String alarmStrategyValue = getAlarmStrategyValue(deviceCode, deviceAlarmType, alarmStrategy);
        //报警策略判断
        boolean isAlarm = AlarmStrategy.strategicJudgment(alarmStrategy, alarmStrategyValue, value);
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setIsAlarm(isAlarm);


        // 设置告警时间
        setAlarmTime(alarmInfo, realTimeData);
        // 设置设备code 和 待处理状态
        setDeviceCodeAndDisposalStatus(alarmInfo,deviceCode);
        // 设置告警位置  告警设备类型  告警分类 告警内容描述 告警级别 告警设备所属子系统名称
        setOtherAlarmInfo(deviceCode, vo, metric);
        // 推送告警数据
        pushAlarmData(deviceCode, vo, dateTime);
    }

    /**
     * 获取对应的告警信息
     *
     * @param deviceCode 设备code
     * @param alias
     * @return
     */
    private DeviceAlarmType getDeviceAlarmType(String deviceCode, String alias) {
        List<DeviceAlarmType> deviceAlarmTypes = subSystemStaticDataDefaultModule.getDeviceTypeCodeByDeviceCode(deviceCode);
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
        String thresholdUrl = baProperties.getThresholdUrl();
        try {
            String body = HttpRequest.get(thresholdUrl)
                    .form(Constants.DEVICE_CODE, deviceCode)
                    .execute()
                    .body();
            //检测是否传输成功，若没有成功就先存数据库，然后定时任务去发送，直至成功
            JSONObject obj = JSONUtil.parseObj(body);
            Integer code = obj.getInt("code");
            if (!NumberUtil.equals(code, 0)) {
                log.error("获取阈值接口报错，code：{}", code);
            } else {
                JSONArray list = obj.getJSONArray("data");
                List<ThresholdVo> thresholdVos = list.toList(ThresholdVo.class);
                //匹配对应告警
                String strategyAlias = deviceAlarmType.getStrategyAlias();
                ThresholdVo thresholdVo = thresholdVos.stream().filter(v -> strategyAlias.equals(v.getParamModelCode())).findFirst().orElse(null);
                if (thresholdVos == null || thresholdVos.isEmpty()) {
                } else if (alarmStrategy.equals("1")) {
                    alarmStrategyValue = thresholdVo.getMaxValue();
                } else if (alarmStrategy.equals("2")) {
                    alarmStrategyValue = thresholdVo.getMinValue();
                }
            }
        } catch (Exception e) {

        }
        return alarmStrategyValue;
    }

    /**
     * 设置告警时间
     *
     * @param vo           告警信息
     * @param realTimeData 实时数据
     */
    private static void setAlarmTime(AlarmInfo vo, RealTimeData realTimeData) {
        Date timestamp = realTimeData.getTimestamp();
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
    private static void setDeviceCodeAndDisposalStatus(AlarmInfo alarmInfo,String deviceCode) {
        //设备code
        alarmInfo.setAlarmDeviceId(deviceCode);
        //待处理状态 默认0
        alarmInfo.setAlarmDisposalStatus(0);
    }

    /**
     * 设置 告警位置  告警设备类型  告警分类 告警内容描述 告警级别 告警设备所属子系统名称
     */
    private static void setOtherAlarmInfo(String deviceCode, AlarmEventVo vo, Metric metric) {
        List<DeviceInfo> deviceInfos = InitDeviceInfo.deviceInfoByCode.get(deviceCode);
        DeviceInfo deviceInfo = deviceInfos.get(0);
        String deviceTypeCode = deviceInfo.getDeviceTypeCode();
        List<DeviceAlarmType> deviceAlarmTypes = InitDeviceInfo.deviceAlarmTypeByType.get(deviceTypeCode);
        vo.setAlarmLocation(deviceInfo.getDeviceLocationName());
        vo.setAlarmDeviceType(deviceInfo.getDeviceTypeName());
        String alias = metric.getAlias();
        Object value = metric.getValue();
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
        List<DeviceAlarmType> deviceAlarmTypesFilter = deviceAlarmTypes.stream().filter(v -> v.getAlarmAlias().equals(alias)).collect(Collectors.toList());
        if (!ArrayUtil.isEmpty(deviceAlarmTypesFilter)) {
            DeviceAlarmType deviceAlarmType = deviceAlarmTypesFilter.get(0);
            if (ObjectUtil.isNotEmpty(deviceAlarmType)) {
                vo.setAlarmCategory(deviceAlarmType.getAlarmContent());
                vo.setAlarmContent(deviceAlarmType.getAlarmMessage(deviceInfo.getDeviceCode()) + threshold);
                vo.setLevel(Integer.valueOf(deviceAlarmType.getAlarmLevel()));
                vo.setAlarmSubsystemName(deviceAlarmType.getSysTypeName());
            }
        }
    }

}
