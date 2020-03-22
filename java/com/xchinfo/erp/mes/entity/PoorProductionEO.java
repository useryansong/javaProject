package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 生产不良
 */
@TableName("mes_poor_production")
@KeySequence("mes_poor_production")
public class PoorProductionEO extends AbstractAuditableEntity<PoorProductionEO> {
    private static final long serialVersionUID = -2572410235818538266L;

    @TableId(type = IdType.INPUT)
    private Long poorProductionId;/** 生产不良id */

    private Integer poorType;/** 不良类型 */

    @TableField(exist = false)
    private String poorTypeName;/** 不良类型 */

    private String elementNo;/** 零件号 */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private Long materialId;/** 物料ID */

    private String poorNote;/** 不良说明 */

    private Integer poorHandle;/** 不良处理 */

    @TableField(exist = false)
    private String poorHandleName;/** 不良处理 */

    private Long accountId;/** 物料台账ID */

    private Integer number;/** 数量 */

    private String remark;/** 备注 */

    private Date productionDate;/** 生产日期 */

    private Integer status;/** 状态;0：启用，1：停用 */

    private String voucherNo;//单据号

    private Long orgId;/** 归属机构 */

    private String erpVoucherNo1;/** 材料出库单流水号 */


    @TableField(exist = false)
    private String orgName;/** 所属机构 */

    @TableField(exist = false)
    private Long mainWarehouseId;

    @TableField(exist = false)
    private String inventoryCode;

    @TableField(exist = false)
    private String specification;


    @Override
    public Serializable getId() {
        return this.poorProductionId;
    }

    public Long getPoorProductionId() {
        return poorProductionId;
    }

    public void setPoorProductionId(Long poorProductionId) {
        this.poorProductionId = poorProductionId;
    }

    public Integer getPoorType() {
        return poorType;
    }

    public void setPoorType(Integer poorType) {
        this.poorType = poorType;
    }

    public String getPoorTypeName() {
        return poorTypeName;
    }

    public void setPoorTypeName(String poorTypeName) {
        this.poorTypeName = poorTypeName;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
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

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getPoorNote() {
        return poorNote;
    }

    public void setPoorNote(String poorNote) {
        this.poorNote = poorNote;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public Integer getPoorHandle() {
        return poorHandle;
    }

    public void setPoorHandle(Integer poorHandle) {
        this.poorHandle = poorHandle;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getMainWarehouseId() {
        return mainWarehouseId;
    }

    public void setMainWarehouseId(Long mainWarehouseId) {
        this.mainWarehouseId = mainWarehouseId;
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

    public String getErpVoucherNo1() {
        return erpVoucherNo1;
    }

    public void setErpVoucherNo1(String erpVoucherNo1) {
        this.erpVoucherNo1 = erpVoucherNo1;
    }

    public String getPoorHandleName() {
        return poorHandleName;
    }

    public void setPoorHandleName(String poorHandleName) {
        this.poorHandleName = poorHandleName;
    }
}
