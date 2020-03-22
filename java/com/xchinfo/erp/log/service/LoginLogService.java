package com.xchinfo.erp.log.service;

import com.xchinfo.erp.log.entity.LoginLogEO;
import com.xchinfo.erp.log.mapper.LoginLogMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.IBaseService;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author roman.li
 * @date 2017/11/10
 * @update
 */
@Service
public class LoginLogService extends BaseServiceImpl<LoginLogMapper, LoginLogEO> implements IBaseService<LoginLogEO> {

}
