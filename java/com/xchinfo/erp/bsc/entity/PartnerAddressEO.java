package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_partner_address")
@KeySequence("bsc_partner_address")
public class PartnerAddressEO extends AbstractAuditableEntity<PartnerAddressEO> {
    private static final long serialVersionUID = 7571665430469266497L;

    @TableId(type = IdType.INPUT)
    private Long addressId;/** 地址ID */

    @NotNull(message = "类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 6, message = "类型长度不能超过 6 个字符")
    @BusinessLogField("类型")
    private Integer partnerType;/** 类型;1-供应商;2-客户 */

    @NotNull(message = "伙伴ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 20, message = "伙伴ID长度不能超过 20 个字符")
    @BusinessLogField("伙伴ID")
    private Long partnerId;/** 伙伴ID */

    @NotBlank(message = "地点名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "地点名称长度不能超过 200 个字符")
    @BusinessLogField("地点名称")
    private String name;/** 地点名称 */

    @NotBlank(message = "地址不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 1000, message = "地址长度不能超过 1000 个字符")
    @BusinessLogField("地点名称")
    private String address;/** 地址 */

    @Length(max = 200, message = "联系人长度不能超过 200 个字符")
    private String contact;/** 联系人 */

    @Length(max = 100, message = "电话长度不能超过 100 个字符")
    private String phone;/** 电话 */

    @Length(max = 100, message = "手机长度不能超过 100 个字符")
    private String mobile;/** 手机 */

    @Length(max = 100, message = "电子邮箱长度不能超过 100 个字符")
    @Email
    private String email;/** 电子邮箱 */

    private Integer defaultRecieveDelivery;/** 默认收发货地址 */

    private Integer defaultInvoice;/** 默认开票地址 */

    private Integer defaultCollectPayment;/** 默认收付款地址 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态 0-无效;1-有效（审核完成-通过）；2-新建（待审核）；3-审核中；4-审核完成-不通过 */

    private String finalApprover;

    private Long finalApproverId;

    private String remarks;/** 备注 */

    @TableField(exist = false)
    private String customerName;/**客户名称*/

    @Override
    public Serializable getId() {
        return this.addressId;
    }

    /** 地址ID */
    public Long getAddressId(){
        return this.addressId;
    }
    /** 地址ID */
    public void setAddressId(Long addressId){
        this.addressId = addressId;
    }
    /** 类型;1-供应商;2-客户 */
    public Integer getPartnerType(){
        return this.partnerType;
    }
    /** 类型;1-供应商;2-客户 */
    public void setPartnerType(Integer partnerType){
        this.partnerType = partnerType;
    }
    /** 伙伴ID */
    public Long getPartnerId(){
        return this.partnerId;
    }
    /** 伙伴ID */
    public void setPartnerId(Long partnerId){
        this.partnerId = partnerId;
    }
    /** 地点名称 */
    public String getName(){
        return this.name;
    }
    /** 地点名称 */
    public void setName(String name){
        this.name = name;
    }
    /** 地址 */
    public String getAddress(){
        return this.address;
    }
    /** 地址 */
    public void setAddress(String address){
        this.address = address;
    }
    /** 联系人 */
    public String getContact(){
        return this.contact;
    }
    /** 联系人 */
    public void setContact(String contact){
        this.contact = contact;
    }
    /** 电话 */
    public String getPhone(){
        return this.phone;
    }
    /** 电话 */
    public void setPhone(String phone){
        this.phone = phone;
    }
    /** 手机 */
    public String getMobile(){
        return this.mobile;
    }
    /** 手机 */
    public void setMobile(String mobile){
        this.mobile = mobile;
    }
    /** 电子邮箱 */
    public String getEmail(){
        return this.email;
    }
    /** 电子邮箱 */
    public void setEmail(String email){
        this.email = email;
    }
    /** 默认收发货地址 */
    public Integer getDefaultRecieveDelivery(){
        return this.defaultRecieveDelivery;
    }
    /** 默认收发货地址 */
    public void setDefaultRecieveDelivery(Integer defaultRecieveDelivery){
        this.defaultRecieveDelivery = defaultRecieveDelivery;
    }
    /** 默认开票地址 */
    public Integer getDefaultInvoice(){
        return this.defaultInvoice;
    }
    /** 默认开票地址 */
    public void setDefaultInvoice(Integer defaultInvoice){
        this.defaultInvoice = defaultInvoice;
    }
    /** 默认收付款地址 */
    public Integer getDefaultCollectPayment(){
        return this.defaultCollectPayment;
    }
    /** 默认收付款地址 */
    public void setDefaultCollectPayment(Integer defaultCollectPayment){
        this.defaultCollectPayment = defaultCollectPayment;
    }
    /** 备注 */
    public String getRemarks(){
        return this.remarks;
    }
    /** 备注 */
    public void setRemarks(String remarks){
        this.remarks = remarks;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
