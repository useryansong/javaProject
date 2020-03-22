package com.xchinfo.erp.bpm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/22
 * @update
 */
@TableName("wf_his_work_item")
public class HisWorkItemEO extends AbstractAuditableEntity<HisWorkItemEO> {
    private static final long serialVersionUID = 4553029291368263455L;

    @TableId(type = IdType.INPUT)
    private Long workItemId;/** 主键ID */

    private Long processInstanceId;/** 流程实例ID */

    private String processInstanceName;/** 流程名称 */

    private String nodeKey;/** 节点标识;通用属性 */

    private String workItemName;/** 工作项名称 */

    private Integer workItemType;/** 工作项类型;1-主办任务；2-协办任务 */

    private Integer performType;/** 参与类型;1-普通任务；2-参与者会签任务 */

    private String operator;/** 处理人 */

    private Date createTime;/** 任务创建时间 */

    private Date finishTime;/** 任务完成时间 */

    private Date expireTime;/** 任务期望完成时间 */

    private Date remindTime;/** 提醒时间 */

    private String actionUrl;/** 任务处理的url */

    private Long parentId;/** 父任务ID */

    private Integer status;/** 状态;0-新建;1-执行中;2-已终止;3-已完成;4-已退回 */

    @TableField(exist = false)
    private List<String> actors;/** 流程参与者 */

    @Override
    public Serializable getId() {
        return this.workItemId;
    }

    /** 主键ID */
    public Long getWorkItemId(){
        return this.workItemId;
    }
    /** 主键ID */
    public void setWorkItemId(Long workItemId){
        this.workItemId = workItemId;
    }
    /** 流程实例ID */
    public Long getProcessInstanceId(){
        return this.processInstanceId;
    }
    /** 流程实例ID */
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
    /** 节点标识;通用属性 */
    public String getNodeKey(){
        return this.nodeKey;
    }
    /** 节点标识;通用属性 */
    public void setNodeKey(String nodeKey){
        this.nodeKey = nodeKey;
    }
    /** 工作项名称 */
    public String getWorkItemName(){
        return this.workItemName;
    }
    /** 工作项名称 */
    public void setWorkItemName(String workItemName){
        this.workItemName = workItemName;
    }
    /** 工作项类型;1-主办任务；2-协办任务 */
    public Integer getWorkItemType(){
        return this.workItemType;
    }
    /** 工作项类型;1-主办任务；2-协办任务 */
    public void setWorkItemType(Integer workItemType){
        this.workItemType = workItemType;
    }
    /** 参与类型;1-普通任务；2-参与者会签任务 */
    public Integer getPerformType(){
        return this.performType;
    }
    /** 参与类型;1-普通任务；2-参与者会签任务 */
    public void setPerformType(Integer performType){
        this.performType = performType;
    }
    /** 处理人 */
    public String getOperator(){
        return this.operator;
    }
    /** 处理人 */
    public void setOperator(String operator){
        this.operator = operator;
    }
    /** 任务创建时间 */
    public Date getCreateTime(){
        return this.createTime;
    }
    /** 任务创建时间 */
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    /** 任务完成时间 */
    public Date getFinishTime(){
        return this.finishTime;
    }
    /** 任务完成时间 */
    public void setFinishTime(Date finishTime){
        this.finishTime = finishTime;
    }
    /** 任务期望完成时间 */
    public Date getExpireTime(){
        return this.expireTime;
    }
    /** 任务期望完成时间 */
    public void setExpireTime(Date expireTime){
        this.expireTime = expireTime;
    }
    /** 提醒时间 */
    public Date getRemindTime(){
        return this.remindTime;
    }
    /** 提醒时间 */
    public void setRemindTime(Date remindTime){
        this.remindTime = remindTime;
    }
    /** 任务处理的url */
    public String getActionUrl(){
        return this.actionUrl;
    }
    /** 任务处理的url */
    public void setActionUrl(String actionUrl){
        this.actionUrl = actionUrl;
    }
    /** 父任务ID */
    public Long getParentId(){
        return this.parentId;
    }
    /** 父任务ID */
    public void setParentId(Long parentId){
        this.parentId = parentId;
    }
    /** 状态;0-新建;1-执行中;2-已终止;3-已完成;4-已退回 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-执行中;2-已终止;3-已完成;4-已退回 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }
}
