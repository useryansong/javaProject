package com.xchinfo.erp.scm.srm.entity;

import java.io.Serializable;

import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("srm_machine_warn_msg")
@KeySequence("srm_machine_warn_msg")
public class MachineWarnMsgEO extends AbstractAuditableEntity<MachineWarnMsgEO> {

	@TableId(type = IdType.INPUT)
	private Long machineWarnMsgId;
	/** 主键 */

	private Long machineId;
	/** 设备id */

	private Integer stopWarnLogId;
	/** 告警id */

	private Long stopWarnNotifyId;
	/** 告警级别id */

	private Long stopWarnNotifyUserId;
	/** 告警通知人id */

	private String phone;
	/** 手机号 */

	private String msgDesc;
	/** 内容 */

	private Integer warnLevel;
	/** 级别 */

	private String stopReanson;
	/** 告警原因 */

	private Long machineProductLogId;
	
	private Long orgId;

	/** 设备日志id */

	@Override
	public Serializable getId() {
		return this.stopWarnLogId;
	}

	/** 主键 */
	public Long getMachineWarnMsgId() {
		return this.machineWarnMsgId;
	}

	/** 主键 */
	public void setMachineWarnMsgId(Long machineWarnMsgId) {
		this.machineWarnMsgId = machineWarnMsgId;
	}

	/** 设备id */
	public Long getMachineId() {
		return this.machineId;
	}

	/** 设备id */
	public void setMachineId(Long machineId) {
		this.machineId = machineId;
	}

	public Integer getStopWarnLogId() {
		return stopWarnLogId;
	}

	public void setStopWarnLogId(Integer stopWarnLogId) {
		this.stopWarnLogId = stopWarnLogId;
	}

	/** 告警级别id */
	public Long getStopWarnNotifyId() {
		return this.stopWarnNotifyId;
	}

	/** 告警级别id */
	public void setStopWarnNotifyId(Long stopWarnNotifyId) {
		this.stopWarnNotifyId = stopWarnNotifyId;
	}

	/** 告警通知人id */
	public Long getStopWarnNotifyUserId() {
		return this.stopWarnNotifyUserId;
	}

	/** 告警通知人id */
	public void setStopWarnNotifyUserId(Long stopWarnNotifyUserId) {
		this.stopWarnNotifyUserId = stopWarnNotifyUserId;
	}

	/** 手机号 */
	public String getPhone() {
		return this.phone;
	}

	/** 手机号 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMsgDesc() {
		return msgDesc;
	}

	public void setMsgDesc(String msgDesc) {
		this.msgDesc = msgDesc;
	}

	public Integer getWarnLevel() {
		return warnLevel;
	}

	public void setWarnLevel(Integer warnLevel) {
		this.warnLevel = warnLevel;
	}

	/** 告警原因 */
	public String getStopReanson() {
		return this.stopReanson;
	}

	/** 告警原因 */
	public void setStopReanson(String stopReanson) {
		this.stopReanson = stopReanson;
	}

	/** 设备日志id */
	public Long getMachineProductLogId() {
		return this.machineProductLogId;
	}

	/** 设备日志id */
	public void setMachineProductLogId(Long machineProductLogId) {
		this.machineProductLogId = machineProductLogId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	

}