package com.xchinfo.erp.bsc.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_work_end_date")
@KeySequence("bsc_work_end_date")
public class WorkEndDateEO extends AbstractAuditableEntity<WorkEndDateEO> {
    private static final long serialVersionUID = -4557798239312754921L;

    @TableId(type = IdType.INPUT)
    private Long workEndDateId;/** 主键 */

    @NotNull(message = "日期不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("日期")
    @JSONField(format="yyyy-MM-dd")
    private Date workEndDate;/** 日期 */

    @NotNull(message = "类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("类型")
    private Integer dateType;/** 类型;关联数据字典*/

    @TableField(exist = false)
    private String dateTypeName;/** 类型 */

    private String message;/*休息日描述，例如：国庆节*/

    private Long orgId;/*所属机构id*/

    @TableField(exist = false)
    private String orgName;/** 所属机构 */

    private Long status; /**  状态;0-启用;1-停用 */

    @Override
    public Serializable getId() {
        return this.workEndDateId;
    }

    /** 主键 */
    public Long getWorkEndDateId(){
        return this.workEndDateId;
    }
    /** 主键 */
    public void setWorkEndDateId(Long workEndDateId){
        this.workEndDateId = workEndDateId;
    }
    /** 日期 */
    public Date getWorkEndDate(){
        return this.workEndDate;
    }
    /** 日期 */
    public void setWorkEndDate(Date workEndDate){
        this.workEndDate = workEndDate;
    }

    public Integer getDateType(){
        return this.dateType;
    }

    public void setDateType(Integer dateType){
        this.dateType = dateType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getDateTypeName() {
        return dateTypeName;
    }

    public void setDateTypeName(String dateTypeName) {
        this.dateTypeName = dateTypeName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
