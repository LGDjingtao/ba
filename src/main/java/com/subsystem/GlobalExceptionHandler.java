package com.subsystem;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice   // 控制器增强类
@ResponseBody
@Aspect
public class GlobalExceptionHandler {
    /**
     * 处理未知异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public String exception(Exception e){
        e.printStackTrace();
        return "未知异常";
        // return Result.err(1002, "未知异常"); 这里也可以返回项目中的统一返回类
    }

    /**
     * 处理手动抛出异常
     * 		如果没有自定义异常类，在代码中出现了可预知的异常，可以手动抛出 "RuntimeException"
     * 		如果抛出的是 "RuntimeException", 则会执行此方法
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public String exception(RuntimeException e){
        e.printStackTrace();
        return e.getMessage();
        // return Result.err(1003, e.getMessage()); 这里也可以返回项目中的统一返回类
    }

//    @Pointcut("execution(* com.subsystem.assembly.*.*(..))")
//    public void pointCut() {
//    }
//
//    @Around("pointCut()")
//    public Object handlerException(ProceedingJoinPoint proceedingJoinPoint) {
//        try {
//            return proceedingJoinPoint.proceed();
//        } catch (Throwable ex) {
//            log.error("execute scheduled occur exception.", ex);
//        }
//        return null;
//    }

}
