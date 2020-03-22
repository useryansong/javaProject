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
@TableName("bsc_shift_type")
@KeySequence("bsc_shift_type")
public class ShiftTypeEO extends AbstractAuditableEntity<ShiftTypeEO> {
    private static final long serialVersionUID = 1024101162395511955L;

    @TableId(type = IdType.INPUT)
    private Long shiftTypeId;/** 班组ID */

    @Length(max = 100, message = "编码长度不能超过 100 个字符")
    @BusinessLogField("班组编码")
    private String shiftTypeCode;/** 班组编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("班组名称")
    private String shiftTypeName;/** 班组名称 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效;1-有效 */

    @Override
    public Serializable getId() {
        return this.shiftTypeId;
    }

    /** 班组ID */
    public Long getShiftTypeId(){
        return this.shiftTypeId;
    }
    /** 班组ID */
    public void setShiftTypeId(Long shiftTypeId){
        this.shiftTypeId = shiftTypeId;
    }
    /** 班组编码 */
    public String getShiftTypeCode(){
        return this.shiftTypeCode;
    }
    /** 班组编码 */
    public void setShiftTypeCode(String shiftTypeCode){
        this.shiftTypeCode = shiftTypeCode;
    }
    /** 班组名称 */
    public String getShiftTypeName(){
        return this.shiftTypeName;
    }
    /** 班组名称 */
    public void setShiftTypeName(String shiftTypeName){
        this.shiftTypeName = shiftTypeName;
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
