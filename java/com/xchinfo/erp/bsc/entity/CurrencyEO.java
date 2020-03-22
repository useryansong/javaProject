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
 * @date 2019/4/5
 * @update
 */
@TableName("bsc_currency")
@KeySequence("bsc_currency")
public class CurrencyEO extends AbstractAuditableEntity<CurrencyEO> {
    private static final long serialVersionUID = -2071550510677943290L;

    @TableId(type = IdType.INPUT)
    private Long currencyId;/** 主键 */

    @Length(max = 100, message = "编码长度不能超过 100 个字符")
    @BusinessLogField("币种编码")
    private String currencyCode;/** 编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("币种名称")
    private String currencyName;/** 名称 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效;1-有效 */

    @Override
    public Serializable getId() {
        return this.currencyId;
    }

    /** 主键 */
    public Long getCurrencyId(){
        return this.currencyId;
    }
    /** 主键 */
    public void setCurrencyId(Long currencyId){
        this.currencyId = currencyId;
    }
    /** 编码 */
    public String getCurrencyCode(){
        return this.currencyCode;
    }
    /** 编码 */
    public void setCurrencyCode(String currencyCode){
        this.currencyCode = currencyCode;
    }
    /** 名称 */
    public String getCurrencyName(){
        return this.currencyName;
    }
    /** 名称 */
    public void setCurrencyName(String currencyName){
        this.currencyName = currencyName;
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
