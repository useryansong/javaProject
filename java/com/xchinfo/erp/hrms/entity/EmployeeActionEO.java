package com.xchinfo.erp.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2018/12/11
 * @update
 */
@TableName("hr_emp_action")
@KeySequence("hr_emp_action")
public class EmployeeActionEO extends AbstractAuditableEntity<EmployeeActionEO> {

    private static final long serialVersionUID = -3618511578263142789L;

    @TableId(type = IdType.INPUT)
    private Long actionId;/** 异动ID */

    private Long employeeId;/** 员工ID */

    @TableField(exist = false)
    @NotBlank(message = "员工不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("员工")
    private String employeeName;

    @NotNull(message = "生效日期不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("生效日期")
    private Date effectiveDate;/** 生效日期 */

    private Long sourceOrgId;/** 源机构 */

    @TableField(exist = false)
    @NotBlank(message = "源机构不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("源机构")
    private String sourceOrgName;

    private Long sourcePositionId;/** 源岗位 */

    @TableField(exist = false)
    @NotBlank(message = "源岗位不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("源岗位")
    private String sourcePositionName;

    private Long targetOrgId;/** 目标机构 */

    @TableField(exist = false)
    @NotBlank(message = "目标机构不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("目标机构")
    private String targetOrgName;

    private Long targetPositionId;/** 目标岗位 */

    @TableField(exist = false)
    @NotBlank(message = "目标岗位不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("目标岗位")
    private String targetPositionName;

    @NotNull(message = "任职类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("任职类型")
    private Integer postType;/** 任职类型;1-专职；2-兼职 */

    @BusinessLogField("状态")
    private Integer status;/** 状态;1-新建；2-审批中；3-审批通过；4-审批退回 */

    public Long getActionId() {
        return actionId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public Long getSourceOrgId() {
        return sourceOrgId;
    }

    public String getSourceOrgName() {
        return sourceOrgName;
    }

    public Long getSourcePositionId() {
        return sourcePositionId;
    }

    public String getSourcePositionName() {
        return sourcePositionName;
    }

    public Long getTargetOrgId() {
        return targetOrgId;
    }

    public String getTargetOrgName() {
        return targetOrgName;
    }

    public Long getTargetPositionId() {
        return targetPositionId;
    }

    public String getTargetPositionName() {
        return targetPositionName;
    }

    public Integer getPostType() {
        return postType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setSourceOrgId(Long sourceOrgId) {
        this.sourceOrgId = sourceOrgId;
    }

    public void setSourceOrgName(String sourceOrgName) {
        this.sourceOrgName = sourceOrgName;
    }

    public void setSourcePositionId(Long sourcePositionId) {
        this.sourcePositionId = sourcePositionId;
    }

    public void setSourcePositionName(String sourcePositionName) {
        this.sourcePositionName = sourcePositionName;
    }

    public void setTargetOrgId(Long targetOrgId) {
        this.targetOrgId = targetOrgId;
    }

    public void setTargetOrgName(String targetOrgName) {
        this.targetOrgName = targetOrgName;
    }

    public void setTargetPositionId(Long targetPositionId) {
        this.targetPositionId = targetPositionId;
    }

    public void setTargetPositionName(String targetPositionName) {
        this.targetPositionName = targetPositionName;
    }

    public void setPostType(Integer postType) {
        this.postType = postType;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public Serializable getId() {
        return this.actionId;
    }
}
