package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhongye
 * @date 2019/5/24
 */
@TableName("srm_return_order_detail")
@KeySequence("srm_return_order_detail")
public class ReturnOrderDetailEO extends AbstractAuditableEntity<ReturnOrderDetailEO> {
    private static final long serialVersionUID = 41040132824808953L;

    @TableId(type = IdType.INPUT)
    private Long returnOrderDetailId;/** 主键 */

    private Long returnOrderId;/** 退货订单ID */

    private Long purchaseOrderId;/** 采购订单ID */

    private Integer type;/** 单据类型;1-采购订单，2-委外订单。 */

    private Long materialId;/** 物料ID */

    private String materialName;/** 物料名称 */

    private String specification;/** 规格型号 */

    private Integer status;/** 状态;0-未完成，1-已完成。 */

    private String materialCode;/** 物料编码 */

    private String inventoryCode;/** 存货编码 */

    private String elementNo;/** 零件号 */

    private String unitName;/** 计量单位 */

    private Date deliveryTime;/** 出库时间 */

    private Double planReturnQuantity;/** 预计退货数量 */

    private Double actualReturnQuantity;/** 实际退货数量 */

    private String memo;/** 备注 */

    @TableField(exist = false)
    private Long unitId;

    @TableField(exist = false)
    private String figureNumber;/** 图号 */

    @TableField(exist = false)
    private String figureVersion;/**版本号*/

    @TableField(exist = false)
    private Long  warehouseLocationId;/**库位*/

    @TableField(exist = false)
    private Long  warehouseId;/**仓库*/

    @TableField(exist = false)
    private String voucherNo;/** 打印退货单流水号 */

    @TableField(exist = false)
    private String exportType;/** 打印类型 */

    @TableField(exist = false)
    private String supplierName;/** 打印供应商 */

    @TableField(exist = false)
    private Date planReturnDate;/** 打印预计退货日期 */

    @TableField(exist = false)
    private Date actualReturnDate;/** 打印实际退货日期 */

    @TableField(exist = false)
    private String returnUserName;/** 打印退货人 */

    @TableField(exist = false)
    private String returnReason;/** 打印退货原因*/

    @TableField(exist = false)
    private String exportStatus;/** 打印状态*/

   // private String lastModifiedBy;/** 打印操作人 */

    //private Date lastModifiedTime;/** 打印操作时间 */

    @TableField(exist = false)
    private Date returnOrderCreatedTime;/** 创建日期(退货单) */

    @TableField(exist = false)
    private String erpCode;/** 供应商ERP编码 */

    @TableField(exist = false)
    private String createBillName;/** 制单人(导出时使用,取当前登录人真实名) */

    @TableField(exist = false)
    private String erpVoucherNo1;/** ERP流水号(采购退货单(采购退货页面)) */


    @Override
    public Serializable getId() {
        return this.returnOrderDetailId;
    }

    /** 主键 */
    public Long getReturnOrderDetailId(){
        return this.returnOrderDetailId;
    }
    /** 主键 */
    public void setReturnOrderDetailId(Long returnOrderDetailId){
        this.returnOrderDetailId = returnOrderDetailId;
    }
    /** 退货订单ID */
    public Long getReturnOrderId(){
        return this.returnOrderId;
    }
    /** 退货订单ID */
    public void setReturnOrderId(Long returnOrderId){
        this.returnOrderId = returnOrderId;
    }
    /** 采购订单ID */
    public Long getPurchaseOrderId(){
        return this.purchaseOrderId;
    }
    /** 采购订单ID */
    public void setPurchaseOrderId(Long purchaseOrderId){
        this.purchaseOrderId = purchaseOrderId;
    }
    /** 单据类型;1-采购订单，2-委外订单。 */
    public Integer getType(){
        return this.type;
    }
    /** 单据类型;1-采购订单，2-委外订单。 */
    public void setType(Integer type){
        this.type = type;
    }
    /** 状态;0-未完成，1-已完成。 */
    public Integer getStatus() {
        return this.status;
    }
    /** 状态;0-未完成，1-已完成。 */
    public void setStatus(Integer status) {
        this.status = status;
    }
    /** 物料ID */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料ID */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 物料名称 */
    public String getMaterialName(){
        return this.materialName;
    }
    /** 物料名称 */
    public void setMaterialName(String materialName){
        this.materialName = materialName;
    }
    /** 规格型号 */
    public String getSpecification(){
        return this.specification;
    }
    /** 规格型号 */
    public void setSpecification(String specification){
        this.specification = specification;
    }
    /** 物料编码 */
    public String getMaterialCode(){
        return this.materialCode;
    }
    /** 物料编码 */
    public void setMaterialCode(String materialCode){
        this.materialCode = materialCode;
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
    /** 计量单位 */
    public String getUnitName(){
        return this.unitName;
    }
    /** 计量单位 */
    public void setUnitName(String unitName){
        this.unitName = unitName;
    }
    /** 出库时间 */
    public Date getDeliveryTime(){
        return this.deliveryTime;
    }
    /** 出库时间 */
    public void setDeliveryTime(Date deliveryTime){
        this.deliveryTime = deliveryTime;
    }
    /** 预计退货数量 */
    public Double getPlanReturnQuantity(){
        return this.planReturnQuantity;
    }
    /** 预计退货数量 */
    public void setPlanReturnQuantity(Double planReturnQuantity){
        this.planReturnQuantity = planReturnQuantity;
    }
    /** 实际退货数量 */
    public Double getActualReturnQuantity(){
        return this.actualReturnQuantity;
    }
    /** 实际退货数量 */
    public void setActualReturnQuantity(Double actualReturnQuantity){
        this.actualReturnQuantity = actualReturnQuantity;
    }
    /** 备注 */
    public String getMemo(){
        return this.memo;
    }
    /** 备注 */
    public void setMemo(String memo){
        this.memo = memo;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getFigureNumber() {
        return figureNumber;
    }

    public void setFigureNumber(String figureNumber) {
        this.figureNumber = figureNumber;
    }

    public String getFigureVersion() {
        return figureVersion;
    }

    public void setFigureVersion(String figureVersion) {
        this.figureVersion = figureVersion;
    }

    public Long getWarehouseLocationId() {
        return warehouseLocationId;
    }

    public void setWarehouseLocationId(Long warehouseLocationId) {
        this.warehouseLocationId = warehouseLocationId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Date getPlanReturnDate() {
        return planReturnDate;
    }

    public void setPlanReturnDate(Date planReturnDate) {
        this.planReturnDate = planReturnDate;
    }

    public Date getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(Date actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public String getReturnUserName() {
        return returnUserName;
    }

    public void setReturnUserName(String returnUserName) {
        this.returnUserName = returnUserName;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public String getExportStatus() {
        return exportStatus;
    }

    public void setExportStatus(String exportStatus) {
        this.exportStatus = exportStatus;
    }

  /*  @Override
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }*/

    public Date getReturnOrderCreatedTime() {
        return returnOrderCreatedTime;
    }

    public void setReturnOrderCreatedTime(Date returnOrderCreatedTime) {
        this.returnOrderCreatedTime = returnOrderCreatedTime;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    public String getCreateBillName() {
        return createBillName;
    }

    public void setCreateBillName(String createBillName) {
        this.createBillName = createBillName;
    }

    public String getErpVoucherNo1() {
        return erpVoucherNo1;
    }

    public void setErpVoucherNo1(String erpVoucherNo1) {
        this.erpVoucherNo1 = erpVoucherNo1;
    }
}
