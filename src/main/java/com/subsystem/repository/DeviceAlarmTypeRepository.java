package com.subsystem.repository;


import com.subsystem.repository.mapping.DeviceAlarmType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceAlarmTypeRepository extends JpaRepository<DeviceAlarmType, String> {

}
