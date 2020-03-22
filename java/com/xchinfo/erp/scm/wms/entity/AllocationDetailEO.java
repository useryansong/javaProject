package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuanchang
 * @date 2019/5/9
 * @update
 */
@TableName("wms_allocation_detail")
@KeySequence("wms_allocation_detail")
public class AllocationDetailEO extends AbstractAuditableEntity<AllocationDetailEO> {

    private static final long serialVersionUID = 5139731738871738803L;

    @TableId(type = IdType.INPUT)
    private Long allocationDetailId;/** 主键 */

    private Long allocationId;/** 调拨单ID */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    private Date allocationDate;/** 调拨日期 */

    private Long materialId;/** 物料 */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private String elementNo;/** 零件号 */

    private String specification;/** 规格型号 */

    private Long unitId;/** 计量单位 */

    private String figureNumber;/** 图号 */

    private String figureVersion;/** 版本号 */

    private Long fromWarehouseId;/** 调出仓库 */

    @TableField(exist = false)
    private String warehouseName;/** 调出仓库名称 */

    private Long fromWarehouseLocationId;/** 调出库位 */

    @TableField(exist = false)
    private String locationName;/** 调出库位名称 */

    private Long toWarehouseId;/** 调入仓库 */

    @TableField(exist = false)
    private String toWarehouseName;/** 调入仓库名称 */

    private Long toWarehouseLocationId;/** 调入库位 */

    private Double amount;/** 调拨数量 */

    @TableField(exist = false)
    private Double count;/** 库存数量 */

    private Integer status;/** 状态;0-新建;1-发布;2-已完成 */

    private String remarks;/** 备注 */

    @Override
    public Serializable getId() {
        return this.allocationDetailId;
    }


    /** 主键 */
    public Long getAllocationDetailId(){
        return this.allocationDetailId;
    }
    /** 主键 */
    public void setAllocationDetailId(Long allocationDetailId){
        this.allocationDetailId = allocationDetailId;
    }
    /** 调拨单ID */
    public Long getAllocationId(){
        return this.allocationId;
    }
    /** 调拨单ID */
    public void setAllocationId(Long allocationId){
        this.allocationId = allocationId;
    }
    /** 调拨日期 */
    public Date getAllocationDate(){
        return this.allocationDate;
    }
    /** 调拨日期 */
    public void setAllocationDate(Date allocationDate){
        this.allocationDate = allocationDate;
    }
    /** 物料 */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料 */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 物料编码 */
    public String getMaterialCode(){
        return this.materialCode;
    }
    /** 物料编码 */
    public void setMaterialCode(String materialCode){
        this.materialCode = materialCode;
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
    /** 零件号 */
    public String getElementNo(){
        return this.elementNo;
    }
    /** 零件号 */
    public void setElementNo(String elementNo){
        this.elementNo = elementNo;
    }
    /** 规格型号 */
    public String getSpecification(){
        return this.specification;
    }
    /** 规格型号 */
    public void setSpecification(String specification){
        this.specification = specification;
    }
    /** 计量单位 */
    public Long getUnitId(){
        return this.unitId;
    }
    /** 计量单位 */
    public void setUnitId(Long unitId){
        this.unitId = unitId;
    }
    /** 图号 */
    public String getFigureNumber(){
        return this.figureNumber;
    }
    /** 图号 */
    public void setFigureNumber(String figureNumber){
        this.figureNumber = figureNumber;
    }
    /** 版本号 */
    public String getFigureVersion(){
        return this.figureVersion;
    }
    /** 版本号 */
    public void setFigureVersion(String figureVersion){
        this.figureVersion = figureVersion;
    }
    /** 调出仓库 */
    public Long getFromWarehouseId(){
        return this.fromWarehouseId;
    }
    /** 调出仓库 */
    public void setFromWarehouseId(Long fromWarehouseId){
        this.fromWarehouseId = fromWarehouseId;
    }
    /** 调出库位 */
    public Long getFromWarehouseLocationId(){
        return this.fromWarehouseLocationId;
    }
    /** 调出库位 */
    public void setFromWarehouseLocationId(Long fromWarehouseLocationId){
        this.fromWarehouseLocationId = fromWarehouseLocationId;
    }
    /** 调入仓库 */
    public Long getToWarehouseId(){
        return this.toWarehouseId;
    }
    /** 调入仓库 */
    public void setToWarehouseId(Long toWarehouseId){
        this.toWarehouseId = toWarehouseId;
    }
    /** 调入库位 */
    public Long getToWarehouseLocationId(){
        return this.toWarehouseLocationId;
    }
    /** 调入库位 */
    public void setToWarehouseLocationId(Long toWarehouseLocationId){
        this.toWarehouseLocationId = toWarehouseLocationId;
    }
    /** 数量 */
    public Double getAmount(){
        return this.amount;
    }
    /** 数量 */
    public void setAmount(Double amount){
        this.amount = amount;
    }
    /** 状态;0-新建;1-发布;2-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-发布;2-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getToWarehouseName() {
        return toWarehouseName;
    }

    public void setToWarehouseName(String toWarehouseName) {
        this.toWarehouseName = toWarehouseName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
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
}

