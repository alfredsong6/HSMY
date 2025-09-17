package com.hsmy.exception;

import com.hsmy.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各类异常，返回标准化的错误响应
 * 
 * @author HSMY
 * @date 2025/09/17
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 处理参数校验异常 - @RequestBody 参数校验失败
     * 
     * @param e 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：{}", e.getMessage());
        
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        return Result.error(400, "参数校验失败: " + errorMessage);
    }
    
    /**
     * 处理参数绑定异常 - 表单参数校验失败
     * 
     * @param e 参数绑定异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result<String> handleBindException(BindException e) {
        log.error("参数绑定异常：{}", e.getMessage());
        
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        return Result.error(400, "参数绑定失败: " + errorMessage);
    }
    
    /**
     * 处理约束违反异常 - @PathVariable 和 @RequestParam 参数校验失败
     * 
     * @param e 约束违反异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result<String> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("约束违反异常：{}", e.getMessage());
        
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        return Result.error(400, "参数约束违反: " + errorMessage);
    }
    
    /**
     * 处理参数类型不匹配异常
     * 
     * @param e 参数类型不匹配异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Result<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("参数类型不匹配：参数名={}, 期望类型={}, 实际值={}", 
                e.getName(), e.getRequiredType(), e.getValue());
        
        String errorMessage = String.format("参数 '%s' 类型不匹配，期望类型为 %s", 
                e.getName(), 
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        
        return Result.error(400, errorMessage);
    }
    
    /**
     * 处理缺少请求参数异常
     * 
     * @param e 缺少请求参数异常
     * @return 错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("缺少请求参数：{}", e.getMessage());
        
        String errorMessage = String.format("缺少必需的请求参数: %s", e.getParameterName());
        return Result.error(400, errorMessage);
    }
    
    /**
     * 处理空指针异常
     * 
     * @param e 空指针异常
     * @return 错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Result<String> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常：", e);
        return Result.error(500, "系统内部错误，请稍后重试");
    }
    
    /**
     * 处理运行时异常
     * 
     * @param e 运行时异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        return Result.error(500, "系统内部错误，请稍后重试");
    }
    
    /**
     * 处理所有未捕获的异常
     * 
     * @param e 异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(500, "系统内部错误，请稍后重试");
    }
}