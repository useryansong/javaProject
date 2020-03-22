package com.xchinfo.erp.scm.srm.entity;

import java.io.Serializable;
import java.util.Date;

import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@TableName("srm_stop_warn_notify_user")
@KeySequence("srm_stop_warn_notify_user")
public class StopWarnNotifyUserEO extends AbstractAuditableEntity<StopWarnNotifyUserEO>{
	      
    @TableId(type = IdType.INPUT)
    private Long stopWarnNotifyUserId;/** 主键 */
    
    private Long stopWarnNotifyId;/** 停机告警通知 */
    
    private Long employeeId;/** 通知人id */
    
    @TableField(exist = false)
    private String employeeName;/** 通知人姓名  */
    
    @TableField(exist = false)
    private String phone;/** 手机号 */
    
    private Integer status;/** 状态;状态：0启用1禁用 */
    
    private Integer version;/** 乐观锁 */
    
    private String createdBy;/** 创建人 */
    
    private Date createdTime;/** 创建时间 */
    
    private String lastModifiedBy;/** 更新人 */
    
    private Date lastModifiedTime;/** 更新时间 */

    @Override
    public Serializable getId() {
    	return this.stopWarnNotifyUserId;
    }
    
    /** 主键 */
    public Long getStopWarnNotifyUserId(){
        return this.stopWarnNotifyUserId;
    }
    /** 主键 */
    public void setStopWarnNotifyUserId(Long stopWarnNotifyUserId){
        this.stopWarnNotifyUserId = stopWarnNotifyUserId;
    }
    /** 停机告警通知 */
    public Long getStopWarnNotifyId(){
        return this.stopWarnNotifyId;
    }
    /** 停机告警通知 */
    public void setStopWarnNotifyId(Long stopWarnNotifyId){
        this.stopWarnNotifyId = stopWarnNotifyId;
    }
    /** 通知人id */
    public Long getEmployeeId(){
        return this.employeeId;
    }
    /** 通知人id */
    public void setEmployeeId(Long employeeId){
        this.employeeId = employeeId;
    }
    
    public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/** 状态;状态：0启用1禁用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;状态：0启用1禁用 */
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
}