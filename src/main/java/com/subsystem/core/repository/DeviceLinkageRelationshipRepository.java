package com.subsystem.core.repository;


import com.subsystem.core.repository.mapping.DeviceLinkageRelationshipData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 设备联动关系表
 */
public interface DeviceLinkageRelationshipRepository extends JpaRepository<DeviceLinkageRelationshipData, String> {

}
