package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_process_material")
@KeySequence("bsc_process_material")
public class ProcessMaterialEO extends AbstractAuditableEntity<ProcessMaterialEO> {
    private static final long serialVersionUID = -6754041214826044165L;

    @TableId(type = IdType.INPUT)
    private Long processMaterialId;/** 主键 */

    private Long processId;/** 工序 */

    private Long materialId;/** 物料 */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String materialName;/** 物料名称 */

    private Long measurementUnit;/** 计量单位 */

    private Double amount;/** 数量 */

    @Override
    public Serializable getId() {
        return this.processMaterialId;
    }

    /** 主键 */
    public Long getProcessMaterialId(){
        return this.processMaterialId;
    }
    /** 主键 */
    public void setProcessMaterialId(Long processMaterialId){
        this.processMaterialId = processMaterialId;
    }
    /** 工序 */
    public Long getProcessId(){
        return this.processId;
    }
    /** 工序 */
    public void setProcessId(Long processId){
        this.processId = processId;
    }
    /** 物料 */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料 */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 计量单位 */
    public Long getMeasurementUnit(){
        return this.measurementUnit;
    }
    /** 计量单位 */
    public void setMeasurementUnit(Long measurementUnit){
        this.measurementUnit = measurementUnit;
    }
    /** 数量 */
    public Double getAmount(){
        return this.amount;
    }
    /** 数量 */
    public void setAmount(Double amount){
        this.amount = amount;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }
}
