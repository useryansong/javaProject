package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.sys.conf.entity.DataEntityEO;
import com.xchinfo.erp.sys.conf.entity.DataEntityPropertyEO;
import com.xchinfo.erp.sys.conf.mapper.DataEntityMapper;
import com.xchinfo.erp.sys.conf.mapper.DataEntityPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import java.io.Serializable;
import java.util.List;

/**
 * @author roman.li
 * @date 2018/11/22
 * @update
 */
@Service
public class DataEntityService extends BaseServiceImpl<DataEntityMapper, DataEntityEO>{

    @Autowired
    private DataEntityPropertyMapper dataEntityPropertyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(DataEntityEO entity) throws BusinessException {
        // 更新配置项信息
        for (DataEntityPropertyEO prop : entity.getDataEntityPropertys())
            this.dataEntityPropertyMapper.updateById(prop);

        return super.updateById(entity);
    }

    @Override
    public DataEntityEO getById(Serializable id) {
        // 查询数据实体
        DataEntityEO entity = super.getById(id);

        // 查询配置项
        List<DataEntityPropertyEO> props = this.dataEntityPropertyMapper.selectListByEntity((String) id);

        entity.setDataEntityPropertys(props);

        return entity;
    }
}
