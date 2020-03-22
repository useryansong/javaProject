package com.xchinfo.erp.sys.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务数据日志
 */
@TableName("sys_biz_data_log")
@KeySequence("sys_biz_data_log")
public class BizDataLogEO extends AbstractAuditableEntity<BizDataLogEO> {
    private static final long serialVersionUID = -2572410286235688266L;

    @TableId(type = IdType.AUTO)
    private Long bizDataLogId;/** id */

    private String description;/** 数据变化描述 */

    private Date oprateTime;/** 操作时间 */

    private String oprater;/** 操作人 */

    private String oprateType;/** 操作类型 */

    private String oldData;/** 原数据;操作前数据（数据位对象json串） */

    private String newData;/** 新数据;操作后数据（数据位对象json串）*/

    private String oprateTable;/** 操作数据表实体名称 */

    private Long dataId;/** 操作数据id*/

    private Long orgId;/**所属组织机构id*/

    @TableField(exist = false)
    private String orgName; /** 组织机构*/

    @Override
    public Serializable getId() {
        return this.bizDataLogId;
    }

    public Long getBizDataLogId() {
        return bizDataLogId;
    }

    public void setBizDataLogId(Long bizDataLogId) {
        this.bizDataLogId = bizDataLogId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getOprateTime() {
        return oprateTime;
    }

    public void setOprateTime(Date oprateTime) {
        this.oprateTime = oprateTime;
    }

    public String getOprater() {
        return oprater;
    }

    public void setOprater(String oprater) {
        this.oprater = oprater;
    }

    public String getOprateType() {
        return oprateType;
    }

    public void setOprateType(String oprateType) {
        this.oprateType = oprateType;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public String getOprateTable() {
        return oprateTable;
    }

    public void setOprateTable(String oprateTable) {
        this.oprateTable = oprateTable;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
