package com.xchinfo.erp.utils;

/**
 * Redis所有Keys
 *
 * @author roman.li
 * @project wms-web
 * @date 2018/5/11 21:42
 * @update
 */
public class RedisKeys {

    public static String getSysConfigKey(String key){
        return "sys:config:" + key;
    }

    public static String getShiroSessionKey(String key){
        return "sessionid:" + key;
    }

}
