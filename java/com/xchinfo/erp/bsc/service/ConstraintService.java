package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.ConstraintEO;
import com.xchinfo.erp.bsc.mapper.ConstraintMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@Service
public class ConstraintService extends BaseServiceImpl<ConstraintMapper, ConstraintEO> {

    public List<ConstraintEO> listAll() {
        return this.baseMapper.selectAll();
    }
}
