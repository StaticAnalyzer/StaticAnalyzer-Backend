package com.staticanalyzer.staticanalyzer.service.error;

/**
 * 服务异常类
 * 
 * @author YangYu
 * @since 0.3
 */
public class ServiceError extends RuntimeException {

    public ServiceError(ServiceErrorType errorType) {
        super(errorType.getMsg());
    }

}
