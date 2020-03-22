package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author yuanchang
 * @date 2019/10/18
 */
@TableName("srm_check_bill_detail")
@KeySequence("srm_check_bill_detail")
public class CheckBillDetailEO extends AbstractAuditableEntity<CheckBillDetailEO> {
    private static final long serialVersionUID = 1040834336026877438L;

    @TableId(type = IdType.INPUT)
    private Long checkDetailId;/** 主键 */

    private Long checkId;/** 对账单ID */

    private String deliveryNoteNo;/** 采购单号 */

    private Long receiveDetailId;/** 入库单明细ID */

    private String receiveVoucherNo;/** 入库单号 */

    private Date receiveDate;/** 入库日期 */

    private Long deliveryDetailId;/** 出库单明细ID */

    private String deliveryVoucherNo;/** 出库单号 */

    private Date deliveryDate;/** 出库日期 */

    private String materialName;/** 物料名称 */

    private String elementNo;/** 零件号 */

    private String specification;/** 规格型号 */

    private String unitId;/** 计量单位 */

    private Double quantity;/** 数量 */

    private Long orgId;/** 所属机构 */

    @TableField(exist = false)
    private String unitName;/** 计量单位名称 */

    @TableField(exist = false)
    private Double totalQuantity;/** 总数量 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @TableField(exist = false)
    private String fullName;/** 归属机构全称 */

    @TableField(exist = false)
    private Date beginDate;/** 开始日期 */

    @TableField(exist = false)
    private String projectNo;/** 项目号 */

    @TableField(exist = false)
    private Double receiveQuantity;/** 收货数量 */

    @TableField(exist = false)
    private Double returnQuantity;/** 退货数量 */

    @TableField(exist = false)
    private Double sumReceiveQuantity;/** 总收货数量 */

    @TableField(exist = false)
    private Double sumReturnQuantity;/** 总退货数量 */

    private Long materialId;/** 物料ID */

    @TableField(exist = false)
    private Map receiveMap;

    @TableField(exist = false)
    private Map returnMap;

    private Integer resourceType;/** 数据来源，1-收货单明细，2-退货单明细*/


    @Override
    public Serializable getId() {
        return checkDetailId;
    }

    public Long getCheckDetailId() {
        return checkDetailId;
    }

    public void setCheckDetailId(Long checkDetailId) {
        this.checkDetailId = checkDetailId;
    }

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public String getDeliveryNoteNo() {
        return deliveryNoteNo;
    }

    public void setDeliveryNoteNo(String deliveryNoteNo) {
        this.deliveryNoteNo = deliveryNoteNo;
    }

    public Long getReceiveDetailId() {
        return receiveDetailId;
    }

    public void setReceiveDetailId(Long receiveDetailId) {
        this.receiveDetailId = receiveDetailId;
    }

    public Long getDeliveryDetailId() {
        return deliveryDetailId;
    }

    public void setDeliveryDetailId(Long deliveryDetailId) {
        this.deliveryDetailId = deliveryDetailId;
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

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getReceiveVoucherNo() {
        return receiveVoucherNo;
    }

    public void setReceiveVoucherNo(String receiveVoucherNo) {
        this.receiveVoucherNo = receiveVoucherNo;
    }

    public String getDeliveryVoucherNo() {
        return deliveryVoucherNo;
    }

    public void setDeliveryVoucherNo(String deliveryVoucherNo) {
        this.deliveryVoucherNo = deliveryVoucherNo;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public Double getReceiveQuantity() {
        return receiveQuantity;
    }

    public void setReceiveQuantity(Double receiveQuantity) {
        this.receiveQuantity = receiveQuantity;
    }

    public Double getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(Double returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Map getReceiveMap() {
        return receiveMap;
    }

    public void setReceiveMap(Map receiveMap) {
        this.receiveMap = receiveMap;
    }

    public Map getReturnMap() {
        return returnMap;
    }

    public void setReturnMap(Map returnMap) {
        this.returnMap = returnMap;
    }

    public Double getSumReceiveQuantity() {
        return sumReceiveQuantity;
    }

    public void setSumReceiveQuantity(Double sumReceiveQuantity) {
        this.sumReceiveQuantity = sumReceiveQuantity;
    }

    public Double getSumReturnQuantity() {
        return sumReturnQuantity;
    }

    public void setSumReturnQuantity(Double sumReturnQuantity) {
        this.sumReturnQuantity = sumReturnQuantity;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }
}
