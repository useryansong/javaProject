package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@TableName("bsc_constraint")
@KeySequence("bsc_constraint")
public class ConstraintEO extends AbstractAuditableEntity<ConstraintEO> {
    private static final long serialVersionUID = -5945916494120114079L;

    @TableId(type = IdType.INPUT)
    private Long constraintId;/** 主键 */

    private String constraintName;/** 约束名称 */

    private String conditionExpr;/** 约束条件 */

    private Integer status;/** 状态;0-无效;1-有效 */

    @Override
    public Serializable getId() {
        return this.constraintId;
    }

    /** 主键 */
    public Long getConstraintId(){
        return this.constraintId;
    }
    /** 主键 */
    public void setConstraintId(Long constraintId){
        this.constraintId = constraintId;
    }
    /** 约束名称 */
    public String getConstraintName(){
        return this.constraintName;
    }
    /** 约束名称 */
    public void setConstraintName(String constraintName){
        this.constraintName = constraintName;
    }
    /** 约束条件 */
    public String getConditionExpr(){
        return this.conditionExpr;
    }
    /** 约束条件 */
    public void setConditionExpr(String conditionExpr){
        this.conditionExpr = conditionExpr;
    }
    /** 状态;0-无效;1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status){
        this.status = status;
    }
}
