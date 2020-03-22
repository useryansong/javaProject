package com.xchinfo.erp.sys.auth.service;

import com.xchinfo.erp.sys.auth.entity.UserOnlineEO;
import com.xchinfo.erp.sys.auth.mapper.UserOnlineMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/26
 * @update
 */
@Service
public class UserOnlineService extends BaseServiceImpl<UserOnlineMapper, UserOnlineEO> {

    public List<UserOnlineEO> listByExpired(Date expiredDate) {
        return this.baseMapper.selectByExpired(expiredDate);
    }
}
