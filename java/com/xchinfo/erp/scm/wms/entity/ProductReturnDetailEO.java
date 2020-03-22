package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;


/**
 * @author yuanchang
 * @date 2019/5/17
 * @update
 */
@TableName("wms_product_return_detail")
@KeySequence("wms_product_return_detail")
public class ProductReturnDetailEO extends AbstractAuditableEntity<ProductReturnDetailEO> {

    private static final long serialVersionUID = -1674209743079864760L;

    @TableId(type = IdType.INPUT)
    private Long returnDetailId;/** 主键 */

    private Long returnId;/** 退货单ID */

    private Long materialId;/** 物料 */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private String elementNo;/** 零件号 */

    private String specification;/** 规格型号 */

    private Long unitId;/** 计量单位 */

    @TableField(exist = false)
    private String unitName;/** 计量单位 */

    @TableField(exist = false)
    private String warehouseName;/**仓库名称*/

    @TableField(exist = false)
    private  String locationName;/**库位名称*/

    private String figureNumber;/** 图号 */

    private String figureVersion;/** 版本号 */

    private Long warehouseId;/** 仓库 */

    private Long warehouseLocationId;/** 库位 */

    private Double returnAmount;/** 退货数量 */

    private Double relReturnAmount;/** 实际退货数量 */

    private String returnReason;/** 退货原因 */

    private Long deliveryDetailId;/** 发货明细ID */

    private Integer status;/** 状态;0-新建;1-发布;9-已关闭  */

    private String remarks;/** 备注 */

    @TableField(exist = false)
    private  Long mainWarehouseId;/**物料默认仓库*/


    @Override
    public Serializable getId() {
        return returnDetailId;
    }


    /** 主键 */
    public Long getReturnDetailId(){
        return this.returnDetailId;
    }
    /** 主键 */
    public void setReturnDetailId(Long returnDetailId){
        this.returnDetailId = returnDetailId;
    }
    /** 退货单ID */
    public Long getReturnId(){
        return this.returnId;
    }
    /** 退货单ID */
    public void setReturnId(Long returnId){
        this.returnId = returnId;
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
    /** 退货数量 */
    public Double getReturnAmount(){
        return this.returnAmount;
    }
    /** 退货数量 */
    public void setReturnAmount(Double returnAmount){
        this.returnAmount = returnAmount;
    }
    /** 退货原因 */
    public String getReturnReason(){
        return this.returnReason;
    }
    /** 退货原因 */
    public void setReturnReason(String returnReason){
        this.returnReason = returnReason;
    }
    /** 发货明细ID */
    public Long getDeliveryDetailId(){
        return this.deliveryDetailId;
    }
    /** 发货明细ID */
    public void setDeliveryDetailId(Long deliveryDetailId){
        this.deliveryDetailId = deliveryDetailId;
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
    public String getRemarks(){
        return this.remarks;
    }
    /** 备注 */
    public void setRemarks(String remarks){
        this.remarks = remarks;
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

    public Double getRelReturnAmount() {
        return relReturnAmount;
    }

    public void setRelReturnAmount(Double relReturnAmount) {
        this.relReturnAmount = relReturnAmount;
    }

    public Long getMainWarehouseId() {
        return mainWarehouseId;
    }

    public void setMainWarehouseId(Long mainWarehouseId) {
        this.mainWarehouseId = mainWarehouseId;
    }
}
