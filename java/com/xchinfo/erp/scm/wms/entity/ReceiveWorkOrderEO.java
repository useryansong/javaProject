package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@TableName("wms_receive_work_order")
@KeySequence("wms_receive_work_order")
public class ReceiveWorkOrderEO extends AbstractAuditableEntity<ReceiveWorkOrderEO> {
    private static final long serialVersionUID = 6821562678849959587L;

    @TableId(type = IdType.INPUT)
    private Long receiveWorkOrderId;/** 主键，入库工单ID */

    private String workOrderId;/** 工单GUID */

    private Long orgId;/** 归属机构 */

    private Integer type;/** 工单类型;1-采购入库;2-成品入库 */

    private Long relationId;/** 关联ID */

    private Long materialId;/** 物料ID */

    private String elementNo;/** 物料零件号 */

    private String materialName;/** 物料名称 */

    private Date orderDate;/** 工单日期 */

    private Double notReceiveQuantity;/** 待入库数量 */

    private Double hasReceiveQuantity;/** 已入库数量 */

    private Integer status;/** 状态;0-待入库;1-入库中;2-入库完成;3-已关闭;4-已取消 */

    private Integer isLock;/** 是否锁定;0-未锁定;1-已锁定 */

    private Long lockUserId;/** 锁定人ID */


    @Override
    public Serializable getId() {
        return receiveWorkOrderId;
    }

    public Long getReceiveWorkOrderId() {
        return receiveWorkOrderId;
    }

    public void setReceiveWorkOrderId(Long receiveWorkOrderId) {
        this.receiveWorkOrderId = receiveWorkOrderId;
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

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
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

    public Double getNotReceiveQuantity() {
        return notReceiveQuantity;
    }

    public void setNotReceiveQuantity(Double notReceiveQuantity) {
        this.notReceiveQuantity = notReceiveQuantity;
    }

    public Double getHasReceiveQuantity() {
        return hasReceiveQuantity;
    }

    public void setHasReceiveQuantity(Double hasReceiveQuantity) {
        this.hasReceiveQuantity = hasReceiveQuantity;
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
}
