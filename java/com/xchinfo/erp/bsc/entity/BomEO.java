package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.AuditableTreeNode;
import javax.validation.constraints.NotNull;

/**
 * @author zhongy
 * @date 2019/4/10
 * @update
 */
@TableName("bsc_bom")
@KeySequence("bsc_bom")
public class BomEO extends AuditableTreeNode<BomEO> {
    private static final long serialVersionUID = -1832476291609437376L;

    @TableId(type = IdType.INPUT)
    private Long bomId;/** 主键 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @NotNull(message = "物料不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("物料")
    private Long materialId;/** 物料ID */

    @NotNull(message = "上级物料不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("上级物料")
    private Long parentBomId;/** 上级物料 */

    @BusinessLogField("数量")
    private Double amount;/** 数量 */

    private Double badRate;/** 不良比率 */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String inventoryCode;/** 存货编码 */

    private String materialName;/** 物料名称 */

    @BusinessLogField("图纸版本")
    private String figureVersion;/** 图纸版本 */

    @BusinessLogField("图纸材料")
    private String materialModel;/** 图纸材料 */

    @BusinessLogField("替代材料")
    private String substituteMaterialModel;/** 替代材料 */

    @BusinessLogField("图号")
    private String figureNumber;/** 图号 */

    @BusinessLogField("重量")
    private Double weight;/** 重量 */

    @BusinessLogField("单位")
    private Long unitId;/** 单位 */

    @BusinessLogField("单位名称")
    @TableField(exist = false)
    private String unitName;/** 单位名称 */

    @BusinessLogField("工序号")
    private String processId;/** 工序号 */

    @BusinessLogField("EBOM零件号")
    private String ebomElementNo;/** EBOM零件号 */

    @BusinessLogField("是否制造")
    private Integer isProduct;/** 是否制造 */

    @BusinessLogField("是否采购")
    private Integer isPurchase;/** 是否采购 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-禁用;1-启用 */

    private Integer releaseStatus;/** 发布状态;1-已发布;0-未发布 */

    @BusinessLogField("备注")
    private String memo;/** 备注 */

    private String elementNo;/** 零件号 */

    private String projectNo;/** 项目号 */

    @TableField(exist = false)
    private String specification;/** 规格型号 */

    @TableField(exist = false)
    private String text;/** 用于下拉框显示用 */

    @TableField(exist = false)
    private String parentMaterialName;/** 子Bom界面新增修改展示 */

    @TableField(exist = false)
    private Long supplierId;/** 物料默认供应商 */

    @TableField(exist = false)
    private Long mainWarehouseId;/** 物料默认仓库 */

    @TableField(exist = false)
    private Integer isOutside;/** 是否为委外物料，0-否，1-是 */

    private Integer pickRule;/** 领料规则，0-不做任何处理，1-领取本身 */

    @TableField(exist = false)
    private String erpCode;


    @Override
    public Long getId() {
        return this.bomId;
    }

    /** 主键 */
    public Long getBomId(){
        return this.bomId;
    }
    /** 主键 */
    public void setBomId(Long bomId){
        this.bomId = bomId;
    }
    /** 物料ID */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料ID */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 归属机构 */
    public Long getOrgId() {
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    /** 归属机构名称 */
    public String getOrgName() {
        return this.orgName;
    }
    /** 归属机构名称 */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    /** 上级物料 */
    public Long getParentBomId(){
        return this.parentBomId;
    }
    /** 上级物料 */
    public void setParentBomId(Long parentBomId){
        this.parentBomId = parentBomId;
    }
    /** 数量 */
    public Double getAmount(){
        return this.amount;
    }
    /** 数量 */
    public void setAmount(Double amount){
        this.amount = amount;
    }
    /** 不良比率 */
    public Double getBadRate() {
        return badRate;
    }
    /** 不良比率 */
    public void setBadRate(Double badRate) {
        this.badRate = badRate;
    }
    /** 物料编码 */
    public String getMaterialCode() {
        return materialCode;
    }
    /** 物料编码 */
    public void setMaterialCode(String materialCode) {
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
    public String getMaterialName() {
        return materialName;
    }
    /** 物料名称 */
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
    /** 图纸版本 */
    public String getFigureVersion() {
        return figureVersion;
    }
    /** 图纸版本 */
    public void setFigureVersion(String figureVersion) {
        this.figureVersion = figureVersion;
    }
    /** 图纸材料 */
    public String getMaterialModel() {
        return materialModel;
    }
    /** 图纸材料 */
    public void setMaterialModel(String materialModel) {
        this.materialModel = materialModel;
    }
    /** 替代材料 */
    public String getSubstituteMaterialModel(){
        return this.substituteMaterialModel;
    }
    /** 替代材料 */
    public void setSubstituteMaterialModel(String substituteMaterialModel){
        this.substituteMaterialModel = substituteMaterialModel;
    }
    /** 图号 */
    public String getFigureNumber(){
        return this.figureNumber;
    }
    /** 图号 */
    public void setFigureNumber(String figureNumber){
        this.figureNumber = figureNumber;
    }
    /** 重量 */
    public Double getWeight() {
        return weight;
    }
    /** 重量 */
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    /** 单位 */
    public Long getUnitId() {
        return unitId;
    }
    /** 单位 */
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
    /** 单位名称 */
    public String getUnitName() {
        return this.unitName;
    }
    /** 单位名称 */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    /** 工序号 */
    public String getProcessId() {
        return processId;
    }
    /** 工序号 */
    public void setProcessId(String processId) {
        this.processId = processId;
    }
    /** EBOM零件号 */
    public String getEbomElementNo() {
        return ebomElementNo;
    }
    /** EBOM零件号 */
    public void setEbomElementNo(String ebomElementNo) {
        this.ebomElementNo = ebomElementNo;
    }
    /** 是否制造 */
    public Integer getIsProduct() {
        return isProduct;
    }
    /** 是否制造 */
    public void setIsProduct(Integer isProduct) {
        this.isProduct = isProduct;
    }
    /** 是否采购 */
    public Integer getIsPurchase() {
        return isPurchase;
    }
    /** 是否采购 */
    public void setIsPurchase(Integer isPurchase) {
        this.isPurchase = isPurchase;
    }
    /** 状态;0-禁用;1-启用 */
    public Integer getStatus() {
        return status;
    }
    /** 状态;0-禁用;1-启用 */
    public void setStatus(Integer status) {
        this.status = status;
    }
    /** 备注 */
    public String getMemo(){
        return this.memo;
    }
    /** 备注 */
    public void setMemo(String memo){
        this.memo = memo;
    }
    /** 零件号 */
    public String getElementNo() {
        return elementNo;
    }
    /** 零件号 */
    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }
    /** 规格型号 */
    public String getSpecification() {
        return specification;
    }
    /** 规格型号 */
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    /** 用于下拉框显示用 */
    public String getText() {
        return this.materialName;
    }
    /** 用于下拉框显示用 */
    public void setText(String text) {
        this.text = text;
    }
    @Override
    public Long getPid() {
        return this.parentBomId;
    }
    /** 发布状态;1-已发布;0-未发布。 */
    public Integer getReleaseStatus(){
        return this.releaseStatus;
    }
    /** 发布状态;1-已发布;0-未发布。 */
    public void setReleaseStatus(Integer releaseStatus){
        this.releaseStatus = releaseStatus;
    }
    /** 项目号 */
    public String getProjectNo() {
        return projectNo;
    }
    /** 项目号 */
    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getParentMaterialName() {
        return parentMaterialName;
    }

    public void setParentMaterialName(String parentMaterialName) {
        this.parentMaterialName = parentMaterialName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getMainWarehouseId() {
        return mainWarehouseId;
    }

    public void setMainWarehouseId(Long mainWarehouseId) {
        this.mainWarehouseId = mainWarehouseId;
    }

    public Integer getIsOutside() {
        return isOutside;
    }

    public void setIsOutside(Integer isOutside) {
        this.isOutside = isOutside;
    }

    public Integer getPickRule() {
        return pickRule;
    }

    public void setPickRule(Integer pickRule) {
        this.pickRule = pickRule;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }
}
