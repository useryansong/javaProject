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
import java.util.List;

/**
 * @author zhongy
 * @date 2019/4/15
 * @update
 */
@TableName("bsc_warehouse_area")
@KeySequence("bsc_warehouse_area")
public class WarehouseAreaEO extends AbstractAuditableEntity<WarehouseAreaEO> {
    private static final long serialVersionUID = -3693944439866166727L;

    @TableId(type = IdType.INPUT)
    private Long warehouseAreaId;/** 主键 */

    @Length(max = 100, message = "编码长度不能超过 100 个字符")
    @BusinessLogField("库区编码")
    private String areaCode;/** 编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("库区名称")
    private String areaName;/** 名称 */

    @NotNull(message = "归属仓库不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属仓库")
    private Long warehouseId;/** 归属仓库 */

    @TableField(exist = false)
    @BusinessLogField("归属仓库名称")
    private String warehouseName;/** 归属仓库名称 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    @BusinessLogField("归属机构名称")
    private String orgName;/** 归属机构名称 */

    @TableField(exist = false)
    @BusinessLogField("归属仓库编码")
    private String warehouseCode;/** 归属仓库编码 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-禁用;1-启用 */

    @BusinessLogField("条码")
    private String barCode;/** 条码 */

    private String storeMaterialType; /** 存放物料类别 */

    private Integer shelfQuantity;/** 货架数量 */

    private Integer shelfLayer;/** 货架层数 */

    private List<WarehouseLocationEO> locations;


    @Override
    public Serializable getId() {
        return this.warehouseAreaId;
    }

    /** 主键 */
    public Long getWarehouseAreaId(){
        return this.warehouseAreaId;
    }
    /** 主键 */
    public void setWarehouseAreaId(Long warehouseAreaId){
        this.warehouseAreaId = warehouseAreaId;
    }
    /** 编码 */
    public String getAreaCode(){
        return this.areaCode;
    }
    /** 编码 */
    public void setAreaCode(String areaCode){
        this.areaCode = areaCode;
    }
    /** 名称 */
    public String getAreaName(){
        return this.areaName;
    }
    /** 名称 */
    public void setAreaName(String areaName){
        this.areaName = areaName;
    }
    /** 归属仓库 */
    public Long getWarehouseId(){
        return this.warehouseId;
    }
    /** 归属仓库 */
    public void setWarehouseId(Long warehouseId){
        this.warehouseId = warehouseId;
    }
    /** 归属仓库名称 */
    public String getWarehouseName() {
        return warehouseName;
    }
    /** 归属仓库名称 */
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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
    /** 归属仓库编码 */
    public String getWarehouseCode() {
        return warehouseCode;
    }
    /** 归属仓库编码 */
    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
    /** 状态;0-禁用;1-启用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-禁用;1-启用 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getStoreMaterialType() {
        return storeMaterialType;
    }

    public void setStoreMaterialType(String storeMaterialType) {
        this.storeMaterialType = storeMaterialType;
    }

    public Integer getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(Integer shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
    }

    public Integer getShelfLayer() {
        return shelfLayer;
    }

    public void setShelfLayer(Integer shelfLayer) {
        this.shelfLayer = shelfLayer;
    }

    public List<WarehouseLocationEO> getLocations() {
        return locations;
    }

    public void setLocations(List<WarehouseLocationEO> locations) {
        this.locations = locations;
    }
}
