package com.xchinfo.erp.hrms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@TableName("hr_emp_skill")
@KeySequence("hr_emp_skill")
public class EmployeeSkillEO extends AbstractAuditableEntity<EmployeeSkillEO> {
    private static final long serialVersionUID = -3898067501935096176L;

    @TableId(type = IdType.INPUT)
    private Long empSkillId;/** 技能ID */

    private Long employeeId;/** 员工ID */

    @NotBlank(message = "技能名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "技能名称不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String skillName;/** 技能名称 */

    @Length(max = 1024, message = "技能描述不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String skillDesc;/** 技能描述 */

    public Long getEmpSkillId() {
        return empSkillId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getSkillDesc() {
        return skillDesc;
    }

    public void setEmpSkillId(Long empSkillId) {
        this.empSkillId = empSkillId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setSkillDesc(String skillDesc) {
        this.skillDesc = skillDesc;
    }

    @Override
    public Serializable getId() {
        return this.empSkillId;
    }
}
