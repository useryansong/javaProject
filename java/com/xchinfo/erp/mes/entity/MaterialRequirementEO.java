package com.xchinfo.erp.mes.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.c
 */
@TableName("cmp_material_requirement")
@KeySequence("cmp_material_requirement")
public class MaterialRequirementEO extends AbstractAuditableEntity<MaterialRequirementEO> {

    @TableId(type = IdType.INPUT)
    private Long serialRequireId;/** 原材料需求ID */

    @NotNull(message = "物料计划ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("物料计划ID")
    private Long serialId;/** 物料计划ID */

    @Length(max = 50, message = "流水号长度不能超过 50 个字符")
    @BusinessLogField("流水号")
    private String serialCode;/** 流水号 */

    @NotNull(message = "客户ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("客户ID")
    private Long customerId;/** 客户ID */

    @NotBlank(message = "客户编码不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 50, message = "客户编码长度不能超过 50 个字符")
    @BusinessLogField("客户编码")
    private String customerCode;/** 客户编码 */

    private Date monthDate;/** 月份 */

    @NotBlank(message = "客户名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "客户名称长度不能超过 200 个字符")
    @BusinessLogField("客户名称")
    private String customerName;/** 客户名称 */

    private Integer planType;/** 计划类型;1-月计划；2-周计划 */

    private Date weekDate;/** 周计划日期 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构 */

    @NotNull(message = "物料不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("物料")
    private Long materialId;/** 物料ID */

    @NotBlank(message = "物料编码不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 50, message = "物料编码长度不能超过 50 个字符")
    @BusinessLogField("物料编码")
    private String materialCode;/** 物料编码 */

    @NotBlank(message = "物料名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "物料编码长度不能超过 200 个字符")
    @BusinessLogField("物料名称")
    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private String elementNo;/** 零件号 */

    private Double weight;/** 重量 */

    private String materialModel;/** 材质 */

    private String materialModelSpecific;/** 牌号 */

    private String specification;/** 规格型号 */

    @TableField(exist = false)
    private Double stockCount;/** 库存数量*/

    private Double requireCount;/** 需求数量 */

    private Double totalWeight;/** 总重量 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-新建;1-已发布;2-执行中;3-已完成;9-已关闭 */

    private Date finishDate;/** 完成日期 */

    private String remark;/** 备注 */

    @TableField(exist = false)
    private String originalMaterialName;/** 原材料物料名称 */

    @TableField(exist = false)
    private String originalElementNo;/** 原材料零件号 */


    @Override
    public Serializable getId() {
        return this.serialRequireId;
    }

    public Long getSerialRequireId() {
        return serialRequireId;
    }

    public void setSerialRequireId(Long serialRequireId) {
        this.serialRequireId = serialRequireId;
    }

    public String getSerialCode() {
        return serialCode;
    }

    public void setSerialCode(String serialCode) {
        this.serialCode = serialCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(Date monthDate) {
        this.monthDate = monthDate;
    }

    public Integer getPlanType() {
        return planType;
    }

    public void setPlanType(Integer planType) {
        this.planType = planType;
    }

    public Date getWeekDate() {
        return weekDate;
    }

    public void setWeekDate(Date weekDate) {
        this.weekDate = weekDate;
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

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getMaterialModel() {
        return materialModel;
    }

    public void setMaterialModel(String materialModel) {
        this.materialModel = materialModel;
    }

    public String getMaterialModelSpecific() {
        return materialModelSpecific;
    }

    public void setMaterialModelSpecific(String materialModelSpecific) {
        this.materialModelSpecific = materialModelSpecific;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Double getRequireCount() {
        return requireCount;
    }

    public void setRequireCount(Double requireCount) {
        this.requireCount = requireCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Double getStockCount() {
        return stockCount;
    }

    public void setStockCount(Double stockCount) {
        this.stockCount = stockCount;
    }

    public Long getSerialId() {
        return serialId;
    }

    public void setSerialId(Long serialId) {
        this.serialId = serialId;
    }

    public String getOriginalMaterialName() {
        return originalMaterialName;
    }

    public void setOriginalMaterialName(String originalMaterialName) {
        this.originalMaterialName = originalMaterialName;
    }

    public String getOriginalElementNo() {
        return originalElementNo;
    }

    public void setOriginalElementNo(String originalElementNo) {
        this.originalElementNo = originalElementNo;
    }
}
