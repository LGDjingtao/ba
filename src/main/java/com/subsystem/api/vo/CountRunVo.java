package com.subsystem.api.vo;


import lombok.Data;

@Data
public class CountRunVo {
    /**
     * 在线数
     */
    private Integer onlineCount;
    /**
     * 离线数
     */
    private Integer offlineCount;
    /**
     * 总数
     */
    private Integer allAssetCount;
    /**
     * 故障数
     */
    private Integer faultCount;
}
