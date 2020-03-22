package com.xchinfo.erp.srm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import com.xchinfo.erp.bsc.entity.ConstraintEO;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.mapper.ConstraintMapper;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class SafeManagerService extends BaseServiceImpl<ConstraintMapper, ConstraintEO> {
   
    public List<ConstraintEO> listAll(Long userId) {
        return this.baseMapper.selectAll();
    }
    
}
