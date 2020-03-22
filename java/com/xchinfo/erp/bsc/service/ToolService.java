package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.ToolEO;
import com.xchinfo.erp.bsc.mapper.ToolMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class ToolService extends BaseServiceImpl<ToolMapper, ToolEO> {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Override
    public boolean save(ToolEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_tool", entity);
        AssertUtils.isBlank(code);

        entity.setToolCode(code);

        return super.save(entity);
    }
    public List<ToolEO> listAll() {
        return this.baseMapper.selectAll();
    }
}
