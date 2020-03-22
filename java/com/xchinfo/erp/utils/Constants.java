package com.xchinfo.erp.utils;

/**
 * 常量
 *
 * @author roman.li
 * @project wms-web
 * @date 2018/6/3 09:50
 * @update
 */
public class Constants {
    /** 超级管理员ID */
    public static final int SUPER_ADMIN = 1;
    /** 数据权限过滤 */
    public static final String SQL_FILTER = "sqlFilter";

    public enum DataEntity {
        ORG("sys_org");// 机构

        private String value;

        DataEntity(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 业务日志操作类型
     */
    public enum BizLogType {
        INSERT("insert"),// 增加
        UPDATE("update"),// 修改
        DELETE("delete");// 删除

        private String value;

        BizLogType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 自动去除表前缀
     */
    public static final String AUTO_REOMVE_PRE = "true";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";
}
