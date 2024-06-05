package com.subsystem.api.controller;


import cn.hutool.json.JSONUtil;
import com.subsystem.api.Common;
import com.subsystem.api.service.BIMServiceImpl;
import com.subsystem.api.vo.*;
import com.subsystem.core.entity.ResultBean;
import com.subsystem.core.module.mqtt.hanlder.MqttPublishGateway;
import com.subsystem.core.module.redis.StringRedisModule;
import com.subsystem.core.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.core.porp.BAProperties;
import com.subsystem.core.repository.mapping.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

@Slf4j
@RestController
@RequestMapping(value = "/basys")
@AllArgsConstructor
public class BIMController {
    MqttPublishGateway publisher;
    StringRedisModule redisUtil;
    BAProperties baProperties;
    BIMServiceImpl bimServic;
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;

    /**
     * 控制接口
     */
    @PostMapping(value = "/control")
    public ResultBean controlBaSys(BaControllerVo baControllerVo) {
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Integer> value = new HashMap<>();
        //应硬件系统厂家的要求，控制设备必须在设备名称后面加 _PTCMD
        value.put(baControllerVo.getDeviceCode() + "_PTCMD", baControllerVo.getControl());
        metrics.put("metrics", value);
        publisher.sendToMqtt(baProperties.getTopic(), JSONUtil.toJsonStr(metrics));
        return ResultBean.success("控制成功!");
    }

    /**
     * 给排水系统势态
     * 集水井当前液位显示
     */
    @GetMapping(value = "/currentliquidlevel")
    public ResultBean getCurrentLiquidLevel() {
        List<Map<String, Object>> currentLiquidLevel = bimServic.getCurrentLiquidLevel();
        return ResultBean.success("获取成功!", currentLiquidLevel);
    }


    /**
     * 绿化灌溉压力表运行状态
     */
    @GetMapping(value = "/findCountRun/ggylcgq")
    public ResultBean getFindCountRunYlcgq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.GGYLCGQ);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 生活用水压力表运行状态运行状态
     */
    @GetMapping(value = "/findCountRun/shylcgq")
    public ResultBean getFindCountRunSHYlcgq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.YLCGQ);
        return ResultBean.success("获取成功!", countRunVo);
    }


    /**
     * 绿化灌溉灌溉水表表运行状态
     */
    @GetMapping(value = "/findCountRun/ggsb")
    public ResultBean getFindCountRunGgsb() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.GGSB);
        return ResultBean.success("获取成功!", countRunVo);
    }


    /**
     * 生活水表表运行状态
     */
    @GetMapping(value = "/findCountRun/shsb")
    public ResultBean getFindCountRunShsb() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.SHSB);
        return ResultBean.success("获取成功!", countRunVo);
    }


    /**
     * 环境设备运行状态
     */
    @GetMapping(value = "/findCountRun/hjsb")
    public ResultBean getFindCountRunHjsb() {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        deviceInfos.addAll(bimServic.getDeviceInfos(Common.SJ));
        deviceInfos.addAll(bimServic.getDeviceInfos(Common.WS));
        deviceInfos.addAll(bimServic.getDeviceInfos(Common.CO));
        deviceInfos.addAll(bimServic.getDeviceInfos(Common.DHY));
        CountRunVo countRunVo = bimServic.getCountRun(deviceInfos);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 电器火灾探测器  探针弹窗 BIM
     *
     * @param tripartite 3方标识
     */
    @GetMapping(value = "/findCountRun/bimdqhz")
    public ResultBean getBIMElectricalFireDetectors(String tripartite) throws Exception {
        //三方标识 转为 设备code
        String deviceCode = subSystemStaticDataDefaultModule.getDeviceCodeByTripartiteCode(tripartite);
        List<ElectricalFireProbeVo> electricalFireDetectors = bimServic.getElectricalFireDetectors(deviceCode);
        return ResultBean.success("获取成功!", electricalFireDetectors);
    }

    /**
     * 电器火灾探测器  探针弹窗 BIM
     *
     * @param deviceCode 设备code
     */
    @GetMapping(value = "/findCountRun/dqhz")
    public ResultBean getElectricalFireDetectors(String deviceCode) {
        List<ElectricalFireProbeVo> electricalFireDetectors = bimServic.getElectricalFireDetectors(deviceCode);
        return ResultBean.success("获取成功!", electricalFireDetectors);
    }

    /**
     * 设备运行统计 -智能单相电表
     */
    @GetMapping(value = "/findCountRun/zndx")
    public ResultBean getFindCountRunZndx() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.ZNDX);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 -智能三相电表
     */
    @GetMapping(value = "/findCountRun/znsx")
    public ResultBean getFindCountRunZnsx() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.ZNSX);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 -单相多户电表
     */
    @GetMapping(value = "/findCountRun/dxdh")
    public ResultBean getFindCountRunDxdh() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.DXDH);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 -消防压力传感器运行统计
     */
    @GetMapping(value = "/findCountRun/xfylcgq")
    public ResultBean getFindCountRunXfylcgq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.XFYLCGQ);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 - 电器火灾探测器
     */
    @GetMapping(value = "/findCountRun/dqhztcq")
    public ResultBean getFindCountRunDqhztcq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.DQHZ);
        return ResultBean.success("获取成功!", countRunVo);
    }


    /**
     * 设备运行统计 - 双数排风机
     */
    @GetMapping(value = "/findCountRun/sspfj")
    public ResultBean getFindCountRunSspfj() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.SPF);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 - 单数排风机
     */
    @GetMapping(value = "/findCountRun/dspfj")
    public ResultBean getFindCountRunDspfj() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.DPF);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 - 温湿度传感器
     */
    @GetMapping(value = "/findCountRun/wsdcgq")
    public ResultBean getFindCountRunWsdcgq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.WS);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 - 一氧化碳传感器
     */
    @GetMapping(value = "/findCountRun/yyhtcgq")
    public ResultBean getFindCountRunYyhtcgq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.CO);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 - 气体多合一传感器
     */
    @GetMapping(value = "/findCountRun/qtdhycgq")
    public ResultBean getFindCountRunQtdhycgq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.DHY);
        return ResultBean.success("获取成功!", countRunVo);
    }


    /**
     * 设备运行统计 - 双泵集水井
     */
    @GetMapping(value = "/findCountRun/sbjsj")
    public ResultBean getFindCountRunSbjsj() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.SJSJ);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 - 单泵集水井
     */
    @GetMapping(value = "/findCountRun/dbjsj")
    public ResultBean getFindCountRunDbjsj() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.DJSJ);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 - 水浸传感器
     */
    @GetMapping(value = "/findCountRun/sjcgq")
    public ResultBean getFindCountRunSjcgq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.SJ);
        return ResultBean.success("获取成功!", countRunVo);
    }

    /**
     * 设备运行统计 - 排水压力传感
     */
    @GetMapping(value = "/findCountRun/psylcgq")
    public ResultBean getFindCountRunPsylcgq() {
        CountRunVo countRunVo = bimServic.getCountRun(Common.YLCGQ);
        return ResultBean.success("获取成功!", countRunVo);
    }


    /**
     * 用电感知设备- 实时监控
     */
    @GetMapping(value = "/electricityPerceptionEquipment")
    public ResultBean getElectricityPerceptionEquipment() {
        List<ElectricityPerceptionEquipmentVo> vos = bimServic.getElectricityPerceptionEquipment();
        return ResultBean.success("获取成功!", vos);
    }

    /**
     * 楼栋用电 - 安全监测
     */
    @GetMapping(value = "/buildingMonitoring")
    public ResultBean getBuildingMonitoring() {
        List<BuildingMonitoringAlarmVo> vos = bimServic.getBuildingMonitoring();
        return ResultBean.success("获取成功!", vos);
    }


}
