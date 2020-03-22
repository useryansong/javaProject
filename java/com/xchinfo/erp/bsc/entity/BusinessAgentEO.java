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

@TableName("bsc_business_agent")
@KeySequence("bsc_business_agent")
public class BusinessAgentEO extends  AbstractAuditableEntity<BusinessAgentEO> {

    private static final long serialVersionUID = -2071550510677943290L;

    @TableId(type = IdType.INPUT)
    private Long agentId;/** 主键 */

    @NotNull(message = "业务组ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("业务组ID")
    private Long groupId;/** 业务组ID */

    @NotNull(message = "业务员类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("业务员类型")
    private Integer agentType;/** 业务员类型;1-采购员;2-销售员;3-库管员;4-计划员;5-质检员 */

    @NotNull(message = "用户ID不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("用户ID")
    private Long userId;/** 用户ID */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效;1-有效 */

    @TableField(exist = false)
    private String userName;/** 用户姓名 */

    @TableField(exist = false)
    private String groupName;/** 业务组 */

    @Override
    public Serializable getId() {
        return this.agentId;
    }

    /** 主键 */
    public Long getAgentId(){
        return this.agentId;
    }
    /** 主键 */
    public void setAgentId(Long agentId){
        this.agentId = agentId;
    }
    /** 业务组 */
    public Long getGroupId(){
        return this.groupId;
    }
    /** 业务组 */
    public void setGroupId(Long groupId){
        this.groupId = groupId;
    }
    /** 业务员类型;1-采购员;2-销售员;3-库管员;4-计划员;5-质检员 */
    public Integer getAgentType(){
        return this.agentType;
    }
    /** 业务员类型;1-采购员;2-销售员;3-库管员;4-计划员;5-质检员 */
    public void setAgentType(Integer agentType){
        this.agentType = agentType;
    }
    /** 用户ID */
    public Long getUserId(){
        return this.userId;
    }
    /** 用户ID */
    public void setUserId(Long userId){
        this.userId = userId;
    }
    /** 状态;0-无效;1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
