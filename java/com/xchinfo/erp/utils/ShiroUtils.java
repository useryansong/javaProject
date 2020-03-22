package com.xchinfo.erp.utils;

import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 用户密码加密
 *
 * @author roman.li
 * @project wms-sys-provider
 * @date 2018/7/11 14:44
 * @update
 */
public class ShiroUtils {
    /**  加密算法 */
    public final static String hashAlgorithmName = "SHA-256";
    /**  循环次数 */
    public final static int hashIterations = 16;

    public static String sha256(String password, String salt) {
        return new SimpleHash(hashAlgorithmName, password, salt, hashIterations).toString();
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static UserEO getUser(){
        return (UserEO) SecurityUtils.getSubject().getPrincipal();
    }
}
