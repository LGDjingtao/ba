package com.subsystem.core.repository;

import com.subsystem.core.repository.mapping.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, String> {

}
