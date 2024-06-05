package com.subsystem.core.entity;


import java.io.Serializable;

public class ResultBean<T> implements Serializable{

    private int code;
    private String message;
    private T data;

    public ResultBean() {

    }

    public static <T> ResultBean<T> error(int code, String message) {
        ResultBean<T> resultBean = new ResultBean<>();
        resultBean.setCode(code);
        resultBean.setMessage(message);
        return resultBean;
    }
    
    public static <T> ResultBean<T> error(String message){
    	ResultBean<T> resultBean = new ResultBean<>();
        resultBean.setCode(300);
        resultBean.setMessage(message);
        return resultBean;
    }

    public static <T> ResultBean<T> success() {
    	ResultBean<T> resultBean = new ResultBean<>();
        resultBean.setCode(200);
        resultBean.setMessage("success");
        return resultBean;
    }

    public static <T> ResultBean<T> success(String message) {
    	ResultBean<T> resultBean = new ResultBean<>();
        resultBean.setCode(200);
        resultBean.setMessage(message);
        return resultBean;
    }
    
    public static<T> ResultBean<T> success(T datas) {
        ResultBean<T> resultBean = new ResultBean<>();
        resultBean.setCode(200);
        resultBean.setMessage("success");
        resultBean.setData(datas);
        return resultBean;
    }
    
    public static<T> ResultBean<T> success(String message,T Data){
        ResultBean<T> resultBean = new ResultBean<>();
        resultBean.setCode(200);
        resultBean.setMessage(message);
        resultBean.setData(Data);
        return resultBean;
    }
    
    public static<T> ResultBean<T> success(int code,String message,T Data){
        ResultBean<T> resultBean = new ResultBean<>();
        resultBean.setCode(code);
        resultBean.setMessage(message);
        resultBean.setData(Data);
        return resultBean;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}