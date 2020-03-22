package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("mes_production_order")
@KeySequence("mes_production_order")
public class ProductionOrderEO extends AbstractAuditableEntity<ProductionOrderEO> {
    private static final long serialVersionUID = 7485585830085833308L;

    @TableId(type = IdType.INPUT)
    private Long orderId;/** 主键 */

    private String orderNo;/** 订单编号 */

    private Date orderDate;/** 日期 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

    private Long customerId;/** 客户 */

    @TableField(exist = false)
    private String customerCode;/** 客户编码 */

    @TableField(exist = false)
    private String customerName;/** 客户名称 */

    private Integer priority;/** 优先级 */

    private Date deliveryDate;/** 交货日期 */

    private Date plannedDeliveryDate;/** 计划交货日期 */

    private Date finishedDate;/** 完成日期 */

    private Integer status;/** 状态;1-新建;2-排产中;3-已排产;4-生产中;5-已完成 */

    private String remarks;/** 备注 */

    @TableField(exist = false)
    private List<ProductionOrderDetailEO> details;/** 订单明细 */

    @Override
    public Serializable getId() {
        return this.orderId;
    }

    /** 主键 */
    public Long getOrderId(){
        return this.orderId;
    }
    /** 主键 */
    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }
    /** 订单编号 */
    public String getOrderNo(){
        return this.orderNo;
    }
    /** 订单编号 */
    public void setOrderNo(String orderNo){
        this.orderNo = orderNo;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 日期 */
    public Date getOrderDate(){
        return this.orderDate;
    }
    /** 日期 */
    public void setOrderDate(Date orderDate){
        this.orderDate = orderDate;
    }
    /** 客户 */
    public Long getCustomerId(){
        return this.customerId;
    }
    /** 客户 */
    public void setCustomerId(Long customerId){
        this.customerId = customerId;
    }
    /** 优先级 */
    public Integer getPriority(){
        return this.priority;
    }
    /** 优先级 */
    public void setPriority(Integer priority){
        this.priority = priority;
    }
    /** 交货日期 */
    public Date getDeliveryDate(){
        return this.deliveryDate;
    }
    /** 交货日期 */
    public void setDeliveryDate(Date deliveryDate){
        this.deliveryDate = deliveryDate;
    }
    /** 计划交货日期 */
    public Date getPlannedDeliveryDate(){
        return this.plannedDeliveryDate;
    }
    /** 计划交货日期 */
    public void setPlannedDeliveryDate(Date plannedDeliveryDate){
        this.plannedDeliveryDate = plannedDeliveryDate;
    }
    /** 完成日期 */
    public Date getFinishedDate(){
        return this.finishedDate;
    }
    /** 完成日期 */
    public void setFinishedDate(Date finishedDate){
        this.finishedDate = finishedDate;
    }
    /** 状态;1-新建;2-已排产;3-生产中;4-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;1-新建;2-已排产;3-生产中;4-已完成 */
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

    public List<ProductionOrderDetailEO> getDetails() {
        return details;
    }

    public void setDetails(List<ProductionOrderDetailEO> details) {
        this.details = details;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
