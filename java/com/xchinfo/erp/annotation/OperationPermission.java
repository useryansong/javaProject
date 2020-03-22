package com.xchinfo.erp.annotation;

import java.lang.annotation.*;

/**
 * @author roman.li
 * @date 2018/11/23
 * @update
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationPermission {
    String value() default "";
}
