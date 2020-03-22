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
@TableName("wf_process_definition")
@KeySequence("wf_process_definition")
public class ProcessDefinitionEO extends AbstractAuditableEntity<ProcessDefinitionEO> {
    private static final long serialVersionUID = 5596398668720342742L;

    @TableId(type = IdType.INPUT)
    private Long processId;/** 流程ID */

    private String processKey;/** 流程标识 */

    private String processName;/** 流程名称 */

    private Integer status;/** 流程是否可用;0-禁用;1-可用 */

    private String processVersion;/** 流程版本 */

    @Override
    public Serializable getId() {
        return this.processId;
    }

    /** 流程ID */
    public Long getProcessId(){
        return this.processId;
    }
    /** 流程ID */
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
    /** 流程名称 */
    public String getProcessName(){
        return this.processName;
    }
    /** 流程名称 */
    public void setProcessName(String processName){
        this.processName = processName;
    }
    /** 流程是否可用;0-禁用;1-可用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 流程是否可用;0-禁用;1-可用 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 流程版本 */
    public String getProcessVersion(){
        return this.processVersion;
    }
    /** 流程版本 */
    public void setProcessVersion(String processVersion){
        this.processVersion = processVersion;
    }
}
