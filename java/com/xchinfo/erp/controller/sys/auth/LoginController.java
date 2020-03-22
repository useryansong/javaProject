package com.xchinfo.erp.controller.sys.auth;


import com.google.code.kaptcha.Constants;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.manager.AsyncManager;
import com.xchinfo.erp.manager.factory.AsyncFactory;
import com.xchinfo.erp.oauth2.ShiroUtils;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.auth.service.UserService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 登录Controller
 *
 * @author Yansong Shi
 * @date 2017/10/9
 * @update
 */
@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    @ResponseBody
    public Result login(@RequestParam("username") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("captcha") String captcha,
                        @RequestParam("type") String type){
        logger.info("======== LoginController.login(userName =>"+userName+") ========");

        //登陆类型 1-APP,0-WEB
        Integer loginType = 1;
        // 只有PC端使用验证码验证
        if (StringUtils.isBlank(type) || "1".equals(type)){
            loginType = 0;
            // 验证验证码
            Object obj = ShiroUtils.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            String code = String.valueOf(obj != null ? obj : "");
            if (StringUtils.isEmpty(captcha) || !captcha.equalsIgnoreCase(code))
            {
                AsyncManager.me().execute(AsyncFactory.recordLoginlog(userName, com.xchinfo.erp.utils.Constants.LOGIN_FAIL, "验证码错误"));
                return new Result().error("验证码错误!");
            }
        }

        UserEO user = this.userService.queryByUserName(userName);
        //账号不存在、密码错误
        if(user == null || !user.getPassword().equals(new Sha256Hash(password, user.getSalt()).toHex())) {
            AsyncManager.me().execute(AsyncFactory.recordLoginlog(userName, com.xchinfo.erp.utils.Constants.LOGIN_FAIL, "账号或密码不正确"));
            return new Result().error("账号或密码不正确");
        }

        //账号锁定
        if(user.getStatus() != 1){
            AsyncManager.me().execute(AsyncFactory.recordLoginlog(userName, com.xchinfo.erp.utils.Constants.LOGIN_FAIL, "账号已被锁定"));
            return new Result().error("账号已被锁定,请联系管理员");
        }

        logger.info("======== ============="+loginType);

        // 创建token，并保存至数据库
        Map map = this.userService.createToken(user.getUserId(),loginType);
        AsyncManager.me().execute(AsyncFactory.recordLoginlog(userName, com.xchinfo.erp.utils.Constants.LOGIN_SUCCESS, ""));
        map.put("realName", user.getRealName());
        map.put("sysName","沿浦MES系统");
        map.put("userId",user.getUserId());
        map.put("userName",user.getUserName());
        map.put("orgId",user.getOrgId());
        map.put("orgName",user.getOrgName());
        List<Long> roleIds =  userService.selectUserRoleIds(user.getUserId());
        map.put("roleIds",roleIds);
        return new Result().ok(map);
    }

    @GetMapping("/unauth")
    public String unauth()
    {
        return "/error/unauth";
    }

    /**
     * 登出
     *
     * @param
     * @return
     */
    @PostMapping("logout/{type}")
    @ResponseBody
    public Result logout(@PathVariable("type") Integer type){
        logger.info("======== LoginController.logout(userName =>"+getUserId()+") ========");
        this.userService.deleteToken(getUserId(),type);
        AsyncManager.me().execute(AsyncFactory.recordLoginlog(getUserName(), com.xchinfo.erp.utils.Constants.LOGOUT, ""));

        return new Result();
    }
}
