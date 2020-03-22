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
@TableName("hr_emp_family")
@KeySequence("hr_emp_family")
public class EmployeeFamilyEO extends AbstractAuditableEntity<EmployeeFamilyEO> {
    private static final long serialVersionUID = 2992946720697557568L;

    @TableId(type = IdType.INPUT)
    private Long empFamilyId;/** 家庭ID */

    private Long employeeId;/** 员工ID */

    @NotBlank(message = "姓名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "姓名不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String name;/** 姓名 */

    private String identityType;/** 证件类型 */

    @Length(max = 200, message = "与本人关系不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String identityId;/** 证件号码 */

    @NotBlank(message = "与本人关系不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "与本人关系不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String relationship;/** 与本人关系 */

    @Length(max = 1024, message = "工作单位不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String company;/** 工作单位 */

    private String politicalType;/** 政治面貌 */

    @Length(max = 200, message = "电话不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String phone;/** 电话 */

    public Long getEmpFamilyId() {
        return empFamilyId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getIdentityType() {
        return identityType;
    }

    public String getIdentityId() {
        return identityId;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getCompany() {
        return company;
    }

    public String getPoliticalType() {
        return politicalType;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmpFamilyId(Long empFamilyId) {
        this.empFamilyId = empFamilyId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPoliticalType(String politicalType) {
        this.politicalType = politicalType;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public Serializable getId() {
        return this.empFamilyId;
    }
}
