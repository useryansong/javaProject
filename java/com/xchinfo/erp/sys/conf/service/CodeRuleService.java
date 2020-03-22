package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import com.xchinfo.erp.sys.conf.mapper.CodeRuleMapper;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author roman.li
 * @date 2017/10/18
 * @update
 */
@Service
public class CodeRuleService extends BaseServiceImpl<CodeRuleMapper, CodeRuleEO> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(CodeRuleEO entity) throws BusinessException {
        // 编码是否已经使用
        if (retBool(baseMapper.selectCountByCode(entity.getCode()))){
            throw new BusinessException("输入的编码已经被使用，请重新输入!");
        }

        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.BATCHDELETE, entityClass = CodeRuleEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.UPDATE)
    public boolean updateById(CodeRuleEO entity) throws BusinessException {
        return super.updateById(entity);
    }
}
