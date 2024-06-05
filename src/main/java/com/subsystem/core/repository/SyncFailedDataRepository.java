package com.subsystem.core.repository;

import com.subsystem.core.repository.mapping.SyncFailedData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncFailedDataRepository  extends JpaRepository<SyncFailedData, String> {

}
