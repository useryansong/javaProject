package com.xchinfo.erp.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务操作日志
 *
 * @author roman.li
 * @date 2018/11/20
 * @update
 */
@TableName("sys_biz_log")
@KeySequence("sys_biz_log")
public class BizLogEO extends AbstractEntity<BizLogEO> {

    private static final long serialVersionUID = 6084806343191917324L;

    @TableId(type = IdType.INPUT)
    private Long logId;/** 日志ID */

    private String optEntity;/** 操作实体 */

    private String entityId;/** 实体ID */

    private String operation;/** 用户操作 */

    private String userName;/** 用户名 */

    private String realName;/** 用户姓名 */

    private String createdBy;/** 创建人 */

    private Date createdTime;/** 创建时间 */

    @Override
    public Serializable getId() {
        return this.logId;
    }

    public Long getLogId() {
        return logId;
    }

    public String getOptEntity() {
        return optEntity;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getOperation() {
        return operation;
    }

    public String getUserName() {
        return userName;
    }

    public String getRealName() {
        return realName;
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

    public void setOptEntity(String optEntity) {
        this.optEntity = optEntity;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
