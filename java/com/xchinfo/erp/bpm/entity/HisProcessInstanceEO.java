package com.xchinfo.erp.bpm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/3/22
 * @update
 */
@TableName("wf_his_process_instance")
public class HisProcessInstanceEO extends AbstractAuditableEntity<HisProcessInstanceEO> {
    private static final long serialVersionUID = -2451472747712243474L;

    @TableId(type = IdType.INPUT)
    private Long processInstanceId;/** 主键ID */

    private Integer isSubProcess;/** 是否子流程;0-否;1-是 */

    private Long parentInstanceId;/** 父流程ID */

    private Long workItemId;/** 工作项ID */

    private String processInstanceName;/** 流程名称 */

    private Long processId;/** 流程定义ID */

    private String processKey;/** 流程标识 */

    private String creator;/** 发起人 */

    private Date createTime;/** 发起时间 */

    private Date finishTime;/** 完成时间 */

    private String processParams;/** 流程参数 */

    private Integer status;/** 流程状态;0-新建;1-执行中;2-已终止;3-已完成 */

    @Override
    public Serializable getId() {
        return this.processInstanceId;
    }
    /** 主键ID */
    public Long getProcessInstanceId(){
        return this.processInstanceId;
    }
    /** 主键ID */
    public void setProcessInstanceId(Long processInstanceId){
        this.processInstanceId = processInstanceId;
    }
    /** 流程名称 */
    public String getProcessInstanceName(){
        return this.processInstanceName;
    }
    /** 流程名称 */
    public void setProcessInstanceName(String processInstanceName){
        this.processInstanceName = processInstanceName;
    }
    /** 是否子流程;0-否;1-是 */
    public Integer getIsSubProcess(){
        return this.isSubProcess;
    }
    /** 是否子流程;0-否;1-是 */
    public void setIsSubProcess(Integer isSubProcess){
        this.isSubProcess = isSubProcess;
    }
    /** 父流程ID */
    public Long getParentInstanceId(){
        return this.parentInstanceId;
    }
    /** 父流程ID */
    public void setParentInstanceId(Long parentInstanceId){
        this.parentInstanceId = parentInstanceId;
    }
    /** 工作项ID */
    public Long getWorkItemId(){
        return this.workItemId;
    }
    /** 工作项ID */
    public void setWorkItemId(Long workItemId){
        this.workItemId = workItemId;
    }
    /** 流程定义ID */
    public Long getProcessId(){
        return this.processId;
    }
    /** 流程定义ID */
    public void setProcessId(Long processId){
        this.processId = processId;
    }
    /** 流程标识 */
    public String getProcessKey(){
        return this.processKey;
    }
    /** 流程标识 */
    public void setProcessKey(String processKey){
        this.processKey = processKey;
    }
    /** 发起人 */
    public String getCreator(){
        return this.creator;
    }
    /** 发起人 */
    public void setCreator(String creator){
        this.creator = creator;
    }
    /** 发起时间 */
    public Date getCreateTime(){
        return this.createTime;
    }
    /** 发起时间 */
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    /** 完成时间 */
    public Date getFinishTime(){
        return this.finishTime;
    }
    /** 完成时间 */
    public void setFinishTime(Date finishTime){
        this.finishTime = finishTime;
    }
    /** 流程参数 */
    public String getProcessParams(){
        return this.processParams;
    }
    /** 流程参数 */
    public void setProcessParams(String processParams){
        this.processParams = processParams;
    }
    /** 流程状态;0-新建;1-执行中;2-已终止;3-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 流程状态;0-新建;1-执行中;2-已终止;3-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }
}
