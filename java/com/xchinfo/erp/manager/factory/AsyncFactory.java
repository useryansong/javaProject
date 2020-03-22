package com.xchinfo.erp.manager.factory;

import com.xchinfo.erp.log.entity.LoginLogEO;
import com.xchinfo.erp.log.entity.OptLogEO;
import com.xchinfo.erp.manager.auth.LoginLogManager;
import com.xchinfo.erp.manager.auth.OptLogManager;
import com.xchinfo.erp.oauth2.ShiroUtils;
import com.xchinfo.erp.utils.Constants;
import com.xchinfo.erp.utils.SpringUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yecat.core.utils.ServletUtils;

import java.util.Date;
import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author roman.li
 * @date 2019/4/26
 * @update
 */
public class AsyncFactory {
    private static final Logger logger = LoggerFactory.getLogger("sys-user");

    /**
     * 记录登陆信息
     *
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLoginlog(final String username, final String status, final String message, final Object... args)
    {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = ShiroUtils.getIp();
        return new TimerTask()
        {
            @Override
            public void run()
            {
                StringBuilder s = new StringBuilder();
                s.append("ip:").append(ip).append("|");
                s.append("user name: ").append(username).append("|");
                s.append("status:").append(status).append("|");
                s.append("message:").append(message);
                // 打印信息到日志
                logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                LoginLogEO loginLog = new LoginLogEO();
                loginLog.setLoginName(username);
                loginLog.setIpAddress(ip);
                loginLog.setBrowser(browser);
                loginLog.setOs(os);
                loginLog.setMsg(message);
                loginLog.setLoginTime(new Date());
                // 日志状态
                if (Constants.LOGIN_SUCCESS.equals(status) || Constants.LOGOUT.equals(status))
                {
                    loginLog.setStatus(1);
                }
                else if (Constants.LOGIN_FAIL.equals(status))
                {
                    loginLog.setStatus(0);
                }
                // 插入数据
                SpringUtils.getBean(LoginLogManager.class).save(loginLog);
            }
        };
    }

    /**
     * 保存操作日志
     *
     * @param optLog 日志
     * @return 任务task
     */
    public static TimerTask recordOptlog(final OptLogEO optLog)
    {
        final String ip = ShiroUtils.getIp();
        return new TimerTask()
        {
            @Override
            public void run()
            {
                StringBuilder s = new StringBuilder();
                s.append("ip:").append(ip).append("|");
                s.append("user name: ").append(optLog.getUserName()).append("|");
                s.append("status:").append(optLog.getStatus()).append("|");
                s.append("operation:").append(optLog.getOperation());
                // 打印信息到日志
                logger.info(s.toString());

                // 插入数据
                SpringUtils.getBean(OptLogManager.class).save(optLog);
            }
        };
    }
}
