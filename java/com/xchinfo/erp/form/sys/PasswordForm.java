package com.xchinfo.erp.form.sys;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 密码表单
 *
 * @author roman.li
 * @project wms-apiweb
 * @date 2018/8/16 10:43
 * @update
 */
public class PasswordForm {

    @NotBlank(message = "原密码不能为空！")
    private String password;// 原密码

    @NotBlank(message = "新密码不能为空！")
    private String newPassword;// 新密码

    public String getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
