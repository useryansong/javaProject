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
@TableName("hr_emp_training")
@KeySequence("hr_emp_training")
public class EmployeeTrainingEO extends AbstractAuditableEntity<EmployeeTrainingEO> {
    private static final long serialVersionUID = 1122803679865379798L;

    @TableId(type = IdType.INPUT)
    private Long empTrainingId;/** 培训经历ID */

    private Long employeeId;/** 员工ID */

    @NotNull(message = "开始时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;/** 开始时间 */

    @NotNull(message = "开始时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;/** 结束时间 */

    @NotBlank(message = "培训课程不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "培训课程不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String trainingCourse;/** 培训课程 */

    @Length(max = 1024, message = "培训内容不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String trainingContent;/** 培训内容 */

    @NotBlank(message = "培训结果不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "培训结果不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String trainingResult;/** 培训结果 */

    @Length(max = 100, message = "培训费用不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String trainingCost;/** 培训费用 */

    public Long getEmpTrainingId() {
        return empTrainingId;
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

    public String getTrainingCourse() {
        return trainingCourse;
    }

    public String getTrainingContent() {
        return trainingContent;
    }

    public String getTrainingResult() {
        return trainingResult;
    }

    public String getTrainingCost() {
        return trainingCost;
    }

    public void setEmpTrainingId(Long empTrainingId) {
        this.empTrainingId = empTrainingId;
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

    public void setTrainingCourse(String trainingCourse) {
        this.trainingCourse = trainingCourse;
    }

    public void setTrainingContent(String trainingContent) {
        this.trainingContent = trainingContent;
    }

    public void setTrainingResult(String trainingResult) {
        this.trainingResult = trainingResult;
    }

    public void setTrainingCost(String trainingCost) {
        this.trainingCost = trainingCost;
    }

    @Override
    public Serializable getId() {
        return this.empTrainingId;
    }
}
