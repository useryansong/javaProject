package com.xchinfo.erp.sys.auth.service;

import org.springframework.stereotype.Service;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import com.xchinfo.erp.sys.auth.entity.IdentityEO;
import com.xchinfo.erp.sys.auth.mapper.IdentityMapper;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@Service
public class IdentityService extends BaseServiceImpl<IdentityMapper, IdentityEO>  {

    public Result<IdentityEO> selectByUserId(Long userId) {
        IdentityEO identity = this.baseMapper.selectByUserId(userId);

        return new Result<IdentityEO>().ok(identity);
    }
}
