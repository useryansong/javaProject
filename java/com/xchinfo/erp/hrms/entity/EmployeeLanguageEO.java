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
@TableName("hr_emp_language")
@KeySequence("hr_emp_language")
public class EmployeeLanguageEO extends AbstractAuditableEntity<EmployeeLanguageEO> {
    private static final long serialVersionUID = -651500457778048515L;

    @TableId(type = IdType.INPUT)
    private Long empLanguageId;/** 语言ID */

    private Long employeeId;/** 员工ID */

    @NotBlank(message = "语言不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "语言不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String language;/** 语言 */

    @Length(max = 200, message = "语言不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String level;/** 等级 */

    private String familiarity;/** 熟悉程度 */

    @Length(max = 1024, message = "证书不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String certificate;/** 证书 */

    public Long getEmpLanguageId() {
        return empLanguageId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getLanguage() {
        return language;
    }

    public String getLevel() {
        return level;
    }

    public String getFamiliarity() {
        return familiarity;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setEmpLanguageId(Long empLanguageId) {
        this.empLanguageId = empLanguageId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setFamiliarity(String familiarity) {
        this.familiarity = familiarity;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @Override
    public Serializable getId() {
        return this.empLanguageId;
    }
}
