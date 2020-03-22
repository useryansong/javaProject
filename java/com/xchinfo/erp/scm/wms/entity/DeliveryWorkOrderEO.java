package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@TableName("wms_delivery_work_order")
@KeySequence("wms_delivery_work_order")
public class DeliveryWorkOrderEO extends AbstractAuditableEntity<DeliveryWorkOrderEO> {
    private static final long serialVersionUID = 6663725040029495820L;

    @TableId(type = IdType.INPUT)
    private Long deliveryWorkOrderId;/** 主键，出库工单ID */

    private String workOrderId;/** 工单GUID */

    private Long orgId;/** 归属机构 */

    private Integer type;/** 工单类型;3-车间叫料;4-成品出库 */

    private Long relationId;/** 关联ID */

    private Long materialId;/** 物料ID */

    private String elementNo;/** 物料零件号 */

    private String materialName;/** 物料名称 */

    private Date orderDate;/** 工单日期 */

    private Double notDeliveryQuantity;/** 待出库数量 */

    private Double hasDeliveryQuantity;/** 已出库数量 */

    private Integer status;/** 状态;0-待出库;1-出库中;2-出库完成;3-已关闭;4-已取消 */

    private Integer isLock;/** 是否锁定;0-未锁定;1-已锁定 */

    private Long lockUserId;/** 锁定人ID */

    private Integer urgeTimes;/** 催料次数 */

    private Date lastUrgeTime;/** 最后催料时间 */

    private String deliveryWorkarea;/** 送料区域 对应 设备表的 配料区域 */

    private String deliveryWorkroom;/** 送料车间 对应 设备表的 加工区域 */

    private List<WorkOrderDetailEO> workOrderDetails;


    @Override
    public Serializable getId() {
        return null;
    }

    public Long getDeliveryWorkOrderId() {
        return deliveryWorkOrderId;
    }

    public void setDeliveryWorkOrderId(Long deliveryWorkOrderId) {
        this.deliveryWorkOrderId = deliveryWorkOrderId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Double getNotDeliveryQuantity() {
        return notDeliveryQuantity;
    }

    public void setNotDeliveryQuantity(Double notDeliveryQuantity) {
        this.notDeliveryQuantity = notDeliveryQuantity;
    }

    public Double getHasDeliveryQuantity() {
        return hasDeliveryQuantity;
    }

    public void setHasDeliveryQuantity(Double hasDeliveryQuantity) {
        this.hasDeliveryQuantity = hasDeliveryQuantity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public Long getLockUserId() {
        return lockUserId;
    }

    public void setLockUserId(Long lockUserId) {
        this.lockUserId = lockUserId;
    }

    public Integer getUrgeTimes() {
        return urgeTimes;
    }

    public void setUrgeTimes(Integer urgeTimes) {
        this.urgeTimes = urgeTimes;
    }

    public Date getLastUrgeTime() {
        return lastUrgeTime;
    }

    public void setLastUrgeTime(Date lastUrgeTime) {
        this.lastUrgeTime = lastUrgeTime;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public List<WorkOrderDetailEO> getWorkOrderDetails() {
        return workOrderDetails;
    }

    public void setWorkOrderDetails(List<WorkOrderDetailEO> workOrderDetails) {
        this.workOrderDetails = workOrderDetails;
    }

    public String getDeliveryWorkarea() {
        return deliveryWorkarea;
    }

    public void setDeliveryWorkarea(String deliveryWorkarea) {
        this.deliveryWorkarea = deliveryWorkarea;
    }

    public String getDeliveryWorkroom() {
        return deliveryWorkroom;
    }

    public void setDeliveryWorkroom(String deliveryWorkroom) {
        this.deliveryWorkroom = deliveryWorkroom;
    }
}
