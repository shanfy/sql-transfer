package com.yang.exception;


/**
 * 自定义异常类
 * @author shanfy
 * @date 2023-08-25 11:51
 */
public class BusinessException extends RuntimeException {

    private final ExceptionInfo exceptionInfo;

    public BusinessException(ExceptionInfo exceptionInfo) {
        super(exceptionInfo.getErrorMsg());
        this.exceptionInfo = exceptionInfo;
    }

    public BusinessException(String errorMsg) {
        super(errorMsg);
        // 默认code为400 代表业务异常
        this.exceptionInfo = new ExceptionInfo(400, errorMsg);
    }

    public ExceptionInfo getExceptionInfo() {
        return exceptionInfo;
    }

}
