package com.subsystem.repository.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 子系统上下文
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "LINKAGE_INFO")
public class LinkageInfo {
    @Id
    @Column(name = "`KEY`")
    String key;
    //上下文信息
    @Column(name = "SUB_SYSTEM_CONTEXT")
    String subSystemContext;
}
