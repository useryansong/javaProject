package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@TableName("mes_working_group")
@KeySequence("mes_working_group")
public class WorkingGroupEO extends AbstractAuditableEntity<WorkingGroupEO> {
    private static final long serialVersionUID = -2572425635815688266L;
    @TableId(type = IdType.AUTO)
    private Long id;/** ID */

    @TableField(exist = false)
    private String orgName;/** 所属部门（车间） */

    private Long orgId;/** 所属部门（车间）id */

    @NotNull(message = "班组名称！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "班组名称长度不能超过 200 个字符")
    private String workingGroupName;/** 班组名称 */

    @NotNull(message = "上班时间不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 20, message = "上班时间长度不能超过 20 个字符")
    private String workingHours;/** 上班时间 */

    @NotNull(message = "下班时间不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 20, message = "下班时间长度不能超过 20 个字符")
    private String quittingHours;/** 下班时间 */

    private Long status; /**  状态;0-启用;1-停用 */

    @Override
    public Serializable getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getWorkingGroupName() {
        return workingGroupName;
    }

    public void setWorkingGroupName(String workingGroupName) {
        this.workingGroupName = workingGroupName;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getQuittingHours() {
        return quittingHours;
    }

    public void setQuittingHours(String quittingHours) {
        this.quittingHours = quittingHours;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
