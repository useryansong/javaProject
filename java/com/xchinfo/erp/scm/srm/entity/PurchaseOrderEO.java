package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhongye
 * @date 2019/5/9
 */
@TableName("srm_purchase_order")
@KeySequence("srm_purchase_order")
public class PurchaseOrderEO extends AbstractAuditableEntity<PurchaseOrderEO> {
    private static final long serialVersionUID = -4571187095995596796L;

    @TableId(type = IdType.INPUT)
    private Long purchaseOrderId;/** 主键 */

    private String voucherNo;/** 流水号 */

    private Integer type;/** 单据类型;1-采购订单，2-委外订单。 */

    private Long supplierId;/** 供应商 */

    @TableField(exist = false)
    private String supplierName;/** 供应商名称 */

    @TableField(exist = false)
    private String supplierCode;/** 供应商编码 */

    private String department;/** 部门 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    private String currencyName;/** 币种 */

    private Long materialId;/** 物料ID */

    private String materialCode;/** 物料编码 */

    private String inventoryCode;/** 存货编码 */

    private String materialName;/** 物料名称 */

    private String elementNo;/** 零件号 */

    private String specification;/** 规格型号 */

    private String unitName;/** 计量单位 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDate;/** 创建日期 */

    @JsonFormat(pattern = "yyyy-MM")
    private Date monthDate;/** 月份 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planArriveDate;/** 计划到货日期 */

    private Double planDeliveryQuantity;/** 计划送货数量 */

    private Double notDeliveryQuantity;/** 未送数量 */

    private Double actualDeliveryQuantity;/** 实际送货数量 */

    private Double qualifiedQuantity;/** 合格数量 */

    private Double returnedQuantity;/** 退货数量 */

    private String purchaseOrderNo;/** 订单编号 */

    private Long createUserId;/** 创单人ID */

    private String createUserName;/** 创单人用户名 */

    private Long chargeUserId;/** 经办人ID */

    private String chargeUserName;/** 经办人用户名 */

    private Integer status;/** 状态;0-新建;1-执行中;2-已完成;9-已关闭 */

    private Integer preStatus;/** 状态(关闭前的);0-新建;1-执行中;2-已完成; */

    private Long serialDistributeId;/** 采购计划ID */

    private Long serialId;/** 客户需求周计划ID */

    @TableField(exist = false)
    private Long parentSerialId;/** 客户需求周计划的父计划ID */

    private Double planRequireQuantity;/** 客户需求周计划数量 */

    private Integer isChangeConfirm;/** 变更确认;0 -需确认,1-已确认 */

    private Integer confirmVersion;/** 确认版本号 */

    private Date lastConfirmTime;/** 最后确认时间 */

    @TableField(exist = false)
    private String serialCode;/** 客户需求周计划的流水号 */

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

    @TableField(exist = false)
    private Long purchaseOrderId01;/**订单ID*/

    @TableField(exist = false)
    private Long purchaseOrderId02;

    @TableField(exist = false)
    private Long purchaseOrderId03;

    @TableField(exist = false)
    private Long purchaseOrderId04;

    @TableField(exist = false)
    private Long purchaseOrderId05;

    @TableField(exist = false)
    private Long purchaseOrderId06;

    @TableField(exist = false)
    private Long purchaseOrderId07;

    @TableField(exist = false)
    private Long purchaseOrderId08;

    @TableField(exist = false)
    private Long purchaseOrderId09;

    @TableField(exist = false)
    private Long purchaseOrderId10;

    @TableField(exist = false)
    private Long purchaseOrderId11;

    @TableField(exist = false)
    private Long purchaseOrderId12;

    @TableField(exist = false)
    private Long purchaseOrderId13;

    @TableField(exist = false)
    private Long purchaseOrderId14;

    @TableField(exist = false)
    private Long purchaseOrderId15;

    @TableField(exist = false)
    private Long purchaseOrderId16;

    @TableField(exist = false)
    private Long purchaseOrderId17;

    @TableField(exist = false)
    private Long purchaseOrderId18;

    @TableField(exist = false)
    private Long purchaseOrderId19;

    @TableField(exist = false)
    private Long purchaseOrderId20;

    @TableField(exist = false)
    private Long purchaseOrderId21;

    @TableField(exist = false)
    private Long purchaseOrderId22;

    @TableField(exist = false)
    private Long purchaseOrderId23;

    @TableField(exist = false)
    private Long purchaseOrderId24;

    @TableField(exist = false)
    private Long purchaseOrderId25;

    @TableField(exist = false)
    private Long purchaseOrderId26;

    @TableField(exist = false)
    private Long purchaseOrderId27;

    @TableField(exist = false)
    private Long purchaseOrderId28;

    @TableField(exist = false)
    private Long purchaseOrderId29;

    @TableField(exist = false)
    private Long purchaseOrderId30;

    @TableField(exist = false)
    private Long purchaseOrderId31;

    @TableField(exist = false)
    private Integer status01;/**状态*/

    @TableField(exist = false)
    private Integer status02;

    @TableField(exist = false)
    private Integer status03;

    @TableField(exist = false)
    private Integer status04;

    @TableField(exist = false)
    private Integer status05;

    @TableField(exist = false)
    private Integer status06;

    @TableField(exist = false)
    private Integer status07;

    @TableField(exist = false)
    private Integer status08;

    @TableField(exist = false)
    private Integer status09;

    @TableField(exist = false)
    private Integer status10;

    @TableField(exist = false)
    private Integer status11;

    @TableField(exist = false)
    private Integer status12;

    @TableField(exist = false)
    private Integer status13;

    @TableField(exist = false)
    private Integer status14;

    @TableField(exist = false)
    private Integer status15;

    @TableField(exist = false)
    private Integer status16;

    @TableField(exist = false)
    private Integer status17;

    @TableField(exist = false)
    private Integer status18;

    @TableField(exist = false)
    private Integer status19;

    @TableField(exist = false)
    private Integer status20;

    @TableField(exist = false)
    private Integer status21;

    @TableField(exist = false)
    private Integer status22;

    @TableField(exist = false)
    private Integer status23;

    @TableField(exist = false)
    private Integer status24;

    @TableField(exist = false)
    private Integer status25;

    @TableField(exist = false)
    private Integer status26;

    @TableField(exist = false)
    private Integer status27;

    @TableField(exist = false)
    private Integer status28;

    @TableField(exist = false)
    private Integer status29;

    @TableField(exist = false)
    private Integer status30;

    @TableField(exist = false)
    private Integer status31;

    @TableField(exist = false)
    private List<DeliveryPlanEO> deliveryPlans;

    @TableField(exist = false)
    private List<ProductOrderReleaseDetailEO> productOrderReleaseDetails;

    @TableField(exist = false)
    private Double snp;


    @Override
    public Serializable getId() {
        return this.purchaseOrderId;
    }

    /** 主键 */
    public Long getPurchaseOrderId(){
        return this.purchaseOrderId;
    }
    /** 主键 */
    public void setPurchaseOrderId(Long purchaseOrderId){
        this.purchaseOrderId = purchaseOrderId;
    }
    /** 流水号 */
    public String getVoucherNo(){
        return this.voucherNo;
    }
    /** 流水号 */
    public void setVoucherNo(String voucherNo){
        this.voucherNo = voucherNo;
    }
    /** 单据类型;1-采购订单，2-委外订单。 */
    public Integer getType(){
        return this.type;
    }
    /** 单据类型;1-采购订单，2-委外订单。 */
    public void setType(Integer type){
        this.type = type;
    }
    /** 供应商 */
    public Long getSupplierId(){
        return this.supplierId;
    }
    /** 供应商 */
    public void setSupplierId(Long supplierId){
        this.supplierId = supplierId;
    }
    /** 供应商名称 */
    public String getSupplierName() {
        return supplierName;
    }
    /** 供应商名称 */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    /** 供应商编码 */
    public String getSupplierCode() {
        return supplierCode;
    }
    /** 供应商编码 */
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
    /** 部门 */
    public String getDepartment() {
        return department;
    }
    /** 部门 */
    public void setDepartment(String department) {
        this.department = department;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 归属机构名称 */
    public String getOrgName() {
        return orgName;
    }
    /** 归属机构名称 */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    /** 币种 */
    public String getCurrencyName() {
        return currencyName;
    }
    /** 币种 */
    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
    /** 物料ID */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料ID */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 物料编码 */
    public String getMaterialCode(){
        return this.materialCode;
    }
    /** 物料编码 */
    public void setMaterialCode(String materialCode){
        this.materialCode = materialCode;
    }
    /** 存货编码 */
    public String getInventoryCode() {
        return inventoryCode;
    }
    /** 存货编码 */
    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }
    /** 物料名称 */
    public String getMaterialName(){
        return this.materialName;
    }
    /** 物料名称 */
    public void setMaterialName(String materialName){
        this.materialName = materialName;
    }
    /** 零件号 */
    public String getElementNo(){
        return this.elementNo;
    }
    /** 零件号 */
    public void setElementNo(String elementNo){
        this.elementNo = elementNo;
    }
    /** 规格型号 */
    public String getSpecification(){
        return this.specification;
    }
    /** 规格型号 */
    public void setSpecification(String specification){
        this.specification = specification;
    }
    /** 计量单位 */
    public String getUnitName() {
        return unitName;
    }
    /** 计量单位 */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    /** 创建日期 */
    public Date getCreateDate() {
        return createDate;
    }
    /** 创建日期 */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    /** 计划到货日期 */
    public Date getPlanArriveDate(){
        return this.planArriveDate;
    }
    /** 计划到货日期 */
    public void setPlanArriveDate(Date planArriveDate){
        this.planArriveDate = planArriveDate;
    }
    /** 计划送货数量 */
    public Double getPlanDeliveryQuantity(){
        return this.planDeliveryQuantity;
    }
    /** 计划送货数量 */
    public void setPlanDeliveryQuantity(Double planDeliveryQuantity){
        this.planDeliveryQuantity = planDeliveryQuantity;
    }
    /** 未送数量 */
    public Double getNotDeliveryQuantity(){
        return this.notDeliveryQuantity;
    }
    /** 未送数量 */
    public void setNotDeliveryQuantity(Double notDeliveryQuantity){
        this.notDeliveryQuantity = notDeliveryQuantity;
    }
    /** 实际送货数量 */
    public Double getActualDeliveryQuantity(){
        return this.actualDeliveryQuantity;
    }
    /** 实际送货数量 */
    public void setActualDeliveryQuantity(Double actualDeliveryQuantity){
        this.actualDeliveryQuantity = actualDeliveryQuantity;
    }
    /** 合格数量 */
    public Double getQualifiedQuantity(){
        return this.qualifiedQuantity;
    }
    /** 合格数量 */
    public void setQualifiedQuantity(Double qualifiedQuantity){
        this.qualifiedQuantity = qualifiedQuantity;
    }
    /** 退货数量 */
    public Double getReturnedQuantity(){
        return this.returnedQuantity;
    }
    /** 退货数量 */
    public void setReturnedQuantity(Double returnedQuantity){
        this.returnedQuantity = returnedQuantity;
    }
    /** 订单编号 */
    public String getPurchaseOrderNo(){
        return this.purchaseOrderNo;
    }
    /** 订单编号 */
    public void setPurchaseOrderNo(String purchaseOrderNo){
        this.purchaseOrderNo = purchaseOrderNo;
    }
    /** 创单人ID */
    public Long getCreateUserId(){
        return this.createUserId;
    }
    /** 创单人ID */
    public void setCreateUserId(Long createUserId){
        this.createUserId = createUserId;
    }
    /** 创单人用户名 */
    public String getCreateUserName(){
        return this.createUserName;
    }
    /** 创单人用户名 */
    public void setCreateUserName(String createUserName){
        this.createUserName = createUserName;
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
    /** 状态;0-新建;1-执行中;2-已完成;9-已关闭 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-执行中;2-已完成;9-已关闭 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 状态(关闭前的);0-新建;1-执行中;2-已完成; */
    public Integer getPreStatus(){
        return this.preStatus;
    }
    /** 状态(关闭前的);0-新建;1-执行中;2-已完成; */
    public void setPreStatus(Integer preStatus){
        this.preStatus = preStatus;
    }
    public List<DeliveryPlanEO> getDeliveryPlans() {
        return deliveryPlans;
    }

    public void setDeliveryPlans(List<DeliveryPlanEO> deliveryPlans) {
        this.deliveryPlans = deliveryPlans;
    }

    public Long getSerialDistributeId() {
        return serialDistributeId;
    }

    public void setSerialDistributeId(Long serialDistributeId) {
        this.serialDistributeId = serialDistributeId;
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

    public Long getPurchaseOrderId01() {
        return purchaseOrderId01;
    }

    public void setPurchaseOrderId01(Long purchaseOrderId01) {
        this.purchaseOrderId01 = purchaseOrderId01;
    }

    public Long getPurchaseOrderId02() {
        return purchaseOrderId02;
    }

    public void setPurchaseOrderId02(Long purchaseOrderId02) {
        this.purchaseOrderId02 = purchaseOrderId02;
    }

    public Long getPurchaseOrderId03() {
        return purchaseOrderId03;
    }

    public void setPurchaseOrderId03(Long purchaseOrderId03) {
        this.purchaseOrderId03 = purchaseOrderId03;
    }

    public Long getPurchaseOrderId04() {
        return purchaseOrderId04;
    }

    public void setPurchaseOrderId04(Long purchaseOrderId04) {
        this.purchaseOrderId04 = purchaseOrderId04;
    }

    public Long getPurchaseOrderId05() {
        return purchaseOrderId05;
    }

    public void setPurchaseOrderId05(Long purchaseOrderId05) {
        this.purchaseOrderId05 = purchaseOrderId05;
    }

    public Long getPurchaseOrderId06() {
        return purchaseOrderId06;
    }

    public void setPurchaseOrderId06(Long purchaseOrderId06) {
        this.purchaseOrderId06 = purchaseOrderId06;
    }

    public Long getPurchaseOrderId07() {
        return purchaseOrderId07;
    }

    public void setPurchaseOrderId07(Long purchaseOrderId07) {
        this.purchaseOrderId07 = purchaseOrderId07;
    }

    public Long getPurchaseOrderId08() {
        return purchaseOrderId08;
    }

    public void setPurchaseOrderId08(Long purchaseOrderId08) {
        this.purchaseOrderId08 = purchaseOrderId08;
    }

    public Long getPurchaseOrderId09() {
        return purchaseOrderId09;
    }

    public void setPurchaseOrderId09(Long purchaseOrderId09) {
        this.purchaseOrderId09 = purchaseOrderId09;
    }

    public Long getPurchaseOrderId10() {
        return purchaseOrderId10;
    }

    public void setPurchaseOrderId10(Long purchaseOrderId10) {
        this.purchaseOrderId10 = purchaseOrderId10;
    }

    public Long getPurchaseOrderId11() {
        return purchaseOrderId11;
    }

    public void setPurchaseOrderId11(Long purchaseOrderId11) {
        this.purchaseOrderId11 = purchaseOrderId11;
    }

    public Long getPurchaseOrderId12() {
        return purchaseOrderId12;
    }

    public void setPurchaseOrderId12(Long purchaseOrderId12) {
        this.purchaseOrderId12 = purchaseOrderId12;
    }

    public Long getPurchaseOrderId13() {
        return purchaseOrderId13;
    }

    public void setPurchaseOrderId13(Long purchaseOrderId13) {
        this.purchaseOrderId13 = purchaseOrderId13;
    }

    public Long getPurchaseOrderId14() {
        return purchaseOrderId14;
    }

    public void setPurchaseOrderId14(Long purchaseOrderId14) {
        this.purchaseOrderId14 = purchaseOrderId14;
    }

    public Long getPurchaseOrderId15() {
        return purchaseOrderId15;
    }

    public void setPurchaseOrderId15(Long purchaseOrderId15) {
        this.purchaseOrderId15 = purchaseOrderId15;
    }

    public Long getPurchaseOrderId16() {
        return purchaseOrderId16;
    }

    public void setPurchaseOrderId16(Long purchaseOrderId16) {
        this.purchaseOrderId16 = purchaseOrderId16;
    }

    public Long getPurchaseOrderId17() {
        return purchaseOrderId17;
    }

    public void setPurchaseOrderId17(Long purchaseOrderId17) {
        this.purchaseOrderId17 = purchaseOrderId17;
    }

    public Long getPurchaseOrderId18() {
        return purchaseOrderId18;
    }

    public void setPurchaseOrderId18(Long purchaseOrderId18) {
        this.purchaseOrderId18 = purchaseOrderId18;
    }

    public Long getPurchaseOrderId19() {
        return purchaseOrderId19;
    }

    public void setPurchaseOrderId19(Long purchaseOrderId19) {
        this.purchaseOrderId19 = purchaseOrderId19;
    }

    public Long getPurchaseOrderId20() {
        return purchaseOrderId20;
    }

    public void setPurchaseOrderId20(Long purchaseOrderId20) {
        this.purchaseOrderId20 = purchaseOrderId20;
    }

    public Long getPurchaseOrderId21() {
        return purchaseOrderId21;
    }

    public void setPurchaseOrderId21(Long purchaseOrderId21) {
        this.purchaseOrderId21 = purchaseOrderId21;
    }

    public Long getPurchaseOrderId22() {
        return purchaseOrderId22;
    }

    public void setPurchaseOrderId22(Long purchaseOrderId22) {
        this.purchaseOrderId22 = purchaseOrderId22;
    }

    public Long getPurchaseOrderId23() {
        return purchaseOrderId23;
    }

    public void setPurchaseOrderId23(Long purchaseOrderId23) {
        this.purchaseOrderId23 = purchaseOrderId23;
    }

    public Long getPurchaseOrderId24() {
        return purchaseOrderId24;
    }

    public void setPurchaseOrderId24(Long purchaseOrderId24) {
        this.purchaseOrderId24 = purchaseOrderId24;
    }

    public Long getPurchaseOrderId25() {
        return purchaseOrderId25;
    }

    public void setPurchaseOrderId25(Long purchaseOrderId25) {
        this.purchaseOrderId25 = purchaseOrderId25;
    }

    public Long getPurchaseOrderId26() {
        return purchaseOrderId26;
    }

    public void setPurchaseOrderId26(Long purchaseOrderId26) {
        this.purchaseOrderId26 = purchaseOrderId26;
    }

    public Long getPurchaseOrderId27() {
        return purchaseOrderId27;
    }

    public void setPurchaseOrderId27(Long purchaseOrderId27) {
        this.purchaseOrderId27 = purchaseOrderId27;
    }

    public Long getPurchaseOrderId28() {
        return purchaseOrderId28;
    }

    public void setPurchaseOrderId28(Long purchaseOrderId28) {
        this.purchaseOrderId28 = purchaseOrderId28;
    }

    public Long getPurchaseOrderId29() {
        return purchaseOrderId29;
    }

    public void setPurchaseOrderId29(Long purchaseOrderId29) {
        this.purchaseOrderId29 = purchaseOrderId29;
    }

    public Long getPurchaseOrderId30() {
        return purchaseOrderId30;
    }

    public void setPurchaseOrderId30(Long purchaseOrderId30) {
        this.purchaseOrderId30 = purchaseOrderId30;
    }

    public Long getPurchaseOrderId31() {
        return purchaseOrderId31;
    }

    public void setPurchaseOrderId31(Long purchaseOrderId31) {
        this.purchaseOrderId31 = purchaseOrderId31;
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

    public Integer getStatus13() {
        return status13;
    }

    public void setStatus13(Integer status13) {
        this.status13 = status13;
    }

    public Integer getStatus14() {
        return status14;
    }

    public void setStatus14(Integer status14) {
        this.status14 = status14;
    }

    public Integer getStatus15() {
        return status15;
    }

    public void setStatus15(Integer status15) {
        this.status15 = status15;
    }

    public Integer getStatus16() {
        return status16;
    }

    public void setStatus16(Integer status16) {
        this.status16 = status16;
    }

    public Integer getStatus17() {
        return status17;
    }

    public void setStatus17(Integer status17) {
        this.status17 = status17;
    }

    public Integer getStatus18() {
        return status18;
    }

    public void setStatus18(Integer status18) {
        this.status18 = status18;
    }

    public Integer getStatus19() {
        return status19;
    }

    public void setStatus19(Integer status19) {
        this.status19 = status19;
    }

    public Integer getStatus20() {
        return status20;
    }

    public void setStatus20(Integer status20) {
        this.status20 = status20;
    }

    public Integer getStatus21() {
        return status21;
    }

    public void setStatus21(Integer status21) {
        this.status21 = status21;
    }

    public Integer getStatus22() {
        return status22;
    }

    public void setStatus22(Integer status22) {
        this.status22 = status22;
    }

    public Integer getStatus23() {
        return status23;
    }

    public void setStatus23(Integer status23) {
        this.status23 = status23;
    }

    public Integer getStatus24() {
        return status24;
    }

    public void setStatus24(Integer status24) {
        this.status24 = status24;
    }

    public Integer getStatus25() {
        return status25;
    }

    public void setStatus25(Integer status25) {
        this.status25 = status25;
    }

    public Integer getStatus26() {
        return status26;
    }

    public void setStatus26(Integer status26) {
        this.status26 = status26;
    }

    public Integer getStatus27() {
        return status27;
    }

    public void setStatus27(Integer status27) {
        this.status27 = status27;
    }

    public Integer getStatus28() {
        return status28;
    }

    public void setStatus28(Integer status28) {
        this.status28 = status28;
    }

    public Integer getStatus29() {
        return status29;
    }

    public void setStatus29(Integer status29) {
        this.status29 = status29;
    }

    public Integer getStatus30() {
        return status30;
    }

    public void setStatus30(Integer status30) {
        this.status30 = status30;
    }

    public Integer getStatus31() {
        return status31;
    }

    public void setStatus31(Integer status31) {
        this.status31 = status31;
    }

    public Long getSerialId() {
        return serialId;
    }

    public void setSerialId(Long serialId) {
        this.serialId = serialId;
    }

    public Double getPlanRequireQuantity() {
        return planRequireQuantity;
    }

    public void setPlanRequireQuantity(Double planRequireQuantity) {
        this.planRequireQuantity = planRequireQuantity;
    }

    public Integer getIsChangeConfirm() {
        return isChangeConfirm;
    }

    public void setIsChangeConfirm(Integer isChangeConfirm) {
        this.isChangeConfirm = isChangeConfirm;
    }

    public Integer getConfirmVersion() {
        return confirmVersion;
    }

    public void setConfirmVersion(Integer confirmVersion) {
        this.confirmVersion = confirmVersion;
    }

    public Date getLastConfirmTime() {
        return lastConfirmTime;
    }

    public void setLastConfirmTime(Date lastConfirmTime) {
        this.lastConfirmTime = lastConfirmTime;
    }

    public Long getParentSerialId() {
        return parentSerialId;
    }

    public void setParentSerialId(Long parentSerialId) {
        this.parentSerialId = parentSerialId;
    }

    public String getSerialCode() {
        return serialCode;
    }

    public void setSerialCode(String serialCode) {
        this.serialCode = serialCode;
    }

    public List<ProductOrderReleaseDetailEO> getProductOrderReleaseDetails() {
        return productOrderReleaseDetails;
    }

    public void setProductOrderReleaseDetails(List<ProductOrderReleaseDetailEO> productOrderReleaseDetails) {
        this.productOrderReleaseDetails = productOrderReleaseDetails;
    }

    public Double getSnp() {
        return snp;
    }

    public void setSnp(Double snp) {
        this.snp = snp;
    }
}
