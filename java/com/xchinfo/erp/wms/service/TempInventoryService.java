package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.scm.wms.entity.TempInventoryEO;
import com.xchinfo.erp.wms.mapper.TempInventoryMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;

import java.util.List;

@Service
public class TempInventoryService extends BaseServiceImpl<TempInventoryMapper, TempInventoryEO> {

    public IPage<TempInventoryEO> selectTempDetailPage(Criteria criteria) {

        return this.selectPage(criteria);
    }

    public List<TempInventoryEO> getByInventoryId(Long inventoryId) {
        return this.baseMapper.getByInventoryId(inventoryId);
    }
}
