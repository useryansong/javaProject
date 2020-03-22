package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.PurchaseGroupEO;
import com.xchinfo.erp.bsc.mapper.PurchaseGroupMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author zhongy
 * @date 2019/4/6
 * @update
 */
@Service
public class PurchaseGroupService extends BaseServiceImpl<PurchaseGroupMapper, PurchaseGroupEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    public List<PurchaseGroupEO> listAll() {
        return this.baseMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PurchaseGroupEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_purchase_group", entity);
        AssertUtils.isBlank(code);
        entity.setGroupCode(code);
        return super.save(entity);
    }
}
