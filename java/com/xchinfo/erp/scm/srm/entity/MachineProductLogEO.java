package com.xchinfo.erp.scm.srm.entity;

import java.io.Serializable;
import java.util.Date;

import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@TableName("srm_machine_product_log")
@KeySequence("srm_machine_product_log")
public class MachineProductLogEO extends AbstractAuditableEntity<MachineProductLogEO>{
	
    @TableId(type = IdType.INPUT)
    private Long machineProductLogId;/** 主键 */
    
    private Long scheduleOrderId;/** 排班id */
    
    private Long machineId;/** 设备id */
    
    private Integer productType;/** 生产状态;0-开始生产1-停机2-停机恢复3-停机恢复 */
    
    private Integer callType;/** 呼叫状态;0-未呼叫1-呼叫2-处理呼叫 */
    
    private Date startTime;/** 开始时间 */
    
    private Date stopTime;/** 停止时间 */
    
    private Date returnProductTime;/** 恢复停机时间 */
    
    private Date callTime;/** 呼叫时间 */
    
    private Date dealCallTime;/** 呼叫完成时间 */
    
//    private Integer version;/** 乐观锁 */
//    
//    private String createdBy;/** 创建人 */
//    
//    private Date createdTime;/** 创建时间 */
//    
//    private String lastModifiedBy;/** 更新人 */
//    
//    private Date lastModifiedTime;/** 更新时间 */
    
    private Long parentProductLogId;
    
    private Long duration;
    
    private Date finishTime;
    
    private String logDesc;
    
    @TableField(exist = false)
    private String machineName;
    
    @TableField(exist = false)
    private Date parentTime;
    
    @TableField(exist = false)
    private String machineCode;
    
    private Integer stopWarnLogId;
    
    @TableField(exist = false)
    private String stopReanson;
    
    @TableField(exist = false)
    private String machineNumber;
    
    @TableField(exist = false)
    private String specification;
    
    @TableField(exist = false)
    private String specificModel;
    
    private Integer debugType;
    
    private Date debugTime;
    
    private Date stopDebugTime;
    
    private Double debugCount;
    
    private Date dealStopTime;

	@TableField(exist = false)
	private Long presentLast;/** 处理到场时长 */

	@TableField(exist = false)
	private Date handleTime;/** 开始处理时间 */

	@TableField(exist = false)
	private Long returnProductLast;/** 停机处理时长 */

	@TableField(exist = false)
	private Long childProductLogId;


    @Override
    public Serializable getId() {
    	return this.machineProductLogId;
    }
    
	public Long getScheduleOrderId() {
		return scheduleOrderId;
	}

	public void setScheduleOrderId(Long scheduleOrderId) {
		this.scheduleOrderId = scheduleOrderId;
	}

	public Long getMachineProductLogId() {
		return machineProductLogId;
	}


	public void setMachineProductLogId(Long machineProductLogId) {
		this.machineProductLogId = machineProductLogId;
	}


	public Long getMachineId() {
		return machineId;
	}


	public void setMachineId(Long machineId) {
		this.machineId = machineId;
	}

	public Integer getProductType() {
		return productType;
	}


	public void setProductType(Integer productType) {
		this.productType = productType;
	}


	public Integer getCallType() {
		return callType;
	}


	public void setCallType(Integer callType) {
		this.callType = callType;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public Date getStopTime() {
		return stopTime;
	}


	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}


	public Date getReturnProductTime() {
		return returnProductTime;
	}


	public void setReturnProductTime(Date returnProductTime) {
		this.returnProductTime = returnProductTime;
	}


	public Date getCallTime() {
		return callTime;
	}


	public void setCallTime(Date callTime) {
		this.callTime = callTime;
	}

	public Date getDealCallTime() {
		return dealCallTime;
	}

	public void setDealCallTime(Date dealCallTime) {
		this.dealCallTime = dealCallTime;
	}

	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}


	public String getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}


	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}


	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}


	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public Long getParentProductLogId() {
		return parentProductLogId;
	}

	public void setParentProductLogId(Long parentProductLogId) {
		this.parentProductLogId = parentProductLogId;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public String getLogDesc() {
		return logDesc;
	}

	public void setLogDesc(String logDesc) {
		this.logDesc = logDesc;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public Date getParentTime() {
		return parentTime;
	}

	public void setParentTime(Date parentTime) {
		this.parentTime = parentTime;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Integer getStopWarnLogId() {
		return stopWarnLogId;
	}

	public void setStopWarnLogId(Integer stopWarnLogId) {
		this.stopWarnLogId = stopWarnLogId;
	}

	public String getStopReanson() {
		return stopReanson;
	}

	public void setStopReanson(String stopReanson) {
		this.stopReanson = stopReanson;
	}

	public String getMachineNumber() {
		return machineNumber;
	}

	public void setMachineNumber(String machineNumber) {
		this.machineNumber = machineNumber;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getSpecificModel() {
		return specificModel;
	}

	public void setSpecificModel(String specificModel) {
		this.specificModel = specificModel;
	}

	public Integer getDebugType() {
		return debugType;
	}

	public void setDebugType(Integer debugType) {
		this.debugType = debugType;
	}

	public Date getDebugTime() {
		return debugTime;
	}

	public void setDebugTime(Date debugTime) {
		this.debugTime = debugTime;
	}

	public Date getStopDebugTime() {
		return stopDebugTime;
	}

	public void setStopDebugTime(Date stopDebugTime) {
		this.stopDebugTime = stopDebugTime;
	}

	public Double getDebugCount() {
		return debugCount;
	}

	public void setDebugCount(Double debugCount) {
		this.debugCount = debugCount;
	}

	public Date getDealStopTime() {
		return dealStopTime;
	}

	public void setDealStopTime(Date dealStopTime) {
		this.dealStopTime = dealStopTime;
	}

	public Long getPresentLast() {
		return presentLast;
	}

	public void setPresentLast(Long presentLast) {
		this.presentLast = presentLast;
	}

	public Long getReturnProductLast() {
		return returnProductLast;
	}

	public void setReturnProductLast(Long returnProductLast) {
		this.returnProductLast = returnProductLast;
	}

	public Date getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(Date handleTime) {
		this.handleTime = handleTime;
	}

	public Long getChildProductLogId() {
		return childProductLogId;
	}

	public void setChildProductLogId(Long childProductLogId) {
		this.childProductLogId = childProductLogId;
	}
}