package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@TableName("wms_work_order_detail")
@KeySequence("wms_work_order_detail")
public class WorkOrderDetailEO extends AbstractAuditableEntity<WorkOrderDetailEO> {
    private static final long serialVersionUID = -6840154818204718660L;

    @TableId(type = IdType.INPUT)
    private Long workOrderDetailId;/** 主键，出入库工单明细ID */

    private String workOrderId;/** 工单GUID(关联入库或出库工单表的工单GUID) */

    private Long materialId;/** 物料ID */

    private String elementNo;/** 物料零件号 */

    private String materialName;/** 物料名称 */

    private Long warehouseId;/** 仓库ID */

    private Long warehouseLocationId;/** 库位ID */

    private String warehouseLocationBarCode;/** 库位条码 */

    private Integer type;/** 类型;1-入库;2-出库 */

    private Double quantity;/** 出入库数量(入库为正数,出库为负数) */

    @TableField(exist = false)
    private Double amount;/** 库存 */

    private Integer contain;/** 容器数 */

    private Integer snp;/** 最小包装数 */

    private Integer status;/** 状态(冗余字段,先保留) */

    private Date operateTime;/** 操作时间(实际入库或出库的时间) */

    private Long operateUserId;/** 操作人ID */

    private Double remainingQuantity;/** 剩余数量 */

    private Integer operationType;/** 操作类型(1-采购入库,2-配料出库,3-成品入库,4-成品出库,5-移库转入,6-移库转出,7-车间退料入库,8-成品退料入库) */

    private String remark;/** 备注 */

    private Long transferOutDetailId;/** 移库转出记录ID */

    @TableField(exist = false)
    private List<WorkOrderDetailEO> records;

    @TableField(exist = false)
    private Double transferQuantity;/** 转移数量 */

    @TableField(exist = false)
    private Long inLocationId;/** 转入库位 */

    @TableField(exist = false)
    private Integer receiveType;/** 入库工单类型;1-采购入库;2-成品入库;3-车间退料 */


    @Override
    public Serializable getId() {
        return null;
    }

    public Long getWorkOrderDetailId() {
        return workOrderDetailId;
    }

    public void setWorkOrderDetailId(Long workOrderDetailId) {
        this.workOrderDetailId = workOrderDetailId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
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

    public Long getWarehouseLocationId() {
        return warehouseLocationId;
    }

    public void setWarehouseLocationId(Long warehouseLocationId) {
        this.warehouseLocationId = warehouseLocationId;
    }

    public String getWarehouseLocationBarCode() {
        return warehouseLocationBarCode;
    }

    public void setWarehouseLocationBarCode(String warehouseLocationBarCode) {
        this.warehouseLocationBarCode = warehouseLocationBarCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getContain() {
        return contain;
    }

    public void setContain(Integer contain) {
        this.contain = contain;
    }

    public Integer getSnp() {
        return snp;
    }

    public void setSnp(Integer snp) {
        this.snp = snp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Long getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Long operateUserId) {
        this.operateUserId = operateUserId;
    }

    public List<WorkOrderDetailEO> getRecords() {
        return records;
    }

    public void setRecords(List<WorkOrderDetailEO> records) {
        this.records = records;
    }

    public Double getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Double remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTransferQuantity() {
        return transferQuantity;
    }

    public void setTransferQuantity(Double transferQuantity) {
        this.transferQuantity = transferQuantity;
    }

    public Long getInLocationId() {
        return inLocationId;
    }

    public void setInLocationId(Long inLocationId) {
        this.inLocationId = inLocationId;
    }

    public Integer getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(Integer receiveType) {
        this.receiveType = receiveType;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getTransferOutDetailId() {
        return transferOutDetailId;
    }

    public void setTransferOutDetailId(Long transferOutDetailId) {
        this.transferOutDetailId = transferOutDetailId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }
}
