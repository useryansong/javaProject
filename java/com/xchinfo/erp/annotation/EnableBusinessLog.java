package com.xchinfo.erp.annotation;

import org.yecat.mybatis.entity.support.AbstractEntity;

import java.lang.annotation.*;

/**
 * 业务日志记录
 *
 * @author roman.li
 * @date 2018/11/27
 * @update
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableBusinessLog {

    /**
     * 操作类型
     *
     * @return
     */
    BusinessLogType value() default BusinessLogType.CREATE;

    /**
     * 数据实体类
     *
     * @return
     */
    Class<?> entityClass() default AbstractEntity.class;
}
