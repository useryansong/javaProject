package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@TableName("wms_receive_order_detail")
@KeySequence("wms_receive_order_detail")
public class ReceiveOrderDetailEO extends AbstractAuditableEntity<ReceiveOrderDetailEO> {
    private static final long serialVersionUID = -1674209743079864760L;

    @TableId(type = IdType.INPUT)
    private Long receiveDetailId;/** 主键 */

    private Long receiveOrderId;/** 入库单ID */

    private Long orderId;/** 订单ID */

    private Long orderDetailId;/** 订单明细ID */

    @NotNull(message = "物料不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("物料")
    private Long materialId;/** 物料 */

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

    private String specification;/** 规格型号 */

    private Long unitId;/** 计量单位 */

    private String figureNumber;/** 图号 */

    private String figureVersion;/** 版本号 */

    private Long warehouseId;/** 仓库 */

    @TableField(exist = false)
    private String warehouseCode;/** 仓库编码 */

    private Long warehouseLocationId;/** 库位 */

    private Double receiveAmount;/** 收货数量 */

    private Double relReceiveAmount;/**实收数*/

    private Integer status;/** 状态;0-新建;1-进行中;2-已完成 */

    private Integer checkStatus;/** 供应商对账状态;0-未对账;1-已对账; */

    private Long checkId;/** 供应商对账ID; */

    @TableField(exist = false)
    private String unitName;/**计量单位名称*/

    @TableField(exist = false)
    private String warehouseName;/**仓库名称*/

    @TableField(exist = false)
    private  String locationName;/**库位名称*/

    @TableField(exist = false)
    private String receiveUserName;/**入库单收货人用户名*/

    @TableField(exist = false)
    private String voucherNo;/**入库单流水号*/

    @TableField(exist = false)
    private String voucherNoSub;/**入库单流水号(去掉流水号前面两位字母)*/

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date receiveDate;/**入库单收货日期*/

    private String remarks;/** 备注 */

    @TableField(exist = false)
    private String erpCode;

    @TableField(exist = false)
    private String createBillName;/** 制单人(导出时使用,取当前登录人真实名) */

    @TableField(exist = false)
    private Double sumRelReceiveAmount;/** 总实收数(按月) */

    private Integer syncStatus; /** 生产领料(生产日报)U8同步状态(0-未同步，1-已提交(等待同步)，2-同步成功，3-部分成功，4-全部失败，5-已禁止) */

    private String syncResult;/** 生产领料(生产日报)U8同步结果 */

    @TableField(exist = false)
    private Date finishDate;/** 排产完成日期 */

    @Override
    public Serializable getId() {
        return receiveDetailId;
    }

    /** 主键 */
    public Long getReceiveDetailId(){
        return this.receiveDetailId;
    }
    /** 主键 */
    public void setReceiveDetailId(Long receiveDetailId){
        this.receiveDetailId = receiveDetailId;
    }
    /** 入库单ID */
    public Long getReceiveOrderId(){
        return this.receiveOrderId;
    }
    /** 入库单ID */
    public void setReceiveOrderId(Long receiveOrderId){
        this.receiveOrderId = receiveOrderId;
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
    /** 库位 */
    public Long getWarehouseLocationId(){
        return this.warehouseLocationId;
    }
    /** 库位 */
    public void setWarehouseLocationId(Long warehouseLocationId){
        this.warehouseLocationId = warehouseLocationId;
    }
    /** 收货数量 */
    public Double getReceiveAmount(){
        return this.receiveAmount;
    }
    /** 收货数量 */
    public void setReceiveAmount(Double receiveAmount){
        this.receiveAmount = receiveAmount;
    }
    /** 状态;0-新建;1-进行中;2-已完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-进行中;2-已完成 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getRelReceiveAmount() {
        return relReceiveAmount;
    }

    public void setRelReceiveAmount(Double relReceiveAmount) {
        this.relReceiveAmount = relReceiveAmount;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
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

    public String getVoucherNoSub() {
        return voucherNoSub;
    }

    public void setVoucherNoSub(String voucherNoSub) {
        this.voucherNoSub = voucherNoSub;
    }

    public Double getSumRelReceiveAmount() {
        return sumRelReceiveAmount;
    }

    public void setSumRelReceiveAmount(Double sumRelReceiveAmount) {
        this.sumRelReceiveAmount = sumRelReceiveAmount;
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

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(String syncResult) {
        this.syncResult = syncResult;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}
