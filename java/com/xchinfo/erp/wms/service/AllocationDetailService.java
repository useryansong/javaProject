package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.scm.wms.entity.AllocationDetailEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.wms.mapper.AllocationDetailMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author yuanchang
 * @date 2019/5/9
 * @update
 */
@Service
public class AllocationDetailService extends BaseServiceImpl<AllocationDetailMapper, AllocationDetailEO> {


    @Autowired
    private StockAccountService stockAccountService;

    @Autowired
    private OrgService orgService;

    @Override
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(AllocationDetailEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        return super.save(entity);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AllocationDetailEO entity) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }


        return  super.updateById(entity);

    }

    @Override
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = AllocationDetailEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        // 删除关系
        for (Serializable id : idList) {
            AllocationDetailEO allocationDetailEO = this.baseMapper.selectById(id);
            //校验机构权限
            if (!checkPer(allocationDetailEO.getOrgId(),userId)) {
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }


            super.removeById(id);
        }

        return true;
    }


    public boolean updateStatusById(Long[] idList, Integer status)throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        for(Long id:idList) {

            AllocationDetailEO allocationDetailEOs = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(allocationDetailEOs.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }
            if (status == 1) {
                List<StockAccountEO> messages = this.baseMapper.getMessage(id);
                for (StockAccountEO message : messages) {
                    if (message.getCount() - message.getAmount() < 0) {
                        throw new BusinessException("调拨失败，物料【" + message.getMaterialName() +
                                "】在【" + message.getWarehouseName() + "】【" + message.getLocationName() + "】库存不足，调拨数量:" +
                                message.getAmount() + "库存数量:" + message.getCount() + ",请确认");
                    }
                }

                AllocationDetailEO allocationDetailEO = this.baseMapper.selectById(id);
                StockAccountEO accountEO = new StockAccountEO();

                accountEO.setVoucherId(id);
                accountEO.setVoucherDate(allocationDetailEO.getAllocationDate());
                accountEO.setVoucherType(5);
                accountEO.setMaterialId(allocationDetailEO.getMaterialId());
                accountEO.setMaterialCode(allocationDetailEO.getMaterialCode());
                accountEO.setMaterialName(allocationDetailEO.getMaterialName());
                accountEO.setInventoryCode(allocationDetailEO.getInventoryCode());
                accountEO.setElementNo(allocationDetailEO.getElementNo());
                accountEO.setSpecification(allocationDetailEO.getSpecification());
                accountEO.setUnitId(allocationDetailEO.getUnitId());
                accountEO.setFigureNumber(allocationDetailEO.getFigureNumber());
                accountEO.setFigureVersion(allocationDetailEO.getFigureVersion());
                accountEO.setWarehouseId(allocationDetailEO.getToWarehouseId());
                accountEO.setWarehouseLocationId(allocationDetailEO.getToWarehouseLocationId());
                accountEO.setAmount(allocationDetailEO.getAmount());
                accountEO.setRemarks(allocationDetailEO.getRemarks());

                this.stockAccountService.save(accountEO);
                accountEO.setVoucherType(11);
                accountEO.setWarehouseId(allocationDetailEO.getFromWarehouseId());
                accountEO.setWarehouseLocationId(allocationDetailEO.getFromWarehouseLocationId());
                this.stockAccountService.save(accountEO);

            } else {
                //删除台账
                this.baseMapper.deleteStockById(id);
            }
            //更新状态
            this.baseMapper.updateStatusById(id, status);
        }
        return true;
    }



    public IPage<StockAccountEO> getMaterialPage(Criteria criteria) {

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
        List<StockAccountEO> totalList = this.baseMapper.getMaterialPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<StockAccountEO> list = this.baseMapper.getMaterialPage(param);

        IPage<StockAccountEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }


    public IPage<StockAccountEO> getDeliveryMaterialPage(Criteria criteria) {

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
        List<StockAccountEO> totalList = this.baseMapper.getDeliveryMaterialPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<StockAccountEO> list = this.baseMapper.getDeliveryMaterialPage(param);

        IPage<StockAccountEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    public IPage<StockAccountEO> getDeliveryProductMaterialPage(Criteria criteria) {

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
        List<StockAccountEO> totalList = this.baseMapper.getDeliveryProductMaterialPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<StockAccountEO> list = this.baseMapper.getDeliveryProductMaterialPage(param);

        IPage<StockAccountEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    public List<WarehouseEO> listWarehouse(Long id) {
        return this.baseMapper.listWarehouse(id);
    }


    public List<WarehouseLocationEO> listWarehouseLocation(Long id,Long warehouseId) {
        return this.baseMapper.listWarehouseLocation(id, warehouseId);
    }


    public int getAmount(Long id, Long warehouseId, Long locationId) {
        return this.baseMapper.getAmount(id, warehouseId ,locationId);
    }


    public List<WarehouseLocationEO> getToWarehouseLocation(Long warehouseId) {
        return this.baseMapper.getToWarehouseLocation(warehouseId);
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }
}
