package com.xchinfo.erp.scm.srm.entity;

import java.io.Serializable;
import java.util.Date;

import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@TableName("srm_stop_warn_notify")
@KeySequence("srm_stop_warn_notify")
public class StopWarnNotifyEO extends AbstractAuditableEntity<StopWarnNotifyEO>{
    
    @TableId(type = IdType.INPUT)
    private Long stopWarnNotifyId;/** 主键 */
    
    private Integer warnLevel;/** 警告等级;1,2,3,4,5级 */
    
    private Integer stopDuration;/** 停机时长;单位：分 */
    
    private String notifyMethod;/** 通知方式; */
    
    private Integer status;/** 状态;0：启用，1：禁用 */
    
    @TableField(exist = false)
    private Integer userCount;/** 通知人数 */
    
    private Long stopWarnLogId;/** 关联的停机告警id */
    
    private Integer version;/** 乐观锁 */
    
    private String createdBy;/** 创建人 */
    
    private Date createdTime;/** 创建时间 */
    
    private String lastModifiedBy;/** 更新人 */
    
    private Date lastModifiedTime;/** 更新时间 */

    @Override
    public Serializable getId() {
    	return this.stopWarnNotifyId;
    }
    
    
    /** 主键 */
    public Long getStopWarnNotifyId(){
        return this.stopWarnNotifyId;
    }
    /** 主键 */
    public void setStopWarnNotifyId(Long stopWarnNotifyId){
        this.stopWarnNotifyId = stopWarnNotifyId;
    }
    /** 警告等级;1,2,3,4,5级 */
    public Integer getWarnLevel(){
        return this.warnLevel;
    }
    /** 警告等级;1,2,3,4,5级 */
    public void setWarnLevel(Integer warnLevel){
        this.warnLevel = warnLevel;
    }
    /** 停机时长;单位：分 */
    public Integer getStopDuration(){
        return this.stopDuration;
    }
    /** 停机时长;单位：分 */
    public void setStopDuration(Integer stopDuration){
        this.stopDuration = stopDuration;
    }
    /** 通知方式 */
    public String getNotifyMethod() {
		return notifyMethod;
	}
	public void setNotifyMethod(String notifyMethod) {
		this.notifyMethod = notifyMethod;
	}
	/** 状态;0：启用，1：禁用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0：启用，1：禁用 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 通知人数 */
    public Integer getUserCount(){
        return this.userCount;
    }
    /** 通知人数 */
    public void setUserCount(Integer userCount){
        this.userCount = userCount;
    }
    /** 关联的停机告警id */
    public Long getStopWarnLogId(){
        return this.stopWarnLogId;
    }
    /** 关联的停机告警id */
    public void setStopWarnLogId(Long stopWarnLogId){
        this.stopWarnLogId = stopWarnLogId;
    }
    /** 乐观锁 */
    public Integer getVersion(){
        return this.version;
    }
    /** 乐观锁 */
    public void setVersion(Integer version){
        this.version = version;
    }
    /** 创建人 */
    public String getCreatedBy(){
        return this.createdBy;
    }
    /** 创建人 */
    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }
    /** 创建时间 */
    public Date getCreatedTime(){
        return this.createdTime;
    }
    /** 创建时间 */
    public void setCreatedTime(Date createdTime){
        this.createdTime = createdTime;
    }
    /** 更新人 */
    public String getLastModifiedBy(){
        return this.lastModifiedBy;
    }
    /** 更新人 */
    public void setLastModifiedBy(String lastModifiedBy){
        this.lastModifiedBy = lastModifiedBy;
    }
    /** 更新时间 */
    public Date getLastModifiedTime(){
        return this.lastModifiedTime;
    }
    /** 更新时间 */
    public void setLastModifiedTime(Date lastModifiedTime){
        this.lastModifiedTime = lastModifiedTime;
    }
}