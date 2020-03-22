package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@TableName("mes_material_call")
@KeySequence("mes_material_call")
public class MaterialCallEO extends AbstractAuditableEntity<MaterialCallEO> {
    private static final long serialVersionUID = -4921986696844874626L;

    @TableId(type = IdType.INPUT)
    private Long callMaterialId;/** 叫料工单ID */

    private Long orgId;/** 归属机构ID */

    private Long scheduleOrderId;/** 生产工单ID(排产表中总成工单的ID) */

    private Date produceDate;/** 生产日期(生产工单的生产日期) */

    private Long materialId;/** 物料ID */

    private String elementNo;/** 零件号 */

    private String materialName;/** 物料名称 */

    private Integer snp;/** 最小包装数(物料档案中的SNP) */

    private Date callMaterialTime;/** 叫料时间 */

    private Double callMaterialQuantity;/** 叫料数量 */

    private Double hasDeliveryQuantity;/** 已出库数量 */

    private Integer status;/** 状态;0-新建; 1-出库中;2-出库完成; 3-已关闭;4-已取消 */


    @Override
    public Serializable getId() {
        return null;
    }

    public Long getCallMaterialId() {
        return callMaterialId;
    }

    public void setCallMaterialId(Long callMaterialId) {
        this.callMaterialId = callMaterialId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getScheduleOrderId() {
        return scheduleOrderId;
    }

    public void setScheduleOrderId(Long scheduleOrderId) {
        this.scheduleOrderId = scheduleOrderId;
    }

    public Date getProduceDate() {
        return produceDate;
    }

    public void setProduceDate(Date produceDate) {
        this.produceDate = produceDate;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getSnp() {
        return snp;
    }

    public void setSnp(Integer snp) {
        this.snp = snp;
    }

    public Date getCallMaterialTime() {
        return callMaterialTime;
    }

    public void setCallMaterialTime(Date callMaterialTime) {
        this.callMaterialTime = callMaterialTime;
    }

    public Double getCallMaterialQuantity() {
        return callMaterialQuantity;
    }

    public void setCallMaterialQuantity(Double callMaterialQuantity) {
        this.callMaterialQuantity = callMaterialQuantity;
    }

    public Double getHasDeliveryQuantity() {
        return hasDeliveryQuantity;
    }

    public void setHasDeliveryQuantity(Double hasDeliveryQuantity) {
        this.hasDeliveryQuantity = hasDeliveryQuantity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
