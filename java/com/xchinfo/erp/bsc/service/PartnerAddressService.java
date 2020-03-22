package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.PartnerAddressEO;
import com.xchinfo.erp.bsc.mapper.PartnerAddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

@Service
public class PartnerAddressService extends BaseServiceImpl<PartnerAddressMapper, PartnerAddressEO>  {

    @Transactional(rollbackFor = Exception.class)
    public boolean removeBySupplier(Long supplierId) {
        return retBool(this.baseMapper.deleteBySupplier(supplierId));
    }

    public PartnerAddressEO queryById(Long addressId) {
        return this.baseMapper.queryById(addressId);
    }
}
