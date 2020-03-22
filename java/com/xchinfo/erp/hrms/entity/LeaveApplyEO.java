package com.xchinfo.erp.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.annotation.BusinessLogField;
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
@TableName("hr_leave_apply")
@KeySequence("hr_leave_apply")
public class LeaveApplyEO extends AbstractAuditableEntity<LeaveApplyEO> {
    private static final long serialVersionUID = 1961489531252496960L;

    @TableId(type = IdType.INPUT)
    private Long leaveApplyId;/** 请假ID */

    private Long employeeId;/** 员工ID */

    @TableField(exist = false)
    @NotBlank(message = "员工不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("员工")
    private String employeeName;/** 姓名 */

    @NotNull(message = "开始时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;/** 开始时间 */

    @NotNull(message = "结束时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;/** 结束时间 */

    @NotBlank(message = "请假类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("请假类型")
    private String leaveType;/** 请假类型 */

    @NotNull(message = "请假天数不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("请假天数")
    private Double leaveDays;/** 请假天数 */

    @NotBlank(message = "请假理由不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 1024, message = "请假理由不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("请假理由")
    private String leaveReason;/** 请假理由 */

    @Length(max = 100, message = "紧急联系人不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("紧急联系人")
    private String emergencyContact;/** 紧急联系人 */

    @Length(max = 100, message = "紧急联系电话不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("紧急联系电话")
    private String emergencyPhone;/** 紧急联系电话 */

    @BusinessLogField("状态")
    private Integer status;/** 状态;1-新建；2-审批中；3-审批通过；4-审批退回 */

    public Long getLeaveApplyId() {
        return leaveApplyId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public Double getLeaveDays() {
        return leaveDays;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setLeaveApplyId(Long leaveApplyId) {
        this.leaveApplyId = leaveApplyId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setLeaveDays(Double leaveDays) {
        this.leaveDays = leaveDays;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public Serializable getId() {
        return this.leaveApplyId;
    }
}
