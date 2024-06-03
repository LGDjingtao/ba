package com.subsystem.repository;


import com.subsystem.repository.mapping.LinkageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 设备联动上下文信息
 */
public interface LinkageInfoRepository extends JpaRepository<LinkageInfo, String> {


}
