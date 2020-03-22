package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@TableName("bsc_process_constraint")
@KeySequence("bsc_process_constraint")
public class ProcessConstraintEO extends AbstractAuditableEntity<ProcessConstraintEO> {
    private static final long serialVersionUID = -546933431865343225L;

    @TableId(type = IdType.INPUT)
    private Long processConstraintId;/** 主键 */

    private Long processId;/** 工序 */

    private String conditionExpr;/** 约束 */

    @TableField(exist = false)
    private String constraintName;/** 约束名*/

    private Integer duration;/** 持续时间 */

    private Integer score;/** 评分 */

    private Double cost;/** 成本 */

    @Override
    public Serializable getId() {
        return this.processConstraintId;
    }

    /** 主键 */
    public Long getProcessConstraintId(){
        return this.processConstraintId;
    }
    /** 主键 */
    public void setProcessConstraintId(Long processConstraintId){
        this.processConstraintId = processConstraintId;
    }
    /** 工序 */
    public Long getProcessId(){
        return this.processId;
    }
    /** 工序 */
    public void setProcessId(Long processId){
        this.processId = processId;
    }
    /** 约束 */
    public String getConditionExpr(){
        return this.conditionExpr;
    }
    /** 约束 */
    public void setConditionExpr(String conditionExpr){
        this.conditionExpr = conditionExpr;
    }
    /** 持续时间 */
    public Integer getDuration(){
        return this.duration;
    }
    /** 持续时间 */
    public void setDuration(Integer duration){
        this.duration = duration;
    }
    /** 评分 */
    public Integer getScore(){
        return this.score;
    }
    /** 评分 */
    public void setScore(Integer score){
        this.score = score;
    }
    /** 成本 */
    public Double getCost(){
        return this.cost;
    }
    /** 成本 */
    public void setCost(Double cost){
        this.cost = cost;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }
}
