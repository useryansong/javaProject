package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * @author yuanchang
 * @date 2019/6/5
 * @update
 */
@TableName("wms_subsidiary_receive_order")
@KeySequence("wms_subsidiary_receive_order")
public class SubsidiaryReceiveOrderEO extends AbstractAuditableEntity<SubsidiaryReceiveOrderEO> {
    private static final long serialVersionUID = 9178106479880702917L;

    @TableId(type = IdType.INPUT)
    private Long receiveId;/** 主键 */

    @NotNull(message = "入库类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("入库类型")
    private Integer receiveType;/** 入库类型;1-辅料入库 */

    @Length(max = 50, message = "单据编号长度不能超过 50 个字符")
    @BusinessLogField("单据编号")
    private String voucherNo;/** 单据编号 */

    private Date receiveDate;/** 收货日期 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

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
    private List<SubsidiaryReceiveOrderDetailEO> details;

    @TableField(exist = false)
    private Double totalReceiveQuantity;/**预计总收货数*/

    @TableField(exist = false)
    private Double totalRelReceiveQuantity;/**实际总收货数*/

    private String receiveUserId;/** 收货人ID */

    private String receiveUserName;/** 收货人用户名 */

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

    public List<SubsidiaryReceiveOrderDetailEO> getDetails() {
        return details;
    }

    public void setDetails(List<SubsidiaryReceiveOrderDetailEO> details) {
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

}
