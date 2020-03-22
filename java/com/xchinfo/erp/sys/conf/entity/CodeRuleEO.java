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

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2017/10/18
 * @update
 */
@TableName("sys_code_rule")
public class CodeRuleEO extends AbstractAuditableEntity<CodeRuleEO> {
    private static final long serialVersionUID = 677154628075276856L;

    @TableId(value = "rule_code", type = IdType.INPUT)
    @NotBlank(message = "编码不能为空！", groups = {AddGroup.class})
    @Length(min = 3, max = 50, message = "编码长度在 3 到 50 个字符", groups = {AddGroup.class})
    @BusinessLogField("规则编码")
    private String code;

    @TableField("rule_name")
    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("规则名称")
    private String name;

    @TableField("table_name")
    @NotBlank(message = "表名不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "表名长度不能超过 200 个字符")
    @BusinessLogField("表名")
    private String tableName;

    @TableField("column_name")
    @NotBlank(message = "列名不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "列名长度不能超过 200 个字符")
    @BusinessLogField("列名")
    private String columnName;

    @Length(max = 200, message = "1级长度不能超过 200 个字符")
    @BusinessLogField("一级")
    private String level1;
    @Length(max = 200, message = "2级长度不能超过 200 个字符")
    @BusinessLogField("二级")
    private String level2;
    @Length(max = 200, message = "3级长度不能超过 200 个字符")
    @BusinessLogField("三级")
    private String level3;
    @Length(max = 200, message = "4级长度不能超过 200 个字符")
    @BusinessLogField("四级")
    private String level4;
    @Length(max = 200, message = "5级长度不能超过 200 个字符")
    @BusinessLogField("五级")
    private String level5;

    @TableField("seq_rule")
    @BusinessLogField("流水码规则")
    private String seqRule;

    @BusinessLogField("当前流水码最大值")
    private String currentMaxCode;

    @Override
    public Serializable pkVal() {
        return this.code;
    }

    @Override
    public Serializable getId() {
        return this.code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getLevel1() {
        return level1;
    }

    public String getLevel2() {
        return level2;
    }

    public String getLevel3() {
        return level3;
    }

    public String getLevel4() {
        return level4;
    }

    public String getLevel5() {
        return level5;
    }

    public String getSeqRule() {
        return seqRule;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    public void setLevel4(String level4) {
        this.level4 = level4;
    }

    public void setLevel5(String level5) {
        this.level5 = level5;
    }

    public void setSeqRule(String seqRule) {
        this.seqRule = seqRule;
    }

    public String getCurrentMaxCode() {
        return currentMaxCode;
    }

    public void setCurrentMaxCode(String currentMaxCode) {
        this.currentMaxCode = currentMaxCode;
    }
}
