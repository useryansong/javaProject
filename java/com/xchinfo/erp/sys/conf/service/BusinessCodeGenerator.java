package com.xchinfo.erp.sys.conf.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.xchinfo.erp.sys.conf.mapper.CodeRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yecat.core.validator.AssertUtils;

/**
 * 业务编码生成器
 *
 * @author roman.li
 * @date 2018/11/20
 * @update
 */
@Service
public class BusinessCodeGenerator {
    private static Integer count = 0;
    private static Logger logger = LoggerFactory.getLogger(BusinessCodeGenerator.class);

    @Autowired
    private CodeRuleMapper codeRuleMapper;

    public String generateNextCode(String ruleCode, Object entity,Long orgId) {
        String nextCode;

        // 获取编码规则
        CodeRuleEO codeRule = this.codeRuleMapper.selectByIdAndOrgId(ruleCode,orgId);
        AssertUtils.isNull(codeRule);

        // 获取前缀
        String prefix = "";

        // 第一级编码
        if (StringUtils.isNotBlank(codeRule.getLevel1()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel1(), entity));

        // 第二级编码
        if (StringUtils.isNotBlank(codeRule.getLevel2()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel2(), entity));

        // 第三级编码
        if (StringUtils.isNotBlank(codeRule.getLevel3()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel3(), entity));

        // 第四级编码
        if (StringUtils.isNotBlank(codeRule.getLevel4()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel4(), entity));

        // 第五级编码
        if (StringUtils.isNotBlank(codeRule.getLevel5()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel5(), entity));

        String maxCode;

        if (StringUtils.isBlank(prefix)){// 全数字格式编码
            // 获取最大编码
            maxCode = this.codeRuleMapper.selectMaxCode(
                    codeRule.getTableName(),
                    codeRule.getColumnName(),
                    codeRule.getSeqRule().length());

            if (StringUtils.isBlank(maxCode))
                maxCode = "0";

            // 获取最大编码
            nextCode = formatNumber(codeRule.getSeqRule(), maxCode);
        } else {
            // 带有前缀编码
            int seqLength = codeRule.getSeqRule().length();
            int prefixLength = prefix.length();
            int codeLength = seqLength + prefixLength;

            maxCode = this.codeRuleMapper.selectMaxCodeWithPrefix(
                    codeRule.getTableName(),
                    codeRule.getColumnName(),
                    seqLength, prefixLength, codeLength, prefix);

            if (StringUtils.isBlank(maxCode))
                maxCode = "0";

            // 获取最大编码
            nextCode = formatNumber(codeRule.getSeqRule(), maxCode);

            // 加上前缀
            nextCode = prefix.concat(nextCode);
        }

        return nextCode;

//        if(StringUtils.isBlank(maxCode)) {
//            this.codeRuleMapper.updateCurrentMaxCode(nextCode, ruleCode);
//            return nextCode;
//        } else {
        // 防止重复调用生成重复的单号
//            String currentMaxCode = this.codeRuleMapper.getCurrentMaxCode(ruleCode);
//            if(currentMaxCode == null) {
//                int count = this.codeRuleMapper.updateByTable(codeRule.getTableName(), codeRule.getColumnName(), ruleCode);
//                if(count > 0) {
//                    currentMaxCode = this.codeRuleMapper.getCurrentMaxCode(ruleCode);
//                } else {
//                    currentMaxCode = nextCode;
//                    this.codeRuleMapper.updateByTable(codeRule.getTableName(), codeRule.getColumnName(), ruleCode);
//                }
//            }
//            String newMaxCode = currentMaxCode.substring(currentMaxCode.length() - codeRule.getSeqRule().length(), currentMaxCode.length());
//            String newNextCode = prefix.concat(formatNumber(codeRule.getSeqRule(), newMaxCode));
//            this.codeRuleMapper.updateCurrentMaxCode(newNextCode, ruleCode);
//            return newNextCode;
//            return getNewNextCode(codeRule, entity);
//        }
    }

    public String generateNextCodeNoOrgId(String ruleCode, Object entity) {
        String nextCode;

        // 获取编码规则
        CodeRuleEO codeRule = this.codeRuleMapper.selectByIdAndOrgIdNoOrgId(ruleCode);
        AssertUtils.isNull(codeRule);

        // 获取前缀
        String prefix = "";

        // 第一级编码
        if (StringUtils.isNotBlank(codeRule.getLevel1()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel1(), entity));

        // 第二级编码
        if (StringUtils.isNotBlank(codeRule.getLevel2()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel2(), entity));

        // 第三级编码
        if (StringUtils.isNotBlank(codeRule.getLevel3()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel3(), entity));

        // 第四级编码
        if (StringUtils.isNotBlank(codeRule.getLevel4()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel4(), entity));

        // 第五级编码
        if (StringUtils.isNotBlank(codeRule.getLevel5()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel5(), entity));

        String maxCode;

        if (StringUtils.isBlank(prefix)){// 全数字格式编码
            // 获取最大编码
            maxCode = this.codeRuleMapper.selectMaxCodeNoOrgId(
                    codeRule.getTableName(),
                    codeRule.getColumnName(),
                    codeRule.getSeqRule().length());

            if (StringUtils.isBlank(maxCode))
                maxCode = "0";

            // 获取最大编码
            nextCode = formatNumber(codeRule.getSeqRule(), maxCode);
        } else {
            // 带有前缀编码
            int seqLength = codeRule.getSeqRule().length();
            int prefixLength = prefix.length();
            int codeLength = seqLength + prefixLength;

            maxCode = this.codeRuleMapper.selectMaxCodeWithPrefixNoOrgId(
                    codeRule.getTableName(),
                    codeRule.getColumnName(),
                    seqLength, prefixLength, codeLength, prefix);

            if (StringUtils.isBlank(maxCode))
                maxCode = "0";

            // 获取最大编码
            nextCode = formatNumber(codeRule.getSeqRule(), maxCode);

            // 加上前缀
            nextCode = prefix.concat(nextCode);
        }

        return nextCode;

    }
//
//    private String getNewNextCode(CodeRuleEO codeRule, Object entity) {
//        String newNextCode;
//        String currentMaxCode = codeRule.getCurrentMaxCode();
//        // 获取前缀
//        String maxCode;
//        String prefix = "";
//        int level1Length = 0;
//        int level2Length = 0;
//        int level3Length = 0;
//        int level4Length = 0;
//        int level5Length = 0;
//        String level2NewStr = "";
//        // 第一级编码
//        if (StringUtils.isNotBlank(codeRule.getLevel1())) {
//            level1Length = codeRule.getLevel1().length();
//            prefix = prefix.concat(formatModifier(codeRule.getLevel1(), entity));
//        }
//        // 第二级编码
//        if (StringUtils.isNotBlank(codeRule.getLevel2())) {
//            level2Length = codeRule.getLevel2().length();
//            level2NewStr = formatModifier(codeRule.getLevel2(), entity);
//            prefix = prefix.concat(level2NewStr);
//        }
//        // 第三级编码
//        if (StringUtils.isNotBlank(codeRule.getLevel3())) {
//            level3Length = codeRule.getLevel3().length();
//            prefix = prefix.concat(formatModifier(codeRule.getLevel3(), entity));
//        }
//        // 第四级编码
//        if (StringUtils.isNotBlank(codeRule.getLevel4())) {
//            level4Length = codeRule.getLevel4().length();
//            prefix = prefix.concat(formatModifier(codeRule.getLevel4(), entity));
//        }
//        // 第五级编码
//        if (StringUtils.isNotBlank(codeRule.getLevel5())) {
//            level5Length = codeRule.getLevel5().length();
//            prefix = prefix.concat(formatModifier(codeRule.getLevel5(), entity));
//        }
//        String level2Str = currentMaxCode.substring(level1Length, level1Length + level2Length);
//        if(level2NewStr.equals(level2Str)) {
//            maxCode = currentMaxCode.substring(level1Length + level2Length + level3Length + level4Length,
//                    level1Length + level2Length + level3Length + level4Length + level5Length);
//        } else {
//            maxCode = "0";
//        }
//        // 加上前缀
//        newNextCode = formatNumber(codeRule.getSeqRule(), maxCode);
//        newNextCode = prefix.concat(newNextCode);
//        this.codeRuleMapper.updateCurrentMaxCode(newNextCode, codeRule.getCode());
//        return newNextCode;
//    }

    /**
     * 检查编码规则是否全数字规则
     *
     * @param codeRule
     * @return
     */
    private static boolean isNumber(String codeRule){
        String regEx = "^[9]*$";
        Pattern patter = Pattern.compile(regEx);
        Matcher matcher = patter.matcher(codeRule);

        return matcher.matches();
    }

    /**
     * 根据指定格式格式化流水码
     *
     * @param rule
     * @param code
     * @return
     */
    private static String formatNumber(String rule, String code){
        // 使用“0”填充编码至指定长度
        String formatter = "%0"+rule.length()+"d";
        return String.format(formatter, Long.valueOf(code) + 1);
    }

    /**
     * 替换除流水码以外的编码规则
     *
     * @param modifier
     * @param obj
     * @return
     */
    private static String formatModifier(String modifier, Object obj){
        if (StringUtils.isBlank(modifier))
            return "";

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(new Date());

        // 判断修饰符是不是由变量组成
        if ("{{yyyymm}}".equals(modifier)){
            // 年月
            return date.substring(0, 6);
        } else if ("{{yyyy}}".equals(modifier)){
            // 年
            return date.substring(0, 4);
        } else if ("{{yyyymmdd}}".equals(modifier)){
            // 年月日
            return date;
        } else if ("{{yymmdd}}".equals(modifier)){
            //获取年后两位+月+日
            return date.substring(2, 8);
        }

        // 使用对象属性进行替换"{{xxxx}}"
        if (modifier.startsWith("{{") && modifier.endsWith("}}")){
            String prop = modifier.substring(2, modifier.indexOf("}}"));

            try {
                if (null != PropertyUtils.getPropertyDescriptor(obj, prop)) {
                    modifier = PropertyUtils.getProperty(obj, prop).toString();
                }

                return modifier;
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);

                // 发生异常则反馈空
                modifier = "";
            }
        }

        return modifier;
    }

    public Long getNextval(String seqName) {
        return this.codeRuleMapper.getNextval(seqName);
    }

    public String getErpVoucherNo(String syncCode) {
        return this.codeRuleMapper.getErpVoucherNo(syncCode);
    }

    public String getSyncValue(String syncCode) {
        return this.codeRuleMapper.getSyncValue(syncCode);
    }
}
