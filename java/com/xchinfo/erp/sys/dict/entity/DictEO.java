package com.xchinfo.erp.sys.dict.entity;

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
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 字典
 *
 * @author roman.li
 * @date 2017/10/18
 * @update
 */
@TableName("sys_dict")
@KeySequence("sys_dict")
public class DictEO extends AbstractAuditableEntity<DictEO> {
    private static final long serialVersionUID = -7183264727333886753L;

    @TableId(value = "dict_id", type = IdType.INPUT)
    private Long dictId;

    private Long typeId;

    @TableField(exist = false)
    @BusinessLogField("上级字典")
    private String typeName;

    /*@NotBlank(message = "字典编码不能为空", groups = {AddGroup.class})
    @Length(max = 50, message = "字典编码长度不能超过 50 个字符", groups = {AddGroup.class})*/
    @BusinessLogField("编码")
    private String dictCode;

    @NotBlank(message = "字典名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "字典名称长度不能超过 200 个字符", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("名称")
    private String dictName;

    @BusinessLogField("状态")
    private Integer status;// 状态：0：隐藏，1：显示

    @BusinessLogField("类型")
    private Integer type;// 类型：0:目录，1:字典项

    private Integer orderNum;// 排序号

    @Length(max = 1000, message = "备注长度不能超过 1000 个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String remarks;// 备注

    @TableField(exist = false)
    private String text;// 用于下拉框显示使用

    private String additionalItems;//字典附加项

    @TableField(exist = false)
    private List<DictEO> dicts;// 字典项

    @Override
    protected Serializable pkVal() {
        return this.dictId;
    }

    @Override
    public Serializable getId() {
        return this.dictId;
    }

    public Long getDictId() {
        return dictId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getType() {
        return type;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public String getRemarks() {
        return remarks;
    }

    public List<DictEO> getDicts() {
        return dicts;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setDicts(List<DictEO> dicts) {
        this.dicts = dicts;
    }

    public String getText() {
        return this.dictName;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAdditionalItems() {
        return additionalItems;
    }

    public void setAdditionalItems(String additionalItems) {
        this.additionalItems = additionalItems;
    }
}
