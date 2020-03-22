package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_process_skill")
@KeySequence("bsc_process_skill")
public class ProcessSkillEO extends AbstractAuditableEntity<ProcessSkillEO> {
    private static final long serialVersionUID = 988795494794988143L;

    @TableId(type = IdType.INPUT)
    private Long processSkillId;/** 主键 */

    private Long processId;/** 工序 */

    private String skillType;/** 技能 */

    @TableField(exist = false)
    private String skillCode;/** 技能编码 */

    @TableField(exist = false)
    private String skillName;/** 技能名称 */

    @Override
    public Serializable getId() {
        return this.processSkillId;
    }

    /** 主键 */
    public Long getProcessSkillId(){
        return this.processSkillId;
    }
    /** 主键 */
    public void setProcessSkillId(Long processSkillId){
        this.processSkillId = processSkillId;
    }
    /** 工序 */
    public Long getProcessId(){
        return this.processId;
    }
    /** 工序 */
    public void setProcessId(Long processId){
        this.processId = processId;
    }

    /** 技能 */
    public String getSkillType(){
        return this.skillType;
    }
    /** 技能 */
    public void setSkillType(String skillType){
        this.skillType = skillType;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillCode() {
        return skillCode;
    }

    public void setSkillCode(String skillCode) {
        this.skillCode = skillCode;
    }
}
