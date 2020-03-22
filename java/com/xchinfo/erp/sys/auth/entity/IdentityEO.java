package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * @date 2017/10/30
 * @update
 */
@TableName("sys_identity")
@KeySequence("sys_identity")
public class IdentityEO extends AbstractAuditableEntity<IdentityEO> {
    private static final long serialVersionUID = -8963270927171631831L;

    @TableId(type = IdType.INPUT)
    private Long identityId;

    @TableField("user_id")
    @NotNull(message = "用户不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Long userId;

    @TableField("identity_name")
    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    private String identityName;

    @TableField("org_id")
    @NotNull(message = "机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Long orgId;

    @TableField(exist = false)
    private String orgName;// 机构名称

    @TableField("effective_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "生效时间不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Date effectiveStartDate;
    @TableField("effective_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "失效时间不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Date effectiveEndDate;

    @Override
    protected Serializable pkVal() {
        return this.identityId;
    }

    @Override
    public Serializable getId() {
        return this.identityId;
    }

    public Long getIdentityId() {
        return identityId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getIdentityName() {
        return identityName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setIdentityId(Long identityId) {
        this.identityId = identityId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
