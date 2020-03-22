package com.xchinfo.erp.sys.auth.service;

import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.auth.entity.UserTokenEO;
import com.xchinfo.erp.sys.auth.mapper.UserMapper;
import com.xchinfo.erp.sys.auth.mapper.UserTokenMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author roman.li
 * @project wms-sys-provider
 * @date 2018/8/16 14:53
 * @update
 */
@Service
public class ShiroService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTokenMapper userTokenMapper;

    public Set<String> getUserPermissions(long userId) {
        List<String> userPerms = this.userMapper.queryUserPerms(userId);

        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perms : userPerms){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }

        return permsSet;
    }

    public UserTokenEO queryByToken(String token) {
        return this.userTokenMapper.selectByToken(token);
    }

    public UserEO queryUser(Long userId) {
        // 获取用户
        UserEO user = this.userMapper.selectById(userId);
        // 获取用户机构权限
        user.setOrgIds(this.userMapper.getUserOrgs(userId));
        // 设置用户权限
        user.setPermisions(this.getUserPermissions(userId));

        return user;
    }

    public List<Long> getUserOrgs(Long useId) {
        return this.userMapper.getUserOrgs(useId);
    }

    public UserEO queryByUserName(String userName) {
        // 获取用户
        UserEO user = this.userMapper.queryByUserName(userName);

        // 获取用户机构权限
        user.setOrgIds(this.userMapper.getUserOrgs(user.getUserId()));
        // 设置用户权限
        user.setPermisions(this.getUserPermissions(user.getUserId()));

        return user;
    }

    public UserEO queryByEmail(String email) {
        // 获取用户
        UserEO user = this.userMapper.queryByEmail(email);

        // 获取用户机构权限
        user.setOrgIds(this.userMapper.getUserOrgs(user.getUserId()));
        // 设置用户权限
        user.setPermisions(this.getUserPermissions(user.getUserId()));

        return user;
    }

    public UserEO queryByMobile(String mobile) {
        // 获取用户
        UserEO user = this.userMapper.queryByMobile(mobile);

        // 获取用户机构权限
        user.setOrgIds(this.userMapper.getUserOrgs(user.getUserId()));
        // 设置用户权限
        user.setPermisions(this.getUserPermissions(user.getUserId()));

        return user;
    }

    public Set<String> getUserRoles(Long userId) {
        return this.userMapper.getUserRoles(userId);
    }
}
