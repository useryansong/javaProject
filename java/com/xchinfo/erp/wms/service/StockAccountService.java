package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.wms.mapper.StockAccountMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yecat.core.exception.BusinessException;
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
public class StockAccountService extends BaseServiceImpl<StockAccountMapper, StockAccountEO> {



    public IPage<StockAccountEO> selectPageByView(Criteria criteria) {


        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<StockAccountEO> wrapper = new QueryWrapper<StockAccountEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<StockAccountEO> totalList = this.baseMapper.selectPageByViewMode(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<StockAccountEO> list = this.baseMapper.selectPageByViewMode(param);

        IPage<StockAccountEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;
    }


    public IPage<StockAccountEO> selectDetailPage(Criteria criteria){
        return this.selectPage(criteria);
    }


    public StockAccountEO countNumById(Long id) throws BusinessException {
        return this.baseMapper.countNumById(id);
    }


    public StockAccountEO getByMaterialIdAndWarehouseId(Long materialId, Long warehouseId) throws BusinessException {
        return this.baseMapper.getByMaterialIdAndWarehouseId(materialId, warehouseId);
    }

    //按库位查找物料库存分页查找
    public IPage<StockAccountEO> checkStockByLocation(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<StockAccountEO> wrapper = new QueryWrapper<StockAccountEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<StockAccountEO> totalList = this.baseMapper.checkStockByLocation(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<StockAccountEO> list = this.baseMapper.checkStockByLocation(param);

        IPage<StockAccountEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    //按库位查找物料库存分页查找
    public IPage<StockAccountEO> locationPage(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<StockAccountEO> wrapper = new QueryWrapper<StockAccountEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<StockAccountEO> totalList = this.baseMapper.locationPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<StockAccountEO> list = this.baseMapper.locationPage(param);

        IPage<StockAccountEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    public void addBatch(List<StockAccountEO> stockAccounts) {
        this.baseMapper.addBatch(stockAccounts);
    }

    public void deleteByIds(String deleteSql) {
        this.baseMapper.deleteByIds(deleteSql);
    }

    public void trySynchro(){

        this.baseMapper.insertSynchro();

        this.baseMapper.updateSynchro();
    }
}
