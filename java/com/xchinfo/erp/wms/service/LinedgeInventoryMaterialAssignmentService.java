package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryDetailEO;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryMaterialAssignmentEO;
import com.xchinfo.erp.wms.mapper.LinedgeInventoryDetailMapper;
import com.xchinfo.erp.wms.mapper.LinedgeInventoryMaterialAssignmentMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class LinedgeInventoryMaterialAssignmentService extends BaseServiceImpl<LinedgeInventoryMaterialAssignmentMapper, LinedgeInventoryMaterialAssignmentEO> {

    //查找所有盘点物料和分解物料
    public IPage<LinedgeInventoryMaterialAssignmentEO> selectAllPage(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<LinedgeInventoryMaterialAssignmentEO> wrapper = new QueryWrapper<LinedgeInventoryMaterialAssignmentEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<LinedgeInventoryMaterialAssignmentEO> totalList = this.baseMapper.checkPbomMaterialPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<LinedgeInventoryMaterialAssignmentEO> list = this.baseMapper.checkPbomMaterialPage(param);

        IPage<LinedgeInventoryMaterialAssignmentEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    public List<LinedgeInventoryMaterialAssignmentEO> listexportDateByInventory(Long inventoryId) {
        return this.baseMapper.listexportDateByInventory(inventoryId);
    }

    public List<LinedgeInventoryMaterialAssignmentEO> listexportDateByInventoryAll(Long inventoryId) {
        return this.baseMapper.listexportDateByInventoryAll(inventoryId);
    }

    public List<LinedgeInventoryMaterialAssignmentEO> getList(Long linedgeInventoryId, Long inventoryLinedgeDetailId, Long childMaterialId, Integer status) {
        return this.baseMapper.getList(linedgeInventoryId, inventoryLinedgeDetailId, childMaterialId, status);
    }
}
