package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 安全生产记录
 */
@TableName("mes_safe_production")
@KeySequence("mes_safe_production")
public class SafeProductionEO extends AbstractAuditableEntity<SafeProductionEO> {
    private static final long serialVersionUID = -2565910235815688266L;

    @TableId(type = IdType.INPUT)
    private Long safeProductionId;/** 记录id */

    @NotNull(message = "组织机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Long orgId;/** 组织机构id */

    @TableField(exist = false)
    private String orgName;/** 所属机构 */

    @NotNull(message = "事故发生日期不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Date accidentDate;/** 事故发生日期 */

    @Length(max = 4000, message = "事故描述长度不能超过 4000 个字符")
    private String description;/** 事故描述 */

    @Length(max = 4000, message = "事故结果长度不能超过 4000 个字符")
    private String result;/** 事故结果 */

    @Override
    public Serializable getId() {
        return this.safeProductionId;
    }

    public Long getSafeProductionId() {
        return safeProductionId;
    }

    public void setSafeProductionId(Long safeProductionId) {
        this.safeProductionId = safeProductionId;
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

    public Date getAccidentDate() {
        return accidentDate;
    }

    public void setAccidentDate(Date accidentDate) {
        this.accidentDate = accidentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
