package com.xchinfo.erp.annotation;

import java.lang.annotation.*;

/**
 * 标记实体哪些字段记录日志
 *
 * @author roman.li
 * @date 2018/11/27
 * @update
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessLogField {
    /**
     * 字段中文名称
     *
     * @return
     */
    String value() default  "";
}
