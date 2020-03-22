package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.TreeNode;

import java.util.Date;

/**
 * 任务树结构
 *
 * @author roman.li
 * @date 2019/3/28
 * @update
 */
@TableName("v_production_task_tree")
public class ProductionTaskTreeEO extends TreeNode<ProductionTaskTreeEO> {
    private static final long serialVersionUID = 4449693240097327093L;

    @TableId(type = IdType.INPUT)
    private Long taskId;/** 主键 */

    private Long parentId;/** 上级节点 */

    private Long orderId;/** 订单 */

    private String orderNo;/** 订单编号 */

    private Long materialId;/** 物料 */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private Long processId;/** 工序 */

    private String processCode;/** 工序编码 */

    private String processName;/** 工序名称 */

    private Long machineId;/** 机器 */

    private String machineCode;/** 机器编码 */

    private String machineName;/** 机器名称 */

    private Date startTime;/** 开始时间 */

    private Date endTime;/** 结束时间 */

    private Double amount;/** 加工数量 */

    private Double finishedAmount;/** 完工数量 */

    private String employeeNo;/** 员工工号 */

    private String employeeName;/** 员工姓名 */

    private Integer status;

    @Override
    public Long getId() {
        return this.taskId;
    }

    /** 状态;1-新建;2-执行中;3-已完成 */

    @Override
    public Long getPid() {
        return this.parentId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
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

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
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

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFinishedAmount() {
        return finishedAmount;
    }

    public void setFinishedAmount(Double finishedAmount) {
        this.finishedAmount = finishedAmount;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
