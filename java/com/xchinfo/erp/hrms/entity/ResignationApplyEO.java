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
@TableName("hr_resignation_apply")
@KeySequence("hr_resignation_apply")
public class ResignationApplyEO extends AbstractAuditableEntity<ResignationApplyEO> {
    private static final long serialVersionUID = 5642898627382321821L;

    @TableId(type = IdType.INPUT)
    private Long resignationApplyId;/** 辞职申请ID */

    private Long employeeId;/** 员工ID */

    @TableField(exist = false)
    @NotBlank(message = "员工不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("员工")
    private String employeeName;/** 姓名 */

    @NotNull(message = "离职日期不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("离职日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date resignDate;/** 离职日期 */

    @NotBlank(message = "离职原因不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 1024, message = "离职原因不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("离职原因")
    private String resignReason;/** 离职原因 */

    @BusinessLogField("状态")
    private Integer status;/** 状态;1-新建；2-审批中；3-审批通过；4-审批退回 */

    public Long getResignationApplyId() {
        return resignationApplyId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Date getResignDate() {
        return resignDate;
    }

    public String getResignReason() {
        return resignReason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setResignationApplyId(Long resignationApplyId) {
        this.resignationApplyId = resignationApplyId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setResignDate(Date resignDate) {
        this.resignDate = resignDate;
    }

    public void setResignReason(String resignReason) {
        this.resignReason = resignReason;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public Serializable getId() {
        return this.resignationApplyId;
    }
}
