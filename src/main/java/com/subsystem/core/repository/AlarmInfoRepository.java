package com.subsystem.core.repository;

import com.subsystem.core.repository.mapping.AlarmInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlarmInfoRepository extends JpaSpecificationExecutor<AlarmInfo>, JpaRepository<AlarmInfo, String> {

}
