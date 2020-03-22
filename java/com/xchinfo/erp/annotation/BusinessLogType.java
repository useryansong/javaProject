package com.xchinfo.erp.annotation;

/**
 * 业务日志操作类型
 *
 * @author roman.li
 * @date 2018/11/27
 * @update
 */
public enum BusinessLogType {

    /**
     * 新增
     */
    CREATE("create"),

    /**
     * 更新
     */
    UPDATE("update"),

    /**
     * 删除
     */
    DELETE("delete"),

    /**
     * 批量删除
     */
    BATCHDELETE("batchDelete");

    private final String value;

    BusinessLogType(String value){
        this.value = value;
    }
}
