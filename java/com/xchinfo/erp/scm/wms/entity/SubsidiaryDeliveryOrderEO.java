package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@TableName("wms_subsidiary_delivery_order")
@KeySequence("wms_subsidiary_delivery_order")
public class SubsidiaryDeliveryOrderEO extends AbstractAuditableEntity<SubsidiaryDeliveryOrderEO> {
    private static final long serialVersionUID = 3485811528895994775L;

    @TableId(type = IdType.INPUT)
    private Long deliveryId;/** 主键 */

    private Integer deliveryType;/** 出库类型;1-销售出库;2-生产领料;4-其他出库 */

    private String voucherNo;/** 单据编号 */

    private Long destinationOrgId;/** 业务对象机构 */

    private Long customerId;/** 客户ID */

    private Long supplierId;/** 供应商ID */

    private String destinationName;/** 业务对象名称 */

    private String destinationCode;/** 业务对象编码 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;/** 出库日期 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName; /** 归属机构名称 */

    private Integer stockGroupId;/** 库存组 */

    @TableField(exist = false)
    private String stockGroupName;/** 库存组名称 */

    private Integer userId;/** 库管员 */

    @TableField(exist = false)
    private String userName;/** 库管员 */

    private Integer status;/** 状态;0-新建;1-进行中;2-完成 */

    @TableField(exist = false)
    private List<SubsidiaryDeliveryOrderDetailEO> details;/** 出库单明细 */

    @TableField(exist = false)
    private Double totalDeliveryQuantity;/**预计发货数*/

    @TableField(exist = false)
    private Double totalRelDeliveryQuantity;/**实际总发货数*/

    private String deliveryUserId;/** 出货人ID */

    private String deliveryUserName;/** 出货人用户名 */

    @TableField(exist = false)
    private String text;/** 用于下拉框显示用 */

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
    public List<SubsidiaryDeliveryOrderDetailEO> getDetails() {
        return details;
    }
    /** 出库单明细 */
    public void setDetails(List<SubsidiaryDeliveryOrderDetailEO> details) {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
