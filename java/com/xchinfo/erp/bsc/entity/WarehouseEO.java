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
 * @author roman.li
 * @date 2019/3/11
 * @update
 */
@TableName("bsc_warehouse")
@KeySequence("bsc_warehouse")
public class WarehouseEO extends AbstractAuditableEntity<WarehouseEO> {
    private static final long serialVersionUID = -7041640722493828576L;

    @TableId(type = IdType.INPUT)
    private Long warehouseId;/** 主键 */

    @Length(max = 100, message = "仓库编码长度不能超过 100 个字符")
    @BusinessLogField("仓库编码")
    private String warehouseCode;/** 仓库编码 */

    private String erpCode;/** erp编码 */

    @NotBlank(message = "仓库名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "仓库名称长度不能超过 200 个字符")
    @BusinessLogField("仓库名称")
    private String warehouseName;/** 仓库名称 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

    @Length(max = 1000, message = "仓库地址长度不能超过 1000 个字符")
    @BusinessLogField("仓库地址")
    private String address;/** 仓库地址 */

    @NotNull(message = "仓库类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("仓库类型")
    private String warehouseType;/** 仓库类型 */

    @Length(max = 200, message = "仓库联系人长度不能超过 200 个字符")
    @BusinessLogField("联系人")
    private String contact;/** 联系人 */

    @Length(max = 100, message = "电话长度不能超过 100 个字符")
    @BusinessLogField("电话")
    private String phone;/** 电话 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效;1-有效 */

    @BusinessLogField("条码")
    private String barCode;/** 条码 */

    private Integer enableWmsOperation; /** 是否允许仓库作业: 0 不允许 1 允许 */

    private List<WarehouseAreaEO> areas;


    @Override
    public Serializable getId() {
        return this.warehouseId;
    }

    /** 主键 */
    public Long getWarehouseId(){
        return this.warehouseId;
    }
    /** 主键 */
    public void setWarehouseId(Long warehouseId){
        this.warehouseId = warehouseId;
    }
    /** 仓库编码 */
    public String getWarehouseCode(){
        return this.warehouseCode;
    }
    /** 仓库编码 */
    public void setWarehouseCode(String warehouseCode){
        this.warehouseCode = warehouseCode;
    }
    /** 仓库名称 */
    public String getWarehouseName(){
        return this.warehouseName;
    }
    /** 仓库名称 */
    public void setWarehouseName(String warehouseName){
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
    /** 仓库地址 */
    public String getAddress(){
        return this.address;
    }
    /** 仓库地址 */
    public void setAddress(String address){
        this.address = address;
    }
    /** 联系人 */
    public String getContact(){
        return this.contact;
    }
    /** 联系人 */
    public void setContact(String contact){
        this.contact = contact;
    }
    /** 电话 */
    public String getPhone(){
        return this.phone;
    }
    /** 电话 */
    public void setPhone(String phone){
        this.phone = phone;
    }
    /** 状态;0-无效;1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /** 仓库类型 */
    public String getWarehouseType(){
        return this.warehouseType;
    }
    /** 仓库类型 */
    public void setWarehouseType(String warehouseType){
        this.warehouseType = warehouseType;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getEnableWmsOperation() {
        return enableWmsOperation;
    }

    public void setEnableWmsOperation(Integer enableWmsOperation) {
        this.enableWmsOperation = enableWmsOperation;
    }

    public List<WarehouseAreaEO> getAreas() {
        return areas;
    }

    public void setAreas(List<WarehouseAreaEO> areas) {
        this.areas = areas;
    }
}
