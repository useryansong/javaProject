package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@TableName("wms_original_material_receive")
@KeySequence("wms_original_material_receive")
public class MaterialReceiveEO extends AbstractAuditableEntity<MaterialReceiveEO> {
    private static final long serialVersionUID = -5758337540202027663L;

    @TableId(type = IdType.INPUT)
    private Long originalMaterialId;/** 主键 */

    private Long stampingMaterialConsumptionQuotaId;/** 冲压表主键ID */

    private Long stampingMaterialId;/** 冲压件的物料ID */

    private String stampingMaterialName;/** 冲压件的物料名称*/

    private String stampingElementNo;/** 冲压件的物料零件号*/

    private String inventoryCoding;/** 冲压件的物料存货编码*/

    private Long materialId;/** 原材料的物料ID */

    private String materialName;/** 原材料的物料名称*/

    private String elementNo;/** 原材料的物料零件号*/

    private String inventoryCode;/** 原材料的物料存货编码*/

    private Long deliveryDetailId;/** 出库单明细ID */

    private Double deliveryAmount;/** 数量 */

    private Date deliveryDate;/** 领料时间 */

    private String remarks;/** 备注 */


    @Override
    public Serializable getId() {
        return this.originalMaterialId;
    }

    /** 主键 */
    public Long getOriginalMaterialId(){
        return this.originalMaterialId;
    }
    /** 主键 */
    public void setOriginalMaterialId(Long originalMaterialId){
        this.originalMaterialId = originalMaterialId;
    }

    /** 原材料的物料ID */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 原材料的物料ID */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 出库单明细ID */
    public Long getDeliveryDetailId(){
        return this.deliveryDetailId;
    }
    /** 出库单明细ID */
    public void setDeliveryDetailId(Long deliveryDetailId){
        this.deliveryDetailId = deliveryDetailId;
    }
    /** 数量 */
    public Double getDeliveryAmount(){
        return this.deliveryAmount;
    }
    /** 数量 */
    public void setDeliveryAmount(Double deliveryAmount){
        this.deliveryAmount = deliveryAmount;
    }
    /** 领料时间 */
    public Date getDeliveryDate(){
        return this.deliveryDate;
    }
    /** 领料时间 */
    public void setDeliveryDate(Date deliveryDate){
        this.deliveryDate = deliveryDate;
    }
    /** 备注 */
    public String getRemarks(){
        return this.remarks;
    }
    /** 备注 */
    public void setRemarks(String remarks){
        this.remarks = remarks;
    }

    public Long getStampingMaterialId() {
        return stampingMaterialId;
    }

    public void setStampingMaterialId(Long stampingMaterialId) {
        this.stampingMaterialId = stampingMaterialId;
    }

    public String getStampingMaterialName() {
        return stampingMaterialName;
    }

    public void setStampingMaterialName(String stampingMaterialName) {
        this.stampingMaterialName = stampingMaterialName;
    }

    public String getStampingElementNo() {
        return stampingElementNo;
    }

    public void setStampingElementNo(String stampingElementNo) {
        this.stampingElementNo = stampingElementNo;
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

    public String getInventoryCoding() {
        return inventoryCoding;
    }

    public void setInventoryCoding(String inventoryCoding) {
        this.inventoryCoding = inventoryCoding;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public Long getStampingMaterialConsumptionQuotaId() {
        return stampingMaterialConsumptionQuotaId;
    }

    public void setStampingMaterialConsumptionQuotaId(Long stampingMaterialConsumptionQuotaId) {
        this.stampingMaterialConsumptionQuotaId = stampingMaterialConsumptionQuotaId;
    }
}
