package com.xchinfo.erp.sys.conf.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统数据结构字段表
 *
 * @author roman.li
 * @project wms-sys-api
 * @date 2018/7/23 16:03
 * @update
 */
@TableName("sys_data_entity_property")
@KeySequence("sys_data_entity_property")
public class DataEntityPropertyEO extends AbstractAuditableEntity<DataEntityPropertyEO> {

    private static final long serialVersionUID = 1057498362335292677L;

    @TableId(type = IdType.INPUT)
    private Long propertyId;/** 属性id */

    private String entityCode;/** 实体编码 */

    private String properyName;/** 属性名称 */

    private String displayName;/** 显示名称 */

    private String columnName;/** 列名 */

    private Integer dataType;/** 数据类型;1-字符型;2-日期型;3-数字型 */

    private Integer canModify;/** 可修改;0-否,1-是 */

    private Integer dataTableVisible;/** 是否显示;0：隐藏,1：显示 */

    private Integer width;/** 显示长度 */

    private Integer sortable;/** 是否排序 */

    private Integer dataFormVisible;/** 表单字段;0-否,1-是 */

    private String useDict;/** 是否使用字典 */

    private String dictCode;/** 字典编码 */

    private Integer bizLogFlag;/** 日志字段;0-否,1-是 */


    public Long getPropertyId() {
        return propertyId;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public String getProperyName() {
        return properyName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getSortable() {
        return sortable;
    }

    public String getUseDict() {
        return useDict;
    }

    public String getDictCode() {
        return dictCode;
    }

    public Integer getDataTableVisible() {
        return dataTableVisible;
    }

    public Integer getDataFormVisible() {
        return dataFormVisible;
    }

    public Integer getBizLogFlag() {
        return bizLogFlag;
    }

    public Integer getDataType() {
        return dataType;
    }

    public Integer getCanModify() {
        return canModify;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public void setProperyName(String properyName) {
        this.properyName = properyName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setSortable(Integer sortable) {
        this.sortable = sortable;
    }

    public void setUseDict(String useDict) {
        this.useDict = useDict;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public void setDataTableVisible(Integer dataTableVisible) {
        this.dataTableVisible = dataTableVisible;
    }

    public void setDataFormVisible(Integer dataFormVisible) {
        this.dataFormVisible = dataFormVisible;
    }

    public void setBizLogFlag(Integer bizLogFlag) {
        this.bizLogFlag = bizLogFlag;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public void setCanModify(Integer canModify) {
        this.canModify = canModify;
    }

    @Override
    protected Serializable pkVal() {
        return this.propertyId;
    }

    @Override
    public Serializable getId() {
        return this.propertyId;
    }
}
