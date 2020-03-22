package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;


/**
 * @author cxx
 * @date 2019/6/5
 * @update
 */
@TableName("wms_temp_inventory")
@KeySequence("wms_temp_inventory")
public class TempInventoryEO extends AbstractAuditableEntity<TempInventoryEO> {

    @TableId(type = IdType.INPUT)
    private Long tempId;/** 主键 */

    private Long inventoryId;/** 盘点单ID */

    private Long materialId;/** 物料ID */

    private Long locationId;/** 库位ID */

    private Double amount;/** 盘点数量 */

    private String inventoryMonth;/** 盘点月份 */

    private Long orgId;/** 库位ID */

    private Date inventoryDate;/** 盘点日期 */

    private Integer status;/** 状态;0-未使用;1-已使用; */

    private String inventoryUserId;/** 盘点人ID */

    private String inventoryUserName;/** 盘点人用户名 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String materialName;/** 物料名称 */

    @TableField(exist = false)
    private String elementNo;/** 零件号 */

    private String inventoryNo;/** 盘点卡号 */

    @TableField(exist = false)
    private String inventoryCode;/** 存货编码 */

    @TableField(exist = false)
    private String locationBarCode;/** 库位条码 */

    @TableField(exist = false)
    private String sqeNo;/**打印序号 */


    @Override
    public Serializable getId() {
        return tempId;
    }

    public Long getTempId() {
        return tempId;
    }

    public void setTempId(Long tempId) {
        this.tempId = tempId;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getInventoryDate() {
        return inventoryDate;
    }

    public void setInventoryDate(Date inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInventoryUserId() {
        return inventoryUserId;
    }

    public void setInventoryUserId(String inventoryUserId) {
        this.inventoryUserId = inventoryUserId;
    }

    public String getInventoryUserName() {
        return inventoryUserName;
    }

    public void setInventoryUserName(String inventoryUserName) {
        this.inventoryUserName = inventoryUserName;
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

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getInventoryMonth() {
        return inventoryMonth;
    }

    public void setInventoryMonth(String inventoryMonth) {
        this.inventoryMonth = inventoryMonth;
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

    public String getLocationBarCode() {
        return locationBarCode;
    }

    public void setLocationBarCode(String locationBarCode) {
        this.locationBarCode = locationBarCode;
    }

    public String getInventoryNo() {
        return inventoryNo;
    }

    public void setInventoryNo(String inventoryNo) {
        this.inventoryNo = inventoryNo;
    }

    public String getSqeNo() {
        return sqeNo;
    }

    public void setSqeNo(String sqeNo) {
        this.sqeNo = sqeNo;
    }
}
