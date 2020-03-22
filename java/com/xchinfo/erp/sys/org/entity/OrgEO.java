package com.xchinfo.erp.sys.org.entity;

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
import org.yecat.mybatis.entity.AuditableTreeNode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@TableName("sys_org")
@KeySequence("sys_org")
public class OrgEO extends AuditableTreeNode<OrgEO> {
    private static final long serialVersionUID = 1769819518400037630L;

    @TableId(type = IdType.INPUT)
    private Long orgId;

    private String orgCode;

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("机构名称")
    private String orgName;

    private String fullName;/** 组织全名 */

    @Length(max = 1000, message = "描述长度不能超过 1000 个字符")
    private String description;

    private Integer orgType;// 机构类型：1-实体机构；2-虚拟机构

    private Long parentOrgId;// 上级机构

    @TableField(exist = false)
    private String parentOrgCode;// 上级机构编码

    @TableField(exist = false)
    @BusinessLogField("上级机构")
    private String parentOrgName;// 上级机构名称

    @BusinessLogField("机构状态")
    private Integer status;// 机构类型：0-失效；1-有效

    private String sortNo;

    @Length(max = 1000, message = "地址长度不能超过 1000 个字符")
    private String address;

    @Length(max = 50, message = "邮政编码长度不能超过 50 个字符")
    private String zipCode;

    @Length(max = 200, message = "主管长度不能超过 200 个字符")
    private String master;

    @Length(max = 200, message = "手机号码长度不能超过 200 个字符")
    private String phone;

    @Length(max = 200, message = "电子邮件长度不能超过 200 个字符")
    private String email;

    @TableField("effective_start_date")
    @NotNull(message = "生效时间不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveStartDate;

    @TableField("effective_end_date")
    @NotNull(message = "失效时间不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveEndDate;

    @Length(max = 1000, message = "备注长度不能超过 1000 个字符")
    private String remarks;

    @TableField(exist = false)
    private String text;// 用于下拉框显示用

    private Date startSafeProductionDate;

    public Long getOrgId() {
        return orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public Long getParentOrgId() {
        return parentOrgId;
    }

    public String getParentOrgCode() {
        return parentOrgCode;
    }

    public String getParentOrgName() {
        return parentOrgName;
    }

    public String getSortNo() {
        return sortNo;
    }

    public String getAddress() {
        return address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getMaster() {
        return master;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public void setParentOrgId(Long parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public void setParentOrgCode(String parentOrgCode) {
        this.parentOrgCode = parentOrgCode;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    protected Serializable pkVal() {
        return this.orgId;
    }

    @Override
    public Long getId() {
        return this.orgId;
    }

    @Override
    public Long getPid() {
        return this.parentOrgId;
    }

    public String getText() {
        return this.orgName;
    }

    public void setText(String text) {
        this.text = text;
    }
    /** 组织全名 */
    public String getFullName(){
        return this.fullName;
    }
    /** 组织全名 */
    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public Date getStartSafeProductionDate() {
        return startSafeProductionDate;
    }

    public void setStartSafeProductionDate(Date startSafeProductionDate) {
        this.startSafeProductionDate = startSafeProductionDate;
    }
}
