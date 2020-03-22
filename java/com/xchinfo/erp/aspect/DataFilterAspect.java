package com.xchinfo.erp.aspect;

import com.xchinfo.erp.annotation.DataFilter;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.exception.ErrorCode;
import org.yecat.mybatis.interceptor.DataScope;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2018/11/22
 * @update
 */
@Aspect
@Component
public class DataFilterAspect {

    @Pointcut("@annotation(com.xchinfo.erp.annotation.DataFilter)")
    public void dataFilterCut() {
    }

    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) {
        Object params = point.getArgs()[0];
        if(params != null && params instanceof Map){
            // 从Dubbo上下文获取用户

            UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
            String userName = user.getUserName();
            List<Long> orgs = user.getOrgIds();
            StringBuilder orgSqlFilter = new StringBuilder();
            if (null != orgs && !orgs.isEmpty()){
                orgSqlFilter.append(" in(");

                for (Long org : orgs){
                    orgSqlFilter.append(org).append(",");
                }
                orgSqlFilter.deleteCharAt(orgSqlFilter.length() - 1);

                orgSqlFilter.append(")");
            }
            Integer superAdmin = user.getSuperAdmin();

            //如果不是超级管理员，则进行数据过滤
            if(superAdmin != 1) {
                Map map = (Map)params;
                String sqlFilter = this.getSqlFilter(userName, orgSqlFilter.toString(), point);
                map.put("sqlFilter", new DataScope(sqlFilter));
            }

            return;
        }

        throw new BusinessException(ErrorCode.DATA_SCOPE_PARAMS_ERROR);
    }

    /**
     * 获取数据过滤的SQL
     */
    private String getSqlFilter(String userName, String orgSqlFilter, JoinPoint point){
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataFilter dataFilter = signature.getMethod().getAnnotation(DataFilter.class);
        //获取表的别名
        String tableAlias = dataFilter.tableAlias();
        if(StringUtils.isNotBlank(tableAlias)){
            tableAlias +=  ".";
        }

        StringBuilder sqlFilter = new StringBuilder();

        //查询条件前缀
        String prefix = dataFilter.prefix();
        if(StringUtils.isNotBlank(prefix)){
            sqlFilter.append(" ").append(prefix);
        }

        sqlFilter.append(" (");

        //部门ID列表
        if (StringUtils.isNotBlank(orgSqlFilter))
            sqlFilter.append(orgSqlFilter).append(" or ");

        //查询本人数据
        sqlFilter.append(tableAlias).append(dataFilter.userId()).append("='").append(userName).append("'");

        sqlFilter.append(")");

        return sqlFilter.toString();
    }
}
