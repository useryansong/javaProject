package com.xchinfo.erp.bsc.entity;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xchinfo.erp.annotation.BusinessLogField;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_machine")
@KeySequence("bsc_machine")
public class MachineEO extends AbstractAuditableEntity<MachineEO> {
    private static final long serialVersionUID = 5139731738871738803L;

    @TableId(type = IdType.INPUT)
    private Long machineId;/** 设备ID */

    @Length(max = 200, message = "设备编码长度不能超过 200 个字符")
    @BusinessLogField("设备编码")
    private String machineCode;/** 设备编码 */

    private String machineNumber;/** 设备机台编号 */

    @NotBlank(message = "设备名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("设备名称")
    private String machineName;/** 设备名称 */

    private String machineType;/** 设备类别;0-生产设备；1-非生产设备 */

    private String specification;/** 规格 */

    private String specificModel;/** 型号 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @NotNull(message = "设备组不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("设备组")
    private String machineGroup;/** 设备组 */

    private String location;/** 加工区域 */

    private String capitalAssetsNo;/** 固定资产编号 */

    private Long maintenance;/** 维护负责人 */

    private String maintenanceName;/** 维护负责人名称 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-禁用;1-可用;2-维修;3-其他 */

    @Length(max = 1000, message = "备注长度不能超过 1000 个字符")
    @BusinessLogField("备注")
    private String remarks;/** 备注 */

    private Integer productStatus;/**生产状态   0-空闲 1-生产中  2-停机*/
    
    private Integer callStatus;/**生产状态   0-空闲 1-生产中  2-停机*/
    
    private Integer stopWarnLogId;
    
    private Integer needDealStop;/** 是否需要处理   0不需要  1需要 */
    
    private Long scheduleOrderId;
    
    private Integer debugStatus;/**  调试状态   0-不用调试   1-要调试  */
    
    @TableField(exist = false)
    private Double planProduceQuantity;
    
    @TableField(exist = false)
    private Date startTime;
    
    @TableField(exist = false)
    private String elementNo;
    
    @TableField(exist = false)
    private Integer isMsg;
    
    @TableField(exist = false)
    private Integer isVoice;
    
    @TableField(exist = false)
    private Long stopWarnId;
    
    @TableField(exist = false)
    private String stopReanson;
    
    @TableField(exist = false)
    private Date stopTime;

    @TableField(exist = false)
    private String projectNo;

    private String storageMaterialArea; /** 配料区域 */

    
    @Override
    public Serializable getId() {
        return this.machineId;
    }

    /** 设备ID */
    public Long getMachineId(){
        return this.machineId;
    }
    /** 设备ID */
    public void setMachineId(Long machineId){
        this.machineId = machineId;
    }
    /** 设备编码 */
    public String getMachineCode(){
        return this.machineCode;
    }
    /** 设备编码 */
    public void setMachineCode(String machineCode){
        this.machineCode = machineCode;
    }
    /** 设备名称 */
    public String getMachineName(){
        return this.machineName;
    }
    /** 设备名称 */
    public void setMachineName(String machineName){
        this.machineName = machineName;
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

    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 设备组 */
    public String getMachineGroup(){
        return this.machineGroup;
    }
    /** 设备组 */
    public void setMachineGroup(String machineGroup){
        this.machineGroup = machineGroup;
    }
    /** 状态;0-禁用;1-可用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-禁用;1-可用 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 备注 */
    public String getRemarks(){
        return this.remarks;
    }
    /** 备注 */
    public void setRemarks(String remarks){
        this.remarks = remarks;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCapitalAssetsNo() {
        return capitalAssetsNo;
    }

    public void setCapitalAssetsNo(String capitalAssetsNo) {
        this.capitalAssetsNo = capitalAssetsNo;
    }

    public Long getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Long maintenance) {
        this.maintenance = maintenance;
    }

    public String getMaintenanceName() {
        return maintenanceName;
    }

    public void setMaintenanceName(String maintenanceName) {
        this.maintenanceName = maintenanceName;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

	public Integer getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}

	public Integer getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(Integer callStatus) {
		this.callStatus = callStatus;
	}

	public Integer getStopWarnLogId() {
		return stopWarnLogId;
	}

	public void setStopWarnLogId(Integer stopWarnLogId) {
		this.stopWarnLogId = stopWarnLogId;
	}

	public Integer getNeedDealStop() {
		return needDealStop;
	}

	public void setNeedDealStop(Integer needDealStop) {
		this.needDealStop = needDealStop;
	}

	public Long getScheduleOrderId() {
		return scheduleOrderId;
	}

	public void setScheduleOrderId(Long scheduleOrderId) {
		this.scheduleOrderId = scheduleOrderId;
	}

	public Double getPlanProduceQuantity() {
		return planProduceQuantity;
	}

	public void setPlanProduceQuantity(Double planProduceQuantity) {
		this.planProduceQuantity = planProduceQuantity;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getElementNo() {
		return elementNo;
	}

	public void setElementNo(String elementNo) {
		this.elementNo = elementNo;
	}

	public Integer getDebugStatus() {
		return debugStatus;
	}

	public void setDebugStatus(Integer debugStatus) {
		this.debugStatus = debugStatus;
	}

	public Integer getIsMsg() {
		return isMsg;
	}

	public void setIsMsg(Integer isMsg) {
		this.isMsg = isMsg;
	}

	public Integer getIsVoice() {
		return isVoice;
	}

	public void setIsVoice(Integer isVoice) {
		this.isVoice = isVoice;
	}

	public Long getStopWarnId() {
		return stopWarnId;
	}

	public void setStopWarnId(Long stopWarnId) {
		this.stopWarnId = stopWarnId;
	}

	public String getStopReanson() {
		return stopReanson;
	}

	public void setStopReanson(String stopReanson) {
		this.stopReanson = stopReanson;
	}

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getStorageMaterialArea() {
        return storageMaterialArea;
    }

    public void setStorageMaterialArea(String storageMaterialArea) {
        this.storageMaterialArea = storageMaterialArea;
    }
}
