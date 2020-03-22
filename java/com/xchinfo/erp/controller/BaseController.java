package com.xchinfo.erp.controller;

import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yecat.core.utils.Result;
import org.yecat.core.utils.ServletUtils;
import org.yecat.core.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller超类，增加获取用户信息的实现
 *
 * @author roman.li
 * @date 2018/12/28
 * @update
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取用户ID
     * @return
     */
    protected Long getUserId() {
        return this.getUser().getUserId();
    }

    /**
     * 获取用户名
     *
     * @return
     */
    protected String getUserName() {
        return this.getUser().getUserName();
    }

    /**
     * 获取用户归属机构
     *
     * @return
     */
    protected Long getOrgId() {
        return this.getUser().getOrgId();
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    protected UserEO getUser(){
        return (UserEO) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取request
     */
    public HttpServletRequest getRequest()
    {
        return ServletUtils.getRequest();
    }

    /**
     * 获取response
     */
    public HttpServletResponse getResponse()
    {
        return ServletUtils.getResponse();
    }

    /**
     * 获取session
     */
    public HttpSession getSession()
    {
        return getRequest().getSession();
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected Result toAjax(int rows)
    {
        return rows > 0 ? success() : error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected Result toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public Result success()
    {
        return new Result();
    }

    /**
     * 返回失败消息
     */
    public Result error()
    {
        return new Result().error();
    }

    /**
     * 返回成功消息
     */
    public Result success(String message)
    {
        return new Result().ok(message);
    }

    /**
     * 返回失败消息
     */
    public Result error(String message)
    {
        return new Result().error(message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }
}
