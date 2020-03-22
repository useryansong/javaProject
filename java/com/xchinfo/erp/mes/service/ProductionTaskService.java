package com.xchinfo.erp.mes.service;

import com.xchinfo.erp.mes.entity.ProductionTaskEO;
import com.xchinfo.erp.mes.entity.ProductionTaskTreeEO;
import com.xchinfo.erp.mes.mapper.ProductionTaskMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.TreeUtils;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class ProductionTaskService extends BaseServiceImpl<ProductionTaskMapper, ProductionTaskEO> {

    public List<ProductionTaskEO> listForDate(String date) {
        return this.baseMapper.selectForDate(date);
    }

    public List<ProductionTaskEO> listForOrder(Long orderId) {
        return this.baseMapper.selectForOrder(orderId);
    }

    public List<ProductionTaskTreeEO> listForTree() {
        List<ProductionTaskTreeEO> tasks = this.baseMapper.selectForTree();

        return TreeUtils.build(tasks, Long.valueOf(0));
    }
}
