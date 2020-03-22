package com.xchinfo.erp.sys.org.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.AuditableTreeNode;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 区域信息
 *
 * @author roman.li
 * @project wms-sys-api
 * @date 2018/6/8 14:16
 * @update
 */
@TableName("sys_area")
@KeySequence("sys_area")
public class AreaEO extends AuditableTreeNode<AreaEO> {
    private static final long serialVersionUID = -622866039074026129L;

    @TableId(type = IdType.INPUT)
    private Long areaId;

    @NotBlank(message = "编码不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 50, message = "编码长度不能超过 50 个字符")
    @TableField("area_code")
    @BusinessLogField("编码")
    private String areaCode; // 区域代码

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @TableField("area_name")
    @BusinessLogField("名称")
    private String areaName; // 区域名称

    private Long parentId; // 父级代码，省级为0

    @TableField(exist = false)
    @BusinessLogField("上级区域")
    private String parentName; //父级名称

    @BusinessLogField("层级")
    private Integer layer;// 层级，1：省级，2：地市，3：区县

    private Integer sortNo;// 排序

    @BusinessLogField("状态")
    private Integer status;// 状态，1：显示，0：隐藏

    @Length(max = 1000, message = "描述长度不能超过 1000 个字符")
    private String remarks;

    @Override
    protected Serializable pkVal() {
        return this.areaId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public Integer getLayer() {
        return layer;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public Integer getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public Long getId() {
        return this.areaId;
    }

    @Override
    public Long getPid() {
        return this.parentId;
    }
}
