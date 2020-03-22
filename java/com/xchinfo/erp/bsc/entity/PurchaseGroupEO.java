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
 * @author zhongy
 * @date 2019/4/6
 * @update
 */
@TableName("bsc_purchase_group")
@KeySequence("bsc_purchase_group")
public class PurchaseGroupEO extends AbstractAuditableEntity<PurchaseGroupEO> {
    private static final long serialVersionUID = 5580675223450690010L;

    @TableId(type = IdType.INPUT)
    private Long groupId;/** 主键 */

    @Length(max = 100, message = "编码长度不能超过 100 个字符")
    @BusinessLogField("采购组编码")
    private String groupCode;/** 编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("采购组名称")
    private String groupName;/** 名称 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    @BusinessLogField("归属机构")
    private String orgName; /** 归属机构名称 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效;1-有效 */

    @Override
    public Serializable getId() {
        return this.groupId;
    }

    /** 主键 */
    public Long getGroupId() {
        return groupId;
    }
    /** 主键 */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    /** 编码 */
    public String getGroupCode() {
        return groupCode;
    }
    /** 编码 */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
    /** 名称 */
    public String getGroupName() {
        return groupName;
    }
    /** 名称 */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    /** 状态;0-无效;1-有效 */
    public Integer getStatus() {
        return status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status) {
        this.status = status;
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
}
