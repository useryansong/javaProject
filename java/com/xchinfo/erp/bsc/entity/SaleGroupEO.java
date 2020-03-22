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
 * @author roman.c
 * @date 2019/4/6
 * @update
 */
@TableName("bsc_sale_group")
@KeySequence("bsc_sale_group")
public class SaleGroupEO extends AbstractAuditableEntity<SaleGroupEO> {


    private static final long serialVersionUID = -4658281461829111742L;

    @TableId(type = IdType.INPUT)
    private Long groupId;/** 主键 */

    @Length(max = 50, message = "编码长度不能超过 50 个字符")
    @BusinessLogField("销售组编码")
    private String groupCode;/** 编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("销售组名称")
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

    /** 主键 */
    public Long getGroupId(){
        return this.groupId;
    }
    /** 主键 */
    public void setGroupId(Long groupId){
        this.groupId = groupId;
    }
    /** 编码 */
    public String getGroupCode(){
        return this.groupCode;
    }
    /** 编码 */
    public void setGroupCode(String groupCode){
        this.groupCode = groupCode;
    }
    /** 组名 */
    public String getGroupName(){
        return this.groupName;
    }
    /** 组名 */
    public void setGroupName(String groupName){
        this.groupName = groupName;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 状态;0-无效;1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
