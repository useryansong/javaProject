package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_process_tool")
@KeySequence("bsc_process_tool")
public class ProcessToolEO extends AbstractAuditableEntity<ProcessToolEO> {
    private static final long serialVersionUID = -3709859356916394920L;

    @TableId(type = IdType.INPUT)
    private Long processToolId;/** 主键 */

    private Long processId;/** 工序 */

    private Long toolId;/** 工具 */

    private Integer amount;/** 数量 */

    @TableField(exist = false)
    private String toolCode;/** 工具编码 */

    @TableField(exist = false)
    private String toolName;/** 工具名称 */

    @Override
    public Serializable getId() {
        return this.processToolId;
    }

    /** 主键 */
    public Long getProcessToolId(){
        return this.processToolId;
    }
    /** 主键 */
    public void setProcessToolId(Long processToolId){
        this.processToolId = processToolId;
    }
    /** 工序 */
    public Long getProcessId(){
        return this.processId;
    }
    /** 工序 */
    public void setProcessId(Long processId){
        this.processId = processId;
    }
    /** 工具 */
    public Long getToolId(){
        return this.toolId;
    }
    /** 工具 */
    public void setToolId(Long toolId){
        this.toolId = toolId;
    }

    /** 数量 */
    public Integer getAmount(){
        return this.amount;
    }
    /** 数量 */
    public void setAmount(Integer amount){
        this.amount = amount;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getToolCode() {
        return toolCode;
    }

    public void setToolCode(String toolCode) {
        this.toolCode = toolCode;
    }
}
