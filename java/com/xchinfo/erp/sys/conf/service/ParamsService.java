package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.sys.conf.entity.ParamsEO;
import com.xchinfo.erp.sys.conf.mapper.ParamsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import java.io.Serializable;
import java.util.Collection;

/**
 * 系统配置服务实现类
 *
 * @author roman.li
 * @date 2017/10/12
 * @update
 */
@Service
public class ParamsService extends BaseServiceImpl<ParamsMapper, ParamsEO> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(ParamsEO entity) throws BusinessException {
        // 检查参数名是否使用
        if (retBool(this.baseMapper.countByKey(entity.getParamKey())))
            throw new BusinessException("参数名已经使用，请重新输入!");

        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.BATCHDELETE, entityClass = ParamsEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.UPDATE)
    public boolean updateById(ParamsEO entity) throws BusinessException {
        return super.updateById(entity);
    }

    public String getParamByKey(String key) {
        return this.baseMapper.getParamByKey(key);
    }
}
