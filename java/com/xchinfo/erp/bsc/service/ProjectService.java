package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.ProjectEO;
import com.xchinfo.erp.bsc.mapper.ProjectMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
@Service
public class ProjectService extends BaseServiceImpl<ProjectMapper, ProjectEO> {
    public boolean updateStatusById(Long[] ids,int status){
        for(Long id : ids){
            this.baseMapper.updateStatusById(status,id);
        }
        return true;
    }
}
