package com.subsystem.repository;


import com.subsystem.repository.mapping.DeviceFaultType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceFaultTypeRepository extends JpaRepository<DeviceFaultType, String> {
}
