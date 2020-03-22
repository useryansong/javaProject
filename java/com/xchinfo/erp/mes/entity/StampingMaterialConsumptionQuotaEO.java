package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@TableName("mes_stamping_material_consumption_quota")
@KeySequence("mes_stamping_material_consumption_quota")
public class StampingMaterialConsumptionQuotaEO extends AbstractAuditableEntity<StampingMaterialConsumptionQuotaEO> {
    private static final long serialVersionUID = -2572410235535688266L;
    @TableId(type = IdType.INPUT)
    private Long stampingMaterialConsumptionQuotaId;/** 冲压材料定额id */
    @NotNull(message = "项目不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "项目长度不能超过 200 个字符")
    private String project;/** 项目 */

    private String materialCode;/** 物料编码 */

    private String elementNo;/** 零件号 */

    private String materialName;/** 物料名称 */

    @NotNull(message = "材料牌号不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "材料牌号长度不能超过 200 个字符")
    private String materialPcode;/** 材料牌号 */

    @NotNull(message = "材料规格不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "材料规格长度不能超过 200 个字符")
    private String materialSpecific;/** 材料规格 */

    private Double thickness;/** 厚度 */

    private Long width;/** 料宽 */

    @NotNull(message = "公差不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "公差长度不能超过 200 个字符")
    private String tolerance;/** 公差 */

    private Long coilMaterial;/** 卷料 */

    private Long sheetMetal;/** 板料 */

    private Double stepDistance;/** 步距 */

    private Long numberOfPunchablePartsPerBoard;/** 每条板可冲零件数量 */

    private Double netWeight;/** 单个零件净重（kg） */

    private Double grossWeight;/** 单个零件毛重（kg） */

    private Long orgId;/** 归属机构 */

    private String inventoryCoding;/** 存货编码 */

    private Long originalMaterialId;/** 耗用原材料物料ID */

    private Long materialId; /** 冲压件物料ID */


    @TableField(exist = false)
    private String orgName;/** 所属机构 */

    private Long status;/** 状态;0：启用，1：停用 */

    @TableField(exist = false)
    private String inventoryCode;/** 冲压件存货编码 */

    @TableField(exist = false)
    private Double backWeight;/** 上个月的线边盘点库存原材料重量（kg） */

    @TableField(exist = false)
    private Double receiveAmount;/** 本月冲压件入库量 */

    @TableField(exist = false)
    private Double bomWeight;/** BOM消耗量 */

    @TableField(exist = false)
    private Double orginalWeight;/** 本月原材料领料数量 */

    @TableField(exist = false)
    private Double linedgeAmount;/** 线边账目库存数 */

    @TableField(exist = false)
    private Double nowWeight;/** 本月线边盘点库存原材料重量 */

    @TableField(exist = false)
    private Double inandputPercent;/** 投入产出率 */

    @TableField(exist = false)
    private Double inandputDiff;/** 投入产出差异 */

    @TableField(exist = false)
    private Double wasteWeight;/** 报废重量 */

    @TableField(exist = false)
    private String dictName;/** 焊装存货分类名称 */

    @TableField(exist = false)
    private Double fAmount;/** ERP月末账面数量 */

    @TableField(exist = false)
    private Double gAmount;/** 上月结存数量 */

    @TableField(exist = false)
    private Double hAmount;/** 本月购入数量 */

    @TableField(exist = false)
    private Double iAmount;/** 月末盘点数量 */

    @TableField(exist = false)
    private Double jAmount;/** 白件电泳小组件 */

    @TableField(exist = false)
    private Double kAmount;/** 在制品分解数量 */

    @TableField(exist = false)
    private Double lAmount;/** 成品分解数量 */

    @TableField(exist = false)
    private Double mAmount;/** 原材料数量 */

    @TableField(exist = false)
    private Double nAmount;/** 生产投入实际出库 */

    @TableField(exist = false)
    private Double oAmount;/** 销售出库 */

    @TableField(exist = false)
    private Double pAmount;/** 销售出库不良品领料返修 */

    @TableField(exist = false)
    private Double qAmount;/** ERP材料出库数量 */

    @TableField(exist = false)
    private Double rAmount;/** 标准用量 */

    @TableField(exist = false)
    private Double sAmount;/** 破检数量 */

    @TableField(exist = false)
    private Double tAmount;/** 来料不良 */

    @TableField(exist = false)
    private Double uAmount;/** 装件不到位 */

    @TableField(exist = false)
    private Double vAmount;/** 调试报废 */

    @TableField(exist = false)
    private Double wAamount;/** 生产报废合计 */

    @TableField(exist = false)
    private Double xAmount;/** 差异总数量 */

    @TableField(exist = false)
    private Double yAmount;/** BOM问题 */

    @TableField(exist = false)
    private Double zAmount;/** 盘点有误 */

    @TableField(exist = false)
    private Double aaAmount;/** 来料问题 */

    @TableField(exist = false)
    private Double abAmount;/** 损失 */

    @TableField(exist = false)
    private Double acAmount;/** 备注 */

    @TableField(exist = false)
    private Double adAmount;/** [实际出库(含生产报废)-标准用量]/标准用量 */

    @TableField(exist = false)
    private Double aeAmount;/** [ERP材料出库数量(含生产报废)-标准用量]/标准用量 */

    @TableField(exist = false)
    private Double afAmount;/** 本月调账 */

    @TableField(exist = false)
    private Double agAmount;/** 结转成本 */

    @TableField(exist = false)
    private Double ahAmount;/** 调账后月末结存 */

    @Override
    public Serializable getId() {
        return this.stampingMaterialConsumptionQuotaId;
    }

    public Long getStampingMaterialConsumptionQuotaId() {
        return stampingMaterialConsumptionQuotaId;
    }

    public void setStampingMaterialConsumptionQuotaId(Long stampingMaterialConsumptionQuotaId) {
        this.stampingMaterialConsumptionQuotaId = stampingMaterialConsumptionQuotaId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
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

    public String getMaterialPcode() {
        return materialPcode;
    }

    public void setMaterialPcode(String materialPcode) {
        this.materialPcode = materialPcode;
    }

    public String getMaterialSpecific() {
        return materialSpecific;
    }

    public void setMaterialSpecific(String materialSpecific) {
        this.materialSpecific = materialSpecific;
    }

    public Double getThickness() {
        return thickness;
    }

    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public String getTolerance() {
        return tolerance;
    }

    public void setTolerance(String tolerance) {
        this.tolerance = tolerance;
    }

    public Long getCoilMaterial() {
        return coilMaterial;
    }

    public void setCoilMaterial(Long coilMaterial) {
        this.coilMaterial = coilMaterial;
    }

    public Long getSheetMetal() {
        return sheetMetal;
    }

    public void setSheetMetal(Long sheetMetal) {
        this.sheetMetal = sheetMetal;
    }

    public Double getStepDistance() {
        return stepDistance;
    }

    public void setStepDistance(Double stepDistance) {
        this.stepDistance = stepDistance;
    }

    public Long getNumberOfPunchablePartsPerBoard() {
        return numberOfPunchablePartsPerBoard;
    }

    public void setNumberOfPunchablePartsPerBoard(Long numberOfPunchablePartsPerBoard) {
        this.numberOfPunchablePartsPerBoard = numberOfPunchablePartsPerBoard;
    }

    public Double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Double netWeight) {
        this.netWeight = netWeight;
    }

    public Double getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Double grossWeight) {
        this.grossWeight = grossWeight;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getInventoryCoding() {
        return inventoryCoding;
    }

    public void setInventoryCoding(String inventoryCoding) {
        this.inventoryCoding = inventoryCoding;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public Double getBackWeight() {
        return backWeight;
    }

    public void setBackWeight(Double backWeight) {
        this.backWeight = backWeight;
    }

    public Double getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(Double receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public Double getBomWeight() {
        return bomWeight;
    }

    public void setBomWeight(Double bomWeight) {
        this.bomWeight = bomWeight;
    }

    public Double getOrginalWeight() {
        return orginalWeight;
    }

    public void setOrginalWeight(Double orginalWeight) {
        this.orginalWeight = orginalWeight;
    }

    public Double getLinedgeAmount() {
        return linedgeAmount;
    }

    public void setLinedgeAmount(Double linedgeAmount) {
        this.linedgeAmount = linedgeAmount;
    }

    public Double getNowWeight() {
        return nowWeight;
    }

    public void setNowWeight(Double nowWeight) {
        this.nowWeight = nowWeight;
    }

    public Double getInandputPercent() {
        return inandputPercent;
    }

    public void setInandputPercent(Double inandputPercent) {
        this.inandputPercent = inandputPercent;
    }

    public Double getInandputDiff() {
        return inandputDiff;
    }

    public void setInandputDiff(Double inandputDiff) {
        this.inandputDiff = inandputDiff;
    }

    public Double getWasteWeight() {
        return wasteWeight;
    }

    public void setWasteWeight(Double wasteWeight) {
        this.wasteWeight = wasteWeight;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public Double getFAmount() {
        return fAmount;
    }

    public void setFAmount(Double fAmount) {
        this.fAmount = fAmount;
    }

    public Double getGAmount() {
        return gAmount;
    }

    public void setGAmount(Double gAmount) {
        this.gAmount = gAmount;
    }

    public Double getHAmount() {
        return hAmount;
    }

    public void setHAmount(Double hAmount) {
        this.hAmount = hAmount;
    }

    public Double getIAmount() {
        return iAmount;
    }

    public void setIAmount(Double iAmount) {
        this.iAmount = iAmount;
    }

    public Double getJAmount() {
        return jAmount;
    }

    public void setJAmount(Double jAmount) {
        this.jAmount = jAmount;
    }

    public Double getKAmount() {
        return kAmount;
    }

    public void setKAmount(Double kAmount) {
        this.kAmount = kAmount;
    }

    public Double getLAmount() {
        return lAmount;
    }

    public void setLAmount(Double lAmount) {
        this.lAmount = lAmount;
    }

    public Double getMAmount() {
        return mAmount;
    }

    public void setMAmount(Double mAmount) {
        this.mAmount = mAmount;
    }

    public Double getNAmount() {
        return nAmount;
    }

    public void setNAmount(Double nAmount) {
        this.nAmount = nAmount;
    }

    public Double getOAmount() {
        return oAmount;
    }

    public void setOAmount(Double oAmount) {
        this.oAmount = oAmount;
    }

    public Double getPAmount() {
        return pAmount;
    }

    public void setPAmount(Double pAmount) {
        this.pAmount = pAmount;
    }

    public Double getQAmount() {
        return qAmount;
    }

    public void setQAmount(Double qAmount) {
        this.qAmount = qAmount;
    }

    public Double getRAmount() {
        return rAmount;
    }

    public void setRAmount(Double rAmount) {
        this.rAmount = rAmount;
    }

    public Double getSAmount() {
        return sAmount;
    }

    public void setSAmount(Double sAmount) {
        this.sAmount = sAmount;
    }

    public Double getTAmount() {
        return tAmount;
    }

    public void setTAmount(Double tAmount) {
        this.tAmount = tAmount;
    }

    public Double getUAmount() {
        return uAmount;
    }

    public void setUAmount(Double uAmount) {
        this.uAmount = uAmount;
    }

    public Double getVAmount() {
        return vAmount;
    }

    public void setVAmount(Double vAmount) {
        this.vAmount = vAmount;
    }

    public Double getWAamount() {
        return wAamount;
    }

    public void setWAamount(Double wAamount) {
        this.wAamount = wAamount;
    }

    public Double getXAmount() {
        return xAmount;
    }

    public void setXAmount(Double xAmount) {
        this.xAmount = xAmount;
    }

    public Double getYAmount() {
        return yAmount;
    }

    public void setYAmount(Double yAmount) {
        this.yAmount = yAmount;
    }

    public Double getZAmount() {
        return zAmount;
    }

    public void setZAmount(Double zAmount) {
        this.zAmount = zAmount;
    }

    public Double getAaAmount() {
        return aaAmount;
    }

    public void setAaAmount(Double aaAmount) {
        this.aaAmount = aaAmount;
    }

    public Double getAbAmount() {
        return abAmount;
    }

    public void setAbAmount(Double abAmount) {
        this.abAmount = abAmount;
    }

    public Double getAcAmount() {
        return acAmount;
    }

    public void setAcAmount(Double acAmount) {
        this.acAmount = acAmount;
    }

    public Double getAdAmount() {
        return adAmount;
    }

    public void setAdAmount(Double adAmount) {
        this.adAmount = adAmount;
    }

    public Double getAeAmount() {
        return aeAmount;
    }

    public void setAeAmount(Double aeAmount) {
        this.aeAmount = aeAmount;
    }

    public Double getAfAmount() {
        return afAmount;
    }

    public void setAfAmount(Double afAmount) {
        this.afAmount = afAmount;
    }

    public Double getAgAmount() {
        return agAmount;
    }

    public void setAgAmount(Double agAmount) {
        this.agAmount = agAmount;
    }

    public Double getAhAmount() {
        return ahAmount;
    }

    public void setAhAmount(Double ahAmount) {
        this.ahAmount = ahAmount;
    }

    public Long getOriginalMaterialId() {
        return originalMaterialId;
    }

    public void setOriginalMaterialId(Long originalMaterialId) {
        this.originalMaterialId = originalMaterialId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }
}
