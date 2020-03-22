package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.MaterialReceiveEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.wms.mapper.DeliveryOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.DeliveryOrderMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.*;


/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class DeliveryOrderDetailService extends BaseServiceImpl<DeliveryOrderDetailMapper, DeliveryOrderDetailEO> {


    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    @Autowired
    private MaterialReceiveService materialReceiveService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;


    @Transactional(rollbackFor = Exception.class)
    public boolean removeByDelivery(Long deliveryId) {
        return retBool(this.baseMapper.deleteByDelivery(deliveryId));
    }


   /* private DeliveryOrderDetailEO setByMaterialId(Long materialId, DeliveryOrderDetailEO entity){
        MaterialEO material = this.materialService.getById(materialId);
        entity.setMaterialName(material.getMaterialName());
        entity.setMaterialCode(material.getMaterialCode());
        entity.setInventoryCode(material.getInventoryCode());
        entity.setElementNo(material.getElementNo());
        entity.setSpecification(material.getSpecification());
        entity.setFigureNumber(material.getFigureNumber());
        entity.setFigureVersion(material.getFigureVersion());
        entity.setWarehouseId(material.getMainWarehouseId());
        entity.setWarehouseLocationId(material.getWarehouseLocationId());
        entity.setUnitId(material.getFirstMeasurementUnit());

        return entity;
    }*/

    // 通过传递的参数获取数据库的实体
    /*private DeliveryOrderDetailEO getEntity(DeliveryOrderDetailEO entity){
        QueryWrapper<DeliveryOrderDetailEO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delivery_order_id", entity.getDeliveryOrderId());
        queryWrapper.eq("material_id", entity.getMaterialId());
        DeliveryOrderDetailEO deliveryOrderDetail = this.baseMapper.selectOne(queryWrapper);
        return deliveryOrderDetail!=null?deliveryOrderDetail:null;
    }*/

    @Override
    @EnableBusinessLog(BusinessLogType.CREATE)
    @Transactional(rollbackFor = Exception.class)
    public boolean save(DeliveryOrderDetailEO entity) throws BusinessException {
        /*DeliveryOrderDetailEO deliveryOrderDetail = getEntity(entity);
         *//*if(deliveryOrderDetail != null){
            throw new BusinessException("请不要重复添加数据!");
        }*/
        /*entity = this.setByMaterialId(entity.getMaterialId(), entity);*/
        super.save(entity);
        DeliveryOrderEO deliveryOrderEO = this.deliveryOrderMapper.selectById(entity.getDeliveryOrderId());
        //如果是生产领料的，还需要保存原材料领料表
        if (deliveryOrderEO.getVoucherType()==2 && deliveryOrderEO.getDeliveryType()==2 ){
            Date date = new Date();
            MaterialReceiveEO materialReceiveEO = new MaterialReceiveEO();

            materialReceiveEO.setStampingMaterialConsumptionQuotaId(entity.getStampingMaterialConsumptionQuotaId());

            MaterialReceiveEO temp = this.baseMapper.selectorigina(entity.getStampingMaterialConsumptionQuotaId());
            materialReceiveEO.setMaterialId(temp.getMaterialId());
            materialReceiveEO.setMaterialName(temp.getMaterialName());
            materialReceiveEO.setElementNo(temp.getElementNo());
            materialReceiveEO.setInventoryCode(temp.getInventoryCode());


            MaterialReceiveEO temp2 = this.baseMapper.selectstamping(entity.getStampingMaterialConsumptionQuotaId());
            if (temp2 != null){
                materialReceiveEO.setStampingMaterialId(temp2.getMaterialId());
                materialReceiveEO.setStampingMaterialName(temp2.getMaterialName());
                materialReceiveEO.setStampingElementNo(temp2.getElementNo());
                materialReceiveEO.setInventoryCoding(temp.getInventoryCode());
            }else{
                throw new BusinessException("对应物料不存在冲压件!");
            }



            materialReceiveEO.setDeliveryDetailId(entity.getDeliveryDetailId());
            materialReceiveEO.setDeliveryAmount(entity.getDeliveryAmount());
            materialReceiveEO.setDeliveryDate(date);
            this.materialReceiveService.save(materialReceiveEO);
        }
        return true;
    }

    // 判断所有数据是否为指定状态
    private boolean isWantedStatus(Long[] deliveryDetailIds, Integer status){
        if(deliveryDetailIds!=null && deliveryDetailIds.length>0){
            for(int i=1; i<deliveryDetailIds.length; i++){
                DeliveryOrderDetailEO deliveryOrderDetail = super.getById(deliveryDetailIds[i]);
                if(deliveryOrderDetail.getStatus().intValue() != status.intValue()){
                    return false;
                }
            }
        }else{
            throw new BusinessException("请先选择数据!");
        }
        return true;
    }


    public void updateStatusByIds(Long[] deliveryDetailIds, Integer status) throws BusinessException{
        if(deliveryDetailIds!=null && deliveryDetailIds.length>0){
            /*for(int i=0; i<deliveryDetailIds.length; i++){

                this.baseMapper.updateStatusById(deliveryDetailIds[i], status);

            }*/
            for(Long id:deliveryDetailIds){
                DeliveryOrderDetailEO detailEO = this.getById(id);
                //判断状态
                if(status == 2 && detailEO.getStatus() != 1){
                    throw new BusinessException("出库单明细状态非已发布状态，请刷新确认");
                }else if(status == 1 && detailEO.getStatus() != 2){
                    throw new BusinessException("出库单明细状态非已完成状态，请刷新确认");
                }
                if(status==2) {
                    detailEO.setStatus(2);
                }else{
                    detailEO.setStatus(1);
                }

                this.updateById(detailEO);

                //判断出库明细是否都已完成
                DeliveryOrderEO deliveryOrderEO = this.deliveryOrderMapper.selectById(detailEO.getDeliveryOrderId());
                if(status == 2 && (deliveryOrderEO.getStatus() == 0 || deliveryOrderEO.getStatus() == 2)){
                    throw new BusinessException("出库单状态为新建或已完成状态、不能进行取料操作,请确认!");
                }

                Integer finishCount = this.deliveryOrderMapper.selectDetailFinishCount(deliveryOrderEO.getDeliveryId());
                if(finishCount > 0){
                    deliveryOrderEO.setStatus(1);
                }else {
                    deliveryOrderEO.setStatus(2);
                }

                this.deliveryOrderMapper.updateById(deliveryOrderEO);
            }
        }
    }


    public List<DeliveryOrderDetailEO> getDeliveryDetailId() {
        return this.baseMapper.getDeliveryDetailId();
    }


    /*   @Override
    public boolean addDetailToStock(Long[] ids) {

        for(Long id:ids){

            DeliveryOrderEO deliveryOrderEO = this.baseMapper.selectDeliveryById(id);
            DeliveryOrderDetailEO deliveryOrderDetailEO =this.baseMapper.selectById(id);

                StockAccountEO accountEO = new StockAccountEO();

                accountEO.setVoucherId(deliveryOrderDetailEO.getDeliveryDetailId());
                accountEO.setVoucherDate(deliveryOrderEO.getDeliveryDate());

                if (deliveryOrderEO.getDeliveryType() == 1){
                    accountEO.setVoucherType(7);
                }else if (deliveryOrderEO.getDeliveryType() == 2){
                    accountEO.setVoucherType(8);
                }else if (deliveryOrderEO.getDeliveryType() == 3){
                accountEO.setVoucherType(10);
                }else {
                    accountEO.setVoucherType(12);
                }

                accountEO.setMaterialId(deliveryOrderDetailEO.getMaterialId());
                accountEO.setMaterialCode(deliveryOrderDetailEO.getMaterialCode());
                accountEO.setMaterialName(deliveryOrderDetailEO.getMaterialName());
                accountEO.setInventoryCode(deliveryOrderDetailEO.getInventoryCode());
                accountEO.setElementNo(deliveryOrderDetailEO.getElementNo());
                accountEO.setSpecification(deliveryOrderDetailEO.getSpecification());
                accountEO.setUnitId(deliveryOrderDetailEO.getUnitId());
                accountEO.setFigureNumber(deliveryOrderDetailEO.getFigureNumber());
                accountEO.setFigureVersion(deliveryOrderDetailEO.getFigureVersion());
                accountEO.setWarehouseId(deliveryOrderDetailEO.getWarehouseId());
                accountEO.setWarehouseLocationId(deliveryOrderDetailEO.getWarehouseLocationId());
                accountEO.setAmount(deliveryOrderDetailEO.getDeliveryAmount());
                accountEO.setRemarks(deliveryOrderDetailEO.getRemarks());

                this.stockAccountService.save(accountEO);
    @Override
    public IPage<DeliveryOrderDetailEO> selectRendererPage(Criteria criteria) {
        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<DeliveryOrderDetailEO> wrapper = new QueryWrapper<DeliveryOrderDetailEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<DeliveryOrderDetailEO> totalList = this.baseMapper.selectRendererPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<DeliveryOrderDetailEO> list = this.baseMapper.selectRendererPage(param);

        IPage<DeliveryOrderDetailEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;
    }

    @Override
    public boolean deleteStock(Long[] ids) {
        for(Long id:ids){
            this.baseMapper.deleteStockById(id);
        }
        return true;
    }*/

    public List<DeliveryOrderDetailEO> getByDeliveryOrderId(Long deliveryOrderId) throws BusinessException {
        return this.baseMapper.getByDeliveryOrderId(deliveryOrderId, null);
    }


    public List<DeliveryOrderDetailEO> getByDeliveryOrderIds(Long[] deliveryOrderIds) throws BusinessException {
        if(deliveryOrderIds!=null && deliveryOrderIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        List<DeliveryOrderDetailEO> deliveryOrderDetails = new ArrayList<>();
        for(Long deliveryOrderId : deliveryOrderIds) {
            List<DeliveryOrderDetailEO> list = this.getByDeliveryOrderId(deliveryOrderId);
            if(list!=null && list.size()>0) {
                deliveryOrderDetails.addAll(list);
            }
        }
        return deliveryOrderDetails;
    }

    public List<DeliveryOrderDetailEO> getByDeliveryOrderIdsAndUserId(Long[] deliveryOrderIds, Long userId, String fileName) throws BusinessException {
        if(deliveryOrderIds!=null && deliveryOrderIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        String sqlStr = "";
        for(Long deliveryOrderId : deliveryOrderIds) {
            sqlStr += (deliveryOrderId + ",");
        }
        if(!"".equals(sqlStr)) {
            sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
        } else {
            sqlStr = "(-1)";
        }
        List<DeliveryOrderEO> deliveryOrders = this.deliveryOrderMapper.getByIds(sqlStr);
//        String updateSqlStr1 = "";
//        String updateSqlStr2 = "";
//        String updateSqlStr3 = "";
        String orderField = "";
        if(deliveryOrders!=null && deliveryOrders.size()>0) {
            for(DeliveryOrderEO deliveryOrder : deliveryOrders) {
                if(fileName != null) {
                    if(fileName.equals("export_delivery_order_detail_3.json")) { // 委外发货单(周计划管理中委外发货)
                        if (deliveryOrder.getErpVoucherNo1() == null || deliveryOrder.getErpVoucherNo1().trim().equals("")) {
//                            updateSqlStr1 += (deliveryOrder.getDeliveryId() + ",");
                            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_delivery_order_detail_3_" + deliveryOrders.get(0).getOrgId());
                            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                                this.deliveryOrderMapper.updateErpVoucherNoByIds("erp_voucher_no1", erpVoucherNo, "(" + deliveryOrder.getDeliveryId() + ")");
                            }
                        }
                        orderField = "do.erp_voucher_no1";
                    }
                    if(fileName.equals("export_delivery_order_detail_1.json")) { // 导出(送货计划页面)
                        if(deliveryOrder.getErpVoucherNo2()==null || deliveryOrder.getErpVoucherNo2().trim().equals("")) {
//                            updateSqlStr2 += (deliveryOrder.getDeliveryId() + ",");
                            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_delivery_order_detail_1_" + deliveryOrders.get(0).getOrgId());
                            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                                this.deliveryOrderMapper.updateErpVoucherNoByIds("erp_voucher_no2", erpVoucherNo, "(" + deliveryOrder.getDeliveryId() + ")");
                            }
                        }
                        orderField = "do.erp_voucher_no2";
                    }
                    if(fileName.equals("export_to_sale_order.json")) { // 导出为销售订单(送货计划页面)
                        if (deliveryOrder.getErpVoucherNo3() == null || deliveryOrder.getErpVoucherNo3().trim().equals("")) {
//                            updateSqlStr3 += (deliveryOrder.getDeliveryId() + ",");
                            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_to_sale_order_" + deliveryOrders.get(0).getOrgId());
                            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                                this.deliveryOrderMapper.updateErpVoucherNoByIds("erp_voucher_no3", erpVoucherNo, "(" + deliveryOrder.getDeliveryId() + ")");
                            }
                        }
                        orderField = "do.erp_voucher_no3";
                    }
                }
            }
        }
//        if(!"".equals(updateSqlStr1)) {
//            updateSqlStr1 = "(" + updateSqlStr1.substring(0, updateSqlStr1.length()-1) + ")";
//            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_delivery_order_detail_3_" + deliveryOrders.get(0).getOrgId());
//            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
//                this.deliveryOrderMapper.updateErpVoucherNoByIds("erp_voucher_no1", erpVoucherNo, updateSqlStr1);
//            }
//        }
//        if(!"".equals(updateSqlStr2)) {
//            updateSqlStr2 = "(" + updateSqlStr2.substring(0, updateSqlStr2.length()-1) + ")";
//            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_delivery_order_detail_1_" + deliveryOrders.get(0).getOrgId());
//            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
//                this.deliveryOrderMapper.updateErpVoucherNoByIds("erp_voucher_no2", erpVoucherNo, updateSqlStr2);
//            }
//        }
//        if(!"".equals(updateSqlStr3)) {
//            updateSqlStr3 = "(" + updateSqlStr3.substring(0, updateSqlStr3.length()-1) + ")";
//            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_to_sale_order_" + deliveryOrders.get(0).getOrgId());
//            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
//                this.deliveryOrderMapper.updateErpVoucherNoByIds("erp_voucher_no3", erpVoucherNo, updateSqlStr3);
//            }
//        }

//        List<DeliveryOrderDetailEO> deliveryOrderDetails = new ArrayList<>();
//        for(int i=deliveryOrderIds.length; i>0; i--) {
//            List<DeliveryOrderDetailEO> list = this.baseMapper.getByDeliveryOrderId(deliveryOrderIds[i-1], userId);
//            if(list!=null && list.size()>0) {
//                deliveryOrderDetails.addAll(list);
//            }
//        }
//        return deliveryOrderDetails;
        return this.baseMapper.getByDeliveryOrderIds(sqlStr, userId, orderField);
    }

    public IPage<DeliveryOrderDetailEO> selectRendererPage(Criteria criteria) {
        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<DeliveryOrderDetailEO> wrapper = new QueryWrapper<DeliveryOrderDetailEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<DeliveryOrderDetailEO> totalList = this.baseMapper.selectRendererPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<DeliveryOrderDetailEO> list = this.baseMapper.selectRendererPage(param);

        IPage<DeliveryOrderDetailEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;
    }

    public void removeByDeliveryOrderId(Long deliveryOrderId) throws BusinessException {
        this.baseMapper.removeByDeliveryOrderId(deliveryOrderId);
    }

    public void updateStatusByDeliveryOrderId(Integer status, Long deliveryOrderId) {
        this.baseMapper.updateStatusByDeliveryOrderId(status, deliveryOrderId);
    }

    public void updateStatusById(Long id,Integer status){
        this.baseMapper.updateStatusById(id,status);
    }

    public void updateDeliveryOrderStatusById(Long id,Integer status){
        this.baseMapper.updateDeliveryOrderStatusById(id,status);
    }

    public DeliveryOrderDetailEO getByDeliveryOrderDetailId(Long deliveryOrderDetailId) {
        return this.baseMapper.getByDeliveryOrderDetailId(deliveryOrderDetailId);
    }

    public List<DeliveryOrderDetailEO> getFromDeliveryOrderIds(Long[] deliveryOrderIds, Integer deliveryType) {
        List<DeliveryOrderDetailEO> deliveryOrderDetails = this.baseMapper.getFromDeliveryOrderIds(deliveryOrderIds, deliveryType);

        Map map = new HashMap();
        if(deliveryOrderDetails!=null && deliveryOrderDetails.size()>0) {
            String sqlStr = "";
            for(Long deliveryOrderId : deliveryOrderIds) {
                sqlStr += (deliveryOrderId + ",");
            }
            if(!"".equals(sqlStr)) {
                sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
            } else {
                sqlStr = "(-1)";
            }

            List<DeliveryOrderEO> deliveryOrders = this.deliveryOrderMapper.getByIds(sqlStr);
            if(deliveryOrders!=null && deliveryOrders.size()>0) {
                for(DeliveryOrderEO deliveryOrder : deliveryOrders) {
                    String erpVoucherNo4 = deliveryOrder.getErpVoucherNo4();
                    if(erpVoucherNo4==null || erpVoucherNo4.trim().equals("")) {
                        erpVoucherNo4 = this.businessCodeGenerator.getErpVoucherNo("delivery_order_" + deliveryOrder.getOrgId());
                        if(erpVoucherNo4.matches("^[A-Z0-9]+$")) {
                            this.deliveryOrderMapper.updateErpVoucherNoByIds("erp_voucher_no4", erpVoucherNo4, "(" + deliveryOrder.getDeliveryId() + ")");
                        }
                    }

                    map.put(deliveryOrder.getDeliveryId(), erpVoucherNo4);
                }
            }

            for(DeliveryOrderDetailEO deliveryOrderDetail : deliveryOrderDetails) {
                String erpVoucherNo4 = map.get(deliveryOrderDetail.getDeliveryOrderId()).toString();
                deliveryOrderDetail.setVoucherNoSub(erpVoucherNo4.substring(2, erpVoucherNo4.length()));
            }
        }

        return deliveryOrderDetails;
    }
}
