package com.xchinfo.erp.bsc.entity;


import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

@TableName("bsc_pbom")
@KeySequence("bsc_pbom")
public class PbomEO extends AbstractAuditableEntity<PbomEO> {

    @TableId(type = IdType.INPUT)
    private Long pbomId;/** 主键ID */

    private Long materialId;/** 主物料ID */

    private String materialName;/** 主物料名称 */

    private String elementNo;/** 主物料零件号 */

    private String projectNo;/** 主物料项目号 */

    private Long childMaterialId;/** 子物料ID */

    private String childMaterialName;/** 子物料名称 */

    private String childElementNo;/** 子物料零件号 */

    private Double amount;/** 耗用量 */

    private Long orgId;/** 归属机构 */

    private Integer status;

    @TableField(exist = false)
    private String orgName;

    @Override
    public Serializable getId() {
        return pbomId;
    }

    public Long getPbomId() {
        return pbomId;
    }

    public void setPbomId(Long pbomId) {
        this.pbomId = pbomId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public Long getChildMaterialId() {
        return childMaterialId;
    }

    public void setChildMaterialId(Long childMaterialId) {
        this.childMaterialId = childMaterialId;
    }

    public String getChildMaterialName() {
        return childMaterialName;
    }

    public void setChildMaterialName(String childMaterialName) {
        this.childMaterialName = childMaterialName;
    }

    public String getChildElementNo() {
        return childElementNo;
    }

    public void setChildElementNo(String childElementNo) {
        this.childElementNo = childElementNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
