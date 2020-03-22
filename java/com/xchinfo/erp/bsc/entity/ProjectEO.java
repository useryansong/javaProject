package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

@TableName("bsc_project")
@KeySequence("bsc_project")
public class ProjectEO extends AbstractAuditableEntity<ProjectEO> {

    private static final long serialVersionUID = 8424785367484669525L;

    @TableId(type = IdType.INPUT)
    private Long projectId;/** 项目id */

    private Long orgId;/** 所属机构 */

    @TableField(exist = false)
    private String orgName;/** 组织机构名称 */

    private String projectType;/** 项目大类 */

    private String projectCode;/** 项目编号 */

    private String projectName;/** 项目名称 */

    private Integer isJs;/** 是否结算;0,未结算，1，已结算 */

    private String projectTypeCode;/** 所属分类码 */

    private String projectTypeName;/** 所属分类名称 */

    private Long status; /**  状态;0-启用;1-停用 */

    @Override
    public Serializable getId() {
        return this.projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getIsJs() {
        return isJs;
    }

    public void setIsJs(Integer isJs) {
        this.isJs = isJs;
    }

    public String getProjectTypeCode() {
        return projectTypeCode;
    }

    public void setProjectTypeCode(String projectTypeCode) {
        this.projectTypeCode = projectTypeCode;
    }

    public String getProjectTypeName() {
        return projectTypeName;
    }

    public void setProjectTypeName(String projectTypeName) {
        this.projectTypeName = projectTypeName;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
