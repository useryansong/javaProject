package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@TableName("wms_delivery_order_detail")
@KeySequence("wms_delivery_order_detail")
public class DeliveryOrderDetailEO extends AbstractAuditableEntity<DeliveryOrderDetailEO> {
    private static final long serialVersionUID = -5758337540202027663L;

    @TableId(type = IdType.INPUT)
    private Long deliveryDetailId;/** 主键 */

    private Long deliveryOrderId;/** 出库单ID */

    private Long orderId;/** 订单ID */

    private Long orderDetailId;/** 订单明细ID */

    private Long materialId;/** 物料 */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private String inventoryCode;/** 存货编码 */

    private String elementNo;/** 零件号 */

    private String specification;/** 规格型号 */

    private Long unitId;/** 计量单位 */

    @TableField(exist = false)
    private String unitName;/** 计量单位名称 */

    private String figureNumber;/** 图号 */

    private String figureVersion;/** 版本号 */

    private Long warehouseId;/** 仓库 */

    @TableField(exist = false)
    private String warehouseName;/** 仓库名称 */

    @TableField(exist = false)
    private String warehouseCode;/** 仓库编码 */

    @TableField(exist = false)
    private String erpCode;/** 仓库ERP编码 */

    private Long warehouseLocationId;/** 库位 */

    @TableField(exist = false)
    private String warehouseLocationName;/** 库位名称 */

    private Double deliveryAmount;/** 出库数量 */

    private Double relDeliveryAmount;/**实际出库数量*/

    @TableField(exist = false)
    private Double sumRelDeliveryAmount;/** 总出库数(按月) */

    @TableField(exist = false)
    private Double count;/** 库存数量 */

    private Integer status;/** 状态;0-新建;1-进行中;2-已完成 */

    private Integer checkStatus;/** 客户对账状态;0-未对账;1-已对账; */

    private Long checkId;/** 客户对账ID; */

    private String remarks;/** 备注 */

    @TableField(exist = false)
    private String voucherNo;/** 单据编号(出库单) */

    @TableField(exist = false)
    private String erpVoucherNo1;/** ERP单据编号(委外发货单(周计划管理中委外发货)) */

    @TableField(exist = false)
    private String erpVoucherNo2;/** ERP单据编号(销售发货(送货计划页面)) */

    @TableField(exist = false)
    private String erpVoucherNo3;/** ERP单据编号(销售订单(送货计划页面)) */

    @TableField(exist = false)
    private String voucherNoSub;/** 单据编号(出库单)去掉前面两位字母 */

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;/** 出库日期(出库单) */

    @TableField(exist = false)
    private String destinationName;/** 业务对象名称(出库单) */

    @TableField(exist = false)
    private String destinationCode;/** 业务对象编码(出库单) */

    @TableField(exist = false)
    private String createBillName;/** 制单人(导出时使用,取当前登录人真实名) */

    @TableField(exist = false)
    private String deliveryTypeCode;/** 出库类别编码(导出为生产领料单使用) */

    private String deliveryNoteNo;/** 内部交易单号 */

    @TableField(exist = false)
    private String projectNo;/** 项目号 */

    @TableField(exist = false)
    private Double snp;/** SNP */

    @TableField(exist = false)
    private String projectType;/** 项目大类 */

    @TableField(exist = false)
    private String projectCode;/** 项目编号 */

    private Long innerMaterialId;

    private Integer isMatch;

    @TableField(exist = false)
    private Long stampingMaterialConsumptionQuotaId;/** 耗用表ID */


    //客户报表
    @TableField(exist = false)
    private Double deliveryQuantity;/** 送货数量 */

    @TableField(exist = false)
    private Double actualReceiveQuantity;/** 实收数量 */

    @TableField(exist = false)
    private Double notQualifiedQuantity;/** 不合格数量 */

    @TableField(exist = false)
    private String monthDate;/** 月份 */

    @TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date currentDay;

    @TableField(exist = false)
    private Long customerId;/** 客户ID */

    @TableField(exist = false)
    private String customerName;/** 客户名称 */

    @TableField(exist = false)
    private Long orgId;

    @TableField(exist = false)
    private String orgName;

    @TableField(exist = false)
    private Double deliveryQuantity01;/** 订单数量01 */

    @TableField(exist = false)
    private Double deliveryQuantity02;/** 订单数量02 */

    @TableField(exist = false)
    private Double deliveryQuantity03;/** 订单数量03 */

    @TableField(exist = false)
    private Double deliveryQuantity04;/** 订单数量04 */

    @TableField(exist = false)
    private Double deliveryQuantity05;/** 订单数量05 */

    @TableField(exist = false)
    private Double deliveryQuantity06;/** 订单数量06 */

    @TableField(exist = false)
    private Double deliveryQuantity07;/** 订单数量07 */

    @TableField(exist = false)
    private Double deliveryQuantity08;/** 订单数量08 */

    @TableField(exist = false)
    private Double deliveryQuantity09;/** 订单数量09 */

    @TableField(exist = false)
    private Double deliveryQuantity10;/** 订单数量10 */

    @TableField(exist = false)
    private Double deliveryQuantity11;/** 订单数量11 */

    @TableField(exist = false)
    private Double deliveryQuantity12;/** 订单数量12 */

    @TableField(exist = false)
    private Double deliveryQuantity13;/** 订单数量13 */

    @TableField(exist = false)
    private Double deliveryQuantity14;/** 订单数量14 */

    @TableField(exist = false)
    private Double deliveryQuantity15;/** 订单数量15 */

    @TableField(exist = false)
    private Double deliveryQuantity16;/** 订单数量16 */

    @TableField(exist = false)
    private Double deliveryQuantity17;/** 订单数量17 */

    @TableField(exist = false)
    private Double deliveryQuantity18;/** 订单数量18 */

    @TableField(exist = false)
    private Double deliveryQuantity19;/** 订单数量19 */

    @TableField(exist = false)
    private Double deliveryQuantity20;/** 订单数量20 */

    @TableField(exist = false)
    private Double deliveryQuantity21;/** 订单数量21 */

    @TableField(exist = false)
    private Double deliveryQuantity22;/** 订单数量22 */

    @TableField(exist = false)
    private Double deliveryQuantity23;/** 订单数量23 */

    @TableField(exist = false)
    private Double deliveryQuantity24;/** 订单数量24 */

    @TableField(exist = false)
    private Double deliveryQuantity25;/** 订单数量25 */

    @TableField(exist = false)
    private Double deliveryQuantity26;/** 订单数量26 */

    @TableField(exist = false)
    private Double deliveryQuantity27;/** 订单数量27 */

    @TableField(exist = false)
    private Double deliveryQuantity28;/** 订单数量28 */

    @TableField(exist = false)
    private Double deliveryQuantity29;/** 订单数量29 */

    @TableField(exist = false)
    private Double deliveryQuantity30;/** 订单数量30 */

    @TableField(exist = false)
    private Double deliveryQuantity31;/** 订单数量31 */

    @TableField(exist = false)
    private Double actualReceiveQuantity01;/** 交付数量01 */

    @TableField(exist = false)
    private Double actualReceiveQuantity02;/** 交付数量02 */

    @TableField(exist = false)
    private Double actualReceiveQuantity03;/** 交付数量03 */

    @TableField(exist = false)
    private Double actualReceiveQuantity04;/** 交付数量04 */

    @TableField(exist = false)
    private Double actualReceiveQuantity05;/** 交付数量05 */

    @TableField(exist = false)
    private Double actualReceiveQuantity06;/** 交付数量06 */

    @TableField(exist = false)
    private Double actualReceiveQuantity07;/** 交付数量07 */

    @TableField(exist = false)
    private Double actualReceiveQuantity08;/** 交付数量08 */

    @TableField(exist = false)
    private Double actualReceiveQuantity09;/** 交付数量09 */

    @TableField(exist = false)
    private Double actualReceiveQuantity10;/** 交付数量10 */

    @TableField(exist = false)
    private Double actualReceiveQuantity11;/** 交付数量11 */

    @TableField(exist = false)
    private Double actualReceiveQuantity12;/** 交付数量12 */

    @TableField(exist = false)
    private Double actualReceiveQuantity13;/** 交付数量13 */

    @TableField(exist = false)
    private Double actualReceiveQuantity14;/** 交付数量14 */

    @TableField(exist = false)
    private Double actualReceiveQuantity15;/** 交付数量15 */

    @TableField(exist = false)
    private Double actualReceiveQuantity16;/** 交付数量16 */

    @TableField(exist = false)
    private Double actualReceiveQuantity17;/** 交付数量17 */

    @TableField(exist = false)
    private Double actualReceiveQuantity18;/** 交付数量18 */

    @TableField(exist = false)
    private Double actualReceiveQuantity19;/** 交付数量19 */

    @TableField(exist = false)
    private Double actualReceiveQuantity20;/** 交付数量20 */

    @TableField(exist = false)
    private Double actualReceiveQuantity21;/** 交付数量21 */

    @TableField(exist = false)
    private Double actualReceiveQuantity22;/** 交付数量22 */

    @TableField(exist = false)
    private Double actualReceiveQuantity23;/** 交付数量23 */

    @TableField(exist = false)
    private Double actualReceiveQuantity24;/** 交付数量24 */

    @TableField(exist = false)
    private Double actualReceiveQuantity25;/** 交付数量25 */

    @TableField(exist = false)
    private Double actualReceiveQuantity26;/** 交付数量26 */

    @TableField(exist = false)
    private Double actualReceiveQuantity27;/** 交付数量27 */

    @TableField(exist = false)
    private Double actualReceiveQuantity28;/** 交付数量28 */

    @TableField(exist = false)
    private Double actualReceiveQuantity29;/** 交付数量29 */

    @TableField(exist = false)
    private Double actualReceiveQuantity30;/** 交付数量30 */

    @TableField(exist = false)
    private Double actualReceiveQuantity31;/** 交付数量31 */

    @TableField(exist = false)
    private Double notQualifiedQuantity01;/** 退货数量01 */

    @TableField(exist = false)
    private Double notQualifiedQuantity02;/** 退货数量02 */

    @TableField(exist = false)
    private Double notQualifiedQuantity03;/** 退货数量03 */

    @TableField(exist = false)
    private Double notQualifiedQuantity04;/** 退货数量04 */

    @TableField(exist = false)
    private Double notQualifiedQuantity05;/** 退货数量05 */

    @TableField(exist = false)
    private Double notQualifiedQuantity06;/** 退货数量06 */

    @TableField(exist = false)
    private Double notQualifiedQuantity07;/** 退货数量07 */

    @TableField(exist = false)
    private Double notQualifiedQuantity08;/** 退货数量08 */

    @TableField(exist = false)
    private Double notQualifiedQuantity09;/** 退货数量09 */

    @TableField(exist = false)
    private Double notQualifiedQuantity10;/** 退货数量10 */

    @TableField(exist = false)
    private Double notQualifiedQuantity11;/** 退货数量11 */

    @TableField(exist = false)
    private Double notQualifiedQuantity12;/** 退货数量12 */

    @TableField(exist = false)
    private Double notQualifiedQuantity13;/** 退货数量13 */

    @TableField(exist = false)
    private Double notQualifiedQuantity14;/** 退货数量14 */

    @TableField(exist = false)
    private Double notQualifiedQuantity15;/** 退货数量15 */

    @TableField(exist = false)
    private Double notQualifiedQuantity16;/** 退货数量16 */

    @TableField(exist = false)
    private Double notQualifiedQuantity17;/** 退货数量17 */

    @TableField(exist = false)
    private Double notQualifiedQuantity18;/** 退货数量18 */

    @TableField(exist = false)
    private Double notQualifiedQuantity19;/** 退货数量19 */

    @TableField(exist = false)
    private Double notQualifiedQuantity20;/** 退货数量20 */

    @TableField(exist = false)
    private Double notQualifiedQuantity21;/** 退货数量21 */

    @TableField(exist = false)
    private Double notQualifiedQuantity22;/** 退货数量22 */

    @TableField(exist = false)
    private Double notQualifiedQuantity23;/** 退货数量23 */

    @TableField(exist = false)
    private Double notQualifiedQuantity24;/** 退货数量24 */

    @TableField(exist = false)
    private Double notQualifiedQuantity25;/** 退货数量25 */

    @TableField(exist = false)
    private Double notQualifiedQuantity26;/** 退货数量26 */

    @TableField(exist = false)
    private Double notQualifiedQuantity27;/** 退货数量27 */

    @TableField(exist = false)
    private Double notQualifiedQuantity28;/** 退货数量28 */

    @TableField(exist = false)
    private Double notQualifiedQuantity29;/** 退货数量29 */

    @TableField(exist = false)
    private Double notQualifiedQuantity30;/** 退货数量30 */

    @TableField(exist = false)
    private Double notQualifiedQuantity31;/** 退货数量31 */

    @TableField(exist = false)
    private Double totalActualReceiveQuantity;/**交付总数*/

    @TableField(exist = false)
    private Double totalNotQualifiedQuantity;/**退货总数*/

    @TableField(exist = false)
    private Double totalDeliveryQuantity;/**订单总数*/

    @TableField(exist = false)
    private Integer totalCount;

    @TableField(exist = false)
    private String stampingMaterialName;/** 冲压件的物料名称*/

    @TableField(exist = false)
    private String stampingElementNo;/** 冲压件的物料零件号*/

    @TableField(exist = false)
    private String inventoryCoding;/** 冲压件的物料存货编码*/

    @TableField(exist = false)
    private String originalMaterialName;/** 原材料的物料名称*/

    @TableField(exist = false)
    private String originalElementNo;/** 原材料的物料零件号*/

    @TableField(exist = false)
    private String originalInventoryCode;/** 原材料的物料存货编码*/

    @TableField(exist = false)
    private String currencyName;

    @TableField(exist = false)
    private Long receiveOrderId;


    @Override
    public Serializable getId() {
        return this.deliveryDetailId;
    }

    /** 主键 */
    public Long getDeliveryDetailId(){
        return this.deliveryDetailId;
    }
    /** 主键 */
    public void setDeliveryDetailId(Long deliveryDetailId){
        this.deliveryDetailId = deliveryDetailId;
    }
    /** 出库单ID */
    public Long getDeliveryOrderId(){
        return this.deliveryOrderId;
    }
    /** 出库单ID */
    public void setDeliveryOrderId(Long deliveryOrderId){
        this.deliveryOrderId = deliveryOrderId;
    }
    /** 订单ID */
    public Long getOrderId(){
        return this.orderId;
    }
    /** 订单ID */
    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }
    /** 订单明细ID */
    public Long getOrderDetailId(){
        return this.orderDetailId;
    }
    /** 订单明细ID */
    public void setOrderDetailId(Long orderDetailId){
        this.orderDetailId = orderDetailId;
    }
    /** 物料 */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料 */
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
    /** 物料名称 */
    public String getMaterialName(){
        return this.materialName;
    }
    /** 物料名称 */
    public void setMaterialName(String materialName){
        this.materialName = materialName;
    }
    /** 存货编码 */
    public String getInventoryCode(){
        return this.inventoryCode;
    }
    /** 存货编码 */
    public void setInventoryCode(String inventoryCode){
        this.inventoryCode = inventoryCode;
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
    public Long getUnitId(){
        return this.unitId;
    }
    /** 计量单位 */
    public void setUnitId(Long unitId){
        this.unitId = unitId;
    }
    /** 计量单位名称 */
    public String getUnitName() {
        return unitName;
    }
    /** 计量单位名称 */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    /** 图号 */
    public String getFigureNumber(){
        return this.figureNumber;
    }
    /** 图号 */
    public void setFigureNumber(String figureNumber){
        this.figureNumber = figureNumber;
    }
    /** 版本号 */
    public String getFigureVersion(){
        return this.figureVersion;
    }
    /** 版本号 */
    public void setFigureVersion(String figureVersion){
        this.figureVersion = figureVersion;
    }
    /** 仓库 */
    public Long getWarehouseId(){
        return this.warehouseId;
    }
    /** 仓库 */
    public void setWarehouseId(Long warehouseId){
        this.warehouseId = warehouseId;
    }
    /** 仓库名称 */
    public String getWarehouseName() {
        return warehouseName;
    }
    /** 仓库名称 */
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
    /** 仓库编码 */
    public String getWarehouseCode() {
        return this.warehouseCode;
    }
    /** 仓库编码 */
    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
    /** 库位 */
    public Long getWarehouseLocationId(){
        return this.warehouseLocationId;
    }
    /** 库位 */
    public void setWarehouseLocationId(Long warehouseLocationId){
        this.warehouseLocationId = warehouseLocationId;
    }
    /** 库位名称 */
    public String getWarehouseLocationName() {
        return warehouseLocationName;
    }
    /** 库位名称 */
    public void setWarehouseLocationName(String warehouseLocationName) {
        this.warehouseLocationName = warehouseLocationName;
    }
    /** 出库数量 */
    public Double getDeliveryAmount(){
        return this.deliveryAmount;
    }
    /** 出库数量 */
    public void setDeliveryAmount(Double deliveryAmount){
        this.deliveryAmount = deliveryAmount;
    }
    /** 状态;0-新建;1-进行中;2-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-进行中;2-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Double getRelDeliveryAmount() {
        return relDeliveryAmount;
    }

    public void setRelDeliveryAmount(Double relDeliveryAmount) {
        this.relDeliveryAmount = relDeliveryAmount;
    }

    /** 单据编号(出库单) */
    public String getVoucherNo() {
        return this.voucherNo;
    }
    /** 单据编号(出库单) */
    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }
    /** 出库日期(出库单) */
    public Date getDeliveryDate() {
        return this.deliveryDate;
    }
    /** 出库日期(出库单) */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    /** 业务对象名称(出库单) */
    public String getDestinationName() {
        return this.destinationName;
    }
    /** 业务对象名称(出库单) */
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
    /** 业务对象编码(出库单) */
    public String getDestinationCode() {
        return this.destinationCode;
    }
    /** 业务对象编码(出库单) */
    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    public String getCreateBillName() {
        return createBillName;
    }

    public void setCreateBillName(String createBillName) {
        this.createBillName = createBillName;
    }

    public String getDeliveryTypeCode() {
        return deliveryTypeCode;
    }

    public void setDeliveryTypeCode(String deliveryTypeCode) {
        this.deliveryTypeCode = deliveryTypeCode;
    }

    public String getVoucherNoSub() {
        return voucherNoSub;
    }

    public void setVoucherNoSub(String voucherNoSub) {
        this.voucherNoSub = voucherNoSub;
    }

    public String getDeliveryNoteNo() {
        return deliveryNoteNo;
    }

    public void setDeliveryNoteNo(String deliveryNoteNo) {
        this.deliveryNoteNo = deliveryNoteNo;
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

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Long getInnerMaterialId() {
        return innerMaterialId;
    }

    public void setInnerMaterialId(Long innerMaterialId) {
        this.innerMaterialId = innerMaterialId;
    }

    public Integer getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(Integer isMatch) {
        this.isMatch = isMatch;
    }

    public Double getDeliveryQuantity() {
        return deliveryQuantity;
    }

    public void setDeliveryQuantity(Double deliveryQuantity) {
        this.deliveryQuantity = deliveryQuantity;
    }

    public Double getActualReceiveQuantity() {
        return actualReceiveQuantity;
    }

    public void setActualReceiveQuantity(Double actualReceiveQuantity) {
        this.actualReceiveQuantity = actualReceiveQuantity;
    }

    public Double getNotQualifiedQuantity() {
        return notQualifiedQuantity;
    }

    public void setNotQualifiedQuantity(Double notQualifiedQuantity) {
        this.notQualifiedQuantity = notQualifiedQuantity;
    }

    public String getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(String monthDate) {
        this.monthDate = monthDate;
    }

    public Date getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(Date currentDay) {
        this.currentDay = currentDay;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public Double getDeliveryQuantity01() {
        return deliveryQuantity01;
    }

    public void setDeliveryQuantity01(Double deliveryQuantity01) {
        this.deliveryQuantity01 = deliveryQuantity01;
    }

    public Double getDeliveryQuantity02() {
        return deliveryQuantity02;
    }

    public void setDeliveryQuantity02(Double deliveryQuantity02) {
        this.deliveryQuantity02 = deliveryQuantity02;
    }

    public Double getDeliveryQuantity03() {
        return deliveryQuantity03;
    }

    public void setDeliveryQuantity03(Double deliveryQuantity03) {
        this.deliveryQuantity03 = deliveryQuantity03;
    }

    public Double getDeliveryQuantity04() {
        return deliveryQuantity04;
    }

    public void setDeliveryQuantity04(Double deliveryQuantity04) {
        this.deliveryQuantity04 = deliveryQuantity04;
    }

    public Double getDeliveryQuantity05() {
        return deliveryQuantity05;
    }

    public void setDeliveryQuantity05(Double deliveryQuantity05) {
        this.deliveryQuantity05 = deliveryQuantity05;
    }

    public Double getDeliveryQuantity06() {
        return deliveryQuantity06;
    }

    public void setDeliveryQuantity06(Double deliveryQuantity06) {
        this.deliveryQuantity06 = deliveryQuantity06;
    }

    public Double getDeliveryQuantity07() {
        return deliveryQuantity07;
    }

    public void setDeliveryQuantity07(Double deliveryQuantity07) {
        this.deliveryQuantity07 = deliveryQuantity07;
    }

    public Double getDeliveryQuantity08() {
        return deliveryQuantity08;
    }

    public void setDeliveryQuantity08(Double deliveryQuantity08) {
        this.deliveryQuantity08 = deliveryQuantity08;
    }

    public Double getDeliveryQuantity09() {
        return deliveryQuantity09;
    }

    public void setDeliveryQuantity09(Double deliveryQuantity09) {
        this.deliveryQuantity09 = deliveryQuantity09;
    }

    public Double getDeliveryQuantity10() {
        return deliveryQuantity10;
    }

    public void setDeliveryQuantity10(Double deliveryQuantity10) {
        this.deliveryQuantity10 = deliveryQuantity10;
    }

    public Double getDeliveryQuantity11() {
        return deliveryQuantity11;
    }

    public void setDeliveryQuantity11(Double deliveryQuantity11) {
        this.deliveryQuantity11 = deliveryQuantity11;
    }

    public Double getDeliveryQuantity12() {
        return deliveryQuantity12;
    }

    public void setDeliveryQuantity12(Double deliveryQuantity12) {
        this.deliveryQuantity12 = deliveryQuantity12;
    }

    public Double getDeliveryQuantity13() {
        return deliveryQuantity13;
    }

    public void setDeliveryQuantity13(Double deliveryQuantity13) {
        this.deliveryQuantity13 = deliveryQuantity13;
    }

    public Double getDeliveryQuantity14() {
        return deliveryQuantity14;
    }

    public void setDeliveryQuantity14(Double deliveryQuantity14) {
        this.deliveryQuantity14 = deliveryQuantity14;
    }

    public Double getDeliveryQuantity15() {
        return deliveryQuantity15;
    }

    public void setDeliveryQuantity15(Double deliveryQuantity15) {
        this.deliveryQuantity15 = deliveryQuantity15;
    }

    public Double getDeliveryQuantity16() {
        return deliveryQuantity16;
    }

    public void setDeliveryQuantity16(Double deliveryQuantity16) {
        this.deliveryQuantity16 = deliveryQuantity16;
    }

    public Double getDeliveryQuantity17() {
        return deliveryQuantity17;
    }

    public void setDeliveryQuantity17(Double deliveryQuantity17) {
        this.deliveryQuantity17 = deliveryQuantity17;
    }

    public Double getDeliveryQuantity18() {
        return deliveryQuantity18;
    }

    public void setDeliveryQuantity18(Double deliveryQuantity18) {
        this.deliveryQuantity18 = deliveryQuantity18;
    }

    public Double getDeliveryQuantity19() {
        return deliveryQuantity19;
    }

    public void setDeliveryQuantity19(Double deliveryQuantity19) {
        this.deliveryQuantity19 = deliveryQuantity19;
    }

    public Double getDeliveryQuantity20() {
        return deliveryQuantity20;
    }

    public void setDeliveryQuantity20(Double deliveryQuantity20) {
        this.deliveryQuantity20 = deliveryQuantity20;
    }

    public Double getDeliveryQuantity21() {
        return deliveryQuantity21;
    }

    public void setDeliveryQuantity21(Double deliveryQuantity21) {
        this.deliveryQuantity21 = deliveryQuantity21;
    }

    public Double getDeliveryQuantity22() {
        return deliveryQuantity22;
    }

    public void setDeliveryQuantity22(Double deliveryQuantity22) {
        this.deliveryQuantity22 = deliveryQuantity22;
    }

    public Double getDeliveryQuantity23() {
        return deliveryQuantity23;
    }

    public void setDeliveryQuantity23(Double deliveryQuantity23) {
        this.deliveryQuantity23 = deliveryQuantity23;
    }

    public Double getDeliveryQuantity24() {
        return deliveryQuantity24;
    }

    public void setDeliveryQuantity24(Double deliveryQuantity24) {
        this.deliveryQuantity24 = deliveryQuantity24;
    }

    public Double getDeliveryQuantity25() {
        return deliveryQuantity25;
    }

    public void setDeliveryQuantity25(Double deliveryQuantity25) {
        this.deliveryQuantity25 = deliveryQuantity25;
    }

    public Double getDeliveryQuantity26() {
        return deliveryQuantity26;
    }

    public void setDeliveryQuantity26(Double deliveryQuantity26) {
        this.deliveryQuantity26 = deliveryQuantity26;
    }

    public Double getDeliveryQuantity27() {
        return deliveryQuantity27;
    }

    public void setDeliveryQuantity27(Double deliveryQuantity27) {
        this.deliveryQuantity27 = deliveryQuantity27;
    }

    public Double getDeliveryQuantity28() {
        return deliveryQuantity28;
    }

    public void setDeliveryQuantity28(Double deliveryQuantity28) {
        this.deliveryQuantity28 = deliveryQuantity28;
    }

    public Double getDeliveryQuantity29() {
        return deliveryQuantity29;
    }

    public void setDeliveryQuantity29(Double deliveryQuantity29) {
        this.deliveryQuantity29 = deliveryQuantity29;
    }

    public Double getDeliveryQuantity30() {
        return deliveryQuantity30;
    }

    public void setDeliveryQuantity30(Double deliveryQuantity30) {
        this.deliveryQuantity30 = deliveryQuantity30;
    }

    public Double getDeliveryQuantity31() {
        return deliveryQuantity31;
    }

    public void setDeliveryQuantity31(Double deliveryQuantity31) {
        this.deliveryQuantity31 = deliveryQuantity31;
    }

    public Double getActualReceiveQuantity01() {
        return actualReceiveQuantity01;
    }

    public void setActualReceiveQuantity01(Double actualReceiveQuantity01) {
        this.actualReceiveQuantity01 = actualReceiveQuantity01;
    }

    public Double getActualReceiveQuantity02() {
        return actualReceiveQuantity02;
    }

    public void setActualReceiveQuantity02(Double actualReceiveQuantity02) {
        this.actualReceiveQuantity02 = actualReceiveQuantity02;
    }

    public Double getActualReceiveQuantity03() {
        return actualReceiveQuantity03;
    }

    public void setActualReceiveQuantity03(Double actualReceiveQuantity03) {
        this.actualReceiveQuantity03 = actualReceiveQuantity03;
    }

    public Double getActualReceiveQuantity04() {
        return actualReceiveQuantity04;
    }

    public void setActualReceiveQuantity04(Double actualReceiveQuantity04) {
        this.actualReceiveQuantity04 = actualReceiveQuantity04;
    }

    public Double getActualReceiveQuantity05() {
        return actualReceiveQuantity05;
    }

    public void setActualReceiveQuantity05(Double actualReceiveQuantity05) {
        this.actualReceiveQuantity05 = actualReceiveQuantity05;
    }

    public Double getActualReceiveQuantity06() {
        return actualReceiveQuantity06;
    }

    public void setActualReceiveQuantity06(Double actualReceiveQuantity06) {
        this.actualReceiveQuantity06 = actualReceiveQuantity06;
    }

    public Double getActualReceiveQuantity07() {
        return actualReceiveQuantity07;
    }

    public void setActualReceiveQuantity07(Double actualReceiveQuantity07) {
        this.actualReceiveQuantity07 = actualReceiveQuantity07;
    }

    public Double getActualReceiveQuantity08() {
        return actualReceiveQuantity08;
    }

    public void setActualReceiveQuantity08(Double actualReceiveQuantity08) {
        this.actualReceiveQuantity08 = actualReceiveQuantity08;
    }

    public Double getActualReceiveQuantity09() {
        return actualReceiveQuantity09;
    }

    public void setActualReceiveQuantity09(Double actualReceiveQuantity09) {
        this.actualReceiveQuantity09 = actualReceiveQuantity09;
    }

    public Double getActualReceiveQuantity10() {
        return actualReceiveQuantity10;
    }

    public void setActualReceiveQuantity10(Double actualReceiveQuantity10) {
        this.actualReceiveQuantity10 = actualReceiveQuantity10;
    }

    public Double getActualReceiveQuantity11() {
        return actualReceiveQuantity11;
    }

    public void setActualReceiveQuantity11(Double actualReceiveQuantity11) {
        this.actualReceiveQuantity11 = actualReceiveQuantity11;
    }

    public Double getActualReceiveQuantity12() {
        return actualReceiveQuantity12;
    }

    public void setActualReceiveQuantity12(Double actualReceiveQuantity12) {
        this.actualReceiveQuantity12 = actualReceiveQuantity12;
    }

    public Double getActualReceiveQuantity13() {
        return actualReceiveQuantity13;
    }

    public void setActualReceiveQuantity13(Double actualReceiveQuantity13) {
        this.actualReceiveQuantity13 = actualReceiveQuantity13;
    }

    public Double getActualReceiveQuantity14() {
        return actualReceiveQuantity14;
    }

    public void setActualReceiveQuantity14(Double actualReceiveQuantity14) {
        this.actualReceiveQuantity14 = actualReceiveQuantity14;
    }

    public Double getActualReceiveQuantity15() {
        return actualReceiveQuantity15;
    }

    public void setActualReceiveQuantity15(Double actualReceiveQuantity15) {
        this.actualReceiveQuantity15 = actualReceiveQuantity15;
    }

    public Double getActualReceiveQuantity16() {
        return actualReceiveQuantity16;
    }

    public void setActualReceiveQuantity16(Double actualReceiveQuantity16) {
        this.actualReceiveQuantity16 = actualReceiveQuantity16;
    }

    public Double getActualReceiveQuantity17() {
        return actualReceiveQuantity17;
    }

    public void setActualReceiveQuantity17(Double actualReceiveQuantity17) {
        this.actualReceiveQuantity17 = actualReceiveQuantity17;
    }

    public Double getActualReceiveQuantity18() {
        return actualReceiveQuantity18;
    }

    public void setActualReceiveQuantity18(Double actualReceiveQuantity18) {
        this.actualReceiveQuantity18 = actualReceiveQuantity18;
    }

    public Double getActualReceiveQuantity19() {
        return actualReceiveQuantity19;
    }

    public void setActualReceiveQuantity19(Double actualReceiveQuantity19) {
        this.actualReceiveQuantity19 = actualReceiveQuantity19;
    }

    public Double getActualReceiveQuantity20() {
        return actualReceiveQuantity20;
    }

    public void setActualReceiveQuantity20(Double actualReceiveQuantity20) {
        this.actualReceiveQuantity20 = actualReceiveQuantity20;
    }

    public Double getActualReceiveQuantity21() {
        return actualReceiveQuantity21;
    }

    public void setActualReceiveQuantity21(Double actualReceiveQuantity21) {
        this.actualReceiveQuantity21 = actualReceiveQuantity21;
    }

    public Double getActualReceiveQuantity22() {
        return actualReceiveQuantity22;
    }

    public void setActualReceiveQuantity22(Double actualReceiveQuantity22) {
        this.actualReceiveQuantity22 = actualReceiveQuantity22;
    }

    public Double getActualReceiveQuantity23() {
        return actualReceiveQuantity23;
    }

    public void setActualReceiveQuantity23(Double actualReceiveQuantity23) {
        this.actualReceiveQuantity23 = actualReceiveQuantity23;
    }

    public Double getActualReceiveQuantity24() {
        return actualReceiveQuantity24;
    }

    public void setActualReceiveQuantity24(Double actualReceiveQuantity24) {
        this.actualReceiveQuantity24 = actualReceiveQuantity24;
    }

    public Double getActualReceiveQuantity25() {
        return actualReceiveQuantity25;
    }

    public void setActualReceiveQuantity25(Double actualReceiveQuantity25) {
        this.actualReceiveQuantity25 = actualReceiveQuantity25;
    }

    public Double getActualReceiveQuantity26() {
        return actualReceiveQuantity26;
    }

    public void setActualReceiveQuantity26(Double actualReceiveQuantity26) {
        this.actualReceiveQuantity26 = actualReceiveQuantity26;
    }

    public Double getActualReceiveQuantity27() {
        return actualReceiveQuantity27;
    }

    public void setActualReceiveQuantity27(Double actualReceiveQuantity27) {
        this.actualReceiveQuantity27 = actualReceiveQuantity27;
    }

    public Double getActualReceiveQuantity28() {
        return actualReceiveQuantity28;
    }

    public void setActualReceiveQuantity28(Double actualReceiveQuantity28) {
        this.actualReceiveQuantity28 = actualReceiveQuantity28;
    }

    public Double getActualReceiveQuantity29() {
        return actualReceiveQuantity29;
    }

    public void setActualReceiveQuantity29(Double actualReceiveQuantity29) {
        this.actualReceiveQuantity29 = actualReceiveQuantity29;
    }

    public Double getActualReceiveQuantity30() {
        return actualReceiveQuantity30;
    }

    public void setActualReceiveQuantity30(Double actualReceiveQuantity30) {
        this.actualReceiveQuantity30 = actualReceiveQuantity30;
    }

    public Double getActualReceiveQuantity31() {
        return actualReceiveQuantity31;
    }

    public void setActualReceiveQuantity31(Double actualReceiveQuantity31) {
        this.actualReceiveQuantity31 = actualReceiveQuantity31;
    }

    public Double getNotQualifiedQuantity01() {
        return notQualifiedQuantity01;
    }

    public void setNotQualifiedQuantity01(Double notQualifiedQuantity01) {
        this.notQualifiedQuantity01 = notQualifiedQuantity01;
    }

    public Double getNotQualifiedQuantity02() {
        return notQualifiedQuantity02;
    }

    public void setNotQualifiedQuantity02(Double notQualifiedQuantity02) {
        this.notQualifiedQuantity02 = notQualifiedQuantity02;
    }

    public Double getNotQualifiedQuantity03() {
        return notQualifiedQuantity03;
    }

    public void setNotQualifiedQuantity03(Double notQualifiedQuantity03) {
        this.notQualifiedQuantity03 = notQualifiedQuantity03;
    }

    public Double getNotQualifiedQuantity04() {
        return notQualifiedQuantity04;
    }

    public void setNotQualifiedQuantity04(Double notQualifiedQuantity04) {
        this.notQualifiedQuantity04 = notQualifiedQuantity04;
    }

    public Double getNotQualifiedQuantity05() {
        return notQualifiedQuantity05;
    }

    public void setNotQualifiedQuantity05(Double notQualifiedQuantity05) {
        this.notQualifiedQuantity05 = notQualifiedQuantity05;
    }

    public Double getNotQualifiedQuantity06() {
        return notQualifiedQuantity06;
    }

    public void setNotQualifiedQuantity06(Double notQualifiedQuantity06) {
        this.notQualifiedQuantity06 = notQualifiedQuantity06;
    }

    public Double getNotQualifiedQuantity07() {
        return notQualifiedQuantity07;
    }

    public void setNotQualifiedQuantity07(Double notQualifiedQuantity07) {
        this.notQualifiedQuantity07 = notQualifiedQuantity07;
    }

    public Double getNotQualifiedQuantity08() {
        return notQualifiedQuantity08;
    }

    public void setNotQualifiedQuantity08(Double notQualifiedQuantity08) {
        this.notQualifiedQuantity08 = notQualifiedQuantity08;
    }

    public Double getNotQualifiedQuantity09() {
        return notQualifiedQuantity09;
    }

    public void setNotQualifiedQuantity09(Double notQualifiedQuantity09) {
        this.notQualifiedQuantity09 = notQualifiedQuantity09;
    }

    public Double getNotQualifiedQuantity10() {
        return notQualifiedQuantity10;
    }

    public void setNotQualifiedQuantity10(Double notQualifiedQuantity10) {
        this.notQualifiedQuantity10 = notQualifiedQuantity10;
    }

    public Double getNotQualifiedQuantity11() {
        return notQualifiedQuantity11;
    }

    public void setNotQualifiedQuantity11(Double notQualifiedQuantity11) {
        this.notQualifiedQuantity11 = notQualifiedQuantity11;
    }

    public Double getNotQualifiedQuantity12() {
        return notQualifiedQuantity12;
    }

    public void setNotQualifiedQuantity12(Double notQualifiedQuantity12) {
        this.notQualifiedQuantity12 = notQualifiedQuantity12;
    }

    public Double getNotQualifiedQuantity13() {
        return notQualifiedQuantity13;
    }

    public void setNotQualifiedQuantity13(Double notQualifiedQuantity13) {
        this.notQualifiedQuantity13 = notQualifiedQuantity13;
    }

    public Double getNotQualifiedQuantity14() {
        return notQualifiedQuantity14;
    }

    public void setNotQualifiedQuantity14(Double notQualifiedQuantity14) {
        this.notQualifiedQuantity14 = notQualifiedQuantity14;
    }

    public Double getNotQualifiedQuantity15() {
        return notQualifiedQuantity15;
    }

    public void setNotQualifiedQuantity15(Double notQualifiedQuantity15) {
        this.notQualifiedQuantity15 = notQualifiedQuantity15;
    }

    public Double getNotQualifiedQuantity16() {
        return notQualifiedQuantity16;
    }

    public void setNotQualifiedQuantity16(Double notQualifiedQuantity16) {
        this.notQualifiedQuantity16 = notQualifiedQuantity16;
    }

    public Double getNotQualifiedQuantity17() {
        return notQualifiedQuantity17;
    }

    public void setNotQualifiedQuantity17(Double notQualifiedQuantity17) {
        this.notQualifiedQuantity17 = notQualifiedQuantity17;
    }

    public Double getNotQualifiedQuantity18() {
        return notQualifiedQuantity18;
    }

    public void setNotQualifiedQuantity18(Double notQualifiedQuantity18) {
        this.notQualifiedQuantity18 = notQualifiedQuantity18;
    }

    public Double getNotQualifiedQuantity19() {
        return notQualifiedQuantity19;
    }

    public void setNotQualifiedQuantity19(Double notQualifiedQuantity19) {
        this.notQualifiedQuantity19 = notQualifiedQuantity19;
    }

    public Double getNotQualifiedQuantity20() {
        return notQualifiedQuantity20;
    }

    public void setNotQualifiedQuantity20(Double notQualifiedQuantity20) {
        this.notQualifiedQuantity20 = notQualifiedQuantity20;
    }

    public Double getNotQualifiedQuantity21() {
        return notQualifiedQuantity21;
    }

    public void setNotQualifiedQuantity21(Double notQualifiedQuantity21) {
        this.notQualifiedQuantity21 = notQualifiedQuantity21;
    }

    public Double getNotQualifiedQuantity22() {
        return notQualifiedQuantity22;
    }

    public void setNotQualifiedQuantity22(Double notQualifiedQuantity22) {
        this.notQualifiedQuantity22 = notQualifiedQuantity22;
    }

    public Double getNotQualifiedQuantity23() {
        return notQualifiedQuantity23;
    }

    public void setNotQualifiedQuantity23(Double notQualifiedQuantity23) {
        this.notQualifiedQuantity23 = notQualifiedQuantity23;
    }

    public Double getNotQualifiedQuantity24() {
        return notQualifiedQuantity24;
    }

    public void setNotQualifiedQuantity24(Double notQualifiedQuantity24) {
        this.notQualifiedQuantity24 = notQualifiedQuantity24;
    }

    public Double getNotQualifiedQuantity25() {
        return notQualifiedQuantity25;
    }

    public void setNotQualifiedQuantity25(Double notQualifiedQuantity25) {
        this.notQualifiedQuantity25 = notQualifiedQuantity25;
    }

    public Double getNotQualifiedQuantity26() {
        return notQualifiedQuantity26;
    }

    public void setNotQualifiedQuantity26(Double notQualifiedQuantity26) {
        this.notQualifiedQuantity26 = notQualifiedQuantity26;
    }

    public Double getNotQualifiedQuantity27() {
        return notQualifiedQuantity27;
    }

    public void setNotQualifiedQuantity27(Double notQualifiedQuantity27) {
        this.notQualifiedQuantity27 = notQualifiedQuantity27;
    }

    public Double getNotQualifiedQuantity28() {
        return notQualifiedQuantity28;
    }

    public void setNotQualifiedQuantity28(Double notQualifiedQuantity28) {
        this.notQualifiedQuantity28 = notQualifiedQuantity28;
    }

    public Double getNotQualifiedQuantity29() {
        return notQualifiedQuantity29;
    }

    public void setNotQualifiedQuantity29(Double notQualifiedQuantity29) {
        this.notQualifiedQuantity29 = notQualifiedQuantity29;
    }

    public Double getNotQualifiedQuantity30() {
        return notQualifiedQuantity30;
    }

    public void setNotQualifiedQuantity30(Double notQualifiedQuantity30) {
        this.notQualifiedQuantity30 = notQualifiedQuantity30;
    }

    public Double getNotQualifiedQuantity31() {
        return notQualifiedQuantity31;
    }

    public void setNotQualifiedQuantity31(Double notQualifiedQuantity31) {
        this.notQualifiedQuantity31 = notQualifiedQuantity31;
    }

    public Double getTotalActualReceiveQuantity() {
        return totalActualReceiveQuantity;
    }

    public void setTotalActualReceiveQuantity(Double totalActualReceiveQuantity) {
        this.totalActualReceiveQuantity = totalActualReceiveQuantity;
    }

    public Double getTotalNotQualifiedQuantity() {
        return totalNotQualifiedQuantity;
    }

    public void setTotalNotQualifiedQuantity(Double totalNotQualifiedQuantity) {
        this.totalNotQualifiedQuantity = totalNotQualifiedQuantity;
    }

    public Double getTotalDeliveryQuantity() {
        return totalDeliveryQuantity;
    }

    public void setTotalDeliveryQuantity(Double totalDeliveryQuantity) {
        this.totalDeliveryQuantity = totalDeliveryQuantity;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Long getStampingMaterialConsumptionQuotaId() {
        return stampingMaterialConsumptionQuotaId;
    }

    public void setStampingMaterialConsumptionQuotaId(Long stampingMaterialConsumptionQuotaId) {
        this.stampingMaterialConsumptionQuotaId = stampingMaterialConsumptionQuotaId;
    }

    public String getStampingMaterialName() {
        return stampingMaterialName;
    }

    public void setStampingMaterialName(String stampingMaterialName) {
        this.stampingMaterialName = stampingMaterialName;
    }

    public String getStampingElementNo() {
        return stampingElementNo;
    }

    public void setStampingElementNo(String stampingElementNo) {
        this.stampingElementNo = stampingElementNo;
    }

    public String getInventoryCoding() {
        return inventoryCoding;
    }

    public void setInventoryCoding(String inventoryCoding) {
        this.inventoryCoding = inventoryCoding;
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

    public String getOriginalInventoryCode() {
        return originalInventoryCode;
    }

    public void setOriginalInventoryCode(String originalInventoryCode) {
        this.originalInventoryCode = originalInventoryCode;
    }

    public String getErpVoucherNo1() {
        return erpVoucherNo1;
    }

    public void setErpVoucherNo1(String erpVoucherNo1) {
        this.erpVoucherNo1 = erpVoucherNo1;
    }

    public String getErpVoucherNo2() {
        return erpVoucherNo2;
    }

    public void setErpVoucherNo2(String erpVoucherNo2) {
        this.erpVoucherNo2 = erpVoucherNo2;
    }

    public String getErpVoucherNo3() {
        return erpVoucherNo3;
    }

    public void setErpVoucherNo3(String erpVoucherNo3) {
        this.erpVoucherNo3 = erpVoucherNo3;
    }

    public Double getSumRelDeliveryAmount() {
        return sumRelDeliveryAmount;
    }

    public void setSumRelDeliveryAmount(Double sumRelDeliveryAmount) {
        this.sumRelDeliveryAmount = sumRelDeliveryAmount;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Long getReceiveOrderId() {
        return receiveOrderId;
    }

    public void setReceiveOrderId(Long receiveOrderId) {
        this.receiveOrderId = receiveOrderId;
    }
}
