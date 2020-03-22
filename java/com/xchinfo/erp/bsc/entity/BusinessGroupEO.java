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

/**
 * @author yuanchang
 * @date 2019/4/11
 * @update
 */
@TableName("bsc_business_group")
@KeySequence("bsc_business_group")
public class BusinessGroupEO extends  AbstractAuditableEntity<BusinessGroupEO> {

    private static final long serialVersionUID = -2071550510677943290L;

    @TableId(type = IdType.INPUT)
    private Long groupId;/** 主键 */

    @NotNull(message = "业务组类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("业务组类型")
    private Integer groupType;/** 业务组类型 */

    @Length(max = 100, message = "编码长度不能超过 100 个字符")
    @BusinessLogField("业务组编码")
    private String groupCode;/** 业务组编码 */

    @NotBlank(message = "组名不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("组名")
    private String groupName;/** 组名 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 机构名称 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效;1-有效 */

    @Override
    public Serializable getId() {
        return this.groupId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }
}
