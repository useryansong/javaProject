package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.scm.wms.entity.DeliveryWorkOrderEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveWorkOrderEO;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/9/11
 */
@TableName("srm_schedule_order")
@KeySequence("srm_schedule_order")
public class ScheduleOrderEO extends AbstractAuditableEntity<ScheduleOrderEO> {
    private static final long serialVersionUID = -880422461298786518L;

    @TableId(type = IdType.INPUT)
    private Long scheduleOrderId;/** 主键 */

    private String voucherNo;/** 流水号 */

    private Long productOrderId;/** 生产订单ID */

    private Long orgId;/** 归属机构ID */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planProductDate;/** 预计生产日期 */

    private Long workingProcedureTimeId;/** 工序工时ID */

    @TableField(exist = false)
    private String workingProcedureCode;/** 工序号 */

    @TableField(exist = false)
    private String workingProcedureName;/** 工序名称 */

    @TableField(exist = false)
    private String workingProcedureTypeName;/** 工序类型名称 */

    @TableField(exist = false)
    private String workshopName;/** 生产车间 */

    private Long machineId;/** 设备ID */

    private String machineCode;/** 设备编码 */

    @TableField(exist = false)
    private String machineNumber;/** 设备机台编号 */

    @TableField(exist = false)
    private String machineName;/** 设备名称 */

    private Double planProduceQuantity;/** 计划生产数量 */

    @TableField(exist = false)
    private Double sumPlanProduceQuantity;/** 总计划生产数量 */

    private Double actualProduceQuantity;/** 实际生产数量 */

    private Double realtimeProduceQuantity;/** 实时生产数量 */

    private Double notQualifiedQuantity;/** 不良数量 */

    private Integer status;/** 状态;0-新建；1-已发布（待生产）；2-生产中； 3-生产完成 */

    private Integer openStatus;/** 打开/关闭状态(4-打开，5-关闭) */

    private Date startTime;/** 开始时间 */

    private Date finishTime;/** 完成时间 */

    private Date planFinishTime;/** 预计完成时间 */

    private Long groupId;/** 班组ID */

    private String classOrder;/** 班次*/

    private String importType;/**导入类型1:机器人 2:铆接3:手工装配4:冲压*/

    private Integer importStatus;/**导入状态0:暂时 1 :正常*/

    private Long checkStatus;/**查看状态0:暂时 1 :正常*/

    private Long importMaterialId;/** 导入物料ID */

    private String importElement;/** 导入零件号 */

    private String importProcedureCode;/** 导入工序号 */

    private Long operatorId;/** 操作工ID */

    private String operatorName;/** 操作工名称 */

    private Double debugQuantity;/** 调试数量 */

    private Date debugStartTime;/** 调试开始时间 */

    private Integer preDebugStatus;/** 调试前状态值;0-新建；1-已发布（待生产）；2-生产中； 3-生产完成 */

    private Integer debugStatus;/** 调试状态值;0-调试；1-不调试 */

    @TableField(exist = false)
    private String wMachineName;

    @TableField(exist = false)
    private String wptMaterialName;

    private Integer stopStatus;

    private Integer stopType;

    private Integer stopWarnLogId;

    private Integer callStatus;

    private Integer callType;

    private Long startProductLogId;

    private Long stopProductLogId;

    private Long callProductLogId;

    private Long dealCallProductLogId;

    private Long dealStopProductLogId;

    private Long finishProductLogId;
    
    private Long debugProductLogId;
    
    private Long returnProductLogId;

    @TableField(exist = false)
    private Integer machineProductStatus;

    @TableField(exist = false)
    private Integer machineCallStatus;

    @TableField(exist = false)
    private Integer needDealStop;

    @TableField(exist = false)
    private String orgName;

    @TableField(exist = false)
    private Long materialId;

    @TableField(exist = false)
    private String materialName;

    @TableField(exist = false)
    private Double ct;

    @TableField(exist = false)
    private String elementNo;
    
    @TableField(exist = false)
    private String employeeName;
    
    @TableField(exist = false)
    private String employeeNo;
    
    @TableField(exist = false)
    private Integer machineDebugStatus;

    @TableField(exist = false)
    private String poVoucherNo;

    @TableField(exist = false)
    private List<DeliveryWorkOrderEO> deliveryWorkOrders;

    @TableField(exist = false)
    private List<ReceiveWorkOrderEO> receiveWorkOrders;

    @TableField(exist = false)
    private String inventoryCode;/** 存货编码 */

    @TableField(exist = false)
    private Date finishDate;/** 排产完成日期 */

    @Override
    public Serializable getId() {
        return this.scheduleOrderId;
    }

    /** 主键 */
    public Long getScheduleOrderId(){
        return this.scheduleOrderId;
    }
    /** 主键 */
    public void setScheduleOrderId(Long scheduleOrderId){
        this.scheduleOrderId = scheduleOrderId;
    }
    /** 流水号 */
    public String getVoucherNo(){
        return this.voucherNo;
    }
    /** 流水号 */
    public void setVoucherNo(String voucherNo){
        this.voucherNo = voucherNo;
    }
    /** 生产订单ID */
    public Long getProductOrderId(){
        return this.productOrderId;
    }
    /** 生产订单ID */
    public void setProductOrderId(Long productOrderId){
        this.productOrderId = productOrderId;
    }
    /** 预计生产日期 */
    public Date getPlanProductDate(){
        return this.planProductDate;
    }
    /** 预计生产日期 */
    public void setPlanProductDate(Date planProductDate){
        this.planProductDate = planProductDate;
    }
    /** 工序工时ID */
    public Long getWorkingProcedureTimeId(){
        return this.workingProcedureTimeId;
    }
    /** 工序工时ID */
    public void setWorkingProcedureTimeId(Long workingProcedureTimeId){
        this.workingProcedureTimeId = workingProcedureTimeId;
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
    /** 计划生产数量 */
    public Double getPlanProduceQuantity(){
        return this.planProduceQuantity;
    }
    /** 计划生产数量 */
    public void setPlanProduceQuantity(Double planProduceQuantity){
        this.planProduceQuantity = planProduceQuantity;
    }
    /** 实际生产数量 */
    public Double getActualProduceQuantity(){
        return this.actualProduceQuantity;
    }
    /** 实际生产数量 */
    public void setActualProduceQuantity(Double actualProduceQuantity){
        this.actualProduceQuantity = actualProduceQuantity;
    }
    /** 实时生产数量 */
    public Double getRealtimeProduceQuantity(){
        return this.realtimeProduceQuantity;
    }
    /** 实时生产数量 */
    public void setRealtimeProduceQuantity(Double realtimeProduceQuantity){
        this.realtimeProduceQuantity = realtimeProduceQuantity;
    }
    /** 不良数量 */
    public Double getNotQualifiedQuantity(){
        return this.notQualifiedQuantity;
    }
    /** 不良数量 */
    public void setNotQualifiedQuantity(Double notQualifiedQuantity){
        this.notQualifiedQuantity = notQualifiedQuantity;
    }
    /** 状态;0-新建；1-已发布（待生产）；2-生产中； 3-生产完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建；1-已发布（待生产）；2-生产中； 3-生产完成 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 开始时间 */
    public Date getStartTime(){
        return this.startTime;
    }
    /** 开始时间 */
    public void setStartTime(Date startTime){
        this.startTime = startTime;
    }
    /** 完成时间 */
    public Date getFinishTime(){
        return this.finishTime;
    }
    /** 完成时间 */
    public void setFinishTime(Date finishTime){
        this.finishTime = finishTime;
    }
    /** 预计完成时间 */
    public Date getPlanFinishTime(){
        return this.planFinishTime;
    }
    /** 预计完成时间 */
    public void setPlanFinishTime(Date planFinishTime){
        this.planFinishTime = planFinishTime;
    }
    /** 班组ID */
    public Long getGroupId(){
        return this.groupId;
    }
    /** 班组ID */
    public void setGroupId(Long groupId){
        this.groupId = groupId;
    }
    /** 操作工ID */
    public Long getOperatorId(){
        return this.operatorId;
    }
    /** 操作工ID */
    public void setOperatorId(Long operatorId){
        this.operatorId = operatorId;
    }
    /** 操作工名称 */
    public String getOperatorName(){
        return this.operatorName;
    }
    /** 操作工名称 */
    public void setOperatorName(String operatorName){
        this.operatorName = operatorName;
    }
    /** 调试数量 */
    public Double getDebugQuantity(){
        return this.debugQuantity;
    }
    /** 调试数量 */
    public void setDebugQuantity(Double debugQuantity){
        this.debugQuantity = debugQuantity;
    }
    /** 调试开始时间 */
    public Date getDebugStartTime(){
        return this.debugStartTime;
    }
    /** 调试开始时间 */
    public void setDebugStartTime(Date debugStartTime){
        this.debugStartTime = debugStartTime;
    }
    /** 调试前状态值;0-新建；1-已发布（待生产）；2-生产中； 3-生产完成 */
    public Integer getPreDebugStatus(){
        return this.preDebugStatus;
    }
    /** 调试前状态值;0-新建；1-已发布（待生产）；2-生产中； 3-生产完成 */
    public void setPreDebugStatus(Integer preDebugStatus){
        this.preDebugStatus = preDebugStatus;
    }
    /** 调试状态值;0-调试；1-不调试 */
    public Integer getDebugStatus(){
        return this.debugStatus;
    }
    /** 调试状态值;0-调试；1-不调试 */
    public void setDebugStatus(Integer debugStatus){
        this.debugStatus = debugStatus;
    }

    public Double getSumPlanProduceQuantity() {
        return sumPlanProduceQuantity;
    }

    public void setSumPlanProduceQuantity(Double sumPlanProduceQuantity) {
        this.sumPlanProduceQuantity = sumPlanProduceQuantity;
    }

    public String getWorkingProcedureCode() {
        return workingProcedureCode;
    }

    public void setWorkingProcedureCode(String workingProcedureCode) {
        this.workingProcedureCode = workingProcedureCode;
    }

    public String getWorkingProcedureName() {
        return workingProcedureName;
    }

    public void setWorkingProcedureName(String workingProcedureName) {
        this.workingProcedureName = workingProcedureName;
    }

    public String getWorkingProcedureTypeName() {
        return workingProcedureTypeName;
    }

    public void setWorkingProcedureTypeName(String workingProcedureTypeName) {
        this.workingProcedureTypeName = workingProcedureTypeName;
    }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getWptMaterialName() {
        return wptMaterialName;
    }

    public void setWptMaterialName(String wptMaterialName) {
        this.wptMaterialName = wptMaterialName;
    }


    public Integer getStopStatus() {
        return stopStatus;
    }

    public void setStopStatus(Integer stopStatus) {
        this.stopStatus = stopStatus;
    }

    public Integer getStopType() {
        return stopType;
    }

    public void setStopType(Integer stopType) {
        this.stopType = stopType;
    }

    public Integer getStopWarnLogId() {
        return stopWarnLogId;
    }

    public void setStopWarnLogId(Integer stopWarnLogId) {
        this.stopWarnLogId = stopWarnLogId;
    }

    public Integer getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(Integer callStatus) {
        this.callStatus = callStatus;
    }

    public Integer getCallType() {
        return callType;
    }

    public void setCallType(Integer callType) {
        this.callType = callType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Double getCt() {
        return ct;
    }

    public void setCt(Double ct) {
        this.ct = ct;
    }

    public String getwMachineName() {
        return wMachineName;
    }

    public void setwMachineName(String wMachineName) {
        this.wMachineName = wMachineName;
    }

    public Long getStartProductLogId() {
        return startProductLogId;
    }

    public void setStartProductLogId(Long startProductLogId) {
        this.startProductLogId = startProductLogId;
    }

    public Long getStopProductLogId() {
        return stopProductLogId;
    }

    public void setStopProductLogId(Long stopProductLogId) {
        this.stopProductLogId = stopProductLogId;
    }

    public Long getCallProductLogId() {
        return callProductLogId;
    }

    public void setCallProductLogId(Long callProductLogId) {
        this.callProductLogId = callProductLogId;
    }

    public Long getDealCallProductLogId() {
        return dealCallProductLogId;
    }

    public void setDealCallProductLogId(Long dealCallProductLogId) {
        this.dealCallProductLogId = dealCallProductLogId;
    }

    public Long getDealStopProductLogId() {
        return dealStopProductLogId;
    }

    public void setDealStopProductLogId(Long dealStopProductLogId) {
        this.dealStopProductLogId = dealStopProductLogId;
    }

    public Long getFinishProductLogId() {
        return finishProductLogId;
    }

    public void setFinishProductLogId(Long finishProductLogId) {
        this.finishProductLogId = finishProductLogId;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }
    public Integer getMachineProductStatus() {
        return machineProductStatus;
    }

    public void setMachineProductStatus(Integer machineProductStatus) {
        this.machineProductStatus = machineProductStatus;
    }

    public Integer getMachineCallStatus() {
        return machineCallStatus;
    }

    public void setMachineCallStatus(Integer machineCallStatus) {
        this.machineCallStatus = machineCallStatus;
    }

    public Integer getNeedDealStop() {
        return needDealStop;
    }

    public void setNeedDealStop(Integer needDealStop) {
        this.needDealStop = needDealStop;
    }

	public String getElementNo() {
		return elementNo;
	}

	public void setElementNo(String elementNo) {
		this.elementNo = elementNo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public Long getDebugProductLogId() {
		return debugProductLogId;
	}

	public void setDebugProductLogId(Long debugProductLogId) {
		this.debugProductLogId = debugProductLogId;
	}

	public Integer getMachineDebugStatus() {
		return machineDebugStatus;
	}

	public void setMachineDebugStatus(Integer machineDebugStatus) {
		this.machineDebugStatus = machineDebugStatus;
	}

	public Long getReturnProductLogId() {
		return returnProductLogId;
	}

	public void setReturnProductLogId(Long returnProductLogId) {
		this.returnProductLogId = returnProductLogId;
	}

    public String getClassOrder() {
        return classOrder;
    }

    public void setClassOrder(String classOrder) {
        this.classOrder = classOrder;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getPoVoucherNo() {
        return poVoucherNo;
    }

    public void setPoVoucherNo(String poVoucherNo) {
        this.poVoucherNo = poVoucherNo;
    }

    public Integer getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(Integer importStatus) {
        this.importStatus = importStatus;
    }

    public Long getImportMaterialId() {
        return importMaterialId;
    }

    public void setImportMaterialId(Long importMaterialId) {
        this.importMaterialId = importMaterialId;
    }

    public String getImportElement() {
        return importElement;
    }

    public void setImportElement(String importElement) {
        this.importElement = importElement;
    }

    public String getImportProcedureCode() {
        return importProcedureCode;
    }

    public void setImportProcedureCode(String importProcedureCode) {
        this.importProcedureCode = importProcedureCode;
    }

    public Long getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Long checkStatus) {
        this.checkStatus = checkStatus;
    }

    public List<DeliveryWorkOrderEO> getDeliveryWorkOrders() {
        return deliveryWorkOrders;
    }

    public void setDeliveryWorkOrders(List<DeliveryWorkOrderEO> deliveryWorkOrders) {
        this.deliveryWorkOrders = deliveryWorkOrders;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public List<ReceiveWorkOrderEO> getReceiveWorkOrders() {
        return receiveWorkOrders;
    }

    public void setReceiveWorkOrders(List<ReceiveWorkOrderEO> receiveWorkOrders) {
        this.receiveWorkOrders = receiveWorkOrders;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }
}
