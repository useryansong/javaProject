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
@TableName("bsc_partner_contact")
@KeySequence("bsc_partner_contact")
public class PartnerContactEO extends AbstractAuditableEntity<PartnerContactEO> {
    private static final long serialVersionUID = 399114679824573967L;

    @TableId(type = IdType.INPUT)
    private Long contactId;/** 联系人ID */

    @NotNull(message = "类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 6, message = "类型长度不能超过 6 个字符")
    @BusinessLogField("类型")
    private Integer partnerType;/** 类型;1-供应商;2-客户 */

    @NotNull(message = "伙伴ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 20, message = "伙伴ID长度不能超过 20 个字符")
    @BusinessLogField("伙伴ID")
    private Long partnerId;/** 伙伴ID */

    @NotBlank(message = "联系人姓名不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "联系人姓名长度不能超过 200 个字符")
    @BusinessLogField("联系人姓名")
    private String name;/** 联系人姓名 */

    @Length(max = 200, message = "职务长度不能超过 200 个字符")
    private String jobTitle;/** 职务 */

    @Length(max = 1000, message = "地址长度不能超过 1000 个字符")
    private String address;/** 地址 */

    @Length(max = 200, message = "电话长度不能超过 200 个字符")
    private String phone;/** 电话 */

    @NotBlank(message = "手机不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "手机长度不能超过 200 个字符")
    @BusinessLogField("手机")
    private String mobile;/** 手机 */

    @Length(max = 200, message = "邮箱长度不能超过 200 个字符")
    @Email
    private String email;/** 邮箱 */

    private Integer defaultContact;/** 是否默认;1-是，0-否 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效，1-有效 */

    private String remarks;/** 备注 */

    @TableField(exist = false)
    private String addressName;/**地址名称*/

    @Override
    public Serializable getId() {
        return this.contactId;
    }

    /** 联系人ID */
    public Long getContactId(){
        return this.contactId;
    }
    /** 联系人ID */
    public void setContactId(Long contactId){
        this.contactId = contactId;
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
    /** 联系人姓名 */
    public String getName(){
        return this.name;
    }
    /** 联系人姓名 */
    public void setName(String name){
        this.name = name;
    }
    /** 职务 */
    public String getJobTitle(){
        return this.jobTitle;
    }
    /** 职务 */
    public void setJobTitle(String jobTitle){
        this.jobTitle = jobTitle;
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
    /** 手机 */
    public String getMobile(){
        return this.mobile;
    }
    /** 手机 */
    public void setMobile(String mobile){
        this.mobile = mobile;
    }
    /** 邮箱 */
    public String getEmail(){
        return this.email;
    }
    /** 邮箱 */
    public void setEmail(String email){
        this.email = email;
    }
    /** 是否默认;1-是，0-否 */
    public Integer getDefaultContact(){
        return this.defaultContact;
    }
    /** 是否默认;1-是，0-否 */
    public void setDefaultContact(Integer defaultContact){
        this.defaultContact = defaultContact;
    }
    /** 状态;0-无效，1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效，1-有效 */
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

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }
}
