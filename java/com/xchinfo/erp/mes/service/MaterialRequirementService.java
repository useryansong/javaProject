package com.xchinfo.erp.mes.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.mes.entity.MaterialRequirementEO;
import com.xchinfo.erp.mes.mapper.MaterialRequirementMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaterialRequirementService extends BaseServiceImpl<MaterialRequirementMapper, MaterialRequirementEO>{


    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = MaterialRequirementEO.class)
    public boolean save(MaterialRequirementEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        // 生成业务编码
        String code = this.generateNextCode("cmp_material_requirement", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        entity.setSerialCode(code);

        return super.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBySerialId(Long Id) throws BusinessException {

        this.baseMapper.deleteBySerialId(Id);

        return true;
    }


    public String generateNextCode(String ruleCode, Object entity,Long orgId) throws BusinessException{
        String nextCode;
        //查询是否存在这个编码规则
        int count = this.baseMapper.selectCountByCode(ruleCode,orgId);
        if(count == 0) {
            throw new BusinessException("请先配置-"+ruleCode+"-的编码规则");
        }
        // 获取编码规则
        CodeRuleEO codeRule = this.baseMapper.selectByRuleCode(ruleCode,orgId);
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
            maxCode = this.baseMapper.selectMaxCode(
                    codeRule.getTableName(),
                    codeRule.getColumnName(),
                    codeRule.getSeqRule().length(),orgId);

            if (StringUtils.isBlank(maxCode))
                maxCode = "0";

            // 获取最大编码
            nextCode = formatNumber(codeRule.getSeqRule(), maxCode);
        } else {
            // 带有前缀编码
            int seqLength = codeRule.getSeqRule().length();
            int prefixLength = prefix.length();
            int codeLength = seqLength + prefixLength;

            maxCode = this.baseMapper.selectMaxCodeWithPrefix(
                    codeRule.getTableName(),
                    codeRule.getColumnName(),
                    seqLength, prefixLength, codeLength, prefix,orgId);

            if (StringUtils.isBlank(maxCode))
                maxCode = "0";

            // 获取最大编码
            nextCode = formatNumber(codeRule.getSeqRule(), maxCode);

            // 加上前缀
            nextCode = prefix.concat(nextCode);
        }

        return nextCode;
    }

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

                // 发生异常则反馈空
                modifier = "";
            }
        }

        return modifier;
    }




}

