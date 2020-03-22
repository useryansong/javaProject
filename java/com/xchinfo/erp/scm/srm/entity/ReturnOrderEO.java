package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhongye
 * @date 2019/5/24
 */
@TableName("srm_return_order")
@KeySequence("srm_return_order")
public class ReturnOrderEO extends AbstractAuditableEntity<ReturnOrderEO> {
    private static final long serialVersionUID = -2252934105659205231L;

    @TableId(type = IdType.INPUT)
    private Long returnOrderId;/** 主键 */

    private Integer type;/** 单据类型值;1-采购订单，2-委外订单。 */

    @TableField(exist = false)
    private String typeText;/** 单据类型描述;1-采购订单，2-委外订单。 */

    private String voucherNo;/** 流水号 */

    private Long supplierId;/** 供应商 */

    @TableField(exist = false)
    private String supplierName;/** 供应商名称 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @TableField(exist = false)
    private String orgCode;/** 归属机构代号 */

    @TableField(exist = false)
    private String fullName;/** 归属机构全称 */

    private Double planReturnQuantity;/** 预计退货总数量 */

    private Double actualReturnQuantity;/** 实际退货总数量 */

    private Date planReturnDate;/** 预计退货日期 */

    private Date actualReturnDate;/** 实际退货日期 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdTime;/** 创建日期 */

    private Integer status;/** 状态值;0-新建;1-待退货;2-退货中;3-已完成 */

    @TableField(exist = false)
    private String statusText;/** 状态描述;0-新建;1-待退货;2-退货中;3-已完成 */

    private Long returnUserId;/** 退货人ID */

    private String returnUserName;/** 退货人用户名 */

    private Long chargeUserId;/** 经办人ID */

    private String chargeUserName;/** 经办人用户名 */

    private String returnReason;/** 退货原因 */

    private String erpVoucherNo1; /** ERP流水号(采购退货单(采购退货页面)) */

    @TableField(exist = false)
    private List<ReturnOrderDetailEO> returnOrderDetails;


    @Override
    public Serializable getId() {
        return this.returnOrderId;
    }

    /** 主键 */
    public Long getReturnOrderId(){
        return this.returnOrderId;
    }
    /** 主键 */
    public void setReturnOrderId(Long returnOrderId){
        this.returnOrderId = returnOrderId;
    }
    /** 单据类型值;1-采购订单，2-委外订单。 */
    public Integer getType(){
        return this.type;
    }
    /** 单据类型值;1-采购订单，2-委外订单。 */
    public void setType(Integer type){
        this.type = type;
    }
    /** 单据类型描述;1-采购订单，2-委外订单。 */
    public String getTypeText() {
        return typeText;
    }
    /** 单据类型描述;1-采购订单，2-委外订单。 */
    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }
    /** 流水号 */
    public String getVoucherNo(){
        return this.voucherNo;
    }
    /** 流水号 */
    public void setVoucherNo(String voucherNo){
        this.voucherNo = voucherNo;
    }
    /** 供应商 */
    public Long getSupplierId(){
        return this.supplierId;
    }
    /** 供应商 */
    public void setSupplierId(Long supplierId){
        this.supplierId = supplierId;
    }
    /** 供应商名称 */
    public String getSupplierName() {
        return this.supplierName;
    }
    /** 供应商名称 */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    /** 归属机构 */
    public Long getOrgId() {
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    /** 归属机构名称 */
    public String getOrgName() {
        return this.orgName;
    }
    /** 归属机构名称 */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    /** 预计退货总数量 */
    public Double getPlanReturnQuantity(){
        return this.planReturnQuantity;
    }
    /** 预计退货总数量 */
    public void setPlanReturnQuantity(Double planReturnQuantity){
        this.planReturnQuantity = planReturnQuantity;
    }
    /** 实际退货总数量 */
    public Double getActualReturnQuantity(){
        return this.actualReturnQuantity;
    }
    /** 实际退货总数量 */
    public void setActualReturnQuantity(Double actualReturnQuantity){
        this.actualReturnQuantity = actualReturnQuantity;
    }
    /** 预计退货日期 */
    public Date getPlanReturnDate(){
        return this.planReturnDate;
    }
    /** 预计退货日期 */
    public void setPlanReturnDate(Date planReturnDate){
        this.planReturnDate = planReturnDate;
    }
    /** 实际退货日期 */
    public Date getActualReturnDate(){
        return this.actualReturnDate;
    }
    /** 实际退货日期 */
    public void setActualReturnDate(Date actualReturnDate){
        this.actualReturnDate = actualReturnDate;
    }
    /** 创建日期 */
    public Date getCreatedTime(){
        return this.createdTime;
    }
    /** 创建日期 */
    public void setCreatedTime(Date createdTime){
        this.createdTime = createdTime;
    }
    /** 状态值;0-新建;1-待退货;2-退货中;3-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态值;0-新建;1-待退货;2-退货中;3-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 状态描述;0-新建;1-待退货;2-退货中;3-已完成 */
    public String getStatusText() {
        return statusText;
    }
    /** 状态描述;0-新建;1-待退货;2-退货中;3-已完成 */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    /** 退货人ID */
    public Long getReturnUserId(){
        return this.returnUserId;
    }
    /** 退货人ID */
    public void setReturnUserId(Long returnUserId){
        this.returnUserId = returnUserId;
    }
    /** 退货人用户名 */
    public String getReturnUserName(){
        return this.returnUserName;
    }
    /** 退货人用户名 */
    public void setReturnUserName(String returnUserName){
        this.returnUserName = returnUserName;
    }
    /** 经办人ID */
    public Long getChargeUserId(){
        return this.chargeUserId;
    }
    /** 经办人ID */
    public void setChargeUserId(Long chargeUserId){
        this.chargeUserId = chargeUserId;
    }
    /** 经办人用户名 */
    public String getChargeUserName(){
        return this.chargeUserName;
    }
    /** 经办人用户名 */
    public void setChargeUserName(String chargeUserName){
        this.chargeUserName = chargeUserName;
    }
    /** 退货原因 */
    public String getReturnReason(){
        return this.returnReason;
    }
    /** 退货原因 */
    public void setReturnReason(String returnReason){
        this.returnReason = returnReason;
    }

    public List<ReturnOrderDetailEO> getReturnOrderDetails() {
        return this.returnOrderDetails;
    }

    public void setReturnOrderDetails(List<ReturnOrderDetailEO> returnOrderDetails) {
        this.returnOrderDetails = returnOrderDetails;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getErpVoucherNo1() {
        return erpVoucherNo1;
    }

    public void setErpVoucherNo1(String erpVoucherNo1) {
        this.erpVoucherNo1 = erpVoucherNo1;
    }
}
