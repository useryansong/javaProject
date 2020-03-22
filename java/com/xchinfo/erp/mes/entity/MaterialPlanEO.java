package com.xchinfo.erp.mes.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.c
 */
@TableName("cmp_material_plan")
@KeySequence("cmp_material_plan")
public class MaterialPlanEO extends AbstractAuditableEntity<MaterialPlanEO> {

    private static final long serialVersionUID = -4586390364193889656L;

    @TableId(type = IdType.INPUT)
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

    @NotBlank(message = "客户名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "客户名称长度不能超过 200 个字符")
    @BusinessLogField("客户名称")
    private String customerName;/** 客户名称 */

    @DateTimeFormat(pattern="yyyy-MM")
    @JsonFormat(pattern="yyyy-MM")
    private Date monthDate;/** 月份 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构 */

    private Integer planType;/** 计划类型;1-月计划；2-周计划;3-周计划父节点 */

    private Integer yearSum;/** 年份 */

    private Date weekDate;/** 周计划日期 */

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

    private String elementNo;/** 零件号 */

    private String inventoryCode;/** 存货编码 */

    private String specification;/** 规格型号 */

    private Double requireCount;/** 需求数量 */

    private Double preRequireCount;/** 变更前需求数量 */

    private Integer isChange;/** 是否变更;0-未变更,1-已变更，默认0 */

    private Date lastChangeTime;/** 最后变更时间 */

    private String changeInfo;/** 变更详情 */

    private Double requirePurchaseCount;/** 需采购数量 */

    private Double requireProductCount;/** 需生产数量 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-新建;1-已发布;2-执行中;3-已完成;9-已关闭 */

    private Integer preStatus;/** 状态(关闭前的);0-新建;1-已发布;2-执行中;3-已完成;9-已关闭 */

    private Date finishDate;/** 完成日期 */

    private String remark;/** 备注 */

    private Double planVersion;/** 计划版本号 */

    private Long parentSerialId;/** 父计划ID */

    @TableField(exist = false)
    private Double stockCount;/** 库存数量*/

    @TableField(exist = false)
    private Double minStock;/** 最小安全库存 */

    @TableField(exist = false)
    private Double inTransitCount;/**在途数量*/

    @TableField(exist = false)
    private Integer isPurchase;/** 是否采购 */

    @TableField(exist = false)
    private Integer isProduct;/** 是否生产 */

    @TableField(exist = false)
    private String saveMsg;/**保存结果*/

    @TableField(exist = false)
    private String materialModel; /** 材质 */

    @TableField(exist = false)
    private String materialModelSpecific;/**牌号*/

    @TableField(exist = false)
    private Double weight;

    @TableField(exist = false)
    private Long supplierId;/** 供应商ID */

    @TableField(exist = false)
    private String supplierCode;/** 供应商编码 */

    @TableField(exist = false)
    private String supplierName;/** 供应商名称 */

    @TableField(exist = false)
    private Integer isCurrentMonth;/** 判断数据是否是当前月的数据 0-非当前月 1-当前月*/

    @TableField(exist = false)
    private String projectNo;/**项目号*/

    @TableField(exist = false)
    private Double snp;/** SNP */

    @TableField(exist = false)
    private Double  requireCount01;

    @TableField(exist = false)
    private Double  requireCount02;

    @TableField(exist = false)
    private Double  requireCount03;

    @TableField(exist = false)
    private Double  requireCount04;

    @TableField(exist = false)
    private Double  requireCount05;

    @TableField(exist = false)
    private Double  requireCount06;

    @TableField(exist = false)
    private Double  requireCount07;

    @TableField(exist = false)
    private Double  requireCount08;

    @TableField(exist = false)
    private Double  requireCount09;

    @TableField(exist = false)
    private Double  requireCount10;

    @TableField(exist = false)
    private Double  requireCount11;

    @TableField(exist = false)
    private Double  requireCount12;

    @TableField(exist = false)
    private Double  requireCount13;

    @TableField(exist = false)
    private Double  requireCount14;

    @TableField(exist = false)
    private Double  requireCount15;

    @TableField(exist = false)
    private Double  requireCount16;

    @TableField(exist = false)
    private Double  requireCount17;

    @TableField(exist = false)
    private Double  requireCount18;

    @TableField(exist = false)
    private Double  requireCount19;

    @TableField(exist = false)
    private Double  requireCount20;

    @TableField(exist = false)
    private Double  requireCount21;

    @TableField(exist = false)
    private Double  requireCount22;

    @TableField(exist = false)
    private Double  requireCount23;

    @TableField(exist = false)
    private Double  requireCount24;

    @TableField(exist = false)
    private Double  requireCount25;

    @TableField(exist = false)
    private Double  requireCount26;

    @TableField(exist = false)
    private Double  requireCount27;

    @TableField(exist = false)
    private Double  requireCount28;

    @TableField(exist = false)
    private Double  requireCount29;

    @TableField(exist = false)
    private Double  requireCount30;

    @TableField(exist = false)
    private Double  requireCount31;

    @TableField(exist = false)
    private Double requireTotalCount;/**周计划月需求数*/

    @TableField(exist = false)
    private Double  planCount01;/**计划数*/

    @TableField(exist = false)
    private Double  planCount02;

    @TableField(exist = false)
    private Double  planCount03;

    @TableField(exist = false)
    private Double  planCount04;

    @TableField(exist = false)
    private Double  planCount05;

    @TableField(exist = false)
    private Double  planCount06;

    @TableField(exist = false)
    private Double  planCount07;

    @TableField(exist = false)
    private Double  planCount08;

    @TableField(exist = false)
    private Double  planCount09;

    @TableField(exist = false)
    private Double  planCount10;

    @TableField(exist = false)
    private Double  planCount11;

    @TableField(exist = false)
    private Double  planCount12;

    @TableField(exist = false)
    private Long  serialId01;/**ID*/

    @TableField(exist = false)
    private Long  serialId02;

    @TableField(exist = false)
    private Long  serialId03;

    @TableField(exist = false)
    private Long  serialId04;

    @TableField(exist = false)
    private Long  serialId05;

    @TableField(exist = false)
    private Long  serialId06;

    @TableField(exist = false)
    private Long  serialId07;

    @TableField(exist = false)
    private Long  serialId08;

    @TableField(exist = false)
    private Long  serialId09;

    @TableField(exist = false)
    private Long  serialId10;

    @TableField(exist = false)
    private Long  serialId11;

    @TableField(exist = false)
    private Long  serialId12;

    @TableField(exist = false)
    private Integer  status01;

    @TableField(exist = false)
    private Integer  status02;

    @TableField(exist = false)
    private Integer  status03;

    @TableField(exist = false)
    private Integer  status04;

    @TableField(exist = false)
    private Integer  status05;

    @TableField(exist = false)
    private Integer  status06;

    @TableField(exist = false)
    private Integer  status07;

    @TableField(exist = false)
    private Integer  status08;

    @TableField(exist = false)
    private Integer  status09;

    @TableField(exist = false)
    private Integer  status10;

    @TableField(exist = false)
    private Integer  status11;

    @TableField(exist = false)
    private Integer  status12;

    @TableField(exist = false)
    private Long tempMaterialId;

    private Integer version;


    @Override
    public Serializable getId() {
        return this.serialId;
    }

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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public Double getRequireCount() {
        return requireCount;
    }

    public void setRequireCount(Double requireCount) {
        this.requireCount = requireCount;
    }

    public Double getRequirePurchaseCount() {
        return requirePurchaseCount;
    }

    public void setRequirePurchaseCount(Double requirePurchaseCount) {
        this.requirePurchaseCount = requirePurchaseCount;
    }

    public Double getRequireProductCount() {
        return requireProductCount;
    }

    public void setRequireProductCount(Double requireProductCount) {
        this.requireProductCount = requireProductCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Double getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(Double planVersion) {
        this.planVersion = planVersion;
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

    public Double getStockCount() {
        return stockCount;
    }

    public void setStockCount(Double stockCount) {
        this.stockCount = stockCount;
    }

    public Double getInTransitCount() {
        return inTransitCount;
    }

    public void setInTransitCount(Double inTransitCount) {
        this.inTransitCount = inTransitCount;
    }

    public String getSerialCode() {
        return serialCode;
    }

    public void setSerialCode(String serialCode) {
        this.serialCode = serialCode;
    }

    public Integer getIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(Integer isPurchase) {
        this.isPurchase = isPurchase;
    }

    public Integer getIsProduct() {
        return isProduct;
    }

    public void setIsProduct(Integer isProduct) {
        this.isProduct = isProduct;
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

    public String getSaveMsg() {
        return saveMsg;
    }

    public void setSaveMsg(String saveMsg) {
        this.saveMsg = saveMsg;
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

    public Integer getPreStatus() {
        return preStatus;
    }

    public void setPreStatus(Integer preStatus) {
        this.preStatus = preStatus;
    }

    public Double getMinStock() {
        return this.minStock;
    }

    public void setMinStock(Double minStock) {
        this.minStock = minStock;
    }

    public Integer getIsCurrentMonth() {
        return this.isCurrentMonth;
    }

    public void setIsCurrentMonth(Integer isCurrentMonth) {
        this.isCurrentMonth = isCurrentMonth;
    }
    public Long getParentSerialId() {
        return parentSerialId;
    }

    public void setParentSerialId(Long parentSerialId) {
        this.parentSerialId = parentSerialId;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public Double getSnp() {
        return snp;
    }

    public void setSnp(Double snp) {
        this.snp = snp;
    }


    public Double getRequireTotalCount() {
        return requireTotalCount;
    }

    public void setRequireTotalCount(Double requireTotalCount) {
        this.requireTotalCount = requireTotalCount;
    }

    public Double getRequireCount01() {
        return requireCount01;
    }

    public void setRequireCount01(Double requireCount01) {
        this.requireCount01 = requireCount01;
    }

    public Double getRequireCount02() {
        return requireCount02;
    }

    public void setRequireCount02(Double requireCount02) {
        this.requireCount02 = requireCount02;
    }

    public Double getRequireCount03() {
        return requireCount03;
    }

    public void setRequireCount03(Double requireCount03) {
        this.requireCount03 = requireCount03;
    }

    public Double getRequireCount04() {
        return requireCount04;
    }

    public void setRequireCount04(Double requireCount04) {
        this.requireCount04 = requireCount04;
    }

    public Double getRequireCount05() {
        return requireCount05;
    }

    public void setRequireCount05(Double requireCount05) {
        this.requireCount05 = requireCount05;
    }

    public Double getRequireCount06() {
        return requireCount06;
    }

    public void setRequireCount06(Double requireCount06) {
        this.requireCount06 = requireCount06;
    }

    public Double getRequireCount07() {
        return requireCount07;
    }

    public void setRequireCount07(Double requireCount07) {
        this.requireCount07 = requireCount07;
    }

    public Double getRequireCount08() {
        return requireCount08;
    }

    public void setRequireCount08(Double requireCount08) {
        this.requireCount08 = requireCount08;
    }

    public Double getRequireCount09() {
        return requireCount09;
    }

    public void setRequireCount09(Double requireCount09) {
        this.requireCount09 = requireCount09;
    }

    public Double getRequireCount10() {
        return requireCount10;
    }

    public void setRequireCount10(Double requireCount10) {
        this.requireCount10 = requireCount10;
    }

    public Double getRequireCount11() {
        return requireCount11;
    }

    public void setRequireCount11(Double requireCount11) {
        this.requireCount11 = requireCount11;
    }

    public Double getRequireCount12() {
        return requireCount12;
    }

    public void setRequireCount12(Double requireCount12) {
        this.requireCount12 = requireCount12;
    }

    public Double getRequireCount13() {
        return requireCount13;
    }

    public void setRequireCount13(Double requireCount13) {
        this.requireCount13 = requireCount13;
    }

    public Double getRequireCount14() {
        return requireCount14;
    }

    public void setRequireCount14(Double requireCount14) {
        this.requireCount14 = requireCount14;
    }

    public Double getRequireCount15() {
        return requireCount15;
    }

    public void setRequireCount15(Double requireCount15) {
        this.requireCount15 = requireCount15;
    }

    public Double getRequireCount16() {
        return requireCount16;
    }

    public void setRequireCount16(Double requireCount16) {
        this.requireCount16 = requireCount16;
    }

    public Double getRequireCount17() {
        return requireCount17;
    }

    public void setRequireCount17(Double requireCount17) {
        this.requireCount17 = requireCount17;
    }

    public Double getRequireCount18() {
        return requireCount18;
    }

    public void setRequireCount18(Double requireCount18) {
        this.requireCount18 = requireCount18;
    }

    public Double getRequireCount19() {
        return requireCount19;
    }

    public void setRequireCount19(Double requireCount19) {
        this.requireCount19 = requireCount19;
    }

    public Double getRequireCount20() {
        return requireCount20;
    }

    public void setRequireCount20(Double requireCount20) {
        this.requireCount20 = requireCount20;
    }

    public Double getRequireCount21() {
        return requireCount21;
    }

    public void setRequireCount21(Double requireCount21) {
        this.requireCount21 = requireCount21;
    }

    public Double getRequireCount22() {
        return requireCount22;
    }

    public void setRequireCount22(Double requireCount22) {
        this.requireCount22 = requireCount22;
    }

    public Double getRequireCount23() {
        return requireCount23;
    }

    public void setRequireCount23(Double requireCount23) {
        this.requireCount23 = requireCount23;
    }

    public Double getRequireCount24() {
        return requireCount24;
    }

    public void setRequireCount24(Double requireCount24) {
        this.requireCount24 = requireCount24;
    }

    public Double getRequireCount25() {
        return requireCount25;
    }

    public void setRequireCount25(Double requireCount25) {
        this.requireCount25 = requireCount25;
    }

    public Double getRequireCount26() {
        return requireCount26;
    }

    public void setRequireCount26(Double requireCount26) {
        this.requireCount26 = requireCount26;
    }

    public Double getRequireCount27() {
        return requireCount27;
    }

    public void setRequireCount27(Double requireCount27) {
        this.requireCount27 = requireCount27;
    }

    public Double getRequireCount28() {
        return requireCount28;
    }

    public void setRequireCount28(Double requireCount28) {
        this.requireCount28 = requireCount28;
    }

    public Double getRequireCount29() {
        return requireCount29;
    }

    public void setRequireCount29(Double requireCount29) {
        this.requireCount29 = requireCount29;
    }

    public Double getRequireCount30() {
        return requireCount30;
    }

    public void setRequireCount30(Double requireCount30) {
        this.requireCount30 = requireCount30;
    }

    public Double getRequireCount31() {
        return requireCount31;
    }

    public void setRequireCount31(Double requireCount31) {
        this.requireCount31 = requireCount31;
    }

    public Double getPlanCount01() {
        return planCount01;
    }

    public void setPlanCount01(Double planCount01) {
        this.planCount01 = planCount01;
    }

    public Double getPlanCount02() {
        return planCount02;
    }

    public void setPlanCount02(Double planCount02) {
        this.planCount02 = planCount02;
    }

    public Double getPlanCount03() {
        return planCount03;
    }

    public void setPlanCount03(Double planCount03) {
        this.planCount03 = planCount03;
    }

    public Double getPlanCount04() {
        return planCount04;
    }

    public void setPlanCount04(Double planCount04) {
        this.planCount04 = planCount04;
    }

    public Double getPlanCount05() {
        return planCount05;
    }

    public void setPlanCount05(Double planCount05) {
        this.planCount05 = planCount05;
    }

    public Double getPlanCount06() {
        return planCount06;
    }

    public void setPlanCount06(Double planCount06) {
        this.planCount06 = planCount06;
    }

    public Double getPlanCount07() {
        return planCount07;
    }

    public void setPlanCount07(Double planCount07) {
        this.planCount07 = planCount07;
    }

    public Double getPlanCount08() {
        return planCount08;
    }

    public void setPlanCount08(Double planCount08) {
        this.planCount08 = planCount08;
    }

    public Double getPlanCount09() {
        return planCount09;
    }

    public void setPlanCount09(Double planCount09) {
        this.planCount09 = planCount09;
    }

    public Double getPlanCount10() {
        return planCount10;
    }

    public void setPlanCount10(Double planCount10) {
        this.planCount10 = planCount10;
    }

    public Double getPlanCount11() {
        return planCount11;
    }

    public void setPlanCount11(Double planCount11) {
        this.planCount11 = planCount11;
    }

    public Double getPlanCount12() {
        return planCount12;
    }

    public void setPlanCount12(Double planCount12) {
        this.planCount12 = planCount12;
    }

    public Long getSerialId01() {
        return serialId01;
    }

    public void setSerialId01(Long serialId01) {
        this.serialId01 = serialId01;
    }

    public Long getSerialId02() {
        return serialId02;
    }

    public void setSerialId02(Long serialId02) {
        this.serialId02 = serialId02;
    }

    public Long getSerialId03() {
        return serialId03;
    }

    public void setSerialId03(Long serialId03) {
        this.serialId03 = serialId03;
    }

    public Long getSerialId04() {
        return serialId04;
    }

    public void setSerialId04(Long serialId04) {
        this.serialId04 = serialId04;
    }

    public Long getSerialId05() {
        return serialId05;
    }

    public void setSerialId05(Long serialId05) {
        this.serialId05 = serialId05;
    }

    public Long getSerialId06() {
        return serialId06;
    }

    public void setSerialId06(Long serialId06) {
        this.serialId06 = serialId06;
    }

    public Long getSerialId07() {
        return serialId07;
    }

    public void setSerialId07(Long serialId07) {
        this.serialId07 = serialId07;
    }

    public Long getSerialId08() {
        return serialId08;
    }

    public void setSerialId08(Long serialId08) {
        this.serialId08 = serialId08;
    }

    public Long getSerialId09() {
        return serialId09;
    }

    public void setSerialId09(Long serialId09) {
        this.serialId09 = serialId09;
    }

    public Long getSerialId10() {
        return serialId10;
    }

    public void setSerialId10(Long serialId10) {
        this.serialId10 = serialId10;
    }

    public Long getSerialId11() {
        return serialId11;
    }

    public void setSerialId11(Long serialId11) {
        this.serialId11 = serialId11;
    }

    public Long getSerialId12() {
        return serialId12;
    }

    public void setSerialId12(Long serialId12) {
        this.serialId12 = serialId12;
    }

    public Integer getYearSum() {
        return yearSum;
    }

    public void setYearSum(Integer yearSum) {
        this.yearSum = yearSum;
    }

    public Integer getStatus01() {
        return status01;
    }

    public void setStatus01(Integer status01) {
        this.status01 = status01;
    }

    public Integer getStatus02() {
        return status02;
    }

    public void setStatus02(Integer status02) {
        this.status02 = status02;
    }

    public Integer getStatus03() {
        return status03;
    }

    public void setStatus03(Integer status03) {
        this.status03 = status03;
    }

    public Integer getStatus04() {
        return status04;
    }

    public void setStatus04(Integer status04) {
        this.status04 = status04;
    }

    public Integer getStatus05() {
        return status05;
    }

    public void setStatus05(Integer status05) {
        this.status05 = status05;
    }

    public Integer getStatus06() {
        return status06;
    }

    public void setStatus06(Integer status06) {
        this.status06 = status06;
    }

    public Integer getStatus07() {
        return status07;
    }

    public void setStatus07(Integer status07) {
        this.status07 = status07;
    }

    public Integer getStatus08() {
        return status08;
    }

    public void setStatus08(Integer status08) {
        this.status08 = status08;
    }

    public Integer getStatus09() {
        return status09;
    }

    public void setStatus09(Integer status09) {
        this.status09 = status09;
    }

    public Integer getStatus10() {
        return status10;
    }

    public void setStatus10(Integer status10) {
        this.status10 = status10;
    }

    public Integer getStatus11() {
        return status11;
    }

    public void setStatus11(Integer status11) {
        this.status11 = status11;
    }

    public Integer getStatus12() {
        return status12;
    }

    public void setStatus12(Integer status12) {
        this.status12 = status12;
    }

    public Long getTempMaterialId() {
        return tempMaterialId;
    }

    public void setTempMaterialId(Long tempMaterialId) {
        this.tempMaterialId = tempMaterialId;
    }

    public Double getPreRequireCount() {
        return preRequireCount;
    }

    public void setPreRequireCount(Double preRequireCount) {
        this.preRequireCount = preRequireCount;
    }

    public Integer getIsChange() {
        return isChange;
    }

    public void setIsChange(Integer isChange) {
        this.isChange = isChange;
    }

    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Date lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public String getChangeInfo() {
        return changeInfo;
    }

    public void setChangeInfo(String changeInfo) {
        this.changeInfo = changeInfo;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }
}
