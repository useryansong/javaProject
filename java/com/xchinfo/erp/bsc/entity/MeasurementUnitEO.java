package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("bsc_measurement_unit")
@KeySequence("bsc_measurement_unit")
public class MeasurementUnitEO extends AbstractAuditableEntity<MeasurementUnitEO> {
    private static final long serialVersionUID = -6614300988077276754L;

    @TableId(type = IdType.INPUT)
    private Long unitId;/** 主键 */

    @Length(max = 50, message = "编码长度不能超过 50 个字符")
    @BusinessLogField("编码")
    private String unitCode;/** 编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "编码长度不能超过 200 个字符")
    @BusinessLogField("名称")
    private String unitName;/** 名称 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Integer status;/** 状态;0-无效;1-有效 */

    @Override
    public Serializable getId() {
        return this.unitId;
    }

    /** 主键 */
    public Long getUnitId(){
        return this.unitId;
    }
    /** 主键 */
    public void setUnitId(Long unitId){
        this.unitId = unitId;
    }
    /** 编码 */
    public String getUnitCode(){
        return this.unitCode;
    }
    /** 编码 */
    public void setUnitCode(String unitCode){
        this.unitCode = unitCode;
    }
    /** 名称 */
    public String getUnitName(){
        return this.unitName;
    }
    /** 名称 */
    public void setUnitName(String unitName){
        this.unitName = unitName;
    }

    /** 状态;0-无效;1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status){
        this.status = status;
    }
}
