package com.xchinfo.erp.controller.sys.auth;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.auth.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/forgetpw")
public class ForgetpwController  extends BaseController {


    @Autowired
    private UserService userService;

    /**
     * 发送验证码到邮箱
     *
     * @return
     */
    @PostMapping("sendCheckCode")
    public Result<String> sendCheckCode(@RequestBody Map map){
        logger.info("======== ForgetpwController.sendCheckCode() ========");
        return userService.sendCheckCode(map);
    }

    /**
     * 重置密码，并发送新密码到邮箱
     *
     * @return
     */
    @PostMapping("resetPswd")
    public Result<String> resetPswd(@RequestBody Map map){
        logger.info("======== ForgetpwController.resetPswd() ========");
        return userService.resetPswd(map);
    }
}
