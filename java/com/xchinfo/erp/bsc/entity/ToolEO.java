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
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_tool")
@KeySequence("bsc_tool")
public class ToolEO extends AbstractAuditableEntity<ToolEO> {
    private static final long serialVersionUID = 1324324681571669730L;

    @TableId(type = IdType.INPUT)
    private Long toolId;/** 工具ID */

    @Length(max = 50, message = "工具编码长度不能超过 50 个字符")
    @BusinessLogField("工具编码")
    private String toolCode;/** 工具编码 */

    @NotBlank(message = "工具名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("工具名称")
    private String toolName;/** 工具名称 */

    @NotNull(message = "归属机构不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("归属机构")
    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @NotNull(message = "工具组不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("工具组")
    private String toolGroup;/** 工具组 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-禁用;1-可用 */

    @Override
    public Serializable getId() {
        return this.toolId;
    }

    /** 工具ID */
    public Long getToolId(){
        return this.toolId;
    }
    /** 工具ID */
    public void setToolId(Long toolId){
        this.toolId = toolId;
    }
    /** 工具编码 */
    public String getToolCode(){
        return this.toolCode;
    }
    /** 工具编码 */
    public void setToolCode(String toolCode){
        this.toolCode = toolCode;
    }
    /** 工具名称 */
    public String getToolName(){
        return this.toolName;
    }
    /** 工具名称 */
    public void setToolName(String toolName){
        this.toolName = toolName;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 工具组 */
    public String getToolGroup(){
        return this.toolGroup;
    }
    /** 工具组 */
    public void setToolGroup(String toolGroup){
        this.toolGroup = toolGroup;
    }
    /** 状态;0-禁用;1-可用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-禁用;1-可用 */
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
