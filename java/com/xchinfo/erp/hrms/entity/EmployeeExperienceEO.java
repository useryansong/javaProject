package com.xchinfo.erp.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("hr_emp_experience")
@KeySequence("hr_emp_experience")
public class EmployeeExperienceEO extends AbstractAuditableEntity<EmployeeExperienceEO> {

    private static final long serialVersionUID = 8447040899243281708L;

    @TableId(type = IdType.INPUT)
    private Long empExperienceId;/** 工作经历ID */

    private Long employeeId;/** 员工ID */

    @NotNull(message = "开始时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;/** 开始时间 */

    @NotNull(message = "结束时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;/** 结束时间 */

    @NotBlank(message = "工作单位不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 1024, message = "工作单位不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String company;/** 工作单位 */

    @Length(max = 1024, message = "所在部门不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String dept;/** 所在部门 */

    @Length(max = 1024, message = "工作内容不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String jobContent;/** 工作内容 */

    @Length(max = 1024, message = "离职原因不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String leavingReason;/** 离职原因 */

    public Long getEmpExperienceId() {
        return empExperienceId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getCompany() {
        return company;
    }

    public String getDept() {
        return dept;
    }

    public String getJobContent() {
        return jobContent;
    }

    public String getLeavingReason() {
        return leavingReason;
    }

    public void setEmpExperienceId(Long empExperienceId) {
        this.empExperienceId = empExperienceId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setJobContent(String jobContent) {
        this.jobContent = jobContent;
    }

    public void setLeavingReason(String leavingReason) {
        this.leavingReason = leavingReason;
    }

    @Override
    public Serializable getId() {
        return this.empExperienceId;
    }
}
