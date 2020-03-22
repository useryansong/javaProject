package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.ShiftTypeEO;
import com.xchinfo.erp.bsc.mapper.ShiftTypeMapper;
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
 * @date 2019/3/7
 * @update
 */
@Service
public class ShiftTypeService extends BaseServiceImpl<ShiftTypeMapper, ShiftTypeEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ShiftTypeEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_shift_type", entity);
        AssertUtils.isBlank(code);
        entity.setShiftTypeCode(code);
        return super.save(entity);
    }

    public List<ShiftTypeEO> listAll() {
        return this.baseMapper.selectAll();
    }
}
