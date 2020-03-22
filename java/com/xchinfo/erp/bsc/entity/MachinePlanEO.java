package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_machine_plan")
@KeySequence("bsc_machine_plan")
public class MachinePlanEO extends AbstractAuditableEntity<MachinePlanEO> {
    private static final long serialVersionUID = -5929483607700370850L;

    @TableId(type = IdType.INPUT)
    private Long machinePlanId;/** 主键 */

    private Long machineId;/** 机器 */

    @TableField(exist = false)
    private String machineCode;/** 设备编码 */

    @TableField(exist = false)
    private String machineName;/** 设备名称 */

    private Date startTime;/** 开始时间 */

    private Date endTime;/** 结束时间 */

    private Integer isEnable;/** 是否可用 */

    private String description;/** 说明 */

    @Override
    public Serializable getId() {
        return this.machinePlanId;
    }

    /** 主键 */
    public Long getMachinePlanId(){
        return this.machinePlanId;
    }
    /** 主键 */
    public void setMachinePlanId(Long machinePlanId){
        this.machinePlanId = machinePlanId;
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
    /** 是否可用 */
    public Integer getIsEnable(){
        return this.isEnable;
    }
    /** 是否可用 */
    public void setIsEnable(Integer isEnable){
        this.isEnable = isEnable;
    }
    /** 说明 */
    public String getDescription(){
        return this.description;
    }
    /** 说明 */
    public void setDescription(String description){
        this.description = description;
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
