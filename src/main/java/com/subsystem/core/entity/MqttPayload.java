
package com.subsystem.core.entity;


import lombok.Data;

import java.util.List;

@Data
public class MqttPayload {
    private List<Metric> metrics;

	@Override
	public String toString() {
		return "[metrics=" + metrics + "]";
	}

    
}