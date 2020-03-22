package com.xchinfo.erp.aspect;

import com.xchinfo.erp.annotation.OperationPermission;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.exception.ErrorCode;
import java.util.Map;

/**
 * 访问权限控制
 *
 * @author roman.li
 * @date 2018/11/23
 * @update
 */
@Aspect
@Component
public class OperationPermissionAspect {

    @Pointcut("@annotation(com.xchinfo.erp.annotation.OperationPermission)")
    public void optPermissionCut() {
    }

    @Before("optPermissionCut()")
    public void dataFilter(JoinPoint point) {
        Object params = point.getArgs()[0];
        if(params != null && params instanceof Map){
            // 从Dubbo上下文获取用户
            UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();

            StringBuilder permissions = new StringBuilder();
            if (null != user.getPermisions() && !user.getPermisions().isEmpty()){
                for (String perm : user.getPermisions()){
                    permissions.append(perm).append(",");
                }
                permissions.deleteCharAt(permissions.length() - 1);
            }
            Integer superAdmin = user.getSuperAdmin();
            String perms = permissions.toString();

            MethodSignature signature = (MethodSignature) point.getSignature();
            OperationPermission permission = signature.getMethod().getAnnotation(OperationPermission.class);

            //如果不是超级管理员，则进行数据过滤
            if(superAdmin != 1) {
                if (perms.contains(permission.value())){
                    return;
                }
            }

            return;
        }

        throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }
}
