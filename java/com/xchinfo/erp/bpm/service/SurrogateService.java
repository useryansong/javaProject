package com.xchinfo.erp.bpm.service;

import com.xchinfo.erp.bpm.entity.SurrogateEO;
import com.xchinfo.erp.bpm.mapper.SurrogateMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.IBaseService;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Service
public class SurrogateService extends BaseServiceImpl<SurrogateMapper, SurrogateEO> implements IBaseService<SurrogateEO> {
}
