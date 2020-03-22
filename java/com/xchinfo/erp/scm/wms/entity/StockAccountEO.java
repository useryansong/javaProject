package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@TableName("wms_stock_account")
@KeySequence("wms_stock_account")
public class StockAccountEO extends AbstractAuditableEntity<StockAccountEO> {
    private static final long serialVersionUID = 3553059275160755263L;

    @TableId(type = IdType.INPUT)
    private Long accountId;/** 主键 */

    private Long voucherId;/** 单据ID;allocation_detail_id */

    private Date voucherDate;/** 单据日期 */

    private Integer voucherType;/** 单据类型;1-采购入库;2-生产入库;3-委外入库;4-辅料入库;5-调拨入库;6-其他入库;7-销售出库;8-生产领料;9-委外领料;10-辅料出库;11-调拨出库;12-其他出库 */

    private Integer childVoucherType;/** 单据子类型;1-其他采购入库;2-其他委外入库;3-成品退货入库;4-采购退货出库;5-委外退货出库;6-盘亏;7-盘盈 */

    private Long materialId;/** 物料 */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private String elementNo;/** 零件号 */

    private String specification;/** 规格型号 */

    @TableField(exist = false)
    private String projectNo;/** 项目号 */

    private Long unitId;/** 计量单位 */

    @TableField(exist = false)
    private String unitName;/** 计量单位 */

    @TableField(exist = false)
    private String orgName;/** 计量单位 */

    private String figureNumber;/** 图号 */

    private String figureVersion;/** 版本号 */

    private Long warehouseId;/** 仓库 */

    @TableField(exist = false)
    private String warehouseName;/** 仓库名称 */

    @TableField(exist = false)
    private String areaName;/** 仓库名称 */

    private Long warehouseLocationId;/** 库位 */

    @TableField(exist = false)
    private String locationName;/** 库位名称 */

    @TableField(exist = false)
    private String barCode;/** 库位条码 */

    private Double amount;/** 数量 */

    private String remarks;/** 备注 */

    @TableField(exist = false)
    private Double minStock;/** 最小库存 */

    @TableField(exist = false)
    private Double maxStock;/** 最大库存 */

    @TableField(exist = false)
    private Double count;/** 物料存量 */

    @TableField(exist = false)
    private String mainWarehouseName;/** 默认仓库 */

    @TableField(exist = false)
    private String safetyStock;/** 安全库存 */

    @TableField(exist = false)
    private Double inAmount;/** 入库数量 */

    @TableField(exist = false)
    private Double outAmount;/** 出库数量 */

    @TableField(exist = false)
    private Double deliveryAmount;/** 出库数量 */

    @TableField(exist = false)
    private Double CountOutAmount;/** 出库总数量 */

    @TableField(exist = false)
    private Double CountInAmount;/** 入库总数量 */

    @TableField(exist = false)
    private String supplierName;/** 默认供应商 */

    @TableField(exist = false)
    private Long usedage;/** 库龄 */

    @TableField(exist = false)
    private Long stampingMaterialConsumptionQuotaId;/** 耗用表ID */

    @TableField(exist = false)
    private String stampingElementNo;/** 耗用表零件号(即冲压件零件号) */

    @TableField(exist = false)
    private String stampingMaterialName;/** 耗用表物料名称(即冲压件物料名称) */

    @TableField(exist = false)
    private String stampingInventoryCode;/** 冲压件存货编码 */

    @TableField(exist = false)
    private Double beginingBalance;/** 初始库存 */

    @TableField(exist = false)
    private Integer materialStatus;



    @Override
    public Serializable getId() {
        return this.accountId;
    }

    /** 主键 */
    public Long getAccountId(){
        return this.accountId;
    }
    /** 主键 */
    public void setAccountId(Long accountId){
        this.accountId = accountId;
    }
    /** 单据ID;allocation_detail_id */
    public Long getVoucherId(){
        return this.voucherId;
    }
    /** 单据ID;allocation_detail_id */
    public void setVoucherId(Long voucherId){
        this.voucherId = voucherId;
    }
    /** 单据日期 */
    public Date getVoucherDate(){
        return this.voucherDate;
    }
    /** 单据日期 */
    public void setVoucherDate(Date voucherDate){
        this.voucherDate = voucherDate;
    }
    /** 单据类型;1-采购入库;2-生产入库;3-委外入库;4-辅料入库;5-调拨入库;6-其他入库;7-销售出库;8-生产领料;9-委外领料;10-辅料出库;11-调拨出库;12-其他出库 */
    public Integer getVoucherType(){
        return this.voucherType;
    }
    /** 单据类型;1-采购入库;2-生产入库;3-委外入库;4-辅料入库;5-调拨入库;6-其他入库;7-销售出库;8-生产领料;9-委外领料;10-辅料出库;11-调拨出库;12-其他出库 */
    public void setVoucherType(Integer voucherType){
        this.voucherType = voucherType;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Double getMinStock() {
        return minStock;
    }

    public void setMinStock(Double minStock) {
        this.minStock = minStock;
    }

    public Double getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Double maxStock) {
        this.maxStock = maxStock;
    }

    public String getMainWarehouseName() {
        return mainWarehouseName;
    }

    public void setMainWarehouseName(String mainWarehouseName) {
        this.mainWarehouseName = mainWarehouseName;
    }

    public String getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(String safetyStock) {
        this.safetyStock = safetyStock;
    }

    public Double getInAmount() {
        return inAmount;
    }

    public void setInAmount(Double inAmount) {
        this.inAmount = inAmount;
    }

    public Double getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(Double outAmount) {
        this.outAmount = outAmount;
    }

    public Double getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(Double deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getUsedage() {
        return usedage;
    }

    public void setUsedage(Long usedage) {
        this.usedage = usedage;
    }

    public Double getCountOutAmount() {
        return CountOutAmount;
    }

    public void setCountOutAmount(Double countOutAmount) {
        CountOutAmount = countOutAmount;
    }

    public Double getCountInAmount() {
        return CountInAmount;
    }

    public void setCountInAmount(Double countInAmount) {
        CountInAmount = countInAmount;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public Integer getChildVoucherType() {
        return childVoucherType;
    }

    public void setChildVoucherType(Integer childVoucherType) {
        this.childVoucherType = childVoucherType;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Long getStampingMaterialConsumptionQuotaId() {
        return stampingMaterialConsumptionQuotaId;
    }

    public void setStampingMaterialConsumptionQuotaId(Long stampingMaterialConsumptionQuotaId) {
        this.stampingMaterialConsumptionQuotaId = stampingMaterialConsumptionQuotaId;
    }

    public String getStampingElementNo() {
        return stampingElementNo;
    }

    public void setStampingElementNo(String stampingElementNo) {
        this.stampingElementNo = stampingElementNo;
    }

    public Double getBeginingBalance() {
        return beginingBalance;
    }

    public void setBeginingBalance(Double beginingBalance) {
        this.beginingBalance = beginingBalance;
    }

    public Integer getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(Integer materialStatus) {
        this.materialStatus = materialStatus;
    }

    public String getStampingMaterialName() {
        return stampingMaterialName;
    }

    public void setStampingMaterialName(String stampingMaterialName) {
        this.stampingMaterialName = stampingMaterialName;
    }

    public String getStampingInventoryCode() {
        return stampingInventoryCode;
    }

    public void setStampingInventoryCode(String stampingInventoryCode) {
        this.stampingInventoryCode = stampingInventoryCode;
    }
}
