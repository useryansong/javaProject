package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.SupplierErpCodeEO;
import com.xchinfo.erp.bsc.mapper.SupplierErpCodeMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

@Service
public class SupplierErpCodeService extends BaseServiceImpl<SupplierErpCodeMapper, SupplierErpCodeEO> {

/*
    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.UPDATE, entityClass = SupplierErpCodeEO.class)
    public boolean updateById(SupplierErpCodeEO entity) throws BusinessException {
        if(this.baseMapper.checkRepeat(entity.getSupplierId(),entity.getOrgId())>0){
            return new Result<>()
        }
        return super.updateById(entity);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = SupplierErpCodeEO.class)
    public boolean save(SupplierErpCodeEO entity) throws BusinessException {

        return super.save(entity);
    }
*/

    /**
     * 校验重复
     * @param supplierId
     * @param orgId
     * @return
     */
    public int checkRepeat(Long supplierId,Long orgId){
        return this.baseMapper.checkRepeat(supplierId,orgId);
    }

    public int getMaxSequence() {
        return this.baseMapper.getMaxSequence();
    }

    public void updateToMaxSequence(Integer currentValue) {
        this.baseMapper.updateToMaxSequence(currentValue);
    }
}
