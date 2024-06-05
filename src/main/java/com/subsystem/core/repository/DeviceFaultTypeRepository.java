package com.subsystem.core.repository;


import com.subsystem.core.repository.mapping.DeviceFaultType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceFaultTypeRepository extends JpaRepository<DeviceFaultType, String> {
}
