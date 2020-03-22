package com.xchinfo.erp.hrms.service;

import com.xchinfo.erp.hrms.entity.EmployeeExperienceEO;
import com.xchinfo.erp.hrms.mapper.EmployeeExperienceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.li
 * @date 2018/12/14
 * @update
 */
@Service
public class EmployeeExperienceService extends BaseServiceImpl<EmployeeExperienceMapper, EmployeeExperienceEO>{

    public List<EmployeeExperienceEO> getByEmployee(Long employeeId) {
        return this.baseMapper.getByEmployee(employeeId);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean removeByEmployee(Long employeeId) {
        return retBool(this.baseMapper.deleteByEmployee(employeeId));
    }
}
