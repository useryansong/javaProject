package com.xchinfo.erp.log.service;

import com.xchinfo.erp.log.entity.OptLogEO;
import com.xchinfo.erp.log.mapper.OptLogMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.IBaseService;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author roman.li
 * @date 2017/11/10
 * @update
 */
@Service
public class OptLogService extends BaseServiceImpl<OptLogMapper, OptLogEO> implements IBaseService<OptLogEO> {
}
