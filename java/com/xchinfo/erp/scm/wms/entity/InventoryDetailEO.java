package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */

@TableName("wms_inventory_detail")
@KeySequence("wms_inventory_detail")
public class InventoryDetailEO extends AbstractAuditableEntity<InventoryDetailEO> {
    private static final long serialVersionUID = -7004360620895347674L;

    @TableId(type = IdType.INPUT)
    private Long inventoryDetailId;/** 主键 */

    private Long inventoryId;/** 盘点ID */

    private Integer inventoryType;/** 盘点类型;1-盘盈;2-盘亏 */

    private String inventoryMonth;/** 盘点月份 */

    private Long materialId;/** 物料 */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private String elementNo;/** 零件号 */

    private String inventoryNo;/** 盘点卡号 */

    private String specification;/** 规格型号 */

    private Long unitId;/** 计量单位 */

    @TableField(exist = false)
    private String unitName;/** 计量单位名称 */

    private String figureNumber;/** 图号 */

    private String figureVersion;/** 版本号 */

    private Long warehouseId;/** 仓库 */

    @TableField(exist = false)
    private String warehouseName;/** 仓库名称 */

    private Long warehouseLocationId;/** 库位 */

    @TableField(exist = false)
    private  String locationName;/**库位名称*/

    private Double amount;/** 数量 */

    private Double relAmount;/** 实际数量 */

    private Double linedgeAmount;/** 线边盘点数量 */

    private Double totalAmount;/** 盘点总数量 */

    private Integer status;/** 状态;0-新建;1-进行中;2-已完成 */

    private Integer adjustStatus;/** 调账状态:0-未加入调账表,1-待调账,2-已调账*/

    private Integer adjustUserId;/** 调节操作人ID*/

    private String remark;/** 备注 */

    private String adjustUserName;/** 调节操作人名称 */

    @TableField(exist = false)
    private Double difAmount;/** 差异数量 */

    @TableField(exist = false)
    private Double adjAmount;/** 调账数量 */

    @TableField(exist = false)
    private Double preCount;/** 本月期初账上结余量(上月期末账上结余量) */

    @TableField(exist = false)
    private Double currentCount;/** 本月期末账上结余量 */

    @TableField(exist = false)
    private Double sumReceiveAmount;/** 本月入库量 */

    @TableField(exist = false)
    private Double sumDeliveryAmount;/** 本月出库量 */

    @TableField(exist = false)
    private String adjustRemark;/** 备注 */

    @TableField(exist = false)
    private String exportAdjustStatus;/**打印调账状态 */

    @TableField(exist = false)
    private String exportStatus;/**打印状态 */

    @TableField(exist = false)
    private String sqeNo;/**打印序号 */

    @TableField(exist = false)
    private String fullName;/**打印机构全称 */

    @TableField(exist = false)
    private String inventoryDate;/**打印盘点日期 */

    @Override
    public Serializable getId() {
        return inventoryDetailId;
    }

    /** 主键 */
    public Long getInventoryDetailId(){
        return this.inventoryDetailId;
    }
    /** 主键 */
    public void setInventoryDetailId(Long inventoryDetailId){
        this.inventoryDetailId = inventoryDetailId;
    }
    /** 盘点ID */
    public Long getInventoryId(){
        return this.inventoryId;
    }
    /** 盘点ID */
    public void setInventoryId(Long inventoryId){
        this.inventoryId = inventoryId;
    }
    /** 盘点类型;1-盘盈;2-盘亏 */
    public Integer getInventoryType(){
        return this.inventoryType;
    }
    /** 盘点类型;1-盘盈;2-盘亏 */
    public void setInventoryType(Integer inventoryType){
        this.inventoryType = inventoryType;
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
    /** 仓库 */
    public Long getWarehouseId(){
        return this.warehouseId;
    }
    /** 仓库 */
    public void setWarehouseId(Long warehouseId){
        this.warehouseId = warehouseId;
    }
    /** 库位 */
    public Long getWarehouseLocationId(){
        return this.warehouseLocationId;
    }
    /** 库位 */
    public void setWarehouseLocationId(Long warehouseLocationId){
        this.warehouseLocationId = warehouseLocationId;
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

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    public Double getRelAmount() {
        return relAmount;
    }

    public void setRelAmount(Double relAmount) {
        this.relAmount = relAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getAdjustStatus() {
        return adjustStatus;
    }

    public void setAdjustStatus(Integer adjustStatus) {
        this.adjustStatus = adjustStatus;
    }

    public Integer getAdjustUserId() {
        return adjustUserId;
    }

    public void setAdjustUserId(Integer adjustUserId) {
        this.adjustUserId = adjustUserId;
    }

    public String getAdjustUserName() {
        return adjustUserName;
    }

    public void setAdjustUserName(String adjustUserName) {
        this.adjustUserName = adjustUserName;
    }

    public Double getAdjAmount() {
        return adjAmount;
    }

    public void setAdjAmount(Double adjAmount) {
        this.adjAmount = adjAmount;
    }

    public String getAdjustRemark() {
        return adjustRemark;
    }

    public void setAdjustRemark(String adjustRemark) {
        this.adjustRemark = adjustRemark;
    }

    public String getExportAdjustStatus() {
        return exportAdjustStatus;
    }

    public void setExportAdjustStatus(String exportAdjustStatus) {
        this.exportAdjustStatus = exportAdjustStatus;
    }

    public String getExportStatus() {
        return exportStatus;
    }

    public void setExportStatus(String exportStatus) {
        this.exportStatus = exportStatus;
    }

    public String getSqeNo() {
        return sqeNo;
    }

    public void setSqeNo(String sqeNo) {
        this.sqeNo = sqeNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInventoryDate() {
        return inventoryDate;
    }

    public void setInventoryDate(String inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public String getInventoryMonth() {
        return inventoryMonth;
    }

    public void setInventoryMonth(String inventoryMonth) {
        this.inventoryMonth = inventoryMonth;
    }

    public Double getLinedgeAmount() {
        return linedgeAmount;
    }

    public void setLinedgeAmount(Double linedgeAmount) {
        this.linedgeAmount = linedgeAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getDifAmount() {
        return difAmount;
    }

    public void setDifAmount(Double difAmount) {
        this.difAmount = difAmount;
    }

    public Double getPreCount() {
        return preCount;
    }

    public void setPreCount(Double preCount) {
        this.preCount = preCount;
    }

    public Double getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Double currentCount) {
        this.currentCount = currentCount;
    }

    public Double getSumReceiveAmount() {
        return sumReceiveAmount;
    }

    public void setSumReceiveAmount(Double sumReceiveAmount) {
        this.sumReceiveAmount = sumReceiveAmount;
    }

    public Double getSumDeliveryAmount() {
        return sumDeliveryAmount;
    }

    public void setSumDeliveryAmount(Double sumDeliveryAmount) {
        this.sumDeliveryAmount = sumDeliveryAmount;
    }

    public String getInventoryNo() {
        return inventoryNo;
    }

    public void setInventoryNo(String inventoryNo) {
        this.inventoryNo = inventoryNo;
    }
}
