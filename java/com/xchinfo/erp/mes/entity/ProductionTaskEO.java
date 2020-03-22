package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("mes_production_task")
@KeySequence("mes_production_task")
public class ProductionTaskEO extends AbstractAuditableEntity<ProductionTaskEO> {
    private static final long serialVersionUID = 961877590707495917L;

    @TableId(type = IdType.INPUT)
    private Long taskId;/** 主键 */

    private Long orderId;/** 订单 */

    @TableField(exist = false)
    private String orderNo;/** 订单编号 */

    private Long materialId;/** 物料 */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String materialName;/** 物料名称 */

    private Long processId;/** 工序 */

    @TableField(exist = false)
    private String processCode;/** 工序编码 */

    @TableField(exist = false)
    private String processName;/** 工序名称 */

    private Long machineId;/** 机器 */

    @TableField(exist = false)
    private String machineCode;/** 机器编码 */

    @TableField(exist = false)
    private String machineName;/** 机器名称 */

    private Date startTime;/** 开始时间 */

    private Date endTime;/** 结束时间 */

    private Double amount;/** 加工数量 */

    private Double finishedAmount;/** 完工数量 */

    private String employeeNo;/** 员工工号 */

    private String employeeName;/** 员工姓名 */

    private Integer status;/** 状态;1-新建;2-执行中;3-已完成 */

    @Override
    public Serializable getId() {
        return this.taskId;
    }

    /** 主键 */
    public Long getTaskId(){
        return this.taskId;
    }
    /** 主键 */
    public void setTaskId(Long taskId){
        this.taskId = taskId;
    }
    /** 订单 */
    public Long getOrderId(){
        return this.orderId;
    }
    /** 订单 */
    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }
    /** 物料 */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料 */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 工序 */
    public Long getProcessId(){
        return this.processId;
    }
    /** 工序 */
    public void setProcessId(Long processId){
        this.processId = processId;
    }
    /** 机器 */
    public Long getMachineId(){
        return this.machineId;
    }
    /** 机器 */
    public void setMachineId(Long machineId){
        this.machineId = machineId;
    }
    /** 开始时间 */
    public Date getStartTime(){
        return this.startTime;
    }
    /** 开始时间 */
    public void setStartTime(Date startTime){
        this.startTime = startTime;
    }
    /** 结束时间 */
    public Date getEndTime(){
        return this.endTime;
    }
    /** 结束时间 */
    public void setEndTime(Date endTime){
        this.endTime = endTime;
    }
    /** 加工数量 */
    public Double getAmount(){
        return this.amount;
    }
    /** 加工数量 */
    public void setAmount(Double amount){
        this.amount = amount;
    }
    /** 完工数量 */
    public Double getFinishedAmount(){
        return this.finishedAmount;
    }
    /** 完工数量 */
    public void setFinishedAmount(Double finishedAmount){
        this.finishedAmount = finishedAmount;
    }
    /** 员工工号 */
    public String getEmployeeNo(){
        return this.employeeNo;
    }
    /** 员工工号 */
    public void setEmployeeNo(String employeeNo){
        this.employeeNo = employeeNo;
    }
    /** 员工姓名 */
    public String getEmployeeName(){
        return this.employeeName;
    }
    /** 员工姓名 */
    public void setEmployeeName(String employeeName){
        this.employeeName = employeeName;
    }
    /** 状态;1-新建;2-执行中;3-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;1-新建;2-执行中;3-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
