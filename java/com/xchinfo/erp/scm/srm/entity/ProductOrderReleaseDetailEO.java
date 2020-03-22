package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/12/24
 */
@TableName("srm_product_order_release_detail")
@KeySequence("srm_product_order_release_detail")
public class ProductOrderReleaseDetailEO extends AbstractAuditableEntity<ProductOrderReleaseDetailEO> {
    private static final long serialVersionUID = -5219837646793438917L;

    @TableId(type = IdType.INPUT)
    private Long productOrderReleaseDetailId;/** 主键 */

    private Long productOrderId;/** 生产订单ID */

    private Long purchaseOrderId;/** 汇总后的关联ID */

    private String voucherNo;/** 汇总后的关联流水号 */

    private Integer type;/** 类型;1-采购，2-委外。 */

    private Integer status;/** 状态;0-新建，1-已发布，2-已关闭 */

    private Long supplierId;/** 供应商ID */

    private Long orgId;/** 归属机构 */

    private Long materialId;/** 物料ID */

    private String elementNo;/** 零件号 */

    private Date planArriveDate;/** 计划到货日期 */

    private Double planDeliveryQuantity;/** 计划送货数量 */

    private String memo;/** 备注 */

    @TableField(exist = false)
    private String materialName;

    @TableField(exist = false)
    private String inventoryCode;

    @TableField(exist = false)
    private String orgName;

    @TableField(exist = false)
    private String supplierName;

    @TableField(exist = false)
    private Double snp;

    @TableField(exist = false)
    private Double sumPlanDeliveryQuantity;

    @TableField(exist = false)
    private Double sumRelDeliveryQuantity;

    @TableField(exist = false)
    private List<ProductOrderReleaseDetailEO> productOrderReleaseDetails;


    @Override
    public Serializable getId() {
        return this.productOrderReleaseDetailId;
    }

    public Long getProductOrderReleaseDetailId() {
        return productOrderReleaseDetailId;
    }

    public void setProductOrderReleaseDetailId(Long productOrderReleaseDetailId) {
        this.productOrderReleaseDetailId = productOrderReleaseDetailId;
    }

    public Long getProductOrderId() {
        return productOrderId;
    }

    public void setProductOrderId(Long productOrderId) {
        this.productOrderId = productOrderId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
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

    public Date getPlanArriveDate() {
        return planArriveDate;
    }

    public void setPlanArriveDate(Date planArriveDate) {
        this.planArriveDate = planArriveDate;
    }

    public Double getPlanDeliveryQuantity() {
        return planDeliveryQuantity;
    }

    public void setPlanDeliveryQuantity(Double planDeliveryQuantity) {
        this.planDeliveryQuantity = planDeliveryQuantity;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Double getSnp() {
        return snp;
    }

    public void setSnp(Double snp) {
        this.snp = snp;
    }

    public Double getSumPlanDeliveryQuantity() {
        return sumPlanDeliveryQuantity;
    }

    public void setSumPlanDeliveryQuantity(Double sumPlanDeliveryQuantity) {
        this.sumPlanDeliveryQuantity = sumPlanDeliveryQuantity;
    }

    public List<ProductOrderReleaseDetailEO> getProductOrderReleaseDetails() {
        return productOrderReleaseDetails;
    }

    public void setProductOrderReleaseDetails(List<ProductOrderReleaseDetailEO> productOrderReleaseDetails) {
        this.productOrderReleaseDetails = productOrderReleaseDetails;
    }

    public Double getSumRelDeliveryQuantity() {
        return sumRelDeliveryQuantity;
    }

    public void setSumRelDeliveryQuantity(Double sumRelDeliveryQuantity) {
        this.sumRelDeliveryQuantity = sumRelDeliveryQuantity;
    }
}
