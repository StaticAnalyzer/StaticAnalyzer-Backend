package com.staticanalyzer.staticanalyzer.annotation.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用此注解的api屏蔽swagger追踪
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeSwagger {
}
