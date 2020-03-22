package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("mes_production_order_detail")
@KeySequence("mes_production_order_detail")
public class ProductionOrderDetailEO extends AbstractAuditableEntity<ProductionOrderDetailEO> {
    private static final long serialVersionUID = -2572410255815688266L;

    @TableId(type = IdType.INPUT)
    private Long orderDetailId;/** 主键 */

    private Long orderId;/** 订单 */

    private Long materialId;/** 物料 */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String materialName;/** 物料名称 */

    @TableField(exist = false)
    private String specification;/** 规格型号 */

    private Long specificId;/** 规格型号 */

    private Long measurementId;/** 计量单位 */

    private Double amount;/** 数量 */

    private Double finishedAmount;/** 完成数量 */

    @Override
    public Serializable getId() {
        return this.orderDetailId;
    }

    /** 主键 */
    public Long getOrderDetailId(){
        return this.orderDetailId;
    }
    /** 主键 */
    public void setOrderDetailId(Long orderDetailId){
        this.orderDetailId = orderDetailId;
    }
    /** 订单 */
    public Long getOrderId(){
        return this.orderId;
    }
    /** 订单 */
    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }
    /** 物料 */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料 */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 规格型号 */
    public Long getSpecificId(){
        return this.specificId;
    }
    /** 规格型号 */
    public void setSpecificId(Long specificId){
        this.specificId = specificId;
    }
    /** 计量单位 */
    public Long getMeasurementId(){
        return this.measurementId;
    }
    /** 计量单位 */
    public void setMeasurementId(Long measurementId){
        this.measurementId = measurementId;
    }
    /** 数量 */
    public Double getAmount(){
        return this.amount;
    }
    /** 数量 */
    public void setAmount(Double amount){
        this.amount = amount;
    }
    /** 完成数量 */
    public Double getFinishedAmount(){
        return this.finishedAmount;
    }
    /** 完成数量 */
    public void setFinishedAmount(Double finishedAmount){
        this.finishedAmount = finishedAmount;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
}
