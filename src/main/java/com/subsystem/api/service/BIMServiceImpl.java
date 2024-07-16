package com.subsystem.api.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.subsystem.api.Common;
import com.subsystem.api.InitDeviceInfo;
import com.subsystem.api.repository.ElectricalFireDetectorAssociationData;
import com.subsystem.api.repository.ElectricitySafetyAreaData;
import com.subsystem.api.vo.BuildingMonitoringAlarmVo;
import com.subsystem.api.vo.CountRunVo;
import com.subsystem.api.vo.ElectricalFireProbeVo;
import com.subsystem.api.vo.ElectricityPerceptionEquipmentVo;
import com.subsystem.core.entity.ResultBean;
import com.subsystem.core.entity.ThresholdVo;
import com.subsystem.core.feign.AssetsFeign;
import com.subsystem.core.module.redis.StringRedisModule;
import com.subsystem.core.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.core.repository.mapping.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BIM 业务逻辑处理
 */
@Slf4j
@Service
@AllArgsConstructor
public class BIMServiceImpl {
    StringRedisModule redisUtil;
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    AssetsFeign assetsFeign;

    /**
     * 获取设备运行统计
     *
     * @param deviceType 设备类型
     */
    public CountRunVo getCountRun(String deviceType) {
        List<DeviceInfo> deviceInfos = getDeviceInfos(deviceType);
        return getCountRun(deviceInfos);
    }

    public List<DeviceInfo> getDeviceInfos(String deviceType) {
        return subSystemStaticDataDefaultModule.getDeviceInfoByTypeCode(deviceType);
    }

    /**
     * 获取设备运行统计
     *
     * @param deviceInfos 设备信息
     */
    public CountRunVo getCountRun(List<DeviceInfo> deviceInfos) {
        int onlineCount = 0;
        int offlineCount = 0;
        int faultCount = 0;
        CountRunVo countRunVo = new CountRunVo();
        for (DeviceInfo deviceInfo : deviceInfos) {
            String deviceCode = deviceInfo.getDeviceCode();
            Object o = redisUtil.get(Common.DEVICE_REPORT + deviceCode);
            if (!ObjectUtil.isEmpty(o)) {
                JSONObject jsonObject = JSONUtil.parseObj(o);
                Integer online = jsonObject.getInt(Common.ONLINE);
                if (null == online || 0 == online) {
                    offlineCount += 1;
                } else {
                    onlineCount += online;
                }

            } else {
                offlineCount += 1;
            }
        }
        countRunVo.setOnlineCount(onlineCount);
        countRunVo.setOfflineCount(offlineCount);
        countRunVo.setAllAssetCount(deviceInfos.size());
        countRunVo.setFaultCount(faultCount);
        return countRunVo;
    }

    /**
     * 给排水系统势态 - 集水井- 获取当前液位
     */
    public List<Map<String, Object>> getCurrentLiquidLevel() {
        List<DeviceInfo> deviceInfosDJSJ = getDeviceInfos(Common.DJSJ);
        List<DeviceInfo> deviceInfosSJSJ = getDeviceInfos(Common.SJSJ);
        List<Map<String, Object>> result = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfosDJSJ) getJSJDAta(result, deviceInfo);
        for (DeviceInfo deviceInfo : deviceInfosSJSJ) getJSJDAta(result, deviceInfo);
        return result;
    }

    private void getJSJDAta(List<Map<String, Object>> result, DeviceInfo deviceInfo) {
        Map<String, Object> map = new HashMap<>();
        String deviceCode = deviceInfo.getDeviceCode();
        map.put("name", deviceCode);
        map.put("data", "标准液位");
        Object o = redisUtil.get(Common.DEVICE_REPORT + deviceCode);
        JSONObject jsonObject = new JSONObject();
        if (!ObjectUtil.isEmpty(o)) jsonObject = JSONUtil.parseObj(o);

        //在线状态
        Integer ONLINE = jsonObject.getInt("ONLINE");
        if (null == ONLINE || NumberUtil.equals(ONLINE, 0)) return;

        //低液位报警
        Integer LL = jsonObject.getInt("LL");
        if (null != LL && NumberUtil.equals(LL, 1)) map.put("data", "液位过低");

        //高液位报警
        Integer HL = jsonObject.getInt("HL");
        if (null != LL && NumberUtil.equals(HL, 1)) map.put("data", "液位过高");

        result.add(map);
    }


    /**
     * 电器火灾探测器  探针弹窗
     *
     * @param deviceCode 设备code
     */
    public List<ElectricalFireProbeVo> getElectricalFireDetectors(String deviceCode) {
        Object o = redisUtil.get(Common.DEVICE_REPORT + deviceCode);
        JSONObject jsonObject = new JSONObject();
        if (!ObjectUtil.isEmpty(o)) {
            jsonObject = JSONUtil.parseObj(o);
        }
        List<ElectricalFireProbeVo> vos = new ArrayList<>();
        //获取 单个 电器火灾探测器关联 表信息
        List<ElectricalFireDetectorAssociationData> electricalFireDetectorAssociationDatas = InitDeviceInfo.electricalFireAssociationByCode.get(deviceCode);
        ElectricalFireDetectorAssociationData electricalFireDetectorAssociationData = electricalFireDetectorAssociationDatas.get(0);
        List<ThresholdVo> thresholds = null;
        try {
            ResultBean<List<ThresholdVo>> receive = assetsFeign.receive(deviceCode);
            thresholds = receive.getData();
        }catch (Exception e){
            log.error("电器火灾获取阈值失败");
        }


        for (int i = 1; i <= 16; i++) {
            ElectricalFireProbeVo vo = createFireProbe(i, jsonObject, electricalFireDetectorAssociationData, thresholds);
            if (ObjectUtil.isEmpty(vo)) continue;
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 用电感知设备- 实时监控
     */
    public List<ElectricityPerceptionEquipmentVo> getElectricityPerceptionEquipment() {
        List<ElectricityPerceptionEquipmentVo> vos = new ArrayList<>();
        for (Map.Entry<String, List<ElectricitySafetyAreaData>> entry : InitDeviceInfo.electricitySafetyAreaByBuildingCode.entrySet()) {
            ElectricityPerceptionEquipmentVo vo = new ElectricityPerceptionEquipmentVo();
            vo.setBuildingCode(entry.getKey());
            vos.add(vo);

            List<ElectricityPerceptionEquipmentVo.ElectricityPerceptionEquipment> electricityPerceptionEquipmentList = vo.getElectricityPerceptionEquipmentList();
            List<ElectricitySafetyAreaData> value = entry.getValue();
            for (ElectricitySafetyAreaData electricitySafetyAreaData : value) {
                ElectricityPerceptionEquipmentVo.ElectricityPerceptionEquipment electricityPerceptionEquipment = new ElectricityPerceptionEquipmentVo.ElectricityPerceptionEquipment();
                electricityPerceptionEquipmentList.add(electricityPerceptionEquipment);
                String deviceCode = electricitySafetyAreaData.getDeviceCode();
                electricityPerceptionEquipment.setDeviceCode(deviceCode);
                String name = createElectricityPerceptionEquipmentName(electricitySafetyAreaData);
                electricityPerceptionEquipment.setDeviceName(name);
                electricityPerceptionEquipment.setIsAbnormal(false);
                Object o = redisUtil.get(Common.DEVICE_REPORT + deviceCode);
                if (!ObjectUtil.isEmpty(o)) {
                    JSONObject jsonObject = JSONUtil.parseObj(o);
                    Integer alarm = jsonObject.getInt(Common.ALARM);
                    if (null != alarm && 1 == alarm) {
                        electricityPerceptionEquipment.setIsAbnormal(true);
                    }
                }
            }
        }
        return vos;
    }


    /**
     * 楼栋 用电监测 楼栋 楼层 报警
     */
    public List<BuildingMonitoringAlarmVo> getBuildingMonitoring() {
        List<BuildingMonitoringAlarmVo> vos = new ArrayList<>();
        for (Map.Entry<String, List<ElectricitySafetyAreaData>> entry : InitDeviceInfo.electricitySafetyAreaByBuildingCode.entrySet()) {
            BuildingMonitoringAlarmVo vo = new BuildingMonitoringAlarmVo();
            vo.setBuildingCode(entry.getKey());
            vos.add(vo);
            List<ElectricitySafetyAreaData> value = entry.getValue();
            for (ElectricitySafetyAreaData electricitySafetyAreaData : value) {
                String deviceCode = electricitySafetyAreaData.getDeviceCode();
                String buildingCode = electricitySafetyAreaData.getBuildingCode();
                vo.setBuildingCode(buildingCode);
                String floor = electricitySafetyAreaData.getFloor();
                List<String> floorList = vo.getFloor();
                Object o = redisUtil.get(Common.DEVICE_REPORT + deviceCode);
                if (!ObjectUtil.isEmpty(o)) {
                    JSONObject jsonObject = JSONUtil.parseObj(o);
                    Integer alarm = jsonObject.getInt(Common.ALARM);
                    if (null != alarm && 1 == alarm) {
                        floorList.add(floor);
                    }
                }
            }
        }
        return vos;
    }

    private String createElectricityPerceptionEquipmentName(ElectricitySafetyAreaData electricitySafetyAreaData) {
        String buildingName = electricitySafetyAreaData.getBuildingName();
        String floor = electricitySafetyAreaData.getFloor();
        String deviceTypeName = electricitySafetyAreaData.getDeviceTypeName();
        StringBuilder builder = new StringBuilder();

        return builder
                .append(buildingName)
                .append(floor)
                .append("F#")
                .append(deviceTypeName)
                .toString();
    }

    /**
     * 构建温度探针对象
     */
    private ElectricalFireProbeVo createFireProbe(Integer number, JSONObject jsonObject, ElectricalFireDetectorAssociationData electricalFireDetectorAssociationData, List<ThresholdVo> thresholds) {
        //名称 楼栋楼层# + 电柜号# + 设备号# +探针号接入端点#+电表号
        String name = createFireProbeName(electricalFireDetectorAssociationData);

        //温度状态Key
        String tcStatusKey = Common.FIRE_PROBE_TEMPERATURE_STATUS + number;
        //剩余电流状态Key
        String irStatusKey = Common.FIRE_PROBE_CURRENT_STATUS + number;
        //温度值Key
        String tcKey = Common.FIRE_PROBE_TEMPERATURE_VALUE + number;
        //温度阈值key
        String tcthKey = tcKey + "_YZ";
        //剩余电流值Key
        String irKey = Common.FIRE_PROBE_CURRENT_VALUE + number;


        String tcStatus = jsonObject.getStr(tcStatusKey);
        tcStatus = tcStatus == null ? "0" : tcStatus;
        String irStatus = jsonObject.getStr(irStatusKey);
        irStatus = irStatus == null ? "0" : irStatus;
        String tc = jsonObject.getStr(tcKey);
        String ir = jsonObject.getStr(irKey);

        if (null == thresholds ){
            tcStatus = 40d < Double.valueOf(tc) ? "1" : "0";
        }else {
            Map<String, ThresholdVo> map = thresholds.stream().collect(Collectors.toMap(ThresholdVo::getParamModelCode, v -> v));
            ThresholdVo thresholdVo = map.get(tcthKey);
            if (null != thresholdVo) {
                tcStatus = Double.valueOf(thresholdVo.getMaxValue()) < Double.valueOf(tc) ? "1" : "0";
            }
        }



        if (Common.FIRE_PROBE_DEFAULT_VALUE.equals(tc) || Common.FIRE_PROBE_DEFAULT_VALUE.equals(ir)) return null;
        if (tc == null || ir == null) return null;


        ElectricalFireProbeVo vo = new ElectricalFireProbeVo();
        vo.setIrStatus(irStatus);
        vo.setTcStatus(tcStatus);
        vo.setIr(ir + Common.CURRENT_UNIT);
        vo.setTc(tc + Common.TEMPERATURE_UNIT);
        vo.setName(name);
        return vo;
    }

    public static String createFireProbeName(ElectricalFireDetectorAssociationData electricalFireDetectorAssociationData) {
        String buildingName = electricalFireDetectorAssociationData.getBuildingName();
        String floor = electricalFireDetectorAssociationData.getFloor();
        String electricCabinetCode = electricalFireDetectorAssociationData.getElectricCabinetCode();
        String probeName = electricalFireDetectorAssociationData.getProbeName();
        String electricityMeterCode = electricalFireDetectorAssociationData.getElectricityMeterCode();
        StringBuilder builder = new StringBuilder();

        return builder
                .append(buildingName)
                .append(floor)
                .append("F#")
                .append(electricCabinetCode)
                .append(Common.CABINET_NUMBER)
                .append(probeName)
                .append("#")
                .append(electricityMeterCode)
                .append(Common.ELECTRICITYMETER)
                .append(Common.FIRE_PROBE_NAME).toString();
    }

}
