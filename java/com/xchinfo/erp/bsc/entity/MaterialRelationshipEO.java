package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.c
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_material_relationship")
@KeySequence("bsc_material_relationship")
public class MaterialRelationshipEO extends AbstractAuditableEntity<MaterialRelationshipEO> {

    private static final long serialVersionUID = -5467582640053682987L;

    @TableId(type = IdType.INPUT)
    private Long parentMaterialId;/** 父物料 */

    @TableId(type = IdType.INPUT)
    private Long childMaterialId;/** 子物料 */

    @TableField(exist = false)
    private String parentMaterialCode;/** 父物料编码 */

    @TableField(exist = false)
    private String parentMaterialName;/** 父物料名称 */

    @TableField(exist = false)
    private String parentMaterialElementNo;/** 父零件号 */

    @TableField(exist = false)
    private String parentMaterialSpecification;/** 父物料规格型号 */

    @TableField(exist = false)
    private String childMaterialCode;/** 子物料编码 */

    @TableField(exist = false)
    private String childMaterialName;/** 子物料名称 */

    @TableField(exist = false)
    private String childMaterialElementNo;/** 子零件号 */

    @TableField(exist = false)
    private String childMaterialSpecification;/** 子物料规格型号 */



    @Override
    public Serializable getId() {
        return this.parentMaterialId;
    }

    /** 父物料 */
    public Long getParentMaterialId(){
        return this.parentMaterialId;
    }
    /** 父物料 */
    public void setParentMaterialId(Long parentMaterialId){
        this.parentMaterialId = parentMaterialId;
    }
    /** 子物料 */
    public Long getChildMaterialId(){
        return this.childMaterialId;
    }
    /** 子物料 */
    public void setChildMaterialId(Long childMaterialId){
        this.childMaterialId = childMaterialId;
    }

    public String getParentMaterialCode() {
        return parentMaterialCode;
    }

    public void setParentMaterialCode(String parentMaterialCode) {
        this.parentMaterialCode = parentMaterialCode;
    }

    public String getParentMaterialName() {
        return parentMaterialName;
    }

    public void setParentMaterialName(String parentMaterialName) {
        this.parentMaterialName = parentMaterialName;
    }

    public String getParentMaterialElementNo() {
        return parentMaterialElementNo;
    }

    public void setParentMaterialElementNo(String parentMaterialElementNo) {
        this.parentMaterialElementNo = parentMaterialElementNo;
    }

    public String getParentMaterialSpecification() {
        return parentMaterialSpecification;
    }

    public void setParentMaterialSpecification(String parentMaterialSpecification) {
        this.parentMaterialSpecification = parentMaterialSpecification;
    }

    public String getChildMaterialCode() {
        return childMaterialCode;
    }

    public void setChildMaterialCode(String childMaterialCode) {
        this.childMaterialCode = childMaterialCode;
    }

    public String getChildMaterialName() {
        return childMaterialName;
    }

    public void setChildMaterialName(String childMaterialName) {
        this.childMaterialName = childMaterialName;
    }

    public String getChildMaterialElementNo() {
        return childMaterialElementNo;
    }

    public void setChildMaterialElementNo(String childMaterialElementNo) {
        this.childMaterialElementNo = childMaterialElementNo;
    }

    public String getChildMaterialSpecification() {
        return childMaterialSpecification;
    }

    public void setChildMaterialSpecification(String childMaterialSpecification) {
        this.childMaterialSpecification = childMaterialSpecification;
    }
}
