package com.xchinfo.erp.oauth2;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

/**
 * @author roman.li
 * @date 2019-05-27
 * @update
 */
@Component
public class ShiroTag {
    /**
     * 是否拥有该权限
     * @param permission  权限标识
     * @return   true：是     false：否
     */
    public boolean hasPermission(String permission) {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isPermitted(permission);
    }

    /**
     * 判断是否拥有某个角色
     *
     * @param roleCode
     * @return
     */
    public boolean hasRole(String roleCode) {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.hasRole(roleCode);
    }
}
