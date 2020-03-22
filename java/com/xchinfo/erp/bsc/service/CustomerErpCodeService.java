package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.CustomerErpCodeEO;
import com.xchinfo.erp.bsc.mapper.CustomerErpCodeMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

@Service
public class CustomerErpCodeService extends BaseServiceImpl<CustomerErpCodeMapper, CustomerErpCodeEO> {

/*
    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.UPDATE, entityClass = CustomerErpCodeEO.class)
    public boolean updateById(CustomerErpCodeEO entity) throws BusinessException {
        if(this.baseMapper.checkRepeat(entity.getCustomerId(),entity.getOrgId())>0){
            return new Result<>()
        }
        return super.updateById(entity);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = CustomerErpCodeEO.class)
    public boolean save(CustomerErpCodeEO entity) throws BusinessException {

        return super.save(entity);
    }
*/

    /**
     * 校验重复
     * @param customerId
     * @param orgId
     * @return
     */
    public int checkRepeat(Long customerId,Long orgId){
        return this.baseMapper.checkRepeat(customerId,orgId);
    }

    public int getMaxSequence() {
        return this.baseMapper.getMaxSequence();
    }

    public void updateToMaxSequence(Integer currentValue) {
        this.baseMapper.updateToMaxSequence(currentValue);
    }
}
