package com.xchinfo.erp.sys.conf.entity;

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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统配置参数实体
 *
 * @author roman.li
 * @date 2017/10/12
 * @update
 */
@TableName("sys_params")
public class ParamsEO extends AbstractAuditableEntity<ParamsEO> {

    private static final long serialVersionUID = 5210363466208796325L;

    @TableId(value = "param_key", type = IdType.INPUT)
    @NotBlank(message = "参数名不能为空！", groups = {AddGroup.class})
    @Length(min = 3, max = 50, message = "参数名长度在 3 到 50 个字符", groups = {AddGroup.class})
    @BusinessLogField("参数名")
    private String paramKey;// 关键字

    @TableField("param_value")
    @NotBlank(message = "参数值不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "参数值长度不能超过 200 个字符")
    @BusinessLogField("参数值")
    private String paramValue;// 值

    @NotNull(message = "状态不能为空")
    @BusinessLogField("状态")
    private Integer status;// 状态：0-无效；1-有效

    @Length(max = 1000, message = "备注长度不能超过 1000 个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String remarks;// 备注

    @Override
    protected Serializable pkVal() {
        return this.paramKey;
    }

    @Override
    public Serializable getId() {
        return this.paramKey;
    }

    public String getParamKey() {
        return paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public Integer getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
