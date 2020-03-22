package com.xchinfo.erp.srm.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.mes.service.MaterialPlanTempService;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.srm.mapper.ProductOrderMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.*;

/**
 * @author zhongy
 * @date 2019/9/2
 */
@Service
public class ProductOrderTempService extends BaseServiceImpl<ProductOrderMapper, ProductOrderEO> {
    public List<ProductOrderEO> getBySerialIds(String sqlStr) {
        return this.baseMapper.getBySerialIds(sqlStr);
    }
}
