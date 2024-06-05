package com.subsystem.core.module.task;

import lombok.Data;

/**
 * 定时任务抽象类
 */
@Data
public abstract class ScheduleTask implements Runnable{

    /** 任务名称唯一 */
    private String tid;

    @Override
    public abstract void run();
}
