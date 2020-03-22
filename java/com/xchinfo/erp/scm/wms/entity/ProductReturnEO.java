package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author yuanchang
 * @date 2019/5/17
 * @update
 */
@TableName("wms_product_return")
@KeySequence("wms_product_return")
public class ProductReturnEO extends AbstractAuditableEntity<ProductReturnEO> {

    private static final long serialVersionUID = 9178106479880702917L;

    @TableId(type = IdType.INPUT)
    private Long returnId;/** 主键 */

    private Integer returnType;/** 入库类型;1-产品退货; */

    private String voucherNo;/** 单据编号 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @TableField(exist = false)
    private String fullName;/** 机构全称 */

    private Date returnDate;/** 退货日期 */

    private Long customerId;/** 客户ID */

    private String customerCode;/** 客户编码 */

    private String customerName;/** 客户名称 */

    private Integer status;/** 状态;0-新建;1-发布;9-已关闭 */

    private String remarks;/** 备注 */

    private String returnUserId;/** 退货人ID */

    private String returnUserName;/** 退货人用户名 */

    @TableField(exist = false)
    private Double totalReturnQuantity;/**预计发货数*/

    @TableField(exist = false)
    private Double totalRelReturnQuantity;/**实际总发货数*/

    @TableField(exist = false)
    private Double count;/** 数量 */

    @TableField(exist = false)
    private List<ProductReturnDetailEO> details;

    @Override
    public Serializable getId() {
        return returnId;
    }



    /** 主键 */
    public Long getReturnId(){
        return this.returnId;
    }
    /** 主键 */
    public void setReturnId(Long returnId){
        this.returnId = returnId;
    }
    /** 入库类型;1-产品退货; */
    public Integer getReturnType(){
        return this.returnType;
    }
    /** 入库类型;1-产品退货; */
    public void setReturnType(Integer returnType){
        this.returnType = returnType;
    }
    /** 单据编号 */
    public String getVoucherNo(){
        return this.voucherNo;
    }
    /** 单据编号 */
    public void setVoucherNo(String voucherNo){
        this.voucherNo = voucherNo;
    }
    /** 退货日期 */
    public Date getReturnDate(){
        return this.returnDate;
    }
    /** 退货日期 */
    public void setReturnDate(Date returnDate){
        this.returnDate = returnDate;
    }
    /** 客户ID */
    public Long getCustomerId(){
        return this.customerId;
    }
    /** 客户ID */
    public void setCustomerId(Long customerId){
        this.customerId = customerId;
    }
    /** 客户编码 */
    public String getCustomerCode(){
        return this.customerCode;
    }
    /** 客户编码 */
    public void setCustomerCode(String customerCode){
        this.customerCode = customerCode;
    }
    /** 客户名称 */
    public String getCustomerName(){
        return this.customerName;
    }
    /** 客户名称 */
    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }
    /** 状态;0-新建;1-发布;2-完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-发布;2-完成 */
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

    public List<ProductReturnDetailEO> getDetails() {
        return details;
    }

    public void setDetails(List<ProductReturnDetailEO> details) {
        this.details = details;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public String getReturnUserId() {
        return returnUserId;
    }

    public void setReturnUserId(String returnUserId) {
        this.returnUserId = returnUserId;
    }

    public String getReturnUserName() {
        return returnUserName;
    }

    public void setReturnUserName(String returnUserName) {
        this.returnUserName = returnUserName;
    }

    public Double getTotalReturnQuantity() {
        return totalReturnQuantity;
    }

    public void setTotalReturnQuantity(Double totalReturnQuantity) {
        this.totalReturnQuantity = totalReturnQuantity;
    }

    public Double getTotalRelReturnQuantity() {
        return totalRelReturnQuantity;
    }

    public void setTotalRelReturnQuantity(Double totalRelReturnQuantity) {
        this.totalRelReturnQuantity = totalRelReturnQuantity;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
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
}
