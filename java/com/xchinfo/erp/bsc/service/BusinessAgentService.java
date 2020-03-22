package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.BusinessAgentEO;
import com.xchinfo.erp.bsc.mapper.BusinessAgentMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author yuanchang
 * @date 2019/4/11
 * @update
 */
@Service
public class BusinessAgentService extends BaseServiceImpl<BusinessAgentMapper, BusinessAgentEO>   {

    public List<BusinessAgentEO> listAll() {
        return this.baseMapper.selectAll();
    }
}
