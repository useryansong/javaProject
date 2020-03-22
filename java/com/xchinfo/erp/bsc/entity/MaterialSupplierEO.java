package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/3/10
 * @update
 */
@TableName("bsc_material_supplier")
@KeySequence("bsc_material_supplier")
public class MaterialSupplierEO extends AbstractAuditableEntity<MaterialSupplierEO> {
    private static final long serialVersionUID = -8806764380164339069L;

    @TableId(type = IdType.INPUT)
    private Long materialSupplierId;/** 主键 */

    @NotNull(message = "物料ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("物料ID")
    private Long materialId;/** 物料ID */

    @NotNull(message = "供应商ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("供应商ID")
    private Long supplierId;/** 供应商 */

    @Length(max = 100, message = "供应商物料编码长度不能超过 100 个字符")
    private String supplierMaterialCode;/** 供应商物料编码 */

    @Length(max = 200, message = "供应商物料名称长度不能超过 200 个字符")
    private String supplierMaterialName;/** 供应商物料名称 */

    @NotNull(message = "是否默认供应商不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Integer isDefault;/** 是否默认;0-否;1-是 */

    @NotNull(message = "供应类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Integer supplyType;/** 供应类型;1-采购;2-委外 */

    @TableField(exist = false)
    private String supplierCode;/** 供应商编码 */

    @TableField(exist = false)
    private String supplierName;/** 供应商名称 */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String materialName;/** 物料名称 */

    @TableField(exist = false)
    private String inventoryCode;/** 存货编码 */


    @TableField(exist = false)
    private Integer isPurchase;/** 是否采购 */

    @TableField(exist = false)
    private Integer isProduct;/** 是否生产 */


    @TableField(exist = false)
    private String elementNo;/** 零件号 */

    @TableField(exist = false)
    private Double requireCount;/** 需求数量 */

    @TableField(exist = false)
    private Double requirePurchaseCount;/** 需采购数量 */

    @TableField(exist = false)
    private Double requireProductCount;/** 需生产数量 */

    @TableField(exist = false)
    private Double stockCount;/** 库存数量*/

    @TableField(exist = false)
    private Date weekDate;/**日计划日期*/

    @TableField(exist = false)
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private Integer errImportType;/**0-无错，1-不允许采购;2-日计划已存在 */

    @TableField(exist = false)
    private Integer weeks;/**所属周数*/

    @TableField(exist = false)
    private String unitName;

    @TableField(exist = false)
    private Double requireCountOne;

    @TableField(exist = false)
    private Double requireCountTwo;

    @TableField(exist = false)
    private Double requireCountThree;

    @TableField(exist = false)
    private Double requireCountFour;

    @TableField(exist = false)
    private Double requireCountFive;

    @TableField(exist = false)
    private Double requireCountSix;

    @TableField(exist = false)
    private Double requireCountSeven;

    @TableField(exist = false)
    private Date weekOne;

    @TableField(exist = false)
    private Date weekTwo;

    @TableField(exist = false)
    private Date weekThree;

    @TableField(exist = false)
    private Date weekFour;

    @TableField(exist = false)
    private Date weekFive;

    @TableField(exist = false)
    private Date weekSix;

    @TableField(exist = false)
    private Date weekSeven;

    @TableField(exist = false)
    private Date monthDate;

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
    private Double  planCount13;

    @TableField(exist = false)
    private Double  planCount14;

    @TableField(exist = false)
    private Double  planCount15;

    @TableField(exist = false)
    private Double  planCount16;

    @TableField(exist = false)
    private Double  planCount17;

    @TableField(exist = false)
    private Double  planCount18;

    @TableField(exist = false)
    private Double  planCount19;

    @TableField(exist = false)
    private Double  planCount20;

    @TableField(exist = false)
    private Double  planCount21;

    @TableField(exist = false)
    private Double  planCount22;

    @TableField(exist = false)
    private Double  planCount23;

    @TableField(exist = false)
    private Double  planCount24;

    @TableField(exist = false)
    private Double  planCount25;

    @TableField(exist = false)
    private Double  planCount26;

    @TableField(exist = false)
    private Double  planCount27;

    @TableField(exist = false)
    private Double  planCount28;

    @TableField(exist = false)
    private Double  planCount29;

    @TableField(exist = false)
    private Double  planCount30;

    @TableField(exist = false)
    private Double  planCount31;


    @Override
    public Serializable getId() {
        return this.materialSupplierId;
    }


    /** 物料ID */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料ID */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 编码 */
    public String getMaterialCode(){
        return this.materialCode;
    }
    /** 编码 */
    public void setMaterialCode(String materialCode){
        this.materialCode = materialCode;
    }
    /** 名称 */
    public String getMaterialName(){
        return this.materialName;
    }
    /** 名称 */
    public void setMaterialName(String materialName){
        this.materialName = materialName;
    }

    public Long getMaterialSupplierId() {
        return materialSupplierId;
    }

    public void setMaterialSupplierId(Long materialSupplierId) {
        this.materialSupplierId = materialSupplierId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierMaterialCode() {
        return supplierMaterialCode;
    }

    public void setSupplierMaterialCode(String supplierMaterialCode) {
        this.supplierMaterialCode = supplierMaterialCode;
    }

    public String getSupplierMaterialName() {
        return supplierMaterialName;
    }

    public void setSupplierMaterialName(String supplierMaterialName) {
        this.supplierMaterialName = supplierMaterialName;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(Integer supplyType) {
        this.supplyType = supplyType;
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

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
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

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
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

    public Double getStockCount() {
        return stockCount;
    }

    public void setStockCount(Double stockCount) {
        this.stockCount = stockCount;
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

    public Integer getErrImportType() {
        return errImportType;
    }

    public void setErrImportType(Integer errImportType) {
        this.errImportType = errImportType;
    }

    public Integer getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Double getRequireCountOne() {
        return requireCountOne;
    }

    public void setRequireCountOne(Double requireCountOne) {
        this.requireCountOne = requireCountOne;
    }

    public Double getRequireCountTwo() {
        return requireCountTwo;
    }

    public void setRequireCountTwo(Double requireCountTwo) {
        this.requireCountTwo = requireCountTwo;
    }

    public Double getRequireCountThree() {
        return requireCountThree;
    }

    public void setRequireCountThree(Double requireCountThree) {
        this.requireCountThree = requireCountThree;
    }

    public Double getRequireCountFour() {
        return requireCountFour;
    }

    public void setRequireCountFour(Double requireCountFour) {
        this.requireCountFour = requireCountFour;
    }

    public Double getRequireCountFive() {
        return requireCountFive;
    }

    public void setRequireCountFive(Double requireCountFive) {
        this.requireCountFive = requireCountFive;
    }

    public Double getRequireCountSix() {
        return requireCountSix;
    }

    public void setRequireCountSix(Double requireCountSix) {
        this.requireCountSix = requireCountSix;
    }

    public Double getRequireCountSeven() {
        return requireCountSeven;
    }

    public void setRequireCountSeven(Double requireCountSeven) {
        this.requireCountSeven = requireCountSeven;
    }

    public Date getWeekOne() {
        return weekOne;
    }

    public void setWeekOne(Date weekOne) {
        this.weekOne = weekOne;
    }

    public Date getWeekTwo() {
        return weekTwo;
    }

    public void setWeekTwo(Date weekTwo) {
        this.weekTwo = weekTwo;
    }

    public Date getWeekThree() {
        return weekThree;
    }

    public void setWeekThree(Date weekThree) {
        this.weekThree = weekThree;
    }

    public Date getWeekFour() {
        return weekFour;
    }

    public void setWeekFour(Date weekFour) {
        this.weekFour = weekFour;
    }

    public Date getWeekFive() {
        return weekFive;
    }

    public void setWeekFive(Date weekFive) {
        this.weekFive = weekFive;
    }

    public Date getWeekSix() {
        return weekSix;
    }

    public void setWeekSix(Date weekSix) {
        this.weekSix = weekSix;
    }

    public Date getWeekSeven() {
        return weekSeven;
    }

    public void setWeekSeven(Date weekSeven) {
        this.weekSeven = weekSeven;
    }


    public Date getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(Date monthDate) {
        this.monthDate = monthDate;
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

    public Double getPlanCount13() {
        return planCount13;
    }

    public void setPlanCount13(Double planCount13) {
        this.planCount13 = planCount13;
    }

    public Double getPlanCount14() {
        return planCount14;
    }

    public void setPlanCount14(Double planCount14) {
        this.planCount14 = planCount14;
    }

    public Double getPlanCount15() {
        return planCount15;
    }

    public void setPlanCount15(Double planCount15) {
        this.planCount15 = planCount15;
    }

    public Double getPlanCount16() {
        return planCount16;
    }

    public void setPlanCount16(Double planCount16) {
        this.planCount16 = planCount16;
    }

    public Double getPlanCount17() {
        return planCount17;
    }

    public void setPlanCount17(Double planCount17) {
        this.planCount17 = planCount17;
    }

    public Double getPlanCount18() {
        return planCount18;
    }

    public void setPlanCount18(Double planCount18) {
        this.planCount18 = planCount18;
    }

    public Double getPlanCount19() {
        return planCount19;
    }

    public void setPlanCount19(Double planCount19) {
        this.planCount19 = planCount19;
    }

    public Double getPlanCount20() {
        return planCount20;
    }

    public void setPlanCount20(Double planCount20) {
        this.planCount20 = planCount20;
    }

    public Double getPlanCount21() {
        return planCount21;
    }

    public void setPlanCount21(Double planCount21) {
        this.planCount21 = planCount21;
    }

    public Double getPlanCount22() {
        return planCount22;
    }

    public void setPlanCount22(Double planCount22) {
        this.planCount22 = planCount22;
    }

    public Double getPlanCount23() {
        return planCount23;
    }

    public void setPlanCount23(Double planCount23) {
        this.planCount23 = planCount23;
    }

    public Double getPlanCount24() {
        return planCount24;
    }

    public void setPlanCount24(Double planCount24) {
        this.planCount24 = planCount24;
    }

    public Double getPlanCount25() {
        return planCount25;
    }

    public void setPlanCount25(Double planCount25) {
        this.planCount25 = planCount25;
    }

    public Double getPlanCount26() {
        return planCount26;
    }

    public void setPlanCount26(Double planCount26) {
        this.planCount26 = planCount26;
    }

    public Double getPlanCount27() {
        return planCount27;
    }

    public void setPlanCount27(Double planCount27) {
        this.planCount27 = planCount27;
    }

    public Double getPlanCount28() {
        return planCount28;
    }

    public void setPlanCount28(Double planCount28) {
        this.planCount28 = planCount28;
    }

    public Double getPlanCount29() {
        return planCount29;
    }

    public void setPlanCount29(Double planCount29) {
        this.planCount29 = planCount29;
    }

    public Double getPlanCount30() {
        return planCount30;
    }

    public void setPlanCount30(Double planCount30) {
        this.planCount30 = planCount30;
    }

    public Double getPlanCount31() {
        return planCount31;
    }

    public void setPlanCount31(Double planCount31) {
        this.planCount31 = planCount31;
    }
}
