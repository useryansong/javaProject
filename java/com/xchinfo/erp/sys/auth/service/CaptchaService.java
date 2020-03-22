package com.xchinfo.erp.sys.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.sys.auth.entity.CaptchaEO;
import com.xchinfo.erp.sys.auth.mapper.CaptchaMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.Date;

/**
 * @author roman.li
 * @project wms-sys-provider
 * @date 2018/8/16 11:03
 * @update
 */
@Service
public class CaptchaService extends BaseServiceImpl<CaptchaMapper, CaptchaEO> {

    public boolean getCaptcha(String uuid, String code) {
        if(StringUtils.isBlank(uuid)){
            throw new BusinessException("uuid不能为空");
        }

        CaptchaEO captchaEntity = new CaptchaEO();
        captchaEntity.setUuid(uuid);
        captchaEntity.setCode(code);
        //5分钟后过期
        captchaEntity.setExpireTime(DateUtils.addDateMinutes(new Date(), 5));

        return this.save(captchaEntity);
    }

    public boolean validate(String uuid, String code) {
        CaptchaEO captchaEntity = this.getOne(new QueryWrapper<CaptchaEO>().eq("uuid", uuid));
        if(captchaEntity == null){
            return false;
        }

        //删除验证码
        this.removeById(uuid);

        if(captchaEntity.getCode().equalsIgnoreCase(code) && captchaEntity.getExpireTime().getTime() >= System.currentTimeMillis()){
            return true;
        }

        return false;
    }
}
