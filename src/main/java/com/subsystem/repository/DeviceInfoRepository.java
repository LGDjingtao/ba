package com.subsystem.repository;

import com.subsystem.repository.mapping.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, String> {

}
