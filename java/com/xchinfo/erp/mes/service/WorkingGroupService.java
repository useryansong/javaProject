package com.xchinfo.erp.mes.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.mes.entity.WorkingGroupEO;
import com.xchinfo.erp.mes.mapper.WorkingGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class WorkingGroupService extends BaseServiceImpl<WorkingGroupMapper, WorkingGroupEO>{
    @Autowired
    private WorkingGroupMapper WorkingGroupMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = WorkingGroupEO.class)
    public boolean save(WorkingGroupEO entity) throws BusinessException {
        entity.setStatus(0L);
        return super.save(entity);
    }

    public boolean updateStatusById(Long[] ids,int status){
        for(Long id : ids){
            this.baseMapper.updateStatusById(status,id);
        }
        return true;
    }

    public int removeWorkingGroupEmployeeByIds(Map map){
        return WorkingGroupMapper.removeWorkingGroupEmployeeByIds(map);
    }


    public int addWorkingGroupEmployeeByIds(Map map){
        Integer workingGroupId = (Integer)map.get("workingGroupId");
        List<Integer> employeeIds = (List<Integer>)map.get("employeeIds");
        for(Integer employeeId : employeeIds){
            WorkingGroupMapper.addWorkingGroupEmployeeByIds(workingGroupId,employeeId);
        }
        return employeeIds.size();
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        boolean result = super.removeByIds(idList);
        if(result==true){
            //删除人员
            for(Serializable id:idList){
                Long workingGroupId = (Long)id;
                WorkingGroupMapper.deleteWorkingGroupEmployeeByIWworkingGroupId(workingGroupId);
            }
        }
        return result;
    }
}
