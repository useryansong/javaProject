
package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_supplier")
@KeySequence("bsc_supplier")
public class SupplierEO extends AbstractAuditableEntity<SupplierEO> {
    private static final long serialVersionUID = 8424582347484669525L;

    @TableId(type = IdType.INPUT)
    private Long supplierId;/** 供应商ID */

    @Length(max = 100, message = "编码长度不能超过 100 个字符")
    @BusinessLogField("供应商编码")
    private String supplierCode;/** 供应商编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("供应商名称")
    private String supplierName;/** 供应商名称 */

    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

    @Length(max = 100, message = "纳税登记号长度不能超过 100 个字符")
    private String taxNo;/** 纳税登记号 */

    private String supplierType;/** 供应商类型 */

    @Length(max = 100, message = "ERP编码长度不能超过 100 个字符")
    private String erpCode;/** ERP编码 */

    @Length(max = 1000, message = "地址长度不能超过 1000 个字符")
    private String address;/** 地址 */

    @Length(max = 100, message = "电话长度不能超过 100 个字符")
    private String phone;/** 电话 */

    @Length(max = 100, message = "邮政编码不能超过 100 个字符")
    private String zipCode;/** 邮政编码 */

    @Length(max = 1000, message = "公司网址不能超过 1000 个字符")
    private String url;/** 公司网址 */

    @Length(max = 200, message = "银行名称不能超过 200 个字符")
    private String bankName;/** 银行名称 */

    @Length(max = 200, message = "开户行不能超过 200 个字符")
    private String depositBank;/** 开户行 */

    @Length(max = 200, message = "银行户名不能超过 200 个字符")
    private String bankBookName;/** 银行户名 */

    @Length(max = 100, message = "银行账号不能超过 100 个字符")
    private String bankAccount;/** 银行账号 */

    @Length(max = 200, message = "联系人姓名不能超过 200 个字符")
    private String contactName;/** 联系人姓名 */

    @Length(max = 200, message = "职务姓名不能超过 200 个字符")
    private String jobTitle;/** 职务 */

    @Length(max = 1000, message = "联系人地址不能超过 1000 个字符")
    private String contactAddress;/** 联系人地址 */

    @Length(max = 100, message = "联系人电话不能超过 100 个字符")
    private String contactPhone;/** 联系人电话 */

    @Length(max = 100, message = "联系人手机不能超过 100 个字符")
    private String contactMobile;/** 联系人手机 */

    @Length(max = 100, message = "联系人传真不能超过 100 个字符")
    private String contactFax;/** 联系人传真 */

    @Length(max = 100, message = "联系人邮箱不能超过 100 个字符")
    @Email
    private String contactEmail;/** 联系人邮箱 */

    private String shortName;/** 供应商简称(外部系统调用使用) */

    @TableField(exist = false)
    private String password;/** 密码(外部系统调用使用,供应商账户的密码)*/

    private Integer status;/** 状态;0-无效;1-有效 */

    private Integer accountStatus;/** 状态;0-无效;1-有效 */

    private String customStringField1;/** 自定义字符1 */

    private String customStringField2;/** 自定义字符2 */

    private String customStringField3;/** 自定义字符3 */

    private String customStringField4;/** 自定义字符4 */

    private String customStringField5;/** 自定义字符5 */

    private Double customNumField1;/** 自定义数值1 */

    private Double customNumField2;/** 自定义数值2 */

    private Double customNumField3;/** 自定义数值3 */

    private Double customNumField4;/** 自定义数值4 */

    private Double customNumField5;/** 自定义数值5 */

    private Date customDateField1;/** 自定义日期1 */

    private Date customDateField2;/** 自定义日期2 */

    private Date customDateField3;/** 自定义日期3 */

    private Date customDateField4;/** 自定义日期4 */

    private Date customDateField5;/** 自定义日期5 */

    @TableField(exist = false)
    private List<PartnerContactEO> contacts;

    @TableField(exist = false)
    private List<PartnerAddressEO> addresses;

    @Override
    public Serializable getId() {
        return this.supplierId;
    }

    /** 供应商ID */
    public Long getSupplierId(){
        return this.supplierId;
    }
    /** 供应商ID */
    public void setSupplierId(Long supplierId){
        this.supplierId = supplierId;
    }
    /** 供应商编码 */
    public String getSupplierCode(){
        return this.supplierCode;
    }
    /** 供应商编码 */
    public void setSupplierCode(String supplierCode){
        this.supplierCode = supplierCode;
    }
    /** 供应商名称 */
    public String getSupplierName(){
        return this.supplierName;
    }
    /** 供应商名称 */
    public void setSupplierName(String supplierName){
        this.supplierName = supplierName;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 纳税登记号 */
    public String getTaxNo(){
        return this.taxNo;
    }
    /** 纳税登记号 */
    public void setTaxNo(String taxNo){
        this.taxNo = taxNo;
    }
    /** 供应商类型 */
    public String getSupplierType(){
        return this.supplierType;
    }
    /** 供应商类型 */
    public void setSupplierType(String supplierType){
        this.supplierType = supplierType;
    }
    /** ERP编码 */
    public String getErpCode() {
        return erpCode;
    }
    /** ERP编码 */
    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }
    /** 地址 */
    public String getAddress(){
        return this.address;
    }
    /** 地址 */
    public void setAddress(String address){
        this.address = address;
    }
    /** 电话 */
    public String getPhone(){
        return this.phone;
    }
    /** 电话 */
    public void setPhone(String phone){
        this.phone = phone;
    }
    /** 邮政编码 */
    public String getZipCode(){
        return this.zipCode;
    }
    /** 邮政编码 */
    public void setZipCode(String zipCode){
        this.zipCode = zipCode;
    }
    /** 公司网址 */
    public String getUrl(){
        return this.url;
    }
    /** 公司网址 */
    public void setUrl(String url){
        this.url = url;
    }
    /** 银行名称 */
    public String getBankName(){
        return this.bankName;
    }
    /** 银行名称 */
    public void setBankName(String bankName){
        this.bankName = bankName;
    }
    /** 开户行 */
    public String getDepositBank(){
        return this.depositBank;
    }
    /** 开户行 */
    public void setDepositBank(String depositBank){
        this.depositBank = depositBank;
    }
    /** 银行户名 */
    public String getBankBookName(){
        return this.bankBookName;
    }
    /** 银行户名 */
    public void setBankBookName(String bankBookName){
        this.bankBookName = bankBookName;
    }
    /** 银行账号 */
    public String getBankAccount(){
        return this.bankAccount;
    }
    /** 银行账号 */
    public void setBankAccount(String bankAccount){
        this.bankAccount = bankAccount;
    }
    /** 联系人姓名 */
    public String getContactName(){
        return this.contactName;
    }
    /** 联系人姓名 */
    public void setContactName(String contactName){
        this.contactName = contactName;
    }
    /** 职务 */
    public String getJobTitle(){
        return this.jobTitle;
    }
    /** 职务 */
    public void setJobTitle(String jobTitle){
        this.jobTitle = jobTitle;
    }
    /** 联系人地址 */
    public String getContactAddress(){
        return this.contactAddress;
    }
    /** 联系人地址 */
    public void setContactAddress(String contactAddress){
        this.contactAddress = contactAddress;
    }
    /** 联系人电话 */
    public String getContactPhone(){
        return this.contactPhone;
    }
    /** 联系人电话 */
    public void setContactPhone(String contactPhone){
        this.contactPhone = contactPhone;
    }
    /** 联系人手机 */
    public String getContactMobile(){
        return this.contactMobile;
    }
    /** 联系人手机 */
    public void setContactMobile(String contactMobile){
        this.contactMobile = contactMobile;
    }
    /** 联系人传真 */
    public String getContactFax(){
        return this.contactFax;
    }
    /** 联系人传真 */
    public void setContactFax(String contactFax){
        this.contactFax = contactFax;
    }
    /** 联系人邮箱 */
    public String getContactEmail(){
        return this.contactEmail;
    }
    /** 联系人邮箱 */
    public void setContactEmail(String contactEmail){
        this.contactEmail = contactEmail;
    }
    /** 状态;0-无效;1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 账号状态;0-未开通;1-已开通 */
    public Integer getAccountStatus() {
        return accountStatus;
    }
    /** 账号状态;0-未开通;1-已开通 */
    public void setAccountStatus(Integer accountStatus) {
        this.accountStatus = accountStatus;
    }

    /** 自定义字符1 */
    public String getCustomStringField1(){
        return this.customStringField1;
    }
    /** 自定义字符1 */
    public void setCustomStringField1(String customStringField1){
        this.customStringField1 = customStringField1;
    }
    /** 自定义字符2 */
    public String getCustomStringField2(){
        return this.customStringField2;
    }
    /** 自定义字符2 */
    public void setCustomStringField2(String customStringField2){
        this.customStringField2 = customStringField2;
    }
    /** 自定义字符3 */
    public String getCustomStringField3(){
        return this.customStringField3;
    }
    /** 自定义字符3 */
    public void setCustomStringField3(String customStringField3){
        this.customStringField3 = customStringField3;
    }
    /** 自定义字符4 */
    public String getCustomStringField4(){
        return this.customStringField4;
    }
    /** 自定义字符4 */
    public void setCustomStringField4(String customStringField4){
        this.customStringField4 = customStringField4;
    }
    /** 自定义字符5 */
    public String getCustomStringField5(){
        return this.customStringField5;
    }
    /** 自定义字符5 */
    public void setCustomStringField5(String customStringField5){
        this.customStringField5 = customStringField5;
    }
    /** 自定义数值1 */
    public Double getCustomNumField1(){
        return this.customNumField1;
    }
    /** 自定义数值1 */
    public void setCustomNumField1(Double customNumField1){
        this.customNumField1 = customNumField1;
    }
    /** 自定义数值2 */
    public Double getCustomNumField2(){
        return this.customNumField2;
    }
    /** 自定义数值2 */
    public void setCustomNumField2(Double customNumField2){
        this.customNumField2 = customNumField2;
    }
    /** 自定义数值3 */
    public Double getCustomNumField3(){
        return this.customNumField3;
    }
    /** 自定义数值3 */
    public void setCustomNumField3(Double customNumField3){
        this.customNumField3 = customNumField3;
    }
    /** 自定义数值4 */
    public Double getCustomNumField4(){
        return this.customNumField4;
    }
    /** 自定义数值4 */
    public void setCustomNumField4(Double customNumField4){
        this.customNumField4 = customNumField4;
    }
    /** 自定义数值5 */
    public Double getCustomNumField5(){
        return this.customNumField5;
    }
    /** 自定义数值5 */
    public void setCustomNumField5(Double customNumField5){
        this.customNumField5 = customNumField5;
    }
    /** 自定义日期1 */
    public Date getCustomDateField1(){
        return this.customDateField1;
    }
    /** 自定义日期1 */
    public void setCustomDateField1(Date customDateField1){
        this.customDateField1 = customDateField1;
    }
    /** 自定义日期2 */
    public Date getCustomDateField2(){
        return this.customDateField2;
    }
    /** 自定义日期2 */
    public void setCustomDateField2(Date customDateField2){
        this.customDateField2 = customDateField2;
    }
    /** 自定义日期3 */
    public Date getCustomDateField3(){
        return this.customDateField3;
    }
    /** 自定义日期3 */
    public void setCustomDateField3(Date customDateField3){
        this.customDateField3 = customDateField3;
    }
    /** 自定义日期4 */
    public Date getCustomDateField4(){
        return this.customDateField4;
    }
    /** 自定义日期4 */
    public void setCustomDateField4(Date customDateField4){
        this.customDateField4 = customDateField4;
    }
    /** 自定义日期5 */
    public Date getCustomDateField5(){
        return this.customDateField5;
    }
    /** 自定义日期5 */
    public void setCustomDateField5(Date customDateField5){
        this.customDateField5 = customDateField5;
    }

    public List<PartnerContactEO> getContacts() {
        return contacts;
    }

    public List<PartnerAddressEO> getAddresses() {
        return addresses;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setContacts(List<PartnerContactEO> contacts) {
        this.contacts = contacts;
    }

    public void setAddresses(List<PartnerAddressEO> addresses) {
        this.addresses = addresses;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
