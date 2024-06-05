package com.subsystem.core.entity;

import java.io.Serializable;

/**
 * Created by TangXiangLin on 2023-05-30 19:10
 * 返回对象
 */
public interface Return extends Serializable {
    /** 业务执行结果 */
    boolean success();
    /** 业务信息 */
    String msg();

}
