package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import org.yecat.mybatis.entity.support.AbstractEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 验证码实体对象
 *
 * @author roman.li
 * @project wms-sys-api
 * @date 2018/8/16 10:54
 * @update
 */
@TableName("sys_captcha")
public class CaptchaEO extends AbstractEntity<CaptchaEO> {
    private static final long serialVersionUID = -6644556994223746602L;

    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 验证码
     */
    private String code;
    /**
     * 过期时间
     */
    private Date expireTime;

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public Serializable getId() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getUuid() {
        return uuid;
    }

    public String getCode() {
        return code;
    }

    public Date getExpireTime() {
        return expireTime;
    }
}
