package com.xchinfo.erp.scm.srm.entity;

import java.io.Serializable;
import java.util.Date;

import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@TableName("srm_stop_warn_log")
@KeySequence("srm_stop_warn_log")
public class StopWarnLogEO extends AbstractAuditableEntity<StopWarnLogEO>{
    
    @TableId(type = IdType.INPUT)
    private Long stopWarnLogId;/** 主键 */
    
    private String stopReanson;/** 停机原因 */
    
    private Long orgId;/** 责任部门 */
    
    private Integer stopType;/** 停机类型;0-正常停机；1-异常停机 */
    
    private Integer isVoice;/** 是否允许语音通报;0-允许；1-不允许 */
    
    private Integer isMsg;/** 是否允许短信通知;0-允许；1-不允许 */
    
    private Integer status;/** 是否启用;0-启用；1-禁用 */
    
    private Integer version;/** 乐观锁 */
    
    private String createdBy;/** 创建人 */
    
    private Date createdTime;/** 创建时间 */
    
    private String lastModifiedBy;/** 更新人 */
    
    private Date lastModifiedTime;/** 更新时间 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 **/
    
    @Override
    public Serializable getId() {
    	return this.stopWarnLogId;
    }
    
    /** 主键 */
    public Long getStopWarnLogId(){
        return this.stopWarnLogId;
    }
    /** 主键 */
    public void setStopWarnLogId(Long stopWarnLogId){
        this.stopWarnLogId = stopWarnLogId;
    }
    /** 停机原因 */
    public String getStopReanson(){
        return this.stopReanson;
    }
    /** 停机原因 */
    public void setStopReanson(String stopReanson){
        this.stopReanson = stopReanson;
    }
    /** 责任部门 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 责任部门 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 停机类型;0-正常停机；1-异常停机 */
    public Integer getStopType(){
        return this.stopType;
    }
    /** 停机类型;0-正常停机；1-异常停机 */
    public void setStopType(Integer stopType){
        this.stopType = stopType;
    }
    /** 是否允许语音通报;0-允许；1-不允许 */
    public Integer getIsVoice(){
        return this.isVoice;
    }
    /** 是否允许语音通报;0-允许；1-不允许 */
    public void setIsVoice(Integer isVoice){
        this.isVoice = isVoice;
    }
    /** 是否允许短信通知;0-允许；1-不允许 */
    public Integer getIsMsg(){
        return this.isMsg;
    }
    /** 是否允许短信通知;0-允许；1-不允许 */
    public void setIsMsg(Integer isMsg){
        this.isMsg = isMsg;
    }
    /** 是否启用;0-启用；1-禁用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 是否启用;0-启用；1-禁用 */
    public void setStatus(Integer status){
        this.status = status;
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

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
    
}