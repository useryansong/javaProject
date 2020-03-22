package com.xchinfo.erp.form.sys;

/**
 * 登录表单
 *
 * @author roman.li
 * @project wms-apiweb
 * @date 2018/8/16 10:43
 * @update
 */
public class LoginForm {

    private String username;
    private String password;
    private String captcha;
    private String uuid;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
