package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户Token实体
 *
 * @author roman.li
 * @date 2017/10/9
 * @update
 */
@TableName("sys_user_token")
@KeySequence("sys_user_token")
public class UserTokenEO extends AbstractEntity<UserTokenEO> {

    private static final long serialVersionUID = -8917720929496445037L;

    @TableId(type = IdType.INPUT)
    private Long tokenId;//Token ID

    @TableField("user_id")
    private Long userId;//用户ID

    @TableField(exist = false)
    private String userName;// 登录名

    @TableField(exist = false)
    private String name;// 姓名

    private String token;//token

    private Integer loginType;/** 登陆类型;0-web;1-app */

    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireTime;//过期时间

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;//更新时间

    @Override
    protected Serializable pkVal() {
        return this.tokenId;
    }

    @Override
    public Serializable getId() {
        return this.tokenId;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }
}
