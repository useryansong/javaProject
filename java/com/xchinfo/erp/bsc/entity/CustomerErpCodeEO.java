package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@TableName("bsc_customer_erp_code")
@KeySequence("bsc_customer_erp_code")
public class CustomerErpCodeEO extends AbstractAuditableEntity<CustomerErpCodeEO> {
    private static final long serialVersionUID = 8424596347484669525L;

    @TableId(type = IdType.INPUT)
    private Long customerErpCodeId;/** 供应商erp编码ID */

    private Long orgId;/** 组织机构id */

    private Long customerId;/** 供应商id */

    @TableField(exist = false)
    private String orgName;/** 组织机构名称 */

    @TableField(exist = false)
    private String customerName;/** 供应商名称 */

    @NotNull(message = "erp编码不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "erp编码长度不能超过 200 个字符")
    private String erpcode;/** erp编码 */

    @Override
    public Serializable getId() {
        return this.customerErpCodeId;
    }

    public Long getCustomerErpCodeId() {
        return customerErpCodeId;
    }

    public void setCustomerErpCodeId(Long customerErpCodeId) {
        this.customerErpCodeId = customerErpCodeId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getErpcode() {
        return erpcode;
    }

    public void setErpcode(String erpcode) {
        this.erpcode = erpcode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
