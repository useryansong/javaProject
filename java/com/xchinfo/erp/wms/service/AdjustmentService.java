package com.xchinfo.erp.wms.service;

import com.xchinfo.erp.scm.wms.entity.AdjustmentEO;
import com.xchinfo.erp.wms.mapper.AdjustmentDetailMapper;
import com.xchinfo.erp.wms.mapper.AdjustmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class AdjustmentService extends BaseServiceImpl<AdjustmentMapper, AdjustmentEO> {

    @Autowired
    private AdjustmentDetailMapper adjustmentDetailMapper;
}
