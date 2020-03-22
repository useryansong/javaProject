package com.xchinfo.erp.hrms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@TableName("hr_emp_education")
@KeySequence("hr_emp_education")
public class EmployeeEducationEO extends AbstractAuditableEntity<EmployeeEducationEO> {
    private static final long serialVersionUID = 3323859389122467895L;

    @TableId(type = IdType.INPUT)
    private Long empEducationId;/** 员工教育ID */

    private Long employeeId;/** 员工ID */

    @NotNull(message = "入学时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date admissionDate;/** 入学时间 */

    @NotNull(message = "毕业时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date graduationDate;/** 毕业时间 */

    @NotBlank(message = "毕业院校不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 1024, message = "毕业院校不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String school;/** 毕业院校 */

    @Length(max = 1024, message = "专业不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String specialtySubject;/** 专业 */

    @Length(max = 1024, message = "学历不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String educationalLevel;/** 学历 */

    @Length(max = 1024, message = "学位不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String degree;/** 学位 */

    private boolean isFinallyLevel;/** 是否最高学历 */

    private boolean isFinallyDegree;/** 是否最高学位 */

    public Long getEmpEducationId() {
        return empEducationId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public Date getGraduationDate() {
        return graduationDate;
    }

    public String getSchool() {
        return school;
    }

    public String getSpecialtySubject() {
        return specialtySubject;
    }

    public String getEducationalLevel() {
        return educationalLevel;
    }

    public String getDegree() {
        return degree;
    }

    public boolean getIsFinallyLevel() {
        return isFinallyLevel;
    }

    public boolean getIsFinallyDegree() {
        return isFinallyDegree;
    }

    public void setEmpEducationId(Long empEducationId) {
        this.empEducationId = empEducationId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public void setGraduationDate(Date graduationDate) {
        this.graduationDate = graduationDate;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setSpecialtySubject(String specialtySubject) {
        this.specialtySubject = specialtySubject;
    }

    public void setEducationalLevel(String educationalLevel) {
        this.educationalLevel = educationalLevel;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setIsFinallyLevel(boolean isFinallyLevel) {
        this.isFinallyLevel = isFinallyLevel;
    }

    public void setIsFinallyDegree(boolean isFinallyDegree) {
        this.isFinallyDegree = isFinallyDegree;
    }

    @Override
    public Serializable getId() {
        return this.empEducationId;
    }
}
