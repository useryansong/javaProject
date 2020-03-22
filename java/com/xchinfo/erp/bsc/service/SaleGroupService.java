package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.SaleGroupEO;
import com.xchinfo.erp.bsc.mapper.SaleGroupMapper;
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
public class SaleGroupService extends BaseServiceImpl<SaleGroupMapper, SaleGroupEO>{

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SaleGroupEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_sale_group", entity);
        AssertUtils.isBlank(code);

        entity.setGroupCode(code);

        return super.save(entity);
    }

    public List<SaleGroupEO> listAll(){
        return this.baseMapper.selectAll();
    }


}


