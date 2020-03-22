package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_material")
@KeySequence("bsc_material")
public class MaterialEO extends AbstractAuditableEntity<MaterialEO> {
    private static final long serialVersionUID = 8478597949599318064L;

    @TableId(type = IdType.INPUT)
    private Long materialId;/** 物料ID */

    @Length(max = 50, message = "物料编码长度不能超过 50 个字符")
    @BusinessLogField("物料编码")
    private String materialCode;/** 物料编码 */

    @NotBlank(message = "物料名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "物料名称长度不能超过 200 个字符")
    @BusinessLogField("物料名称")
    private String materialName;/** 物料名称 */

    @NotNull(message = "物料分类不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("物料分类")
    private String materialType;/** 物料分类 */

    @TableField(exist = false)
    private String materialTypeName;/** 物料分类名称 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构 */

    @NotNull(message = "主计量单位！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("主计量单位")
    private Long firstMeasurementUnit;/** 主计量单位 */

    private Long secondMeasurementUnit;/** 辅助计量单位 */

    @NotNull(message = "辅料不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("是否辅料")
    private Integer consumedProducts;/** 物料属性;0-否，1-是 */

    private String specification;/** 规格型号 */

    private Long sizeUnit;/** 尺寸单位 */

    private Double length;/** 长 */

    private Double width;/** 宽 */

    private Double height;/** 高 */

    private Double bulk;/** 体积 */

    @NotNull(message = "是否采购不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("是否采购")
    private Integer isPurchase;/** 是否采购 */

    @NotNull(message = "是否采购不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("是否采购")
    private Integer isSale;/** 是否销售 */

    @NotNull(message = "是否生产不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("是否生产")
    private Integer isProduct;/** 是否生产 */

    private Integer lotAmount;/** 批量加工数量 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效;1-有效 */

    @NotBlank(message = "零件号不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 100, message = "零件号长度不能超过 100 个字符")
    @BusinessLogField("零件号")
    private String elementNo;/** 零件号 */

    private Double weight;/** 重量 */

    private Long mainWarehouseId;/** 默认仓库 */

    private Double safetyStock;/** 安全库存 */

    private Double minStock;/** 最小库存 */

    private Double maxStock;/** 最大库存 */

    //@NotNull(message = "生产比例不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("生产比例")
    private String productPercent;/** 生产比例 */

    // @NotNull(message = "采购比例不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("采购比例")
    private String purchasePercent;/** 采购比例 */

    // @NotNull(message = "委外比例不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("委外比例")
    private String outSourcePercent;/** 委外比例 */

    @Length(max = 100, message = "零件号长度不能超过 100 个字符")
    private String inventoryCode;/** 存货编码 */

    private String figureNumber;/** 图号 */

    private String figureVersion;/**版本号*/

    private String materialModel;/**材质*/

    private String materialModelSpecific;/**牌号*/

    private String projectNo;/**项目号*/

    private Long projectId;/**项目ID*/

    private String ebomElementNo;/**EBOM零件号*/

    private Integer minProductAmount;/**最小加工量*/

    private Long  warehouseLocationId;/**库位*/

    @NotNull(message = "是否存在子零件不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Integer isExistChild;/** 是否存在子零件;0-否;1-是 */

    private Double snp;/** SNP */

    private Double standardPackingFee;/** 标准包装费 */

    private Double standardFee;/** 标准运费 */

    private Double conversionFactor;/** 折算系数 */

    @TableField(exist = false)
    private Double preCount;/** 本月期初账上结余量(上月期末账上结余量) */

    @TableField(exist = false)
    private Double currentCount;/** 本月期末账上结余量 */

    @TableField(exist = false)
    private Double sumReceiveAmount;/** 本月入库量 */

    @TableField(exist = false)
    private Double sumDeliveryAmount;/** 本月出库量 */

    @TableField(exist = false)
    private String erpCode;

    @TableField(exist = false)
    private List<MaterialRelationshipEO> parentMaterialRelations;

    @TableField(exist = false)
    private List<MaterialRelationshipEO> chileMaterialRelations;


    private String url;/** 图片URL */


    private Long supplierId;/** 默认供应商ID */

    @TableField(exist = false)
    private String supplierName;

    private String customStringField1;/** 自定义字符1 */

    private String customStringField2;/** 自定义字符2 */

    private String customStringField3;/** 自定义字符3 */

    private String customStringField4;/** 自定义字符4 */

    private String customStringField5;/** 自定义字符5 */

    private String customStringField6;/** 自定义字符5 */

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

    @TableField(exist = false)
    private List<MaterialSpecificEO> specifics;/** 规格型号 */

    @TableField(exist = false)
    private List<MaterialCustomerEO> customers;/** 客户自定义名称 */

    @TableField(exist = false)
    private List<MaterialSupplierEO> supliers;/** 供应商自定义名称 */

    @TableField(exist = false)
    private String unitName;/**计量单位名称*/

    @TableField(exist = false)
    private String warehouseName;/**仓库名称*/

    @TableField(exist = false)
    private  String locationName;/**库位名称*/

    private Integer isOutside;/** 领料方式(0-无特殊，1-领自身) */

    @Override
    public Serializable getId() {
        return this.materialId;
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
    /** 物料名称 */
    public String getMaterialName(){
        return this.materialName;
    }
    /** 物料名称 */
    public void setMaterialName(String materialName){
        this.materialName = materialName;
    }
    /** 物料分类 */
    public String getMaterialType(){
        return this.materialType;
    }
    /** 物料分类 */
    public void setMaterialType(String materialType){
        this.materialType = materialType;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 主计量单位 */
    public Long getFirstMeasurementUnit(){
        return this.firstMeasurementUnit;
    }
    /** 主计量单位 */
    public void setFirstMeasurementUnit(Long firstMeasurementUnit){
        this.firstMeasurementUnit = firstMeasurementUnit;
    }
    /** 辅助计量单位 */
    public Long getSecondMeasurementUnit(){
        return this.secondMeasurementUnit;
    }
    /** 辅助计量单位 */
    public void setSecondMeasurementUnit(Long secondMeasurementUnit){
        this.secondMeasurementUnit = secondMeasurementUnit;
    }

    public Integer getConsumedProducts() {
        return consumedProducts;
    }

    public void setConsumedProducts(Integer consumedProducts) {
        this.consumedProducts = consumedProducts;
    }

    /** 规格型号 */
    public String getSpecification(){
        return this.specification;
    }
    /** 规格型号 */
    public void setSpecification(String specification){
        this.specification = specification;
    }
    /** 尺寸单位 */
    public Long getSizeUnit(){
        return this.sizeUnit;
    }
    /** 尺寸单位 */
    public void setSizeUnit(Long sizeUnit){
        this.sizeUnit = sizeUnit;
    }
    /** 长 */
    public Double getLength(){
        return this.length;
    }
    /** 长 */
    public void setLength(Double length){
        this.length = length;
    }
    /** 宽 */
    public Double getWidth(){
        return this.width;
    }
    /** 宽 */
    public void setWidth(Double width){
        this.width = width;
    }
    /** 高 */
    public Double getHeight(){
        return this.height;
    }
    /** 高 */
    public void setHeight(Double height){
        this.height = height;
    }
    /** 体积 */
    public Double getBulk(){
        return this.bulk;
    }
    /** 体积 */
    public void setBulk(Double bulk){
        this.bulk = bulk;
    }
    /** 是否采购 */
    public Integer getIsPurchase(){
        return this.isPurchase;
    }
    /** 是否采购 */
    public void setIsPurchase(Integer isPurchase){
        this.isPurchase = isPurchase;
    }
    /** 是否销售 */
    public Integer getIsSale(){
        return this.isSale;
    }
    /** 是否销售 */
    public void setIsSale(Integer isSale){
        this.isSale = isSale;
    }
    /** 是否生产 */
    public Integer getIsProduct(){
        return this.isProduct;
    }
    /** 是否生产 */
    public void setIsProduct(Integer isProduct){
        this.isProduct = isProduct;
    }
    /** 批量加工数量 */
    public Integer getLotAmount(){
        return this.lotAmount;
    }
    /** 批量加工数量 */
    public void setLotAmount(Integer lotAmount){
        this.lotAmount = lotAmount;
    }
    /** 状态;0-无效;1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 自定义字符1 */
    public String getCustomStringField1(){
        return this.customStringField1;
    }
    /** 自定义字符1 */
    public void setCustomStringField1(String customStringField1){
        this.customStringField1 = customStringField1;
    }
    /** 自定义字符2 */
    public String getCustomStringField2(){
        return this.customStringField2;
    }
    /** 自定义字符2 */
    public void setCustomStringField2(String customStringField2){
        this.customStringField2 = customStringField2;
    }
    /** 自定义字符3 */
    public String getCustomStringField3(){
        return this.customStringField3;
    }
    /** 自定义字符3 */
    public void setCustomStringField3(String customStringField3){
        this.customStringField3 = customStringField3;
    }
    /** 自定义字符4 */
    public String getCustomStringField4(){
        return this.customStringField4;
    }
    /** 自定义字符4 */
    public void setCustomStringField4(String customStringField4){
        this.customStringField4 = customStringField4;
    }
    /** 自定义字符5 */
    public String getCustomStringField5(){
        return this.customStringField5;
    }
    /** 自定义字符5 */
    public void setCustomStringField5(String customStringField5){
        this.customStringField5 = customStringField5;
    }
    /** 自定义数值1 */
    public Double getCustomNumField1(){
        return this.customNumField1;
    }
    /** 自定义数值1 */
    public void setCustomNumField1(Double customNumField1){
        this.customNumField1 = customNumField1;
    }
    /** 自定义数值2 */
    public Double getCustomNumField2(){
        return this.customNumField2;
    }
    /** 自定义数值2 */
    public void setCustomNumField2(Double customNumField2){
        this.customNumField2 = customNumField2;
    }
    /** 自定义数值3 */
    public Double getCustomNumField3(){
        return this.customNumField3;
    }
    /** 自定义数值3 */
    public void setCustomNumField3(Double customNumField3){
        this.customNumField3 = customNumField3;
    }
    /** 自定义数值4 */
    public Double getCustomNumField4(){
        return this.customNumField4;
    }
    /** 自定义数值4 */
    public void setCustomNumField4(Double customNumField4){
        this.customNumField4 = customNumField4;
    }
    /** 自定义数值5 */
    public Double getCustomNumField5(){
        return this.customNumField5;
    }
    /** 自定义数值5 */
    public void setCustomNumField5(Double customNumField5){
        this.customNumField5 = customNumField5;
    }
    /** 自定义日期1 */
    public Date getCustomDateField1(){
        return this.customDateField1;
    }
    /** 自定义日期1 */
    public void setCustomDateField1(Date customDateField1){
        this.customDateField1 = customDateField1;
    }
    /** 自定义日期2 */
    public Date getCustomDateField2(){
        return this.customDateField2;
    }
    /** 自定义日期2 */
    public void setCustomDateField2(Date customDateField2){
        this.customDateField2 = customDateField2;
    }
    /** 自定义日期3 */
    public Date getCustomDateField3(){
        return this.customDateField3;
    }
    /** 自定义日期3 */
    public void setCustomDateField3(Date customDateField3){
        this.customDateField3 = customDateField3;
    }
    /** 自定义日期4 */
    public Date getCustomDateField4(){
        return this.customDateField4;
    }
    /** 自定义日期4 */
    public void setCustomDateField4(Date customDateField4){
        this.customDateField4 = customDateField4;
    }
    /** 自定义日期5 */
    public Date getCustomDateField5(){
        return this.customDateField5;
    }
    /** 自定义日期5 */
    public void setCustomDateField5(Date customDateField5){
        this.customDateField5 = customDateField5;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<MaterialSpecificEO> getSpecifics() {
        return specifics;
    }

    public List<MaterialCustomerEO> getCustomers() {
        return customers;
    }

    public void setSpecifics(List<MaterialSpecificEO> specifics) {
        this.specifics = specifics;
    }

    public void setCustomers(List<MaterialCustomerEO> customers) {
        this.customers = customers;
    }

    public List<MaterialSupplierEO> getSupliers() {
        return supliers;
    }

    public void setSupliers(List<MaterialSupplierEO> supliers) {
        this.supliers = supliers;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getMainWarehouseId() {
        return mainWarehouseId;
    }

    public void setMainWarehouseId(Long mainWarehouseId) {
        this.mainWarehouseId = mainWarehouseId;
    }

    public Double getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(Double safetyStock) {
        this.safetyStock = safetyStock;
    }

    public Double getMinStock() {
        return minStock;
    }

    public void setMinStock(Double minStock) {
        this.minStock = minStock;
    }

    public Double getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Double maxStock) {
        this.maxStock = maxStock;
    }

    public String getProductPercent() {
        return productPercent;
    }

    public void setProductPercent(String productPercent) {
        this.productPercent = productPercent;
    }

    public String getPurchasePercent() {
        return purchasePercent;
    }

    public void setPurchasePercent(String purchasePercent) {
        this.purchasePercent = purchasePercent;
    }

    public String getOutSourcePercent() {
        return outSourcePercent;
    }

    public void setOutSourcePercent(String outSourcePercent) {
        this.outSourcePercent = outSourcePercent;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public List<MaterialRelationshipEO> getParentMaterialRelations() {
        return parentMaterialRelations;
    }

    public void setParentMaterialRelations(List<MaterialRelationshipEO> parentMaterialRelations) {
        this.parentMaterialRelations = parentMaterialRelations;
    }

    public List<MaterialRelationshipEO> getChileMaterialRelations() {
        return chileMaterialRelations;
    }

    public void setChileMaterialRelations(List<MaterialRelationshipEO> chileMaterialRelations) {
        this.chileMaterialRelations = chileMaterialRelations;
    }

    public String getFigureNumber() {
        return figureNumber;
    }

    public void setFigureNumber(String figureNumber) {
        this.figureNumber = figureNumber;
    }

    public String getFigureVersion() {
        return figureVersion;
    }

    public void setFigureVersion(String figureVersion) {
        this.figureVersion = figureVersion;
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

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getEbomElementNo() {
        return ebomElementNo;
    }

    public void setEbomElementNo(String ebomElementNo) {
        this.ebomElementNo = ebomElementNo;
    }

    public Integer getMinProductAmount() {
        return minProductAmount;
    }

    public void setMinProductAmount(Integer minProductAmount) {
        this.minProductAmount = minProductAmount;
    }

    public Long getWarehouseLocationId() {
        return warehouseLocationId;
    }

    public void setWarehouseLocationId(Long warehouseLocationId) {
        this.warehouseLocationId = warehouseLocationId;
    }

    public Integer getIsExistChild() {
        return isExistChild;
    }

    public void setIsExistChild(Integer isExistChild) {
        this.isExistChild = isExistChild;
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

    public Double getSnp() {
        return snp;
    }

    public void setSnp(Double snp) {
        this.snp = snp;
    }

    public Double getStandardPackingFee() {
        return standardPackingFee;
    }

    public void setStandardPackingFee(Double standardPackingFee) {
        this.standardPackingFee = standardPackingFee;
    }

    public Double getStandardFee() {
        return standardFee;
    }

    public void setStandardFee(Double standardFee) {
        this.standardFee = standardFee;
    }

    public Double getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(Double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCustomStringField6() {
        return customStringField6;
    }

    public void setCustomStringField6(String customStringField6) {
        this.customStringField6 = customStringField6;
    }

    public Double getPreCount() {
        return preCount;
    }

    public void setPreCount(Double preCount) {
        this.preCount = preCount;
    }

    public Double getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Double currentCount) {
        this.currentCount = currentCount;
    }

    public Double getSumReceiveAmount() {
        return sumReceiveAmount;
    }

    public void setSumReceiveAmount(Double sumReceiveAmount) {
        this.sumReceiveAmount = sumReceiveAmount;
    }

    public Double getSumDeliveryAmount() {
        return sumDeliveryAmount;
    }

    public void setSumDeliveryAmount(Double sumDeliveryAmount) {
        this.sumDeliveryAmount = sumDeliveryAmount;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public Integer getIsOutside() {
        return isOutside;
    }

    public void setIsOutside(Integer isOutside) {
        this.isOutside = isOutside;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }
}
