package com.xchinfo.erp.sys.conf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 系统数据结构配置表
 *
 * @author roman.li
 * @project wms-sys-api
 * @date 2018/7/23 16:01
 * @update
 */
@TableName("sys_data_entity")
public class DataEntityEO extends AbstractAuditableEntity<DataEntityEO> {

    private static final long serialVersionUID = 2903047235539284195L;

    @TableId(type = IdType.INPUT)
    private String entityCode;/** 实体编码 */

    private String entityName;/** 实体名称 */

    private Integer bizLogFlag;/** 记录业务日志;0-否,1-是 */

    private Integer dataTableVisible;/** 显示列表;0-否,1-是 */

    private Integer dataFormVisible;/** 显示表单;0-否,1-是 */

    @TableField(exist = false)
    private List<DataEntityPropertyEO> dataEntityPropertys;/** 数据实体属性 */

    public String getEntityCode() {
        return entityCode;
    }

    public String getEntityName() {
        return entityName;
    }

    public Integer getBizLogFlag() {
        return bizLogFlag;
    }

    public Integer getDataTableVisible() {
        return dataTableVisible;
    }

    public Integer getDataFormVisible() {
        return dataFormVisible;
    }

    public List<DataEntityPropertyEO> getDataEntityPropertys() {
        return dataEntityPropertys;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setBizLogFlag(Integer bizLogFlag) {
        this.bizLogFlag = bizLogFlag;
    }

    public void setDataTableVisible(Integer dataTableVisible) {
        this.dataTableVisible = dataTableVisible;
    }

    public void setDataFormVisible(Integer dataFormVisible) {
        this.dataFormVisible = dataFormVisible;
    }

    public void setDataEntityPropertys(List<DataEntityPropertyEO> dataEntityPropertys) {
        this.dataEntityPropertys = dataEntityPropertys;
    }

    @Override
    protected Serializable pkVal() {
        return this.entityCode;
    }

    @Override
    public Serializable getId() {
        return this.entityCode;
    }
}
