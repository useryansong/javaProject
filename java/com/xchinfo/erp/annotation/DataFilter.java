package com.xchinfo.erp.annotation;

import java.lang.annotation.*;

/**
 * 数据过滤注解
 *
 * @author roman.li
 * @project wms-sys-api
 * @date 2018/6/4 17:08
 * @update
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {

    /**
     * 表的别名
     */
    String tableAlias() default "";

    /**
     * 查询条件前缀，可选值有：[where、and]
     */
    String prefix() default "";

    /**
     * 用户ID
     */
    String userId() default "created_by";

    /**
     * 部门ID
     */
    String orgId() default "org_id";
}
