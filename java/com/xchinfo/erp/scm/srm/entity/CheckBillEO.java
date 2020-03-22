package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author yuanchang
 * @date 2019/10/18
 */
@TableName("srm_check_bill")
@KeySequence("srm_check_bill")
public class CheckBillEO extends AbstractAuditableEntity<CheckBillEO> {
    private static final long serialVersionUID = 1040834336026877438L;

    @TableId(type = IdType.INPUT)
    private Long checkId;/** 主键 */

    private String voucherNo;/** 单号 */

    private Integer checkType;/** 对账类型;1:供应商；2:客户 */

    private Long supplierId;/** 供应商ID */

    private String supplierName;/** 供应商 */

    private Long customerId;/** 客户ID */

    private String customerName;/** 客户 */

    private Date checkDate;/** 对账日期 */

    private Date beginDate;/** 起始日期 */

    private Date endDate;/** 截止日期 */

    private Double totalQuantity;/** 总数量 */

    private String finalApprover;/** 审批人 */

    private Long finalApproverId;/** 审批人ID */

    private String approverIdea;/** 审批意见 */

    private Date approverTime;/** 审批时间 */

    private Integer status;/** 状态;0-无效;1-有效（审核完成-通过）；2-新建（待审核）；3-审核中；4-审核完成-不通过 */

    private Long orgId;/** 所属机构 */

    private String remark;/** 备注 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @TableField(exist = false)
    private String fullName;/** 归属机构全称 */

    @TableField(exist = false)
    private List<CheckBillDetailEO> checkBillDetailEOS;

    @TableField(exist = false)
    private String supplierCode;


    @Override
    public Serializable getId() {
        return checkId;
    }

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getFinalApprover() {
        return finalApprover;
    }

    public void setFinalApprover(String finalApprover) {
        this.finalApprover = finalApprover;
    }

    public Long getFinalApproverId() {
        return finalApproverId;
    }

    public void setFinalApproverId(Long finalApproverId) {
        this.finalApproverId = finalApproverId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<CheckBillDetailEO> getCheckBillDetailEOS() {
        return checkBillDetailEOS;
    }

    public void setCheckBillDetailEOS(List<CheckBillDetailEO> checkBillDetailEOS) {
        this.checkBillDetailEOS = checkBillDetailEOS;
    }

    public String getApproverIdea() {
        return approverIdea;
    }

    public void setApproverIdea(String approverIdea) {
        this.approverIdea = approverIdea;
    }

    public Date getApproverTime() {
        return approverTime;
    }

    public void setApproverTime(Date approverTime) {
        this.approverTime = approverTime;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
}
