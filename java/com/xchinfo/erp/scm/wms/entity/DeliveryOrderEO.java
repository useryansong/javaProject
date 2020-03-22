package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@TableName("wms_delivery_order")
@KeySequence("wms_delivery_order")
public class DeliveryOrderEO extends AbstractAuditableEntity<DeliveryOrderEO> {
    private static final long serialVersionUID = 3485811528895994775L;

    @TableId(type = IdType.INPUT)
    private Long deliveryId;/** 主键 */

    private Long vehiclePlanId;/** 排车计划ID */

    private String deliveryTime;/** 发货时间 */

    private String vehicleType;/** 车型 */

    private Integer deliveryType;/** 出库类型;1-销售出库;2-生产领料;3-辅料出库;6-其他出库 */

    @BusinessLogField("其他出库类型")
    private Integer childDeliveryType;/** 其他出库类型:4-采购退货出库;5-委外退货出库 */

    @BusinessLogField("单据来源")
    private Integer voucherType;/** 单据来源:1-内部自动创建;2-用户手动创建 */

    private String voucherNo;/** 单据编号 */

    private String erpVoucherNo1;/** ERP单据编号(委外发货单(周计划管理中委外发货)) */

    private String erpVoucherNo2;/** ERP单据编号(销售发货(送货计划页面)) */

    private String erpVoucherNo3;/** ERP单据编号(销售订单(送货计划页面)) */

    private String erpVoucherNo4;/** ERP流水号(生产领料单导出(生产领料单页面)) */

    private Long destinationOrgId;/** 业务对象机构 */

    private Long customerId;/** 客户ID */

    private Long supplierId;/** 供应商ID */

    @TableField(exist = false)
    private String supplierName;/** 供应商名称 */

    @TableField(exist = false)
    private String supplierAddress;/** 供应商名称 */

    @TableField(exist = false)
    private String supplierPhone;/** 供应商电话 */

    @TableField(exist = false)
    private String supplierContactPhone;/** 供应商联系人电话 */

    @TableField(exist = false)
    private String supplierContactName;/** 供应商联系人姓名 */

    @TableField(exist = false)
    private String supplierErpCode;/** 供应商ERP编号 */

    private String destinationName;/** 业务对象名称 */

    private String destinationCode;/** 业务对象编码 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;/** 出库日期 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName; /** 归属机构名称 */

    @TableField(exist = false)
    private String orgCode; /** 归属机构名称 */

    @TableField(exist = false)
    private String fullName;/** 机构全称 */

    @TableField(exist = false)
    private Integer type; /** 库存子类型传递 */

    private Integer stockGroupId;/** 库存组 */

    @TableField(exist = false)
    private String stockGroupName;/** 库存组名称 */

    private Integer userId;/** 库管员 */

    @TableField(exist = false)
    private String userName;/** 库管员 */

    private Integer status;/** 状态;0-新建;1-进行中;2-完成 */

    private Integer isInnerTran;/** 是否内部交易 0-否，1-是*/

    @TableField(exist = false)
    private List<DeliveryOrderDetailEO> details;/** 出库单明细 */

    @TableField(exist = false)
    private Double totalDeliveryQuantity;/**预计发货数*/

    @TableField(exist = false)
    private Double totalRelDeliveryQuantity;/**实际总发货数*/

    private String deliveryUserId;/** 出货人ID */

    private String deliveryUserName;/** 出货人用户名 */


    private Long relationId;/** 关联ID */

    @TableField(exist = false)
    private String customStringField1;/** 配置的打印导出文件名 */

    private String trainNumber;/** 车次 */

    private String memo;/** 备注 */

    @TableField(exist = false)
    private Double sumDeliveryAmount;

    @TableField(exist = false)
    private String contactAddress;

    private Integer syncStatus; /** 生产领料(生产日报)U8同步状态(0-未同步，1-已提交(等待同步)，2-同步成功，3-部分成功，4-全部失败，5-已禁止) */

    private String syncResult;/** 生产领料(生产日报)U8同步结果 */

    private Integer syncPdStatus; /** 销售出库(送货计划)U8同步状态(0-未同步，1-已提交(等待同步)，2-同步成功，3-部分成功，4-全部失败，5-已禁止) */

    private String syncPdResult;/** 销售出库(送货计划)U8同步结果 */


    @Override
    public Serializable getId() {
        return this.deliveryId;
    }

    /** 主键 */
    public Long getDeliveryId() {
        return deliveryId;
    }
    /** 主键 */
    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
    /** 出库类型;1-销售出库;2-生产领料;3-辅料出库;4-其他出库 */
    public Integer getDeliveryType() {
        return deliveryType;
    }
    /** 出库类型;1-销售出库;2-生产领料;3-辅料出库;4-其他出库 */
    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }
    /** 单据编号 */
    public String getVoucherNo() {
        return voucherNo;
    }
    /** 单据编号 */
    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }
    /** 客户ID */
    public Long getCustomerId(){
        return this.customerId;
    }
    /** 客户ID */
    public void setCustomerId(Long customerId){
        this.customerId = customerId;
    }
    /** 供应商ID */
    public Long getSupplierId(){
        return this.supplierId;
    }
    /** 供应商ID */
    public void setSupplierId(Long supplierId){
        this.supplierId = supplierId;
    }
    /** 业务对象名称 */
    public String getDestinationName(){
        return this.destinationName;
    }
    /** 业务对象名称 */
    public void setDestinationName(String destinationName){
        this.destinationName = destinationName;
    }
    /** 业务对象编码 */
    public String getDestinationCode() {
        return this.destinationCode;
    }
    /** 业务对象编码 */
    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }
    ;/** 出库日期 */
    public Date getDeliveryDate() {
        return deliveryDate;
    }
    ;/** 出库日期 */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    /** 归属机构 */
    public Long getOrgId() {
        return orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    /** 库存组 */
    public Integer getStockGroupId() {
        return stockGroupId;
    }
    /** 库存组 */
    public void setStockGroupId(Integer stockGroupId) {
        this.stockGroupId = stockGroupId;
    }
    /** 库存组名称 */
    public String getStockGroupName() {
        return stockGroupName;
    }
    /** 库存组名称 */
    public void setStockGroupName(String stockGroupName) {
        this.stockGroupName = stockGroupName;
    }

    /** 库管员 */
    public Integer getUserId() {
        return userId;
    }
    /** 库管员 */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    /** 库管员 */
    public String getUserName() {
        return userName;
    }
    /** 库管员 */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /** 状态;0-新建;1-进行中;2-完成 */
    public Integer getStatus() {
        return status;
    }
    /** 状态;0-新建;1-进行中;2-完成 */
    public void setStatus(Integer status) {
        this.status = status;
    }
    /** 出库单明细 */
    public List<DeliveryOrderDetailEO> getDetails() {
        return details;
    }
    /** 出库单明细 */
    public void setDetails(List<DeliveryOrderDetailEO> details) {
        this.details = details;
    }

    public Double getTotalDeliveryQuantity() {
        return totalDeliveryQuantity;
    }

    public void setTotalDeliveryQuantity(Double totalDeliveryQuantity) {
        this.totalDeliveryQuantity = totalDeliveryQuantity;
    }

    public Double getTotalRelDeliveryQuantity() {
        return totalRelDeliveryQuantity;
    }

    public void setTotalRelDeliveryQuantity(Double totalRelDeliveryQuantity) {
        this.totalRelDeliveryQuantity = totalRelDeliveryQuantity;
    }

    public String getDeliveryUserId() {
        return deliveryUserId;
    }

    public void setDeliveryUserId(String deliveryUserId) {
        this.deliveryUserId = deliveryUserId;
    }

    public String getDeliveryUserName() {
        return deliveryUserName;
    }

    public void setDeliveryUserName(String deliveryUserName) {
        this.deliveryUserName = deliveryUserName;
    }

    public Long getDestinationOrgId() {
        return destinationOrgId;
    }

    public void setDestinationOrgId(Long destinationOrgId) {
        this.destinationOrgId = destinationOrgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Long getVehiclePlanId() {
        return vehiclePlanId;
    }

    public void setVehiclePlanId(Long vehiclePlanId) {
        this.vehiclePlanId = vehiclePlanId;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCustomStringField1() {
        return customStringField1;
    }

    public void setCustomStringField1(String customStringField1) {
        this.customStringField1 = customStringField1;
    }

    public Integer getIsInnerTran() {
        return isInnerTran;
    }

    public void setIsInnerTran(Integer isInnerTran) {
        this.isInnerTran = isInnerTran;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    /** 备注 */
    public String getMemo(){
        return this.memo;
    }
    /** 备注 */
    public void setMemo(String memo){
        this.memo = memo;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Double getSumDeliveryAmount() {
        return sumDeliveryAmount;
    }

    public void setSumDeliveryAmount(Double sumDeliveryAmount) {
        this.sumDeliveryAmount = sumDeliveryAmount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getChildDeliveryType() {
        return childDeliveryType;
    }

    public void setChildDeliveryType(Integer childDeliveryType) {
        this.childDeliveryType = childDeliveryType;
    }

    public Integer getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(Integer voucherType) {
        this.voucherType = voucherType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getErpVoucherNo1() {
        return erpVoucherNo1;
    }

    public void setErpVoucherNo1(String erpVoucherNo1) {
        this.erpVoucherNo1 = erpVoucherNo1;
    }

    public String getErpVoucherNo2() {
        return erpVoucherNo2;
    }

    public void setErpVoucherNo2(String erpVoucherNo2) {
        this.erpVoucherNo2 = erpVoucherNo2;
    }

    public String getErpVoucherNo3() {
        return erpVoucherNo3;
    }

    public void setErpVoucherNo3(String erpVoucherNo3) {
        this.erpVoucherNo3 = erpVoucherNo3;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public String getSupplierContactPhone() {
        return supplierContactPhone;
    }

    public void setSupplierContactPhone(String supplierContactPhone) {
        this.supplierContactPhone = supplierContactPhone;
    }

    public String getSupplierContactName() {
        return supplierContactName;
    }

    public void setSupplierContactName(String supplierContactName) {
        this.supplierContactName = supplierContactName;
    }

    public String getSupplierErpCode() {
        return supplierErpCode;
    }

    public void setSupplierErpCode(String supplierErpCode) {
        this.supplierErpCode = supplierErpCode;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(String syncResult) {
        this.syncResult = syncResult;
    }

    public Integer getSyncPdStatus() {
        return syncPdStatus;
    }

    public void setSyncPdStatus(Integer syncPdStatus) {
        this.syncPdStatus = syncPdStatus;
    }

    public String getSyncPdResult() {
        return syncPdResult;
    }

    public void setSyncPdResult(String syncPdResult) {
        this.syncPdResult = syncPdResult;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getErpVoucherNo4() {
        return erpVoucherNo4;
    }

    public void setErpVoucherNo4(String erpVoucherNo4) {
        this.erpVoucherNo4 = erpVoucherNo4;
    }
}
