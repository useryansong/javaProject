package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.scm.wms.entity.*;
import com.xchinfo.erp.wms.mapper.*;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yecat.core.utils.DateUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yuanchang
 * @date 2019/5/6
 * @update
 */
@Service
public class InventoryDetailService extends BaseServiceImpl<InventoryDetailMapper, InventoryDetailEO> {

    @Autowired
    private ReceiveOrderDetailMapper receiveOrderDetailMapper;

    @Autowired
    private DeliveryOrderDetailMapper deliveryOrderDetailMapper;

    @Autowired
    private StockAccountMapper stockAccountMapper;


    public void saveBatch(InventoryDetailEO[] entitys) {
        if(entitys!=null && entitys.length>0){

            for(int i=0; i<entitys.length; i++){
                Double amount = this.baseMapper.getMaterialCount(entitys[i].getMaterialId(),entitys[i].getWarehouseId(),entitys[i].getWarehouseLocationId());
                if ( amount == null ){
                    amount = 0d;
                }
                entitys[i].setAmount(amount);
                super.save(entitys[i]);
            }
        }
    }


    public boolean insertAllById(Long id,String warehouseType,String materialName) {

        List<MaterialEO> materials = this.baseMapper.selectMaterialByInventory(id,warehouseType,materialName);
        if(materials!=null && materials.size()>0){
            for(MaterialEO material : materials){
                InventoryDetailEO inventoryDetail = new InventoryDetailEO();

                inventoryDetail.setInventoryId(Long.valueOf(id));
                inventoryDetail.setMaterialId(material.getMaterialId());
                inventoryDetail.setMaterialCode(material.getMaterialCode());
                inventoryDetail.setMaterialName(material.getMaterialName());
                inventoryDetail.setElementNo(material.getElementNo());
                inventoryDetail.setSpecification(material.getSpecification());
                inventoryDetail.setFigureNumber(material.getFigureNumber());
                inventoryDetail.setFigureVersion(material.getFigureVersion());
                inventoryDetail.setInventoryCode(material.getInventoryCode());
                inventoryDetail.setInventoryType(1);
                inventoryDetail.setUnitId(material.getFirstMeasurementUnit());
                inventoryDetail.setWarehouseId(material.getMainWarehouseId());
                inventoryDetail.setWarehouseLocationId(material.getWarehouseLocationId());
                inventoryDetail.setStatus(0);

                Double amount = this.baseMapper.getMaterialCount(material.getMaterialId(),material.getMainWarehouseId(),material.getWarehouseLocationId());
                if ( amount == null ){
                    amount = 0.00;
                }
                inventoryDetail.setAmount(amount);

                super.save(inventoryDetail);
            }
        }
        return true;
    }

    //待加入调节表分页查找
    public IPage<InventoryDetailEO> waitingJoinPage(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<InventoryDetailEO> wrapper = new QueryWrapper<InventoryDetailEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<InventoryDetailEO> totalList = this.baseMapper.waitingJoinPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<InventoryDetailEO> list = this.baseMapper.waitingJoinPage(param);

        IPage<InventoryDetailEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    //待调节物料信息分页查找
    public IPage<AdjustInventoryEO> waitingAdjustPage(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<AdjustInventoryEO> wrapper = new QueryWrapper<AdjustInventoryEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<AdjustInventoryEO> totalList = this.baseMapper.waitingAdjustPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<AdjustInventoryEO> list = this.baseMapper.waitingAdjustPage(param);

        IPage<AdjustInventoryEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    //已调节物料信息分页查找
    public IPage<AdjustInventoryEO> doneAdjustPage(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<AdjustInventoryEO> wrapper = new QueryWrapper<AdjustInventoryEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<AdjustInventoryEO> totalList = this.baseMapper.doneAdjustPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<AdjustInventoryEO> list = this.baseMapper.doneAdjustPage(param);

        IPage<AdjustInventoryEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }


    public List<InventoryDetailEO> listexportDateByInventory(Long inventoryId) {
        return this.baseMapper.listexportDateByInventory(inventoryId);
    }

    public List<InventoryDetailEO> getByInventoryId(Long inventoryId) {
        List<InventoryDetailEO> inventoryDetails = this.baseMapper.getByInventoryId(inventoryId);
        if(inventoryDetails!=null && inventoryDetails.size()>0) {
//            String sqlStr = "";
            List<Long> materialIds = new ArrayList<>();
            for(InventoryDetailEO inventoryDetail : inventoryDetails) {
                materialIds.add(inventoryDetail.getMaterialId());
//                sqlStr += (inventoryDetail.getMaterialId() + ",");
            }
//            if(sqlStr.equals("")) {
//                sqlStr = "(-1)";
//            } else {
//                sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
//            }

            if(materialIds!=null && materialIds.size()>0) {
                materialIds.add(Long.valueOf(-1));
            }

            String inventoryMonth = inventoryDetails.get(0).getInventoryMonth();
            String preInventoryMonth = DateUtils.format(DateUtils.addDateMonths(DateUtils.stringToDate(inventoryMonth, "yyyy-MM"), -1), "yyyy-MM");
            // 获取本月期初账上结余量(即上月账上结余量)
            List<StockAccountEO> preStockAccounts = this.stockAccountMapper.getByMaterialIdsAndMonth(materialIds, preInventoryMonth.substring(0,4) + preInventoryMonth.substring(5,7));
            // 获取本月期末账上结余量(即本月账上结余量)
            List<StockAccountEO> currentStockAccounts = this.stockAccountMapper.getByMaterialIdsAndMonth(materialIds, inventoryMonth.substring(0,4) + inventoryMonth.substring(5,7));
            // 获取本月入库量
            List<ReceiveOrderDetailEO> receiveOrderDetails = this.receiveOrderDetailMapper.getByMaterialIdsAndReceiveDate(materialIds, inventoryMonth);
            // 获取本月出库量
            List<DeliveryOrderDetailEO> deliveryOrderDetails = this.deliveryOrderDetailMapper.getByMaterialIdsAndDeliveryDate(materialIds, inventoryMonth);

            for(InventoryDetailEO inventoryDetail : inventoryDetails) {
                if(currentStockAccounts!=null && currentStockAccounts.size()>0) {
                    for(StockAccountEO currentStockAccount : currentStockAccounts) {
                        if(currentStockAccount.getMaterialId().longValue() == inventoryDetail.getMaterialId().longValue()) {
                            inventoryDetail.setCurrentCount(currentStockAccount.getCount());
                        }
                    }
                }

                if(preStockAccounts!=null && preStockAccounts.size()>0) {
                    for(StockAccountEO preStockAccount : preStockAccounts) {
                        if(preStockAccount.getMaterialId().longValue() == inventoryDetail.getMaterialId().longValue()) {
                            inventoryDetail.setPreCount(preStockAccount.getCount());
                        }
                    }
                }


                if(deliveryOrderDetails!=null && deliveryOrderDetails.size()>0) {
                    for(DeliveryOrderDetailEO deliveryOrderDetail : deliveryOrderDetails) {
                        if(deliveryOrderDetail.getMaterialId().longValue() == inventoryDetail.getMaterialId().longValue()) {
                            inventoryDetail.setSumDeliveryAmount(deliveryOrderDetail.getSumRelDeliveryAmount());
                        }
                    }
                }

                if(receiveOrderDetails!=null && receiveOrderDetails.size()>0) {
                    for(ReceiveOrderDetailEO receiveOrderDetail : receiveOrderDetails) {
                        if(receiveOrderDetail.getMaterialId().longValue() == inventoryDetail.getMaterialId().longValue()) {
                            inventoryDetail.setSumReceiveAmount(receiveOrderDetail.getSumRelReceiveAmount());
                        }
                    }
                }

            }
        }

        return inventoryDetails;
    }
}

