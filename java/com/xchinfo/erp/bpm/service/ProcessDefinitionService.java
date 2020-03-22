package com.xchinfo.erp.bpm.service;

import com.xchinfo.erp.bpm.entity.ProcessDefinitionEO;
import com.xchinfo.erp.bpm.mapper.ProcessDefinitionMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Service
public class ProcessDefinitionService extends BaseServiceImpl<ProcessDefinitionMapper, ProcessDefinitionEO> {

    public ProcessDefinitionEO getByKey(String key) {
        return this.baseMapper.selectBykey(key);
    }
}
