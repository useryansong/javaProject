package com.xchinfo.erp.sys.auth.service;

import org.yecat.core.exception.BusinessException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 生成Token
 *
 * @author roman.li
 * @date 2017/10/9
 * @update
 */
public class TokenGenerator {
    private static final char[] hexCode = "0123456789abcdef".toCharArray();

    /**
     * 根据随机数生成Token
     *
     * @return
     */
    public static String generateValue() {
        return generateValue(UUID.randomUUID().toString());
    }

    /**
     * 转换成16进制
     *
     * @param data
     * @return
     */
    public static String toHexString(byte[] data) {
        if(data == null) {
            return null;
        }
        StringBuilder r = new StringBuilder(data.length*2);
        for ( byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    /**
     * 根据指定的字符串生成Token
     *
     * @param param
     * @return
     */
    public static String generateValue(String param){
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(param.getBytes());
            byte[] messageDigest = algorithm.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("生成Token失败!", e);
        }
    }
}
