package com.xchinfo.erp.wms.service;

import com.xchinfo.erp.scm.wms.entity.AllocationEO;
import com.xchinfo.erp.wms.mapper.AllocationDetailMapper;
import com.xchinfo.erp.wms.mapper.AllocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class AllocationService extends BaseServiceImpl<AllocationMapper, AllocationEO> {

    @Autowired
    private AllocationDetailMapper allocationDetailMapper;
}
