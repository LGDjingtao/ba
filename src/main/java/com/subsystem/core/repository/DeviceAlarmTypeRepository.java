package com.subsystem.core.repository;


import com.subsystem.core.repository.mapping.DeviceAlarmType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceAlarmTypeRepository extends JpaRepository<DeviceAlarmType, String> {

}
