package com.xchinfo.erp.hrms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@TableName("hr_emp_position")
@KeySequence("hr_emp_position")
public class EmployeePositionEO extends AbstractAuditableEntity<EmployeePositionEO> {
    private static final long serialVersionUID = -5871725388807072211L;

    @TableId(type = IdType.INPUT)
    private Long empPositionId;/** 员工任职ID */

    private Long employeeId;/** 员工ID */

    @NotNull(message = "入学时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;/** 开始时间 */

    @NotNull(message = "入学时间不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;/** 结束时间 */

    private Long orgId;/** 机构ID */

    private String orgName;/** 机构 */

    private Long positionId;/** 岗位ID */

    private String positionName;/** 岗位 */

    private Integer postType;/** 任职类型;1-专职；2-兼职 */

    public Long getEmpPositionId() {
        return empPositionId;
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

    public Long getOrgId() {
        return orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public Long getPositionId() {
        return positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public Integer getPostType() {
        return postType;
    }

    public void setEmpPositionId(Long empPositionId) {
        this.empPositionId = empPositionId;
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

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void setPostType(Integer postType) {
        this.postType = postType;
    }

    @Override
    public Serializable getId() {
        return this.empPositionId;
    }
}
