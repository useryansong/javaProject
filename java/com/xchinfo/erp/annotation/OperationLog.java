package com.xchinfo.erp.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @author roman.li
 * @project wms-sys-api
 * @date 2018/6/4 16:42
 * @update
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    String value() default "";
}
