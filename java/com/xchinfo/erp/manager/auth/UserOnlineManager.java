package com.xchinfo.erp.manager.auth;

import com.xchinfo.erp.sys.auth.entity.UserOnlineEO;
import com.xchinfo.erp.sys.auth.service.UserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

/**
 * @author roman.li
 * @date 2019/4/28
 * @update
 */
@Component
public class UserOnlineManager {

    @Autowired
    private UserOnlineService userOnlineService;

    public boolean save(UserOnlineEO online){
        return this.userOnlineService.save(online);
    }
}
