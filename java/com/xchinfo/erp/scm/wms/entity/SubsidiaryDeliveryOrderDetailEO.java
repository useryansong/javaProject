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
@TableName("wms_subsidiary_delivery_order_detail")
@KeySequence("wms_subsidiary_delivery_order_detail")
public class SubsidiaryDeliveryOrderDetailEO extends AbstractAuditableEntity<SubsidiaryDeliveryOrderDetailEO> {
    private static final long serialVersionUID = -5758337540202027663L;

    @TableId(type = IdType.INPUT)
    private Long deliveryDetailId;/** 主键 */

    private Long deliveryOrderId;/** 出库单ID */

    private Long orderId;/** 订单ID */

    private Long orderDetailId;/** 订单明细ID */

    private Long materialId;/** 物料 */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private String elementNo;/** 零件号 */

    private String specification;/** 规格型号 */

    private Long unitId;/** 计量单位 */

    @TableField(exist = false)
    private String unitName;/** 计量单位名称 */

    private String figureNumber;/** 图号 */

    private String figureVersion;/** 版本号 */

    private Long warehouseId;/** 仓库 */

    @TableField(exist = false)
    private String warehouseName;/** 仓库名称 */

    @TableField(exist = false)
    private String warehouseCode;/** 仓库编码 */

    private Long warehouseLocationId;/** 库位 */

    @TableField(exist = false)
    private String warehouseLocationName;/** 库位名称 */

    private Double deliveryAmount;/** 出库数量 */

    private Double relDeliveryAmount;/**实际出库数量*/

    @TableField(exist = false)
    private Double count;/** 库存数量 */

    private Integer status;/** 状态;0-新建;1-进行中;2-已完成 */

    private String remarks;/** 备注 */

    @TableField(exist = false)
    private String voucherNo;/** 单据编号(出库单) */

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;/** 出库日期(出库单) */

    @TableField(exist = false)
    private String destinationName;/** 业务对象名称(出库单) */

    @TableField(exist = false)
    private String destinationCode;/** 业务对象编码(出库单) */


    @Override
    public Serializable getId() {
        return this.deliveryDetailId;
    }

    /** 主键 */
    public Long getDeliveryDetailId(){
        return this.deliveryDetailId;
    }
    /** 主键 */
    public void setDeliveryDetailId(Long deliveryDetailId){
        this.deliveryDetailId = deliveryDetailId;
    }
    /** 出库单ID */
    public Long getDeliveryOrderId(){
        return this.deliveryOrderId;
    }
    /** 出库单ID */
    public void setDeliveryOrderId(Long deliveryOrderId){
        this.deliveryOrderId = deliveryOrderId;
    }
    /** 订单ID */
    public Long getOrderId(){
        return this.orderId;
    }
    /** 订单ID */
    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }
    /** 订单明细ID */
    public Long getOrderDetailId(){
        return this.orderDetailId;
    }
    /** 订单明细ID */
    public void setOrderDetailId(Long orderDetailId){
        this.orderDetailId = orderDetailId;
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
    /** 计量单位名称 */
    public String getUnitName() {
        return unitName;
    }
    /** 计量单位名称 */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
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
    /** 仓库名称 */
    public String getWarehouseName() {
        return warehouseName;
    }
    /** 仓库名称 */
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
    /** 仓库编码 */
    public String getWarehouseCode() {
        return this.warehouseCode;
    }
    /** 仓库编码 */
    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
    /** 库位 */
    public Long getWarehouseLocationId(){
        return this.warehouseLocationId;
    }
    /** 库位 */
    public void setWarehouseLocationId(Long warehouseLocationId){
        this.warehouseLocationId = warehouseLocationId;
    }
    /** 库位名称 */
    public String getWarehouseLocationName() {
        return warehouseLocationName;
    }
    /** 库位名称 */
    public void setWarehouseLocationName(String warehouseLocationName) {
        this.warehouseLocationName = warehouseLocationName;
    }
    /** 出库数量 */
    public Double getDeliveryAmount(){
        return this.deliveryAmount;
    }
    /** 出库数量 */
    public void setDeliveryAmount(Double deliveryAmount){
        this.deliveryAmount = deliveryAmount;
    }
    /** 状态;0-新建;1-进行中;2-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-进行中;2-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Double getRelDeliveryAmount() {
        return relDeliveryAmount;
    }

    public void setRelDeliveryAmount(Double relDeliveryAmount) {
        this.relDeliveryAmount = relDeliveryAmount;
    }

    /** 单据编号(出库单) */
    public String getVoucherNo() {
        return this.voucherNo;
    }
    /** 单据编号(出库单) */
    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }
    /** 出库日期(出库单) */
    public Date getDeliveryDate() {
        return this.deliveryDate;
    }
    /** 出库日期(出库单) */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    /** 业务对象名称(出库单) */
    public String getDestinationName() {
        return this.destinationName;
    }
    /** 业务对象名称(出库单) */
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
    /** 业务对象编码(出库单) */
    public String getDestinationCode() {
        return this.destinationCode;
    }
    /** 业务对象编码(出库单) */
    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }
}
