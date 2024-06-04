package com.subsystem.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Metric {
	private String deviceId;
	private String subSystem;
	private String name;
	private Object value;
	//todo 这个data迟早要优化 先标记
	private Date timestamp;
	private String quality;
	private String alias;

	/**
	 * 设备编码
	 */
	private String deviceCode;

	/**
	 * 控制服务编码
	 */
	private String attr;

	@Override
	public String toString() {
		return "Metric{" +
				"deviceId='" + deviceId + '\'' +
				", subSystem='" + subSystem + '\'' +
				", name='" + name + '\'' +
				", value=" + value +
				", timestamp=" + timestamp +
				", quality='" + quality + '\'' +
				", alias='" + alias + '\'' +
				", deviceCode='" + deviceCode + '\'' +
				", attr='" + attr + '\'' +
				'}';
	}
}
