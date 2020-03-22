package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhongye
 * @date 2019/5/9
 */
@TableName("srm_delivery_plan")
@KeySequence("srm_delivery_plan")
public class DeliveryPlanEO extends AbstractAuditableEntity<DeliveryPlanEO> {
    private static final long serialVersionUID = 5447332628821416799L;

    @TableId(type = IdType.INPUT)
    private Long deliveryPlanId;/** 主键 */

    private Long purchaseOrderId;/** 采购订单ID */

    private Long planWeekId;/** 周计划ID */

    private Integer type;/** 单据类型;1-采购订单，2-委外订单。 */

    private Long supplierId;/** 供应商 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    private String voucherNo;/** 流水号 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDeliveryDate;/** 计划送货日期 */

    private Integer isSupplierConfirm;/** 供应商是否确认 */

    private Long supplierConfirmUserId;/** 供应商确认人ID */

    private String supplierConfirmUserName;/** 供应商确认人用户名 */

    private Date supplierConfirmTime;/** 供应商确认时间 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planArriveDate;/** 计划到货日期 */

    private Double planDeliveryQuantity;/** 计划送货数量 */

    private Double actualDeliveryQuantity;/** 实际送货数量 */

    @TableField(exist = false)
    private Double undeliveredQuantity;/** 剩余未送数量 = 计划送货数量 - 实际送货数量(所有送货单已送)*/

    private Double actualReceiveQuantity;/** 实际收货数量 */

    private Double qualifiedQuantity;/** 合格数量 */

    private Double notQualifiedQuantity;/** 不合格数量 */

    private Double returnedQuantity;/** 退货数量 */

    private Double intransitQuantity;/** 在途数量 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date finishDate;/** 完成日期 */

    private Long createUserId;/** 制单人ID */

    private String createUserName;/** 制单人用户名 */

    private Long chargeUserId;/** 经办人ID */

    private String chargeUserName;/** 经办人用户名 */

    private String memo;/** 备注 */

    private Integer status;/** 状态;0-新建;1-送货中;2-已完成;9-已关闭 */

    @TableField(exist = false)
    private String purchaseOrderNo;/** 订单编号 */

    @TableField(exist = false)
    private Long materialId;/** 物料ID */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String materialName;/** 物料名称 */

    @TableField(exist = false)
    private String elementNo;/** 零件号 */

    @TableField(exist = false)
    private String unitName;/** 单位 */

    @TableField(exist = false)
    private String specification;/** 规格型号 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdTime;/** 创建时间 */

    @TableField(exist = false)
    private String exportType;/** 导出类型 */

    @TableField(exist = false)
    private String exportIsSupplierConfirm;/** 导出供应商确认 */

    @TableField(exist = false)
    private String exportStatus;/** 导出状态 */

    @Override
    public Serializable getId() {
        return this.deliveryPlanId;
    }

    /** 主键 */
    public Long getDeliveryPlanId(){
        return this.deliveryPlanId;
    }
    /** 主键 */
    public void setDeliveryPlanId(Long deliveryPlanId){
        this.deliveryPlanId = deliveryPlanId;
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
    /** 供应商 */
    public Long getSupplierId(){
        return this.supplierId;
    }
    /** 供应商 */
    public void setSupplierId(Long supplierId){
        this.supplierId = supplierId;
    }
    /** 归属机构 */
    public Long getOrgId() {
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    /** 归属机构名称 */
    public String getOrgName() {
        return this.orgName;
    }
    /** 归属机构名称 */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    /** 流水号 */
    public String getVoucherNo(){
        return this.voucherNo;
    }
    /** 流水号 */
    public void setVoucherNo(String voucherNo){
        this.voucherNo = voucherNo;
    }
    /** 计划送货日期 */
    public Date getPlanDeliveryDate(){
        return this.planDeliveryDate;
    }
    /** 计划送货日期 */
    public void setPlanDeliveryDate(Date planDeliveryDate){
        this.planDeliveryDate = planDeliveryDate;
    }
    /** 供应商是否确认 */
    public Integer getIsSupplierConfirm(){
        return this.isSupplierConfirm;
    }
    /** 供应商是否确认 */
    public void setIsSupplierConfirm(Integer isSupplierConfirm){
        this.isSupplierConfirm = isSupplierConfirm;
    }
    /** 供应商确认人ID */
    public Long getSupplierConfirmUserId(){
        return this.supplierConfirmUserId;
    }
    /** 供应商确认人ID */
    public void setSupplierConfirmUserId(Long supplierConfirmUserId){
        this.supplierConfirmUserId = supplierConfirmUserId;
    }
    /** 供应商确认人用户名 */
    public String getSupplierConfirmUserName() {
        return supplierConfirmUserName;
    }
    /** 供应商确认人用户名 */
    public void setSupplierConfirmUserName(String supplierConfirmUserName) {
        this.supplierConfirmUserName = supplierConfirmUserName;
    }
    /** 供应商确认时间 */
    public Date getSupplierConfirmTime(){
        return this.supplierConfirmTime;
    }
    /** 供应商确认时间 */
    public void setSupplierConfirmTime(Date supplierConfirmTime){
        this.supplierConfirmTime = supplierConfirmTime;
    }
    /** 计划到货日期 */
    public Date getPlanArriveDate(){
        return this.planArriveDate;
    }
    /** 计划到货日期 */
    public void setPlanArriveDate(Date planArriveDate){
        this.planArriveDate = planArriveDate;
    }
    /** 计划送货数量 */
    public Double getPlanDeliveryQuantity(){
        return this.planDeliveryQuantity;
    }
    /** 计划送货数量 */
    public void setPlanDeliveryQuantity(Double planDeliveryQuantity){
        this.planDeliveryQuantity = planDeliveryQuantity;
    }
    /** 实际送货数量 */
    public Double getActualDeliveryQuantity() {
        return actualDeliveryQuantity;
    }
    /** 实际送货数量 */
    public void setActualDeliveryQuantity(Double actualDeliveryQuantity) {
        this.actualDeliveryQuantity = actualDeliveryQuantity;
    }
    /** 实际收货数量 */
    public Double getActualReceiveQuantity(){
        return this.actualReceiveQuantity;
    }
    /** 实际收货数量 */
    public void setActualReceiveQuantity(Double actualReceiveQuantity){
        this.actualReceiveQuantity = actualReceiveQuantity;
    }
    /** 合格数量 */
    public Double getQualifiedQuantity(){
        return this.qualifiedQuantity;
    }
    /** 合格数量 */
    public void setQualifiedQuantity(Double qualifiedQuantity){
        this.qualifiedQuantity = qualifiedQuantity;
    }
    /** 不合格数量 */
    public Double getNotQualifiedQuantity(){
        return this.notQualifiedQuantity;
    }
    /** 不合格数量 */
    public void setNotQualifiedQuantity(Double notQualifiedQuantity){
        this.notQualifiedQuantity = notQualifiedQuantity;
    }
    /** 退货数量 */
    public Double getReturnedQuantity(){
        return this.returnedQuantity;
    }
    /** 退货数量 */
    public void setReturnedQuantity(Double returnedQuantity){
        this.returnedQuantity = returnedQuantity;
    }
    /** 在途数量 */
    public Double getIntransitQuantity(){
        return this.intransitQuantity;
    }
    /** 在途数量 */
    public void setIntransitQuantity(Double intransitQuantity){
        this.intransitQuantity = intransitQuantity;
    }
    /** 完成日期 */
    public Date getFinishDate(){
        return this.finishDate;
    }
    /** 完成日期 */
    public void setFinishDate(Date finishDate){
        this.finishDate = finishDate;
    }
    /** 制单人ID */
    public Long getCreateUserId(){
        return this.createUserId;
    }
    /** 制单人ID */
    public void setCreateUserId(Long createUserId){
        this.createUserId = createUserId;
    }
    /** 制单人用户名 */
    public String getCreateUserName(){
        return this.createUserName;
    }
    /** 制单人用户名 */
    public void setCreateUserName(String createUserName){
        this.createUserName = createUserName;
    }
    /** 经办人ID */
    public Long getChargeUserId(){
        return this.chargeUserId;
    }
    /** 经办人ID */
    public void setChargeUserId(Long chargeUserId){
        this.chargeUserId = chargeUserId;
    }
    /** 经办人用户名 */
    public String getChargeUserName(){
        return this.chargeUserName;
    }
    /** 经办人用户名 */
    public void setChargeUserName(String chargeUserName){
        this.chargeUserName = chargeUserName;
    }
    /** 备注 */
    public String getMemo(){
        return this.memo;
    }
    /** 备注 */
    public void setMemo(String memo){
        this.memo = memo;
    }
    /** 状态;0-新建;1-送货中;2-已完成;9-已关闭 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-送货中;2-已完成;9-已关闭 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 订单编号 */
    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }
    /** 订单编号 */
    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }
    /** 物料编码 */
    public String getMaterialCode() {
        return materialCode;
    }
    /** 物料编码 */
    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }
    /** 物料名称 */
    public String getMaterialName() {
        return materialName;
    }
    /** 物料名称 */
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
    /** 零件号 */
    public String getElementNo() {
        return elementNo;
    }
    /** 零件号 */
    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }
    /** 单位 */
    public String getUnitName() {
        return unitName;
    }
    /** 单位 */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    /** 规格型号 */
    public String getSpecification() {
        return specification;
    }
    /** 规格型号 */
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    /** 创建时间 */
    public Date getCreatedTime() {
        return createdTime;
    }
    /** 创建时间 */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Long getPlanWeekId() {
        return planWeekId;
    }

    public void setPlanWeekId(Long planWeekId) {
        this.planWeekId = planWeekId;
    }

    public Double getUndeliveredQuantity() {
        return undeliveredQuantity;
    }

    public void setUndeliveredQuantity(Double undeliveredQuantity) {
        this.undeliveredQuantity = undeliveredQuantity;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getExportIsSupplierConfirm() {
        return exportIsSupplierConfirm;
    }

    public void setExportIsSupplierConfirm(String exportIsSupplierConfirm) {
        this.exportIsSupplierConfirm = exportIsSupplierConfirm;
    }

    public String getExportStatus() {
        return exportStatus;
    }

    public void setExportStatus(String exportStatus) {
        this.exportStatus = exportStatus;
    }
}
