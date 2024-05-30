package com.subsystem.repository;

import com.subsystem.repository.mapping.SyncFailedData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncFailedDataRepository  extends JpaRepository<SyncFailedData, String> {

}
