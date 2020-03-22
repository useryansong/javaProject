package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @date 2019/4/18
 * @update
 */
@TableName("wms_receive_order")
@KeySequence("wms_receive_order")
public class ReceiveOrderEO extends AbstractAuditableEntity<ReceiveOrderEO> {
    private static final long serialVersionUID = 9178106479880702917L;

    @TableId(type = IdType.INPUT)
    private Long receiveId;/** 主键 */

    private Long deliveryNoteId;/**送货单ID*/

    private Long poorProductionId;/** 生产不良ID */

    @NotNull(message = "入库类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("入库类型")
    private Integer receiveType;/** 入库类型;1-采购入库;2-生产入库;3-委外入库;4-辅料入库;5-调拨入库;6-其他入库;*/

    @BusinessLogField("其他入库类型")
    private Integer childReceiveType;/** 其他入库子类型;1-其他采购入库;2-其他委外入库;3-成品退货入库;4-原材料退料入库;5-返修入库;6-客供件 */

    @BusinessLogField("单据来源")
    private Integer voucherType;/** 单据来源:1-内部自动创建;2-用户手动创建 */

    @Length(max = 50, message = "单据编号长度不能超过 50 个字符")
    @BusinessLogField("单据编号")
    private String voucherNo;/** 单据编号 */

    private Date receiveDate;/** 收货日期 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

    @TableField(exist = false)
    private String orgCode;/** 机构编码 */

    @TableField(exist = false)
    private String fullName;/** 机构全称 */

    @TableField(exist = false)
    private Integer type;/** 库存子类型传递 */


    private Long stockGroupId;/** 库存组 */

    @TableField(exist = false)
    private  String groupName;

    private Long userId;/** 库管员 */

    @TableField(exist = false)
    private  String userName;

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-新建;1-进行中;2-完成 */

    @TableField(exist = false)
    private List<ReceiveOrderDetailEO> details;

    @TableField(exist = false)
    private Double totalReceiveQuantity;/**预计总收货数*/

    @TableField(exist = false)
    private Double totalRelReceiveQuantity;/**实际总收货数*/

    private String receiveUserId;/** 收货人ID */

    private String receiveUserName;/** 收货人用户名 */

    @TableField(exist = false)
    private Double count;/** 数量 */

    private String memo;/** 备注 */

    private Integer syncStatus; /** 成品入库(生产日报)U8同步状态(0-未同步，1-已提交(等待同步)，2-同步成功，3-部分成功，4-全部失败，5-已禁止) */

    private String syncResult;/** 成品入库(生产日报)U8同步结果 */

    private String erpVoucherNo1;/** ERP流水号(导出领料单(生产日报页面)) */


    @Override
    public Serializable getId() {
        return receiveId;
    }

    /** 主键 */
    public Long getReceiveId(){
        return this.receiveId;
    }
    /** 主键 */
    public void setReceiveId(Long receiveId){
        this.receiveId = receiveId;
    }
    /** 入库类型;1-采购入库;2-生产入库;3-委外入库;4-辅料入库;5-其他入库 */
    public Integer getReceiveType(){
        return this.receiveType;
    }
    /** 入库类型;1-采购入库;2-生产入库;3-委外入库;4-辅料入库;5-其他入库 */
    public void setReceiveType(Integer receiveType){
        this.receiveType = receiveType;
    }
    /** 单据编号 */
    public String getVoucherNo(){
        return this.voucherNo;
    }
    /** 单据编号 */
    public void setVoucherNo(String voucherNo){
        this.voucherNo = voucherNo;
    }
    /** 收货日期 */
    public Date getReceiveDate(){
        return this.receiveDate;
    }
    /** 收货日期 */
    public void setReceiveDate(Date receiveDate){
        this.receiveDate = receiveDate;
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

    public List<ReceiveOrderDetailEO> getDetails() {
        return details;
    }

    public void setDetails(List<ReceiveOrderDetailEO> details) {
        this.details = details;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public Double getTotalReceiveQuantity() {
        return totalReceiveQuantity;
    }

    public void setTotalReceiveQuantity(Double totalReceiveQuantity) {
        this.totalReceiveQuantity = totalReceiveQuantity;
    }

    public Double getTotalRelReceiveQuantity() {
        return totalRelReceiveQuantity;
    }

    public void setTotalRelReceiveQuantity(Double totalRelReceiveQuantity) {
        this.totalRelReceiveQuantity = totalRelReceiveQuantity;
    }

    public String getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public Long getDeliveryNoteId() {
        return deliveryNoteId;
    }

    public void setDeliveryNoteId(Long deliveryNoteId) {
        this.deliveryNoteId = deliveryNoteId;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getChildReceiveType() {
        return childReceiveType;
    }

    public void setChildReceiveType(Integer childReceiveType) {
        this.childReceiveType = childReceiveType;
    }

    public Integer getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(Integer voucherType) {
        this.voucherType = voucherType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getPoorProductionId() {
        return poorProductionId;
    }

    public void setPoorProductionId(Long poorProductionId) {
        this.poorProductionId = poorProductionId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(String syncResult) {
        this.syncResult = syncResult;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getErpVoucherNo1() {
        return erpVoucherNo1;
    }

    public void setErpVoucherNo1(String erpVoucherNo1) {
        this.erpVoucherNo1 = erpVoucherNo1;
    }
}
