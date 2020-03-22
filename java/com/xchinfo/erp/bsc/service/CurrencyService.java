package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.CurrencyEO;
import com.xchinfo.erp.bsc.mapper.CurrencyMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/5
 * @update
 */
@Service
public class CurrencyService extends BaseServiceImpl<CurrencyMapper, CurrencyEO> {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CurrencyEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_currency", entity);
        AssertUtils.isBlank(code);

        entity.setCurrencyCode(code);

        return super.save(entity);
    }

    public List<CurrencyEO> listAll() {
        return this.baseMapper.selectAll();
    }
}
