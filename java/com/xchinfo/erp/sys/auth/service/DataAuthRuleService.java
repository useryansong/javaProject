package com.xchinfo.erp.sys.auth.service;

import com.xchinfo.erp.sys.auth.entity.DataAuthRuleEO;
import com.xchinfo.erp.sys.auth.mapper.DataAuthRuleMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/24
 * @update
 */
@Service
public class DataAuthRuleService extends BaseServiceImpl<DataAuthRuleMapper, DataAuthRuleEO> {

    public List<DataAuthRuleEO> listAll(String dataEntry) {
        return this.baseMapper.selectAll(dataEntry);
    }
}
