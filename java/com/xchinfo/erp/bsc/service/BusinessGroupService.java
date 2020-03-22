package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.BusinessGroupEO;
import com.xchinfo.erp.bsc.mapper.BusinessGroupMapper;
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
 * @date 2019/4/11
 * @update
 */
@Service
public class BusinessGroupService extends BaseServiceImpl<BusinessGroupMapper, BusinessGroupEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(BusinessGroupEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_business_group", entity);
        AssertUtils.isBlank(code);

        entity.setGroupCode(code);

        return super.save(entity);
    }

    public List<BusinessGroupEO> listAll() {
        return this.baseMapper.selectAll();
    }

    public List<BusinessGroupEO> listByGroupType(Integer groupType) {
        return this.baseMapper.listByGroupType(groupType);
    }
}
