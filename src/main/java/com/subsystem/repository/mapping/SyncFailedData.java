package com.subsystem.repository.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 同步失败数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "SYNC_FAILED_DATA")
public class SyncFailedData {
    @Id
    @Column(name = "`KEY`")
    String key;
    //设备类型名称
    @Column(name = "VALUE")
    String value;
}
