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

@TableName("wms_linedge_inventory_material_assignment")
@KeySequence("wms_linedge_inventory_material_assignment")
public class LinedgeInventoryMaterialAssignmentEO extends AbstractAuditableEntity<LinedgeInventoryMaterialAssignmentEO> {
    private static final long serialVersionUID = -7004360620895347674L;

    @TableId(type = IdType.INPUT)
    private Long materialAssignmentId;/** 主键 */

    private Long linedgeInventoryId;/** 线边盘点ID */

    private Long inventoryLinedgeDetailId;/** 线边盘点明细ID */

    private String inventoryMonth;/** 盘点月份 */

    private Long orgId;/** 归属机构 */

    private Long materialId;/** 物料 */

    private String elementNo;/** 零件号 */

    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private Double amount;/** 数量 */

    private Double weight;/** 原材料重量 */

    private Integer status;/** 状态;0-新建;1-进行中;2-已完成 */

    private String remark;/** 备注 */

    @TableField(exist = false)
    private String sqeNo;/** 序号 */

    @TableField(exist = false)
    private String specification;/** 规格型号 */

    @TableField(exist = false)
    private String figureNumber;/** 图号 */

    @TableField(exist = false)
    private Double relAmount;/** 仓库盘点数量 */

    @TableField(exist = false)
    private Double stockAmount;/** 当前库存数量 */

    @TableField(exist = false)
    private Double difAmount;/** 差异数量 */

    @TableField(exist = false)
    private String fullName;/** 机构全称 */

    @TableField(exist = false)
    private String inventoryNo;/** 盘点卡号 */

    @Override
    public Serializable getId() {
        return materialAssignmentId;
    }

    /** 主键 */
    public Long getMaterialAssignmentId(){
        return this.materialAssignmentId;
    }
    /** 主键 */
    public void setMaterialAssignmentId(Long materialAssignmentId){
        this.materialAssignmentId = materialAssignmentId;
    }
    /** 线边盘点ID */
    public Long getLinedgeInventoryId(){
        return this.linedgeInventoryId;
    }
    /** 线边盘点ID */
    public void setLinedgeInventoryId(Long linedgeInventoryId){
        this.linedgeInventoryId = linedgeInventoryId;
    }
    /** 线边盘点明细ID */
    public Long getInventoryLinedgeDetailId(){
        return this.inventoryLinedgeDetailId;
    }
    /** 线边盘点明细ID */
    public void setInventoryLinedgeDetailId(Long inventoryLinedgeDetailId){
        this.inventoryLinedgeDetailId = inventoryLinedgeDetailId;
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
    /** 数量 */
    public Double getAmount(){
        return this.amount;
    }
    /** 数量 */
    public void setAmount(Double amount){
        this.amount = amount;
    }
    /** 原材料重量 */
    public Double getWeight(){
        return this.weight;
    }
    /** 原材料重量 */
    public void setWeight(Double weight){
        this.weight = weight;
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

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getFigureNumber() {
        return figureNumber;
    }

    public void setFigureNumber(String figureNumber) {
        this.figureNumber = figureNumber;
    }

    public String getSqeNo() {
        return sqeNo;
    }

    public void setSqeNo(String sqeNo) {
        this.sqeNo = sqeNo;
    }

    public Double getRelAmount() {
        return relAmount;
    }

    public void setRelAmount(Double relAmount) {
        this.relAmount = relAmount;
    }

    public Double getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(Double stockAmount) {
        this.stockAmount = stockAmount;
    }

    public Double getDifAmount() {
        return difAmount;
    }

    public void setDifAmount(Double difAmount) {
        this.difAmount = difAmount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInventoryNo() {
        return inventoryNo;
    }

    public void setInventoryNo(String inventoryNo) {
        this.inventoryNo = inventoryNo;
    }
}
