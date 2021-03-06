package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;
import java.util.Date;

/**
 * 生产任务
 */
@TableName("mes_product_task")
@KeySequence("mes_product_task")
public class ProductTaskEO  extends AbstractAuditableEntity<ProductTaskEO> {

    private static final long serialVersionUID = -2572414512815688266L;

    @TableId(type = IdType.INPUT)
    private Long productTaskId;/** 生产任务ID */

    private Long serialDistributeId;/** 计划ID */

    private String serialPurchaseCode;/** 采购流水号 */

    private String serialProductCode;/** 生产流水号 */

    private String serialOutSourceCode;/** 委外流水号 */

    private Long serialId;/** 父物料计划ID */

    private Long customerId;/** 客户ID */

    private String customerCode;/** 客户编码 */

    private String customerName;/** 客户名称 */

    private Date monthDate;/** 月份 */

    private Integer planType;/** 计划类型;1-月计划；2-周计划 */

    private Integer distributeType;/** 子类型;1-采购；2-委外；3-生产 */

    private Date weekDate;/** 周计划日期 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构 */

    private Long materialId;/** 物料ID */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private String elementNo;/** 零件号 */

    private String inventoryCode;/** 存货编码 */

    private String specification;/** 规格型号 */

    private Long supplierId;/** 供应商ID */

    private String supplierCode;/** 供应商编码 */

    private String supplierName;/** 供应商名称 */

    private Double requireCount;/** 需求数量 */

    private Double productionCount;/** 生产数量 */

    private Double planVersion;/** 计划版本号 */

    private Double taskVersion;/** 任务版本号 */

    private Integer planStatus;/** 计划状态;0,待确认，1.已确认，2.计划变更 */

    private Integer exeStatus;/** 执行状态;0.待排产，1.已排产，2.执行中，3.已完工，4.已关闭 */

    private Date finishDate;/** 完成日期 */

    private String remark;/** 备注 */

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

    @Override
    public Serializable getId() {
        return this.productTaskId;
    }

    public Long getProductTaskId() {
        return productTaskId;
    }

    public void setProductTaskId(Long productTaskId) {
        this.productTaskId = productTaskId;
    }

    public Long getSerialDistributeId() {
        return serialDistributeId;
    }

    public void setSerialDistributeId(Long serialDistributeId) {
        this.serialDistributeId = serialDistributeId;
    }

    public String getSerialPurchaseCode() {
        return serialPurchaseCode;
    }

    public void setSerialPurchaseCode(String serialPurchaseCode) {
        this.serialPurchaseCode = serialPurchaseCode;
    }

    public String getSerialProductCode() {
        return serialProductCode;
    }

    public void setSerialProductCode(String serialProductCode) {
        this.serialProductCode = serialProductCode;
    }

    public String getSerialOutSourceCode() {
        return serialOutSourceCode;
    }

    public void setSerialOutSourceCode(String serialOutSourceCode) {
        this.serialOutSourceCode = serialOutSourceCode;
    }

    public Long getSerialId() {
        return serialId;
    }

    public void setSerialId(Long serialId) {
        this.serialId = serialId;
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

    public Integer getDistributeType() {
        return distributeType;
    }

    public void setDistributeType(Integer distributeType) {
        this.distributeType = distributeType;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Double getRequireCount() {
        return requireCount;
    }

    public void setRequireCount(Double requireCount) {
        this.requireCount = requireCount;
    }

    public Double getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(Double planVersion) {
        this.planVersion = planVersion;
    }

    public Double getTaskVersion() {
        return taskVersion;
    }

    public void setTaskVersion(Double taskVersion) {
        this.taskVersion = taskVersion;
    }

    public Integer getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(Integer planStatus) {
        this.planStatus = planStatus;
    }

    public Integer getExeStatus() {
        return exeStatus;
    }

    public void setExeStatus(Integer exeStatus) {
        this.exeStatus = exeStatus;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCustomStringField1() {
        return customStringField1;
    }

    public void setCustomStringField1(String customStringField1) {
        this.customStringField1 = customStringField1;
    }

    public String getCustomStringField2() {
        return customStringField2;
    }

    public void setCustomStringField2(String customStringField2) {
        this.customStringField2 = customStringField2;
    }

    public String getCustomStringField3() {
        return customStringField3;
    }

    public void setCustomStringField3(String customStringField3) {
        this.customStringField3 = customStringField3;
    }

    public String getCustomStringField4() {
        return customStringField4;
    }

    public void setCustomStringField4(String customStringField4) {
        this.customStringField4 = customStringField4;
    }

    public String getCustomStringField5() {
        return customStringField5;
    }

    public void setCustomStringField5(String customStringField5) {
        this.customStringField5 = customStringField5;
    }

    public Double getCustomNumField1() {
        return customNumField1;
    }

    public void setCustomNumField1(Double customNumField1) {
        this.customNumField1 = customNumField1;
    }

    public Double getCustomNumField2() {
        return customNumField2;
    }

    public void setCustomNumField2(Double customNumField2) {
        this.customNumField2 = customNumField2;
    }

    public Double getCustomNumField3() {
        return customNumField3;
    }

    public void setCustomNumField3(Double customNumField3) {
        this.customNumField3 = customNumField3;
    }

    public Double getCustomNumField4() {
        return customNumField4;
    }

    public void setCustomNumField4(Double customNumField4) {
        this.customNumField4 = customNumField4;
    }

    public Double getCustomNumField5() {
        return customNumField5;
    }

    public void setCustomNumField5(Double customNumField5) {
        this.customNumField5 = customNumField5;
    }

    public Date getCustomDateField1() {
        return customDateField1;
    }

    public void setCustomDateField1(Date customDateField1) {
        this.customDateField1 = customDateField1;
    }

    public Date getCustomDateField2() {
        return customDateField2;
    }

    public void setCustomDateField2(Date customDateField2) {
        this.customDateField2 = customDateField2;
    }

    public Date getCustomDateField3() {
        return customDateField3;
    }

    public void setCustomDateField3(Date customDateField3) {
        this.customDateField3 = customDateField3;
    }

    public Date getCustomDateField4() {
        return customDateField4;
    }

    public void setCustomDateField4(Date customDateField4) {
        this.customDateField4 = customDateField4;
    }

    public Date getCustomDateField5() {
        return customDateField5;
    }

    public void setCustomDateField5(Date customDateField5) {
        this.customDateField5 = customDateField5;
    }

    public Double getProductionCount() {
        return productionCount;
    }

    public void setProductionCount(Double productionCount) {
        this.productionCount = productionCount;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
