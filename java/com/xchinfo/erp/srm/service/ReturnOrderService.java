package com.xchinfo.erp.srm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.service.WarehouseLocationService;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.srm.mapper.ReturnOrderMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.wms.mapper.DeliveryOrderMapper;
import com.xchinfo.erp.wms.service.DeliveryOrderDetailService;
import com.xchinfo.erp.wms.service.DeliveryOrderService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.io.Serializable;
import java.util.*;

/**
 * @author zhongye
 * @date 2019/5/24
 */
 @Service
public class ReturnOrderService extends BaseServiceImpl<ReturnOrderMapper, ReturnOrderEO> {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private ReturnOrderDetailService returnOrderDetailService;

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private DeliveryOrderDetailService deliveryOrderDetailService;

    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    @Autowired
    private OrgService orgService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    static Set<Long> procedureLock =new HashSet<>();/** 防止页面发送多条相同的请求导致重复入库，新增一个程序锁 */

    public List<ReturnOrderEO> getPage(Map map) {
        return this.baseMapper.getPage(map);
    }


    public ReturnOrderEO saveEntity(ReturnOrderEO entity, UserEO user) throws BusinessException {
//        this.orgService.checkUserPermissions(entity.getOrgId(), user.getUserId(), "退货单的归属机构权限不存在该用户权限,请确认!");

        // 生成业务编码
        String voucherNo = this.businessCodeGenerator.generateNextCode("srm_return_order", entity,user.getOrgId());
        AssertUtils.isBlank(voucherNo);
        entity.setVoucherNo(voucherNo);

        entity.setPlanReturnQuantity(Double.valueOf(0));
        entity.setActualReturnQuantity(Double.valueOf(0));
        entity.setStatus(0);
        entity.setChargeUserId(user.getUserId());
        entity.setChargeUserName(user.getUserName());
        entity.setCreatedTime(new Date());
        super.save(entity);

        return entity;
    }


    public boolean removeByIds(Long[] returnOrderIds, Long userId) throws BusinessException {
        if(returnOrderIds==null || returnOrderIds.length==0) {
            throw new BusinessException("请选择数据!");
        }

        // 判断用户是否拥有退货单的归属机构权限
        String alertMsg = "";
        for(Long returnOrderId : returnOrderIds) {
            ReturnOrderEO returnOrder = super.getById(returnOrderId);
            Boolean flag = this.orgService.checkUserPermissions(returnOrder.getOrgId(), userId);
            if(!flag.booleanValue()) {
                alertMsg += ("退货单【" + returnOrder.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(alertMsg)) {
            throw new BusinessException(alertMsg);
        }

        for(Long returnOrderId : returnOrderIds) {
            this.returnOrderDetailService.removeByReturnOrderId(returnOrderId);
        }
        return super.removeByIds(returnOrderIds);
    }


    public void releaseByIds(Long[] returnOrderIds, Long userId) throws BusinessException  {
        if(returnOrderIds==null || returnOrderIds.length==0) {
            throw new BusinessException("请选择数据!");
        }

        // 判断用户是否拥有退货单的归属机构权限
        String alertMsg = "";
        for(Long returnOrderId : returnOrderIds) {
            ReturnOrderEO returnOrder = super.getById(returnOrderId);
            Boolean flag = this.orgService.checkUserPermissions(returnOrder.getOrgId(), userId);
            if(!flag.booleanValue()) {
                alertMsg += ("退货单【" + returnOrder.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(alertMsg)) {
            throw new BusinessException(alertMsg);
        }

        if(returnOrderIds!=null && returnOrderIds.length>0){
            for(int i=0; i<returnOrderIds.length; i++){
                this.baseMapper.updateStatusById(returnOrderIds[i], 1);
            }
        }
    }


    public void cancelReleaseByIds(Long[] returnOrderIds, Long userId) throws BusinessException  {
        if(returnOrderIds==null || returnOrderIds.length==0) {
            throw new BusinessException("请选择数据!");
        }

        // 判断用户是否拥有退货单的归属机构权限
        String alertMsg = "";
        for(Long returnOrderId : returnOrderIds) {
            ReturnOrderEO returnOrder = super.getById(returnOrderId);
            Boolean flag = this.orgService.checkUserPermissions(returnOrder.getOrgId(), userId);
            if(!flag.booleanValue()) {
                alertMsg += ("退货单【" + returnOrder.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(alertMsg)) {
            throw new BusinessException(alertMsg);
        }

        if(returnOrderIds!=null && returnOrderIds.length>0){
            for(int i=0; i<returnOrderIds.length; i++){
                this.baseMapper.updateStatusById(returnOrderIds[i], 0);
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ReturnOrderEO returnOrder, Long userId) throws BusinessException {
        this.orgService.checkUserPermissions(returnOrder.getOrgId(), userId, "退货单的归属机构权限不存在该用户权限,请确认!");

        ReturnOrderEO returnOrderFromDb = super.getById(returnOrder.getReturnOrderId());
        if(returnOrderFromDb == null) {
            throw new BusinessException("数据已不存在!");
        }

        // 如果归属机构被修改,删掉该退货单下的明细,需要重新添加明细
        if(returnOrder.getOrgId().longValue() != returnOrderFromDb.getOrgId().longValue()) {
            this.returnOrderDetailService.removeByReturnOrderId(returnOrder.getReturnOrderId());
        }

        return super.updateById(returnOrder);
    }


    public void updateStatusByPrint(ReturnOrderEO returnOrder, Long userId) {
        this.orgService.checkUserPermissions(returnOrder.getOrgId(), userId, "退货单的归属机构权限不存在该用户权限，请确认!");

        this.baseMapper.updateStatusById(returnOrder.getReturnOrderId(), 2);
    }


    public void checkExport(ReturnOrderEO[] returnOrders, Long userId) throws BusinessException {
        if(returnOrders==null || returnOrders.length==0) {
            throw new BusinessException("无数据!");
        }

        // 判断用户是否拥有退货单的归属机构权限
        String alertMsg = "";
        for(ReturnOrderEO returnOrder : returnOrders) {
            Boolean flag = this.orgService.checkUserPermissions(returnOrder.getOrgId(), userId);
            if(!flag.booleanValue()) {
                alertMsg += ("退货单【" + returnOrder.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(alertMsg)) {
            throw new BusinessException(alertMsg);
        }
    }

    @Override
    public ReturnOrderEO getById(Serializable returnOrderId) {
        ReturnOrderEO returnOrder = super.getById(returnOrderId);
        List<ReturnOrderDetailEO> returnOrderDetails = this.returnOrderDetailService.getByReturnOrderId((Long) returnOrderId);
        returnOrder.setReturnOrderDetails(returnOrderDetails);
        return returnOrder;
    }


    public void confirm(Long[] ids, UserEO user) throws BusinessException {
        if(ids==null || ids.length==0){
            throw new BusinessException("请选择数据!");
        }

        for(Long id : ids){
            ReturnOrderEO returnOrder = super.getById(id);
            returnOrder.setChargeUserId(user.getUserId());
            returnOrder.setChargeUserName(user.getUserName());
            returnOrder.setStatus(3);
            super.updateById(returnOrder);
        }
    }



    public ReturnOrderEO getDetailInfoByNo(String voucherNo) throws BusinessException {
        ReturnOrderEO orderEO = this.baseMapper.getDetailInfoByNo(voucherNo);
        //校验机构权限
        if(null != orderEO) {
            UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
            String userName = user.getUserName();
            Long userId = user.getUserId();

            if (!checkPer(orderEO.getOrgId(),userId)) {
                throw new BusinessException("退货单的归属机构下的数据该用户没有操作权限，请确认！");
            }
        }else{
            throw new BusinessException("单据号不存在，请确认！");
        }

        List<ReturnOrderDetailEO> details = this.baseMapper.getReturnInfoById(orderEO.getReturnOrderId());
        orderEO.setReturnOrderDetails(details);
        return orderEO;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean returnOne(Long Id, Double amount, Long userId, String userName, String action) throws BusinessException {
        try {
            if(procedureLock.contains(Id)){

                throw  new BusinessException("当前数据正在操作中，无法操作请刷新后重试");
            }else{
                procedureLock.add(Id);
                UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
                ReturnOrderDetailEO detailEO = this.baseMapper.selectReturnOrderDetailInfo(Id);
                //判断状态
                if("add".equals(action) && detailEO.getStatus() != 0){
                    throw new BusinessException("退货单明细状态非未完成状态，请刷新确认");
                }else if("remove".equals(action) && detailEO.getStatus() != 1){
                    throw new BusinessException("退货单明细状态非已完成状态，请刷新确认");
                }

                if("add".equals(action)) {
                    detailEO.setStatus(1);
                    detailEO.setActualReturnQuantity(amount);
                    detailEO.setDeliveryTime(new Date());
                }else{
                    detailEO.setStatus(0);
                    detailEO.setActualReturnQuantity(0d);
                    detailEO.setDeliveryTime(null);
                    this.baseMapper.updateDetailDateNull(detailEO.getReturnOrderDetailId());
                }

                this.returnOrderDetailService.updateById(detailEO);

                //判断出库明细是否都已完成
                ReturnOrderEO returnOrderEO = this.baseMapper.selectById(detailEO.getReturnOrderId());

                //判断出库单是否存在
                DeliveryOrderEO deliveryOrderEO = this.baseMapper.selectDeliveryOrderCount(returnOrderEO.getReturnOrderId());
                if(null == deliveryOrderEO){
                    DeliveryOrderEO deliveryOrderTemp = new DeliveryOrderEO();
                    deliveryOrderTemp.setRelationId(returnOrderEO.getReturnOrderId());
                    deliveryOrderTemp.setDeliveryDate(new Date());
                    deliveryOrderTemp.setDeliveryType(5);
                    deliveryOrderTemp.setOrgId(returnOrderEO.getOrgId());
                    deliveryOrderTemp.setStatus(1);

                    // 生成业务编码
                    String code = this.businessCodeGenerator.generateNextCode("wms_delivery_order", deliveryOrderTemp,user.getOrgId());
                    AssertUtils.isBlank(code);
                    deliveryOrderTemp.setVoucherNo(code);
                    this.deliveryOrderService.saveOrUpdate(deliveryOrderTemp);
                    deliveryOrderEO = deliveryOrderTemp;
                }

                //判断出库明细是否存在
                DeliveryOrderDetailEO deliveryOrderDetailEO = this.baseMapper.selectDeliveryOrderDetailCount(detailEO.getReturnOrderDetailId(),deliveryOrderEO.getDeliveryId());
                if (null == deliveryOrderDetailEO){
                    DeliveryOrderDetailEO deliveryOrderDetailTemp = new DeliveryOrderDetailEO();
                    deliveryOrderDetailTemp.setDeliveryOrderId(deliveryOrderEO.getDeliveryId());
                    deliveryOrderDetailTemp.setOrderId(detailEO.getReturnOrderDetailId());
                    deliveryOrderDetailTemp.setMaterialId(detailEO.getMaterialId());
                    deliveryOrderDetailTemp.setMaterialCode(detailEO.getMaterialCode());
                    deliveryOrderDetailTemp.setMaterialName(detailEO.getMaterialName());
                    deliveryOrderDetailTemp.setInventoryCode(detailEO.getInventoryCode());
                    deliveryOrderDetailTemp.setElementNo(detailEO.getElementNo());

                    deliveryOrderDetailTemp.setFigureNumber(detailEO.getFigureNumber());
                    deliveryOrderDetailTemp.setFigureVersion(detailEO.getFigureVersion());
                    deliveryOrderDetailTemp.setWarehouseId(detailEO.getWarehouseId());
        //            deliveryOrderDetailTemp.setWarehouseLocationId(detailEO.getWarehouseLocationId());
                    deliveryOrderDetailTemp.setUnitId(detailEO.getUnitId());
                    deliveryOrderDetailTemp.setDeliveryAmount(detailEO.getPlanReturnQuantity());
                    deliveryOrderDetailTemp.setRelDeliveryAmount(amount);
                    deliveryOrderDetailTemp.setStatus(2);

                    this.deliveryOrderDetailService.save(deliveryOrderDetailTemp);
                    deliveryOrderDetailEO = deliveryOrderDetailTemp;
                }

                //用于设置库存子类型
                if(returnOrderEO.getType() == 1){
                    deliveryOrderEO.setType(4);
                }else if (returnOrderEO.getType() == 2){
                    deliveryOrderEO.setType(5);
                }

                //已存在则更新状态
                if("add".equals(action)) {
                    deliveryOrderDetailEO.setStatus(2);
                    deliveryOrderDetailEO.setRelDeliveryAmount(amount);
                    //新增库存
                    this.deliveryOrderService.setStock(deliveryOrderDetailEO,deliveryOrderEO);
                }else{
                    deliveryOrderDetailEO.setStatus(1);
                    deliveryOrderDetailEO.setRelDeliveryAmount(0d);
                    //删除库存
                    this.deliveryOrderService.deleteStockByDetailId(deliveryOrderDetailEO.getDeliveryDetailId(),12);
                }

                this.deliveryOrderDetailService.updateById(deliveryOrderDetailEO);

                Integer finishDeliveryOrderDetailCount = this.baseMapper.selectDeliveryOrderDetailFinishCount(deliveryOrderEO.getDeliveryId());
                if(finishDeliveryOrderDetailCount > 0){
                    deliveryOrderEO.setStatus(1);
                    deliveryOrderEO.setDeliveryUserId("");
                    deliveryOrderEO.setDeliveryUserName("");
                }else {
                    deliveryOrderEO.setDeliveryUserId(userId+"");
                    deliveryOrderEO.setDeliveryUserName(userName);
                    deliveryOrderEO.setStatus(2);
                }
                this.deliveryOrderMapper.updateById(deliveryOrderEO);



                if("add".equals(action) && returnOrderEO.getStatus() != 2){
                    throw new BusinessException("退货单状态为非退货中、不能进行退货出库操作,请确认!");
                }

                if (!checkPer(returnOrderEO.getOrgId(),userId)) {
                    throw new BusinessException("退货单的归属机构下的数据该用户没有操作权限，请确认！");
                }

                if("add".equals(action)) {
                    Double total = nvl(returnOrderEO.getActualReturnQuantity()) + amount;
                    returnOrderEO.setActualReturnQuantity(total);
                }else{
                    Double total = nvl(returnOrderEO.getActualReturnQuantity()) - amount;
                    returnOrderEO.setActualReturnQuantity(total);
                }

                Integer finishCount = this.baseMapper.selectDetailFinishCount(returnOrderEO.getReturnOrderId());
                List<ReturnOrderDetailEO> returnOrderDetails = this.returnOrderDetailService.getByReturnOrderId(returnOrderEO.getReturnOrderId());
                if(finishCount > 0){
                    if(finishCount < returnOrderDetails.size()) { // 部分退货，退货中
                        returnOrderEO.setStatus(2);
                    } else if (finishCount == returnOrderDetails.size()) { // 全都未退货，待退货
                        returnOrderEO.setStatus(1);
                    }
                    returnOrderEO.setReturnUserId(null);
                    returnOrderEO.setReturnUserName("");
                    returnOrderEO.setActualReturnDate(null);
                    this.baseMapper.updateDateNull(returnOrderEO.getReturnOrderId());
                } else { // 全部退货，退货完成
                    returnOrderEO.setStatus(3);
                    returnOrderEO.setReturnUserId(userId);
                    returnOrderEO.setReturnUserName(userName);
                    returnOrderEO.setActualReturnDate(new Date());
                }
                this.baseMapper.updateById(returnOrderEO);
            }
        }catch (Exception e){
            throw e;
        }finally {
            procedureLock.remove(Id);
        }
        return true;
    }

    public Double nvl(Double value){
        if(null == value || "".equals(value)){
            return 0d;
        }

        return value;
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
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

    @Transactional(rollbackFor = Exception.class)
    public boolean returnOneByLocation(Long Id, Double amount, Long userId, String userName, String action,Long locationId,Long stockFinishFlag) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        ReturnOrderDetailEO detailEO = this.baseMapper.selectReturnOrderDetailInfo(Id);
        //判断状态
        if("add".equals(action) && detailEO.getStatus() != 0){
            throw new BusinessException("退货单明细状态非未完成状态，请刷新确认");
        }else if("remove".equals(action) && detailEO.getStatus() != 1){
            throw new BusinessException("退货单明细状态非已完成状态，请刷新确认");
        }

        if("add".equals(action)) {

            if (stockFinishFlag == 1){
                detailEO.setStatus(1);
            }

            Double sumStockCount = this.baseMapper.selectSumStockByReturnDetailId(Id);

            if(null != sumStockCount){
                detailEO.setActualReturnQuantity(amount+sumStockCount);
            }else{
                detailEO.setActualReturnQuantity(amount);
            }

            detailEO.setDeliveryTime(new Date());
        }else{
            detailEO.setStatus(0);
            detailEO.setActualReturnQuantity(0d);
            detailEO.setDeliveryTime(null);
            this.baseMapper.updateDetailDateNull(detailEO.getReturnOrderDetailId());
        }

        this.returnOrderDetailService.updateById(detailEO);

        //判断出库明细是否都已完成
        ReturnOrderEO returnOrderEO = this.baseMapper.selectById(detailEO.getReturnOrderId());

        //判断出库单是否存在
        DeliveryOrderEO deliveryOrderEO = this.baseMapper.selectDeliveryOrderCount(returnOrderEO.getReturnOrderId());
        if(null == deliveryOrderEO){
            DeliveryOrderEO deliveryOrderTemp = new DeliveryOrderEO();
            deliveryOrderTemp.setRelationId(returnOrderEO.getReturnOrderId());
            deliveryOrderTemp.setDeliveryDate(returnOrderEO.getPlanReturnDate());
            deliveryOrderTemp.setDeliveryType(5);
            deliveryOrderTemp.setOrgId(returnOrderEO.getOrgId());
            deliveryOrderTemp.setStatus(1);

            // 生成业务编码
            String code = this.businessCodeGenerator.generateNextCode("wms_delivery_order", deliveryOrderTemp,user.getOrgId());
            AssertUtils.isBlank(code);
            deliveryOrderTemp.setVoucherNo(code);
            this.deliveryOrderService.saveOrUpdate(deliveryOrderTemp);
            deliveryOrderEO = deliveryOrderTemp;
        }

        //判断出库明细是否存在
        DeliveryOrderDetailEO deliveryOrderDetailEO = this.baseMapper.selectDeliveryOrderDetailCount(detailEO.getReturnOrderDetailId(),deliveryOrderEO.getDeliveryId());
        if (null == deliveryOrderDetailEO){
            DeliveryOrderDetailEO deliveryOrderDetailTemp = new DeliveryOrderDetailEO();
            deliveryOrderDetailTemp.setDeliveryOrderId(deliveryOrderEO.getDeliveryId());
            deliveryOrderDetailTemp.setOrderId(detailEO.getReturnOrderDetailId());
            deliveryOrderDetailTemp.setMaterialId(detailEO.getMaterialId());
            deliveryOrderDetailTemp.setMaterialCode(detailEO.getMaterialCode());
            deliveryOrderDetailTemp.setMaterialName(detailEO.getMaterialName());
            deliveryOrderDetailTemp.setInventoryCode(detailEO.getInventoryCode());
            deliveryOrderDetailTemp.setElementNo(detailEO.getElementNo());

            deliveryOrderDetailTemp.setFigureNumber(detailEO.getFigureNumber());
            deliveryOrderDetailTemp.setFigureVersion(detailEO.getFigureVersion());
            deliveryOrderDetailTemp.setWarehouseId(detailEO.getWarehouseId());
//            deliveryOrderDetailTemp.setWarehouseLocationId(detailEO.getWarehouseLocationId());
            deliveryOrderDetailTemp.setUnitId(detailEO.getUnitId());
            deliveryOrderDetailTemp.setDeliveryAmount(detailEO.getPlanReturnQuantity());
            deliveryOrderDetailTemp.setRelDeliveryAmount(amount);
            deliveryOrderDetailTemp.setStatus(2);

            this.deliveryOrderDetailService.save(deliveryOrderDetailTemp);
            deliveryOrderDetailEO = deliveryOrderDetailTemp;
        }

        //用于设置库存子类型
        if(returnOrderEO.getType() == 1){
            deliveryOrderEO.setType(4);
        }else if (returnOrderEO.getType() == 2){
            deliveryOrderEO.setType(5);
        }

        //已存在则更新状态
        if("add".equals(action)) {
            if (stockFinishFlag == 1){
                deliveryOrderDetailEO.setStatus(2);
            }

            Double sumStockCount = this.baseMapper.selectSumStockCount(deliveryOrderDetailEO.getDeliveryDetailId());

            if(null != sumStockCount){
                deliveryOrderDetailEO.setRelDeliveryAmount(amount+sumStockCount);
            }else{
                deliveryOrderDetailEO.setRelDeliveryAmount(amount);
            }

            if(locationId==0){
                deliveryOrderDetailEO.setWarehouseLocationId(null);
            }else{
                deliveryOrderDetailEO.setWarehouseLocationId(locationId);
                WarehouseLocationEO locationEO = this.warehouseLocationService.getById(locationId);
                deliveryOrderDetailEO.setWarehouseId(locationEO.getWarehouseId());
            }
            //新增库存
            this.deliveryOrderService.setStockByLocation(deliveryOrderDetailEO,deliveryOrderEO,amount);
        }else{
            deliveryOrderDetailEO.setStatus(1);
            deliveryOrderDetailEO.setRelDeliveryAmount(0d);
            //删除库存
            this.deliveryOrderService.deleteStockByDetailId(deliveryOrderDetailEO.getDeliveryDetailId(),12);
        }

        this.deliveryOrderDetailService.updateById(deliveryOrderDetailEO);

        Integer finishDeliveryOrderDetailCount = this.baseMapper.selectDeliveryOrderDetailFinishCount(deliveryOrderEO.getDeliveryId());
        if(finishDeliveryOrderDetailCount > 0){
            deliveryOrderEO.setStatus(1);
            deliveryOrderEO.setDeliveryUserId("");
            deliveryOrderEO.setDeliveryUserName("");
        }else {
            deliveryOrderEO.setDeliveryUserId(userId+"");
            deliveryOrderEO.setDeliveryUserName(userName);
            deliveryOrderEO.setStatus(2);
        }
        this.deliveryOrderMapper.updateById(deliveryOrderEO);



        if("add".equals(action) && returnOrderEO.getStatus() != 2){
            throw new BusinessException("退货单状态为非退货中、不能进行退货出库操作,请确认!");
        }

        if (!checkPer(returnOrderEO.getOrgId(),userId)) {
            throw new BusinessException("退货单的归属机构下的数据该用户没有操作权限，请确认！");
        }

        if("add".equals(action)) {
            Double total = nvl(returnOrderEO.getActualReturnQuantity()) + amount;
            returnOrderEO.setActualReturnQuantity(total);
        }else{
            Double total = nvl(returnOrderEO.getActualReturnQuantity()) - amount;
            returnOrderEO.setActualReturnQuantity(total);
        }

        Integer finishCount = this.baseMapper.selectDetailFinishCount(returnOrderEO.getReturnOrderId());
        if(finishCount > 0){
            returnOrderEO.setStatus(2);
            returnOrderEO.setReturnUserId(null);
            returnOrderEO.setReturnUserName("");
            returnOrderEO.setActualReturnDate(null);
            this.baseMapper.updateDateNull(returnOrderEO.getReturnOrderId());
        }else {
            if (stockFinishFlag == 1 ){
                returnOrderEO.setStatus(3);
            }
            returnOrderEO.setReturnUserId(userId);
            returnOrderEO.setReturnUserName(userName);
            returnOrderEO.setActualReturnDate(new Date());

        }
        this.baseMapper.updateById(returnOrderEO);

        return true;
    }

    public List<ReturnOrderEO> getByIds(String sqlStr) {
        return this.baseMapper.getByIds(sqlStr);
    }

    public void updateErpVoucherNoByIds(String column, String erpVoucherNo, String sqlStr) {
        this.baseMapper.updateErpVoucherNoByIds(column, erpVoucherNo, sqlStr);
    }
}
