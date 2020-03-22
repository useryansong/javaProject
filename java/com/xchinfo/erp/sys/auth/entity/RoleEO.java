package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
import java.util.List;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@TableName("sys_role")
@KeySequence("sys_role")
public class RoleEO extends AbstractAuditableEntity<RoleEO> {

    private static final long serialVersionUID = 8493121167568686952L;

    @TableId(type = IdType.INPUT)
    private Long roleId;

    @NotBlank(message = "角色标识不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 100, message = "角色标识长度不能超过 100 个字符")
    @BusinessLogField("角色编码")
    private String roleCode;// 角色标识
    
    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("角色名称")
    private String roleName;

    @Length(max = 500, message = "描述长度不能超过 500 个字符")
    private String description;

    @NotNull(message = "类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Integer type;// 角色类型：1-功能角色；2-工作流角色

//    @NotNull(message = "机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("机构")
    private Long orgId;// 机构

    @BusinessLogField("状态")
    private Integer status;//状态：0-无效；1-有效

    @TableField(exist = false)
    @BusinessLogField("菜单权限")
    private List<Long> menuIds;

    @TableField(exist = false)
    @BusinessLogField("机构权限")
    private List<Long> orgIds;

    @Override
    protected Serializable pkVal() {
        return this.roleId;
    }

    @Override
    public Serializable getId() {
        return this.roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getType() {
        return type;
    }

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public List<Long> getOrgIds() {
        return orgIds;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }

    public void setOrgIds(List<Long> orgIds) {
        this.orgIds = orgIds;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
