package com.xchinfo.erp.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@TableName("hr_position")
@KeySequence("hr_position")
public class PositionEO extends AbstractAuditableEntity<PositionEO> {

    private static final long serialVersionUID = -6035917832174315807L;

    @TableId(type = IdType.INPUT)
    private Long positionId;/** 岗位ID */

    private String positionCode;/** 岗位编码 */

    @NotBlank(message = "名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("名称")
    private String positionName;/** 岗位名称 */

    private String orgId;/** 机构 */

    @TableField(exist = false)
    @NotBlank(message = "机构不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("机构")
    private String orgName;/** 机构名称 */

    @Length(max = 1024, message = "说明长度不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String description;/** 岗位说明 */

    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效；1-有效 */

    public Long getPositionId() {
        return positionId;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public Serializable getId() {
        return this.positionId;
    }
}
