package com.xchinfo.erp.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统操作日志
 *
 * @author roman.li
 * @date 2017/11/9
 * @update
 */
@TableName("sys_opt_log")
@KeySequence("sys_opt_log")
public class OptLogEO extends AbstractEntity<OptLogEO> {

    private static final long serialVersionUID = -6835659299290622943L;

    @TableId(type = IdType.INPUT)
    private Long logId;

    private String operation;/** 用户操作 */

    private String requestUri;/** 请求URI */

    private String requestMethod;/** 请求方式 */

    private String requestParams;/** 请求参数 */

    private Integer requestTime;/** 请求时长 */

    private String userAgent;/** 用户代理 */

    private String ip;/** 操作IP */

    private Integer status;/** 状态;0：失败   1：成功 */

    private String userName;/** 用户名 */

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

    public String getOperation() {
        return operation;
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

    public Integer getRequestTime() {
        return requestTime;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIp() {
        return ip;
    }

    public Integer getStatus() {
        return status;
    }

    public String getUserName() {
        return userName;
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

    public void setOperation(String operation) {
        this.operation = operation;
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

    public void setRequestTime(Integer requestTime) {
        this.requestTime = requestTime;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
