package com.subsystem.module.staticdata;

import java.util.List;

/**
 * 子系统静态数据组件接口
 */
public interface SubSystemStaticDataModule {

    /**
     * 获取设备code，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return 设备code
     */
    String getDeviceCodeByTripartiteCode(String tripartiteCode);



    /**
     * 获取设备物模型存redis的key，通过三方标识
     *
     * @param tripartiteCode 三方标识
     * @return redis key
     */
    String getDeviceCodeRedisKeyByTripartiteCode(String tripartiteCode);

    /**
     * 获取三方标识，通过设备code
     *
     * @param deviceCode 设备code
     * @return 设备code
     */
    String getTripartiteCodeByDeviceCode(String deviceCode);

    /**
     * 提供更新静态缓存的方法
     * 使得更改数据库后可以热更数据
     */
    void updateSubSystemStaticData();


    /**
     * 获取所有三方标识
     */
    List<String> getAllTripartiteCode();


    /**
     * 获取所有设备编码（资产编码）
     */
    List<String> getAllDeviceCode();


    /**
     * 获取所有设备物模型的key
     */
    List<String> getAllModelKeys();
}
