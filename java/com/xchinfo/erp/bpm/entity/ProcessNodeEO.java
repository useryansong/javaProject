package com.xchinfo.erp.bpm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/19
 * @update
 */
@TableName("wf_process_node")
@KeySequence("wf_process_node")
public class ProcessNodeEO extends AbstractAuditableEntity<ProcessNodeEO> {
    private static final long serialVersionUID = -1515825292363684703L;

    @TableId(type = IdType.INPUT)
    private Long nodeId;/** 节点ID */

    private Long processId;/** 流程ID */

    private Integer nodeType;/** 节点类型;1-开始；2-结束；3-任务节点；4-路由；5-子流程 */

    private String nodeKey;/** 节点标识 */

    private String nodeName;/** 显示名称 */

    private Integer performType;/** 参与类型;1-任何一个参与者处理完即执行下一步，2-所有参与者都完成才可执行下一步 */

    private Integer expireTime;/** 期望完成时间 */

    private Integer reminderTime;/** 提醒时间 */

    private Integer reminderRepeat;/** 提醒间隔 */

    private Integer autoExecute;/** 是否自动执行 */

    private String callback;/** 回调类 */

    private Integer actorType;/** 参与者类型:1-启动者;2-组;3-角色;4-自然人 */

    private String actor;/** 参与者 */

    private String actionUrl;/** 操作URL */

    private String subProcessKey;/** 子流程名称 */

    private String subProcessVersion;/** 子流程版本号 */

    @Override
    public Serializable getId() {
        return this.nodeId;
    }

    /** 节点ID */
    public Long getNodeId(){
        return this.nodeId;
    }
    /** 节点ID */
    public void setNodeId(Long nodeId){
        this.nodeId = nodeId;
    }
    /** 流程ID */
    public Long getProcessId(){
        return this.processId;
    }
    /** 流程ID */
    public void setProcessId(Long processId){
        this.processId = processId;
    }
    /** 节点类型;1-开始；2-结束；3-任务节点；4-路由；5-子流程 */
    public Integer getNodeType(){
        return this.nodeType;
    }
    /** 节点类型;1-开始；2-结束；3-任务节点；4-路由；5-子流程 */
    public void setNodeType(Integer nodeType){
        this.nodeType = nodeType;
    }
    /** 节点标识;通用属性 */
    public String getNodeKey(){
        return this.nodeKey;
    }
    /** 节点标识;通用属性 */
    public void setNodeKey(String nodeKey){
        this.nodeKey = nodeKey;
    }
    /** 显示名称;通用属性 */
    public String getNodeName(){
        return this.nodeName;
    }
    /** 显示名称;通用属性 */
    public void setNodeName(String nodeName){
        this.nodeName = nodeName;
    }
    /** 参与类型;任务使用:1-任何一个参与者处理完即执行下一步，2-所有参与者都完成才可执行下一步 */
    public Integer getPerformType(){
        return this.performType;
    }
    /** 参与类型;任务使用:1-任何一个参与者处理完即执行下一步，2-所有参与者都完成才可执行下一步 */
    public void setPerformType(Integer performType){
        this.performType = performType;
    }
    /** 期望完成时间;任务使用 */
    public Integer getExpireTime(){
        return this.expireTime;
    }
    /** 期望完成时间;任务使用 */
    public void setExpireTime(Integer expireTime){
        this.expireTime = expireTime;
    }
    /** 提醒时间;任务使用 */
    public Integer getReminderTime(){
        return this.reminderTime;
    }
    /** 提醒时间;任务使用 */
    public void setReminderTime(Integer reminderTime){
        this.reminderTime = reminderTime;
    }
    /** 提醒间隔;任务使用 */
    public Integer getReminderRepeat(){
        return this.reminderRepeat;
    }
    /** 提醒间隔;任务使用 */
    public void setReminderRepeat(Integer reminderRepeat){
        this.reminderRepeat = reminderRepeat;
    }
    /** 是否自动执行;任务使用 */
    public Integer getAutoExecute(){
        return this.autoExecute;
    }
    /** 是否自动执行;任务使用 */
    public void setAutoExecute(Integer autoExecute){
        this.autoExecute = autoExecute;
    }
    /** 回调类;任务使用 */
    public String getCallback(){
        return this.callback;
    }
    /** 回调类;任务使用 */
    public void setCallback(String callback){
        this.callback = callback;
    }
    /** 参与者类型;任务使用 */
    public Integer getActorType(){
        return this.actorType;
    }
    /** 参与者类型;任务使用 */
    public void setActorType(Integer actorType){
        this.actorType = actorType;
    }
    /** 参与者;任务使用 */
    public String getActor(){
        return this.actor;
    }
    /** 参与者;任务使用 */
    public void setActor(String actor){
        this.actor = actor;
    }
    /** 操作URL */
    public String getActionUrl(){
        return this.actionUrl;
    }
    /** 操作URL */
    public void setActionUrl(String actionUrl){
        this.actionUrl = actionUrl;
    }
    /** 子流程名称;子流程使用 */
    public String getSubProcessKey(){
        return this.subProcessKey;
    }
    /** 子流程名称;子流程使用 */
    public void setSubProcessKey(String subProcessKey){
        this.subProcessKey = subProcessKey;
    }
    /** 子流程版本号;子流程使用 */
    public String getSubProcessVersion(){
        return this.subProcessVersion;
    }
    /** 子流程版本号;子流程使用 */
    public void setSubProcessVersion(String subProcessVersion){
        this.subProcessVersion = subProcessVersion;
    }
}
