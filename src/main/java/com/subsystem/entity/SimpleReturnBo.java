package com.subsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleReturnBo implements Return{
    private static final long serialVersionUID = 8587315569034866329L;
    private boolean success;
    private String msg;

    @Override
    public boolean success() {
        return this.success;
    }

    @Override
    public String msg() {
        return this.msg;
    }

    public static SimpleReturnBo getDefaultInstance(){
        return SimpleReturnBo.builder()
                .success(Boolean.FALSE)
                .msg(null)
                .build();
    }

    public static SimpleReturnBo getSuccessInstance(){
        return SimpleReturnBo.builder()
                .success(Boolean.TRUE)
                .msg("操作成功")
                .build();
    }

    public static SimpleReturnBo getFailedInstance(){
        return SimpleReturnBo.builder()
                .success(Boolean.FALSE)
                .msg("操作失败")
                .build();
    }

    public static SimpleReturnBo getFailedInstance(String errorMsg){
        return SimpleReturnBo.builder()
                .success(Boolean.FALSE)
                .msg("操作失败")
                .build();
    }
}
