package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhongye
 * @date 2019/5/14
 */
@TableName("srm_delivery_note")
@KeySequence("srm_delivery_note")
public class DeliveryNoteEO extends AbstractAuditableEntity<DeliveryNoteEO> {
    private static final long serialVersionUID = 1040834336026877438L;

    @TableId(type = IdType.INPUT)
    private Long deliveryNoteId;/** 主键 */

    private String voucherNo;/** 流水号 */

    private String erpVoucherNo1;/** ERP流水号(采购收货单(采购收货页面)) */

    private String erpVoucherNo2;/** ERP流水号(采购订单(采购收货页面)) */

    private String erpVoucherNo3;/** ERP流水号(委外收货单(委外收货页面)) */

    private String erpVoucherNo4;/** ERP流水号(委外出库单(委外收货页面)) */

    private Integer type;/** 单据类型;1-采购订单，2-委外订单。 */

    private Long supplierId;/** 供应商 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;/** 送货日期 */

    private String deliveryPersonName;/** 送货人 */

    private Double totalDeliveryQuantity;/** 计划送货总数量 */

    private Double totalReceiveQuantity;/** 总收货数量 */

    private Double totalQualifiedQuantity;/** 总合格数量 */

    private Double totalNotQualifiedQuantity;/** 总不合格数量 */

    private Integer status;/** 状态;0-新建;1-待送货;2-送货中;3-已完成 */

    private String receiveUserId;/** 收货人ID */

    private String receiveUserName;/** 收货人用户名 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date receiveDate;/** 收货日期 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @TableField(exist = false)
    private String orgCode;/** 归属机构编码 */

    @TableField(exist = false)
    private String fullName;/** 归属机构全称 */

    private String memo;/** 备注 */

    private Long chargeUserId;/** 经办人ID */

    private String chargeUserName;/** 经办人用户名 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdTime;/** 创建时间 */

    private Integer hasNotQualified;/** 是否有不合格;0-没有不合格;1-有不合格; */

    @TableField(exist = false)
    List<DeliveryNoteDetailEO> deliveryNoteDetails;

    @TableField(exist = false)
    private String supplierName;

    @TableField(exist = false)
    private String supplierCode;

    private Integer syncStatus; /** 采购到货(采购收货)U8同步状态(0-未同步，1-已提交(等待同步)，2-同步成功，3-部分成功，4-全部失败，5-已禁止) */

    private String syncResult;/** 采购到货(采购收货)U8同步结果 */


    @Override
    public Serializable getId() {
        return null;
    }

    /** 主键 */
    public Long getDeliveryNoteId(){
        return this.deliveryNoteId;
    }
    /** 主键 */
    public void setDeliveryNoteId(Long deliveryNoteId){
        this.deliveryNoteId = deliveryNoteId;
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
    /** 单据类型;1-采购订单，2-委外订单。 */
    public Integer getType(){
        return this.type;
    }
    /** 单据类型;1-采购订单，2-委外订单。 */
    public void setType(Integer type){
        this.type = type;
    }
    /** 送货日期 */
    public Date getDeliveryDate(){
        return this.deliveryDate;
    }
    /** 送货日期 */
    public void setDeliveryDate(Date deliveryDate){
        this.deliveryDate = deliveryDate;
    }
    /** 送货人 */
    public String getDeliveryPersonName(){
        return this.deliveryPersonName;
    }
    /** 送货人 */
    public void setDeliveryPersonName(String deliveryPersonName){
        this.deliveryPersonName = deliveryPersonName;
    }
    /** 计划送货总数量 */
    public Double getTotalDeliveryQuantity(){
        return this.totalDeliveryQuantity;
    }
    /** 计划送货总数量 */
    public void setTotalDeliveryQuantity(Double totalDeliveryQuantity){
        this.totalDeliveryQuantity = totalDeliveryQuantity;
    }
    /** 总收货数量 */
    public Double getTotalReceiveQuantity(){
        return this.totalReceiveQuantity;
    }
    /** 总收货数量 */
    public void setTotalReceiveQuantity(Double totalReceiveQuantity){
        this.totalReceiveQuantity = totalReceiveQuantity;
    }
    /** 总合格数量 */
    public Double getTotalQualifiedQuantity(){
        return this.totalQualifiedQuantity;
    }
    /** 总合格数量 */
    public void setTotalQualifiedQuantity(Double totalQualifiedQuantity){
        this.totalQualifiedQuantity = totalQualifiedQuantity;
    }
    /** 总不合格数量 */
    public Double getTotalNotQualifiedQuantity(){
        return this.totalNotQualifiedQuantity;
    }
    /** 总不合格数量 */
    public void setTotalNotQualifiedQuantity(Double totalNotQualifiedQuantity){
        this.totalNotQualifiedQuantity = totalNotQualifiedQuantity;
    }
    /** 状态;0-新建;1-待送货;2-送货中;3-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-待送货;2-送货中;3-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 收货人ID */
    public String getReceiveUserId(){
        return this.receiveUserId;
    }
    /** 收货人ID */
    public void setReceiveUserId(String receiveUserId){
        this.receiveUserId = receiveUserId;
    }
    /** 收货人用户名 */
    public String getReceiveUserName(){
        return this.receiveUserName;
    }
    /** 收货人用户名 */
    public void setReceiveUserName(String receiveUserName){
        this.receiveUserName = receiveUserName;
    }
    /** 收货日期 */
    public Date getReceiveDate(){
        return this.receiveDate;
    }
    /** 收货日期 */
    public void setReceiveDate(Date receiveDate){
        this.receiveDate = receiveDate;
    }
    /** 所属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 所属机构 */
    public void setOrgId(Long orgId){
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
    /** 归属机构全称 */
    public String getFullName() {
        return fullName;
    }
    /** 归属机构全称 */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    /** 备注 */
    public String getMemo(){
        return this.memo;
    }
    /** 备注 */
    public void setMemo(String memo){
        this.memo = memo;
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
    /** 创建时间 */
    public Date getCreatedTime(){
        return this.createdTime;
    }
    /** 创建时间 */
    public void setCreatedTime(Date createdTime){
        this.createdTime = createdTime;
    }
    /** 是否有不合格;0-没有不合格;1-有不合格; */
    public Integer getHasNotQualified(){
        return this.hasNotQualified;
    }
    /** 是否有不合格;0-没有不合格;1-有不合格; */
    public void setHasNotQualified(Integer hasNotQualified){
        this.hasNotQualified = hasNotQualified;
    }

    public List<DeliveryNoteDetailEO> getDeliveryNoteDetails() {
        return deliveryNoteDetails;
    }

    public void setDeliveryNoteDetails(List<DeliveryNoteDetailEO> deliveryNoteDetails) {
        this.deliveryNoteDetails = deliveryNoteDetails;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
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

    public String getErpVoucherNo4() {
        return erpVoucherNo4;
    }

    public void setErpVoucherNo4(String erpVoucherNo4) {
        this.erpVoucherNo4 = erpVoucherNo4;
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

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}
