package com.xchinfo.erp.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统登录日志
 *
 * @author roman.li
 * @date 2017/11/9
 * @update
 */
@TableName("sys_login_log")
@KeySequence("sys_login_log")
public class LoginLogEO extends AbstractEntity<LoginLogEO> {

    private static final long serialVersionUID = -4289073188051488076L;

    @TableId(type = IdType.INPUT)
    private Long logId;

    private String loginName;/** 登录账号 */

    private String ipAddress;/** 登录IP地址 */

    private String loginLocation;/** 登录地点 */

    private String browser;/** 浏览器类型 */

    private String os;/** 操作系统 */

    private Integer status;/** 登录状态;0-失败;1-成功 */

    private String msg;/** 提示消息 */

    private Date loginTime;/** 访问时间 */

    @Override
    public Serializable getId() {
        return this.logId;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    /** 登录账号 */
    public String getLoginName(){
        return this.loginName;
    }
    /** 登录账号 */
    public void setLoginName(String loginName){
        this.loginName = loginName;
    }
    /** 登录IP地址 */
    public String getIpAddress(){
        return this.ipAddress;
    }
    /** 登录IP地址 */
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }
    /** 登录地点 */
    public String getLoginLocation(){
        return this.loginLocation;
    }
    /** 登录地点 */
    public void setLoginLocation(String loginLocation){
        this.loginLocation = loginLocation;
    }
    /** 浏览器类型 */
    public String getBrowser(){
        return this.browser;
    }
    /** 浏览器类型 */
    public void setBrowser(String browser){
        this.browser = browser;
    }
    /** 操作系统 */
    public String getOs(){
        return this.os;
    }
    /** 操作系统 */
    public void setOs(String os){
        this.os = os;
    }
    /** 登录状态;0-失败;1-成功 */
    public Integer getStatus(){
        return this.status;
    }
    /** 登录状态;0-失败;1-成功 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 提示消息 */
    public String getMsg(){
        return this.msg;
    }
    /** 提示消息 */
    public void setMsg(String msg){
        this.msg = msg;
    }
    /** 访问时间 */
    public Date getLoginTime(){
        return this.loginTime;
    }
    /** 访问时间 */
    public void setLoginTime(Date loginTime){
        this.loginTime = loginTime;
    }
}
