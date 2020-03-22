package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;


/**
 * @author cxx
 * @date 2019/6/5
 * @update
 */
@TableName("wms_adjust_inventory")
@KeySequence("wms_adjust_inventory")
public class AdjustInventoryEO extends AbstractAuditableEntity<AdjustInventoryEO> {

    @TableId(type = IdType.INPUT)
    private Long adjustId;/** 主键 */

    private Long inventoryId;/** 盘点单ID */

    private Long inventoryDetailId;/** 盘点明细单ID */

    private Long orgId;/** 归属机构 */

    private Long materialId;/** 物料ID */

    private String elementNo;/** 零件号 */

    private Double amount;/** 盘点前数量 */

    private Double relAmount;/** 盘点数量 */

    private Double linedgeAmount;/** 线边盘点数量 */

    private Double totalAmount;/** 盘点总数量 */

    private Integer inventoryType;/** 盘点类型;1-盘盈;2-盘亏 */

    private Double difAmount;/** 差异数量 */

    private Double adjAmount;/** 调节数量 */

    private String remark;/** 调节原因 */

    private Integer status;/** 调账状态;0 待调账;1 已调账 */

    private Long accountId;/** 台账流水ID */

    private Long adjustUserId;/** 调节操作人ID */

    private String adjustUserName;/** 调节操作人名称 */

    private Date inventoryDate;/** 调节提交日期 */

    @TableField(exist = false)
    private String materialName;/** 物料名称 */

    @TableField(exist = false)
    private String inventoryCode;/** 存货编码 */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String specification;/** 规格型号 */

    @TableField(exist = false)
    private Long unitId;/** 计量单位 */

    @TableField(exist = false)
    private String figureNumber;/** 图号 */

    @TableField(exist = false)
    private String figureVersion;/** 版本号 */

    @TableField(exist = false)
    private Long warehouseId;/** 仓库 */

    @TableField(exist = false)
    private Long warehouseLocationId;/** 库位 */

    @Override
    public Serializable getId() {
        return adjustId;
    }

    /** 主键 */
    public Long getAdjustId(){
        return this.adjustId;
    }
    /** 主键 */
    public void setAdjustId(Long adjustId){
        this.adjustId = adjustId;
    }
    /** 盘点单ID */
    public Long getInventoryId(){
        return this.inventoryId;
    }
    /** 盘点单ID */
    public void setInventoryId(Long inventoryId){
        this.inventoryId = inventoryId;
    }
    /** 盘点明细单ID */
    public Long getInventoryDetailId(){
        return this.inventoryDetailId;
    }
    /** 盘点明细单ID */
    public void setInventoryDetailId(Long inventoryDetailId){
        this.inventoryDetailId = inventoryDetailId;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 物料ID */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料ID */
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
    /** 盘点前数量 */
    public Double getAmount(){
        return this.amount;
    }
    /** 盘点前数量 */
    public void setAmount(Double amount){
        this.amount = amount;
    }
    /** 盘点数量 */
    public Double getRelAmount(){
        return this.relAmount;
    }
    /** 盘点数量 */
    public void setRelAmount(Double relAmount){
        this.relAmount = relAmount;
    }
    /** 盘点类型;1-盘盈;2-盘亏 */
    public Integer getInventoryType(){
        return this.inventoryType;
    }
    /** 盘点类型;1-盘盈;2-盘亏 */
    public void setInventoryType(Integer inventoryType){
        this.inventoryType = inventoryType;
    }
    /** 差异数量 */
    public Double getDifAmount(){
        return this.difAmount;
    }
    /** 差异数量 */
    public void setDifAmount(Double difAmount){
        this.difAmount = difAmount;
    }
    /** 调节数量 */
    public Double getAdjAmount(){
        return this.adjAmount;
    }
    /** 调节数量 */
    public void setAdjAmount(Double adjAmount){
        this.adjAmount = adjAmount;
    }
    /** 调节原因 */
    public String getRemark(){
        return this.remark;
    }
    /** 调节原因 */
    public void setRemark(String remark){
        this.remark = remark;
    }
    /** 调账状态;0 待调账;1 已调账 */
    public Integer getStatus(){
        return this.status;
    }
    /** 调账状态;0 待调账;1 已调账 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 台账流水ID */
    public Long getAccountId(){
        return this.accountId;
    }
    /** 台账流水ID */
    public void setAccountId(Long accountId){
        this.accountId = accountId;
    }
    /** 调节操作人ID */
    public Long getAdjustUserId(){
        return this.adjustUserId;
    }
    /** 调节操作人ID */
    public void setAdjustUserId(Long adjustUserId){
        this.adjustUserId = adjustUserId;
    }
    /** 调节操作人名称 */
    public String getAdjustUserName(){
        return this.adjustUserName;
    }
    /** 调节操作人名称 */
    public void setAdjustUserName(String adjustUserName){
        this.adjustUserName = adjustUserName;
    }
    /** 调节提交日期 */
    public Date getInventoryDate(){
        return this.inventoryDate;
    }
    /** 调节提交日期 */
    public void setInventoryDate(Date inventoryDate){
        this.inventoryDate = inventoryDate;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseLocationId() {
        return warehouseLocationId;
    }

    public void setWarehouseLocationId(Long warehouseLocationId) {
        this.warehouseLocationId = warehouseLocationId;
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
}
