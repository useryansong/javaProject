package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.WorkEndDateEO;
import com.xchinfo.erp.bsc.mapper.WorkEndDateMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.text.SimpleDateFormat;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class WorkEndDateService extends BaseServiceImpl<WorkEndDateMapper, WorkEndDateEO> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WorkEndDateEO entity) throws BusinessException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        WorkEndDateEO workEndDateEO = this.baseMapper.selectByWorkEndDate(sdf.format(entity.getWorkEndDate()));
        if(workEndDateEO != null) {
            throw new BusinessException("日期已存在");
        }
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WorkEndDateEO entity) throws BusinessException {
//        WorkEndDateEO workEndDateEO = super.getById(entity.getId());
//        Date date = workEndDateEO.getWorkEndDate();
//        Date now = new Date();
//        if(now.getTime() > date.getTime()) {
//            throw new BusinessException("当前日期之前的数据不允许修改");
//        }
        return super.updateById(entity);
    }

    public boolean updateStatusById(Long[] ids,int status){
        for(Long id : ids){
            this.baseMapper.updateStatusById(status,id);
        }
        return true;
    }
}
