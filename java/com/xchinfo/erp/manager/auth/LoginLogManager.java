package com.xchinfo.erp.manager.auth;

import com.xchinfo.erp.log.entity.LoginLogEO;
import com.xchinfo.erp.log.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

/**
 * @author roman.li
 * @date 2019/4/28
 * @update
 */
@Component
public class LoginLogManager {

    @Autowired
    private LoginLogService loginLogService;

    public boolean save(LoginLogEO loginLog){
        return this.loginLogService.save(loginLog);
    }
}
