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

@TableName("wms_linedge_inventory_detail")
@KeySequence("wms_linedge_inventory_detail")
public class LinedgeInventoryDetailEO extends AbstractAuditableEntity<LinedgeInventoryDetailEO> {
    private static final long serialVersionUID = -7004360620895347674L;

    @TableId(type = IdType.INPUT)
    private Long inventoryLinedgeDetailId;/** 线边盘点明细ID */

    private Long linedgeInventoryId;/** 线边盘点ID */

    @JsonFormat(pattern = "yyyy-MM")
    private String inventoryMonth;/** 盘点月份 */

    private Long orgId;/** 归属机构 */

    private Long materialId;/** 物料 */

    private String elementNo;/** 零件号 */

    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private Long warehouseId;/** 仓库ID */

    private String warehouseBarCode;/** 仓库条码 */

    private Long warehouseLocationId;/** 库位 */

    private String locationBarCode;/** 库位条码 */

    private Double amount;/** 数量 */

    private Integer status;/** 状态;0-新建;1-进行中;2-已完成 */

    private String remark;/** 备注 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

    private String inventoryNo;/** 盘点卡号 */


    @Override
    public Serializable getId() {
        return inventoryLinedgeDetailId;
    }

    /** 线边盘点明细ID */
    public Long getInventoryLinedgeDetailId(){
        return this.inventoryLinedgeDetailId;
    }
    /** 线边盘点明细ID */
    public void setInventoryLinedgeDetailId(Long inventoryLinedgeDetailId){
        this.inventoryLinedgeDetailId = inventoryLinedgeDetailId;
    }
    /** 线边盘点ID */
    public Long getLinedgeInventoryId(){
        return this.linedgeInventoryId;
    }
    /** 线边盘点ID */
    public void setLinedgeInventoryId(Long linedgeInventoryId){
        this.linedgeInventoryId = linedgeInventoryId;
    }
    /** 盘点月份 */
    public String getInventoryMonth(){
        return this.inventoryMonth;
    }
    /** 盘点月份 */
    public void setInventoryMonth(String inventoryMonth){
        this.inventoryMonth = inventoryMonth;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 物料 */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料 */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 零件号 */
    public String getElementNo(){
        return this.elementNo;
    }
    /** 零件号 */
    public void setElementNo(String elementNo){
        this.elementNo = elementNo;
    }
    /** 物料名称 */
    public String getMaterialName(){
        return this.materialName;
    }
    /** 物料名称 */
    public void setMaterialName(String materialName){
        this.materialName = materialName;
    }
    /** 存货编码 */
    public String getInventoryCode(){
        return this.inventoryCode;
    }
    /** 存货编码 */
    public void setInventoryCode(String inventoryCode){
        this.inventoryCode = inventoryCode;
    }
    /** 仓库ID */
    public Long getWarehouseId(){
        return this.warehouseId;
    }
    /** 仓库ID */
    public void setWarehouseId(Long warehouseId){
        this.warehouseId = warehouseId;
    }
    /** 仓库条码 */
    public String getWarehouseBarCode(){
        return this.warehouseBarCode;
    }
    /** 仓库条码 */
    public void setWarehouseBarCode(String warehouseBarCode){
        this.warehouseBarCode = warehouseBarCode;
    }
    /** 库位 */
    public Long getWarehouseLocationId(){
        return this.warehouseLocationId;
    }
    /** 库位 */
    public void setWarehouseLocationId(Long warehouseLocationId){
        this.warehouseLocationId = warehouseLocationId;
    }
    /** 库位条码 */
    public String getLocationBarCode(){
        return this.locationBarCode;
    }
    /** 库位条码 */
    public void setLocationBarCode(String locationBarCode){
        this.locationBarCode = locationBarCode;
    }
    /** 数量 */
    public Double getAmount(){
        return this.amount;
    }
    /** 数量 */
    public void setAmount(Double amount){
        this.amount = amount;
    }
    /** 状态;0-新建;1-进行中;2-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-进行中;2-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 备注 */
    public String getRemark(){
        return this.remark;
    }
    /** 备注 */
    public void setRemark(String remark){
        this.remark = remark;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getInventoryNo() {
        return inventoryNo;
    }

    public void setInventoryNo(String inventoryNo) {
        this.inventoryNo = inventoryNo;
    }
}
