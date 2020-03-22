package com.xchinfo.erp.log.service;

import com.xchinfo.erp.log.entity.ErrorLogEO;
import com.xchinfo.erp.log.mapper.ErrorLogMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.IBaseService;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author roman.li
 * @date 2018/11/12
 * @update
 */
@Service
public class ErrorLogService extends BaseServiceImpl<ErrorLogMapper, ErrorLogEO> implements IBaseService<ErrorLogEO> {
}
