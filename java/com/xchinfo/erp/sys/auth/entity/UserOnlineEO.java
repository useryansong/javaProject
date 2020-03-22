package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/4/26
 * @update
 */
@TableName("sys_user_online")
public class UserOnlineEO extends AbstractEntity<UserOnlineEO> {
    private static final long serialVersionUID = 4056849357986089030L;

    @TableId(type = IdType.INPUT)
    private String sessionId;/** 用户会话id */

    private String loginName;/** 登录账号 */

    private Long orgId;/** 机构 */

    private String ipAddress;/** 登录IP地址 */

    private String loginLocation;/** 登录地点 */

    private String browser;/** 浏览器类型 */

    private String os;/** 操作系统 */

    private Integer status;/** 在线状态;0-离线;1-在线 */

    private Date startTimestamp;/** 创建时间 */

    private Date lastAccessTime;/** 最后访问时间 */

    private Integer expireTime;/** 超时时间 */

    @Override
    public Serializable getId() {
        return this.sessionId;
    }

    /** 用户会话id */
    public String getSessionId(){
        return this.sessionId;
    }
    /** 用户会话id */
    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }
    /** 登录账号 */
    public String getLoginName(){
        return this.loginName;
    }
    /** 登录账号 */
    public void setLoginName(String loginName){
        this.loginName = loginName;
    }
    /** 机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
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
    /** 在线状态;0-离线;1-在线 */
    public Integer getStatus(){
        return this.status;
    }
    /** 在线状态;0-离线;1-在线 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 创建时间 */
    public Date getStartTimestamp(){
        return this.startTimestamp;
    }
    /** 创建时间 */
    public void setStartTimestamp(Date startTimestamp){
        this.startTimestamp = startTimestamp;
    }
    /** 最后访问时间 */
    public Date getLastAccessTime(){
        return this.lastAccessTime;
    }
    /** 最后访问时间 */
    public void setLastAccessTime(Date lastAccessTime){
        this.lastAccessTime = lastAccessTime;
    }
    /** 超时时间 */
    public Integer getExpireTime(){
        return this.expireTime;
    }
    /** 超时时间 */
    public void setExpireTime(Integer expireTime){
        this.expireTime = expireTime;
    }
}
