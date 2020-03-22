package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/4/24
 * @update
 */
@TableName("sys_data_auth_rule")
@KeySequence("sys_data_auth_rule")
public class DataAuthRuleEO extends AbstractAuditableEntity<DataAuthRuleEO> {
    private static final long serialVersionUID = -712058248486908198L;

    @TableId(type = IdType.INPUT)
    private Long ruleId;/** 主键 */

    private String dataEntry;/** 授权实体 */

    private String ruleName;/** 规则名称 */

    private String ruleExpr;/** 表达式;SQL语句 */

    @Override
    public Serializable getId() {
        return this.ruleId;
    }
    /** 主键 */
    public Long getRuleId(){
        return this.ruleId;
    }
    /** 主键 */
    public void setRuleId(Long ruleId){
        this.ruleId = ruleId;
    }
    /** 授权实体 */
    public String getDataEntry(){
        return this.dataEntry;
    }
    /** 授权实体 */
    public void setDataEntry(String dataEntry){
        this.dataEntry = dataEntry;
    }
    /** 规则名称 */
    public String getRuleName(){
        return this.ruleName;
    }
    /** 规则名称 */
    public void setRuleName(String ruleName){
        this.ruleName = ruleName;
    }
    /** 表达式;SQL语句 */
    public String getRuleExpr(){
        return this.ruleExpr;
    }
    /** 表达式;SQL语句 */
    public void setRuleExpr(String ruleExpr){
        this.ruleExpr = ruleExpr;
    }
}
