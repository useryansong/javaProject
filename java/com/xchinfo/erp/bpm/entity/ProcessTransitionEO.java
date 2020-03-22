package com.xchinfo.erp.bpm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/19
 * @update
 */
@TableName("wf_process_transition")
@KeySequence("wf_process_transition")
public class ProcessTransitionEO extends AbstractEntity<ProcessTransitionEO> {
    private static final long serialVersionUID = -8611176553289702228L;

    @TableId(type = IdType.INPUT)
    private Long transitionId;/** 路径ID */

    private Long fromNode;/** 源节点 */

    private Long toNode;/** 目标节点 */

    private String transitionName;/** 显示名称 */

    private String expr;/** 表达式;路由节点使用 */

    @Override
    public Serializable getId() {
        return this.transitionId;
    }

    /** 路径ID */
    public Long getTransitionId(){
        return this.transitionId;
    }
    /** 路径ID */
    public void setTransitionId(Long transitionId){
        this.transitionId = transitionId;
    }
    /** 源节点 */
    public Long getFromNode(){
        return this.fromNode;
    }
    /** 源节点 */
    public void setFromNode(Long fromNode){
        this.fromNode = fromNode;
    }
    /** 目标节点 */
    public Long getToNode(){
        return this.toNode;
    }
    /** 目标节点 */
    public void setToNode(Long toNode){
        this.toNode = toNode;
    }
    /** 显示名称 */
    public String getTransitionName(){
        return this.transitionName;
    }
    /** 显示名称 */
    public void setTransitionName(String transitionName){
        this.transitionName = transitionName;
    }
    /** 表达式;路由节点使用 */
    public String getExpr(){
        return this.expr;
    }
    /** 表达式;路由节点使用 */
    public void setExpr(String expr){
        this.expr = expr;
    }
}
