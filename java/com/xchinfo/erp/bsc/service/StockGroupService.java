package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.StockGroupEO;
import com.xchinfo.erp.bsc.mapper.StockGroupMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author yuanchang
 * @date 2019/4/6
 * @update
 */
@Service
public class StockGroupService extends BaseServiceImpl<StockGroupMapper, StockGroupEO>{

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(StockGroupEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_stock_group", entity);
        AssertUtils.isBlank(code);

        entity.setGroupCode(code);

        return super.save(entity);
    }

    public List<StockGroupEO> listAll() {
        return this.baseMapper.selectAll();
    }
}
