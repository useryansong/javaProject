package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@TableName("wms_linedge_inventory")
@KeySequence("wms_linedge_inventory")
public class LinedgeInventoryEO extends AbstractAuditableEntity<LinedgeInventoryEO> {
    private static final long serialVersionUID = -159787221893311120L;

    @TableId(type = IdType.INPUT)
    private Long linedgeInventoryId;/** 线边盘点ID */

    @JsonFormat(pattern = "yyyy-MM")
    private String inventoryMonth;/** 盘点月份 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inventoryDate;/** 盘点日期 */

    private Long orgId;/** 归属机构 */

    private Integer status;/** 状态;0-新建;1-进行中;2-完成 */

    private Long inventoryUserId;/** 盘点人ID */

    private String inventoryUserName;/** 盘点人用户名 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

    @TableField(exist = false)
    private List<LinedgeInventoryDetailEO> details;/** 线边盘点明细 */

    private Long inventoryId;/** 盘点单ID */

    @Override
    public Serializable getId() {
        return this.linedgeInventoryId;
    }

    /** 线边盘点ID */
    public Long getLinedgeInventoryId(){
        return this.linedgeInventoryId;
    }
    /** 线边盘点ID */
    public void setLinedgeInventoryId(Long linedgeInventoryId){
        this.linedgeInventoryId = linedgeInventoryId;
    }
    /** 盘点月份 */
    public String getInventoryMonth(){
        return this.inventoryMonth;
    }
    /** 盘点月份 */
    public void setInventoryMonth(String inventoryMonth){
        this.inventoryMonth = inventoryMonth;
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
    /** 状态;0-新建;1-进行中;2-完成 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-新建;1-进行中;2-完成 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 盘点人ID */
    public Long getInventoryUserId(){
        return this.inventoryUserId;
    }
    /** 盘点人ID */
    public void setInventoryUserId(Long inventoryUserId){
        this.inventoryUserId = inventoryUserId;
    }
    /** 盘点人用户名 */
    public String getInventoryUserName(){
        return this.inventoryUserName;
    }
    /** 盘点人用户名 */
    public void setInventoryUserName(String inventoryUserName){
        this.inventoryUserName = inventoryUserName;
    }

    public List<LinedgeInventoryDetailEO> getDetails() {
        return details;
    }

    public void setDetails(List<LinedgeInventoryDetailEO> details) {
        this.details = details;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }
}
