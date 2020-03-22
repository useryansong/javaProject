package com.xchinfo.erp.wms.service;

import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.wms.mapper.ReceiveOrderDetailMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author roman.c
 * @date 2019/4/18
 * @update
 */
@Service
public class ReceiveOrderDetailService extends BaseServiceImpl<ReceiveOrderDetailMapper, ReceiveOrderDetailEO> {

    public Integer countAll() {
        return this.baseMapper.countAll();
    }
}
