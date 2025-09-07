package com.hsmy.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应结果类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应信息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    public Result() {
    }
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功响应
     * 
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }
    
    /**
     * 成功响应（带数据）
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应（自定义消息）
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * 失败响应
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
    
    /**
     * 失败响应（自定义状态码）
     * 
     * @param code 状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    
    /**
     * 未登录响应
     * 
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "您尚未登录，请先登录", null);
    }
    
    /**
     * 无权限响应
     * 
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "您无权访问该资源", null);
    }
    
    /**
     * 资源不存在响应
     * 
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> notFound() {
        return new Result<>(404, "抱歉，未找到您请求的页面", null);
    }
}