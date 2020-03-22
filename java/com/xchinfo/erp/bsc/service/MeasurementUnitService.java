package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.MeasurementUnitEO;
import com.xchinfo.erp.bsc.mapper.MeasurementUnitMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class MeasurementUnitService extends BaseServiceImpl<MeasurementUnitMapper, MeasurementUnitEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    public List<MeasurementUnitEO> listAll() {
        return this.baseMapper.selectAll();
    }

    @Override
    public boolean save(MeasurementUnitEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_measurement_unit", entity);
        AssertUtils.isBlank(code);

        entity.setUnitCode(code);

        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = MeasurementUnitEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {



        // 被物料使用的不能删除
        for (Serializable id : idList){

            MeasurementUnitEO measurementUnitEO = this.baseMapper.findCountById((Long) id);

            if (null != measurementUnitEO ){

                throw new BusinessException("编码为："+ measurementUnitEO.getUnitCode() +" 的计量单位已被使用，不能删除！");
            }

        }

        return super.removeByIds(idList);
    }
}
