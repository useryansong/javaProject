package com.xchinfo.erp.oauth2;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author roman.li
 * @date 2017/11/23
 * @update
 */
public class OAuth2Token implements AuthenticationToken {
    private String token;

    public OAuth2Token(String token){
        this.token = token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
