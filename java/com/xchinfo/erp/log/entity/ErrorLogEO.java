package com.xchinfo.erp.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统错误日志
 *
 * @author roman.li
 * @date 2018/11/12
 * @update
 */
@TableName("sys_error_log")
@KeySequence("sys_error_log")
public class ErrorLogEO extends AbstractEntity<ErrorLogEO> {

    private static final long serialVersionUID = 3896937972852890420L;

    @TableId(type = IdType.INPUT)
    private Long logId;/** 日志ID */

    private String requestUri;/** 请求URI */

    private String requestMethod;/** 请求方式 */

    private String requestParams;/** 请求参数 */

    private String userAgent;/** 用户代理 */

    private String ip;/** 操作IP */

    private String errorInfo;/** 异常信息 */

    @TableField(value = "created_by")
    private String createdBy;/** 创建人 */

    @TableField(value = "created_time")
    private Date createdTime;/** 创建时间 */

    @Override
    public Serializable getId() {
        return this.logId;
    }

    public Long getLogId() {
        return logId;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIp() {
        return ip;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
