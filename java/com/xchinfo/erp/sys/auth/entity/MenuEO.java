package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.yecat.mybatis.entity.AuditableTreeNode;

import java.io.Serializable;

/**
 * 系统菜单对象
 *
 * @author roman.li
 * @project wms-sys-api
 * @date 2018/5/11 12:47
 * @update
 */
@TableName("sys_menu")
@KeySequence("sys_menu")
public class MenuEO extends AuditableTreeNode<MenuEO> implements Serializable {

    private static final long serialVersionUID = -1231891541100580438L;

    @TableId(type = IdType.INPUT)
    private Long menuId;

    private Long parentId;

    @BusinessLogField("菜单编码")
    private String code;

    @BusinessLogField("菜单名称")
    private String name;

    @TableField(exist = false)
    @BusinessLogField("上级菜单")
    private String parentName;// 父菜单名称

    @BusinessLogField("URL")
    private String url;

    @BusinessLogField("类型")
    private Integer type;// 类型：0：菜单   1：按钮

    @BusinessLogField("授权")
    private String permissions; //授权(多个用逗号分隔，如：user:list,user:create)

    private String icon;

    private String sort;

    private Integer status;/** 状态;0-禁用;1-显示;2-不显示 */

    @TableField(exist = false)
    private boolean checked;/** 树形结构：复选框是否被选中*/


    @TableField(exist = false)
    private String text;

    public Long getMenuId() {
        return menuId;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getParentName() {
        return parentName;
    }

    public String getUrl() {
        return url;
    }

    public Integer getType() {
        return type;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getIcon() {
        return icon;
    }

    public String getSort() {
        return sort;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public Long getId() {
        return this.menuId;
    }

    @Override
    public Long getPid() {
        return this.parentId;
    }

    public String getText() {
        return this.name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
