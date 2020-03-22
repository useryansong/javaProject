package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.PartnerContactEO;
import com.xchinfo.erp.bsc.mapper.PartnerContactMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

@Service
public class PartnerContactService extends BaseServiceImpl<PartnerContactMapper, PartnerContactEO>{

    @Transactional(rollbackFor = Exception.class)
    public boolean removeBySupplier(Long supplierId) {
        return retBool(this.baseMapper.deleteBySupplier(supplierId));
    }
}
