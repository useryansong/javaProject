package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_process_machine")
@KeySequence("bsc_process_machine")
public class ProcessMachineEO extends AbstractAuditableEntity<ProcessMachineEO> {
    private static final long serialVersionUID = 6273069258695832623L;

    @TableId(type = IdType.INPUT)
    private Long processMachineId;/** 主键 */

    private Long processId;/** 工序 */

    private Long machineId;/** 机器 */

    @TableField(exist = false)
    private String machineCode;/** 设备编码 */

    @TableField(exist = false)
    private String machineName;/** 设备名称 */

    private Integer duration;/** 加工时长 */

    private Integer durationType;/** 时长单位;s-秒;m-分钟;h-小时 */

    private String conditionExpr;/** 匹配条件 */

    @Override
    public Serializable getId() {
        return this.processMachineId;
    }

    /** 主键 */
    public Long getProcessMachineId(){
        return this.processMachineId;
    }
    /** 主键 */
    public void setProcessMachineId(Long processMachineId){
        this.processMachineId = processMachineId;
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
    /** 加工时长 */
    public Integer getDuration(){
        return this.duration;
    }
    /** 加工时长 */
    public void setDuration(Integer duration){
        this.duration = duration;
    }
    /** 时长单位;s-秒;m-分钟;h-小时 */
    public Integer getDurationType(){
        return this.durationType;
    }
    /** 时长单位;s-秒;m-分钟;h-小时 */
    public void setDurationType(Integer durationType){
        this.durationType = durationType;
    }
    /** 匹配条件 */
    public String getConditionExpr() {
        return conditionExpr;
    }
    /** 匹配条件 */
    public void setConditionExpr(String conditionExpr) {
        this.conditionExpr = conditionExpr;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }
}
