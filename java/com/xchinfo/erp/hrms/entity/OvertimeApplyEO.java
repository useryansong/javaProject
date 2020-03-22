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
@TableName("hr_overtime_apply")
@KeySequence("hr_overtime_apply")
public class OvertimeApplyEO extends AbstractAuditableEntity<OvertimeApplyEO> {
    private static final long serialVersionUID = 2616476634419072495L;

    @TableId(type = IdType.INPUT)
    private Long overtimeApplyId;/** 加班申请ID */

    private Long employeeId;/** 员工ID */

    @TableField(exist = false)
    @NotBlank(message = "员工不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("员工")
    private String employeeName;/** 姓名 */

    @NotNull(message = "加班日期不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("加班日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date overtimeDate;/** 加班日期 */

    @NotNull(message = "开始时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("开始时间")
    private Date startTime;/** 开始时间 */

    @NotNull(message = "结束时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("结束时间")
    private Date endTime;/** 结束时间 */

    @NotNull(message = "加班时长不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("加班时长")
    private Integer overtimeTimes;/** 加班时长 */

    @NotBlank(message = "加班事由不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 1024, message = "加班事由不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("加班事由")
    private String overtimeReason;/** 加班事由 */

    @BusinessLogField("状态")
    private Integer status;/** 状态;1-新建；2-审批中；3-审批通过；4-审批退回 */

    public Long getOvertimeApplyId() {
        return overtimeApplyId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Date getOvertimeDate() {
        return overtimeDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Integer getOvertimeTimes() {
        return overtimeTimes;
    }

    public String getOvertimeReason() {
        return overtimeReason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setOvertimeApplyId(Long overtimeApplyId) {
        this.overtimeApplyId = overtimeApplyId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setOvertimeDate(Date overtimeDate) {
        this.overtimeDate = overtimeDate;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setOvertimeTimes(Integer overtimeTimes) {
        this.overtimeTimes = overtimeTimes;
    }

    public void setOvertimeReason(String overtimeReason) {
        this.overtimeReason = overtimeReason;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public Serializable getId() {
        return this.overtimeApplyId;
    }
}
