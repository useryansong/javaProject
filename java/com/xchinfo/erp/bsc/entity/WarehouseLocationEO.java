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

/**
 * @author zhongy
 * @date 2019/4/15
 * @update
 */
@TableName("bsc_warehouse_location")
@KeySequence("bsc_warehouse_location")
public class WarehouseLocationEO extends AbstractAuditableEntity<WarehouseLocationEO> {
    private static final long serialVersionUID = 5614181756849581085L;

    @TableId(type = IdType.INPUT)
    private Long warehouseLocationId;/** 主键 */

    @Length(max = 100, message = "编码长度不能超过 100 个字符")
    @BusinessLogField("库位编码")
    private String locationCode;/** 编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("库位名称")
    private String locationName;/** 名称 */

    @NotNull(message = "归属仓库不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属仓库")
    private Long warehouseId;/** 归属仓库 */

    @TableField(exist = false)
    @BusinessLogField("归属仓库名称")
    private String warehouseName;/** 归属仓库名称 */

    @TableField(exist = false)
    @BusinessLogField("归属仓库编码")
    private String warehouseCode;/** 归属仓库编码 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    @BusinessLogField("归属机构名称")
    private String orgName;/** 归属机构名称 */

    @NotNull(message = "库区不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("库区")
    private Long warehouseAreaId;/** 库区 */

    @TableField(exist = false)
    @BusinessLogField("库区名称")
    private String areaName;/** 库区名称 */

    @TableField(exist = false)
    @BusinessLogField("库区编码")
    private String areaCode;/** 库区编码 */

    @BusinessLogField("条码")
    private String barCode;/** 条码 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-禁用;1-启用 */

    @TableField(exist = false)
    private Long materialLocationId;/** 物料默认库位 */

    private Integer maxContain; /** 最大容器数 */

    private Integer storageContainer; /** 已存放容器数 */


    @Override
    public Serializable getId() {
        return this.warehouseLocationId;
    }

    /** 主键 */
    public Long getWarehouseLocationId(){
        return this.warehouseLocationId;
    }
    /** 主键 */
    public void setWarehouseLocationId(Long warehouseLocationId){
        this.warehouseLocationId = warehouseLocationId;
    }
    /** 编码 */
    public String getLocationCode(){
        return this.locationCode;
    }
    /** 编码 */
    public void setLocationCode(String locationCode){
        this.locationCode = locationCode;
    }
    /** 名称 */
    public String getLocationName(){
        return this.locationName;
    }
    /** 名称 */
    public void setLocationName(String locationName){
        this.locationName = locationName;
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
        return warehouseCode;
    }
    /** 仓库编码 */
    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
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
        return this.orgName;
    }
    /** 归属机构名称 */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    /** 库区 */
    public Long getWarehouseAreaId(){
        return this.warehouseAreaId;
    }
    /** 库区 */
    public void setWarehouseAreaId(Long warehouseAreaId){
        this.warehouseAreaId = warehouseAreaId;
    }
    /** 库区名称 */
    public String getAreaName() {
        return areaName;
    }
    /** 库区名称 */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    /** 库区编码 */
    public String getAreaCode() {
        return areaCode;
    }
    /** 库区编码 */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    /** 条码 */
    public String getBarCode() {
        return barCode;
    }
    /** 条码 */
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    /** 状态;0-禁用;1-启用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-禁用;1-启用 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public Long getMaterialLocationId() {
        return materialLocationId;
    }

    public void setMaterialLocationId(Long materialLocationId) {
        this.materialLocationId = materialLocationId;
    }

    public Integer getMaxContain() {
        return maxContain;
    }

    public void setMaxContain(Integer maxContain) {
        this.maxContain = maxContain;
    }

    public Integer getStorageContainer() {
        return storageContainer;
    }

    public void setStorageContainer(Integer storageContainer) {
        this.storageContainer = storageContainer;
    }
}
