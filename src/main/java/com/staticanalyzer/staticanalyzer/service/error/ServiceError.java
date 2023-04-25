package com.staticanalyzer.staticanalyzer.service.error;

/**
 * 服务异常父类
 * 
 * @author iu_oi
 * @since 0.0.3
 */
public class ServiceError extends RuntimeException {

    public ServiceError(ServiceErrorType errorType) {
        super(errorType.getMsg());
    }
}
