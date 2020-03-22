package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@TableName("wms_inventory")
@KeySequence("wms_inventory")
public class InventoryEO extends AbstractAuditableEntity<InventoryEO> {
    private static final long serialVersionUID = -159787221893311120L;

    @TableId(type = IdType.INPUT)
    private Long inventoryId;/** 主键 */

    @Length(max = 50, message = "单据编号长度不能超过 50 个字符")
    @BusinessLogField("单据编号")
    private String voucherNo;/** 单据编号 */

    @NotNull(message = "盘点日期不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("盘点日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inventoryDate;/** 盘点日期 */

    private String inventoryMonth;/** 盘点月份 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    private Long stockGroupId;/** 库存组 */

/*    @TableField(exist = false)
    private String stockGroupName;*//** 库存组名称 */

    @TableField(exist = false)
    private String groupName;/** 库存组名称 */

    private Long userId;/** 库管员 */

    @TableField(exist = false)
    private String userName;/** 库管员名称 */

    private Integer status;/** 状态;0-新建;1-进行中;2-完成 */

    private Integer adjustStatus;/** 调账状态:0-未加入调账表,1-待调账,2-调账中,3-调账完成 */

    @TableField(exist = false)
    private List<InventoryDetailEO> details;/** 盘点明细 */

    private String inventoryUserId;/** 盘点人ID */

    private String inventoryUserName;/** 盘点人用户名 */

    @TableField(exist = false)
    private Double materialSum;/** 物料总数 */

    private Double difMaterialAmount;/** 盘点差异物料数量 */

    @TableField(exist = false)
    private Double recordAmount;/** 盘点调节记录数量 */

    @TableField(exist = false)
    private Double adjustAmount;/** 调账已完成记录数量 */

    @Override
    public Serializable getId() {
        return this.inventoryId;
    }

    /** 主键 */
    public Long getInventoryId(){
        return this.inventoryId;
    }
    /** 主键 */
    public void setInventoryId(Long inventoryId){
        this.inventoryId = inventoryId;
    }
    /** 单据编号 */
    public String getVoucherNo(){
        return this.voucherNo;
    }
    /** 单据编号 */
    public void setVoucherNo(String voucherNo){
        this.voucherNo = voucherNo;
    }
    /** 盘点日期 */
    public Date getInventoryDate(){
        return this.inventoryDate;
    }
    /** 盘点日期 */
    public void setInventoryDate(Date inventoryDate){
        this.inventoryDate = inventoryDate;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 库存组 */
    public Long getStockGroupId(){
        return this.stockGroupId;
    }
    /** 库存组 */
    public void setStockGroupId(Long stockGroupId){
        this.stockGroupId = stockGroupId;
    }
    /** 库管员 */
    public Long getUserId(){
        return this.userId;
    }
    /** 库管员 */
    public void setUserId(Long userId){
        this.userId = userId;
    }
    /** 状态;0-新建;1-进行中;2-完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-进行中;2-完成 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getInventoryMonth() {
        return inventoryMonth;
    }

    public void setInventoryMonth(String inventoryMonth) {
        this.inventoryMonth = inventoryMonth;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<InventoryDetailEO> getDetails() {
        return details;
    }

    public void setDetails(List<InventoryDetailEO> details) {
        this.details = details;
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

    public Double getMaterialSum() {
        return materialSum;
    }

    public void setMaterialSum(Double materialSum) {
        this.materialSum = materialSum;
    }

    public Double getDifMaterialAmount() {
        return difMaterialAmount;
    }

    public void setDifMaterialAmount(Double difMaterialAmount) {
        this.difMaterialAmount = difMaterialAmount;
    }

    public Double getRecordAmount() {
        return recordAmount;
    }

    public void setRecordAmount(Double recordAmount) {
        this.recordAmount = recordAmount;
    }

    public Double getAdjustAmount() {
        return adjustAmount;
    }

    public void setAdjustAmount(Double adjustAmount) {
        this.adjustAmount = adjustAmount;
    }

    public Integer getAdjustStatus() {
        return adjustStatus;
    }

    public void setAdjustStatus(Integer adjustStatus) {
        this.adjustStatus = adjustStatus;
    }
}
