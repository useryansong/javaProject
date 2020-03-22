package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@TableName("sys_data_auth")
@KeySequence("sys_data_auth")
public class DataAuthEO extends AbstractAuditableEntity<DataAuthEO> {
    private static final long serialVersionUID = 580086247120914852L;

    @TableId(type = IdType.INPUT)
    private Long dataAuthId;

    @NotNull(message = "角色不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Long roleId;

    @NotBlank(message = "授权对象不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 50, message = "授权对象不能超过 50 个字符")
    private String authEntry;

    @NotNull(message = "授权ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Long entryId;

    @Override
    protected Serializable pkVal() {
        return this.dataAuthId;
    }

    @Override
    public Serializable getId() {
        return this.dataAuthId;
    }

    public Long getDataAuthId() {
        return dataAuthId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getAuthEntry() {
        return authEntry;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setDataAuthId(Long dataAuthId) {
        this.dataAuthId = dataAuthId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setAuthEntry(String authEntry) {
        this.authEntry = authEntry;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }
}
