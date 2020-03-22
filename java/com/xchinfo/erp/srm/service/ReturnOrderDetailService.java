package com.xchinfo.erp.srm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.srm.mapper.ReturnOrderDetailMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/24
 */
 @Service
public class ReturnOrderDetailService extends BaseServiceImpl<ReturnOrderDetailMapper, ReturnOrderDetailEO>  {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    @Lazy
    private ReturnOrderService returnOrderService;

    @Autowired
    private DeliveryPlanService deliveryPlanService;


    public List<ReturnOrderDetailEO> getPage(Map map) {
        return this.baseMapper.getPage(map);
    }

    // 根据实体的deliveryPlanId与deliveryNoteId字段查询实体
    private ReturnOrderDetailEO getEntity(Long purchaseOrderId, Long returnOrderId){
        QueryWrapper<ReturnOrderDetailEO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("purchase_order_id", purchaseOrderId);
        queryWrapper.eq("return_order_id", returnOrderId);
        ReturnOrderDetailEO returnOrderDetail = this.baseMapper.selectOne(queryWrapper);
        return returnOrderDetail!=null?returnOrderDetail:null;
    }

    @Transactional(rollbackFor = Exception.class)
    public String addBatch(Long[] purchaseOrderIds, Long returnOrderId) throws BusinessException {
        if(purchaseOrderIds==null || purchaseOrderIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        // 判断采购订单/委外订单的归属机构与选择的归属机构是否一致
        String alertMsg = "";
        ReturnOrderEO returnOrder = this.returnOrderService.getById(returnOrderId);
        for(Long purchaseOrderId : purchaseOrderIds){
            PurchaseOrderEO purchaseOrder = this.purchaseOrderService.getById(purchaseOrderId);
            if(purchaseOrder.getOrgId().longValue() != returnOrder.getOrgId().longValue()) {
                alertMsg += ("订单【" + purchaseOrder.getVoucherNo() + "】的归属机构与选择的归属机构不一致!<br/>");
            }
        }

        if(!"".equals(alertMsg)) {
            throw new BusinessException(alertMsg);
        }

        int sum = 0;
        String errorMsg = "";
        for(Long purchaseOrderId : purchaseOrderIds){
            PurchaseOrderEO purchaseOrder = this.purchaseOrderService.getById(purchaseOrderId);
            ReturnOrderDetailEO returnOrderDetailFromDb = getEntity(purchaseOrderId, returnOrderId);
            if(returnOrderDetailFromDb != null){
                errorMsg += "添加["+ purchaseOrder.getVoucherNo() +"]订单到退货单时出错:当前送货单已经添加了此订单!<br/>";
            }else{
                List<ReturnOrderDetailEO> returnOrderDetails = this.baseMapper.getByPurchaseOrderId(purchaseOrderId);
                Double sumPlanReturnQuantity = 0d;
                if (returnOrderDetails!=null && returnOrderDetails.size()>0) {
                    for(ReturnOrderDetailEO returnOrderDetail : returnOrderDetails) {
                        if(returnOrderDetail.getStatus() == 0) {
                            sumPlanReturnQuantity += returnOrderDetail.getPlanReturnQuantity();
                        }
                        if(returnOrderDetail.getStatus() == 1) {
                            sumPlanReturnQuantity += returnOrderDetail.getActualReturnQuantity();
                        }
                    }
                }

                if(sumPlanReturnQuantity >= purchaseOrder.getActualDeliveryQuantity()) {
                    errorMsg += "添加["+ purchaseOrder.getVoucherNo() +"]订单到退货单时出错,剩余可退数量为0!<br/>";
                }

                if(sumPlanReturnQuantity < purchaseOrder.getActualDeliveryQuantity()) {
                    ReturnOrderDetailEO returnOrderDetail = new ReturnOrderDetailEO();
                    returnOrderDetail.setPurchaseOrderId(purchaseOrderId);
                    returnOrderDetail.setReturnOrderId(returnOrderId);
                    returnOrderDetail.setMaterialId(purchaseOrder.getMaterialId());
                    returnOrderDetail.setMaterialName(purchaseOrder.getMaterialName());
                    returnOrderDetail.setSpecification(purchaseOrder.getSpecification());
                    returnOrderDetail.setMaterialCode(purchaseOrder.getMaterialCode());
                    returnOrderDetail.setInventoryCode(purchaseOrder.getInventoryCode());
                    returnOrderDetail.setElementNo(purchaseOrder.getElementNo());
                    returnOrderDetail.setUnitName(purchaseOrder.getUnitName());
                    returnOrderDetail.setPlanReturnQuantity(Double.valueOf(0));
                    returnOrderDetail.setActualReturnQuantity(Double.valueOf(0));
                    returnOrderDetail.setStatus(0);
                    boolean flag = super.save(returnOrderDetail);
                    if (flag) {
                        sum += 1;
                    }
                }
            }
        }

        String successMsg = "已添加" + sum + "项<br/>";
        return successMsg + errorMsg;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ReturnOrderDetailEO entity) throws BusinessException {
        List<ReturnOrderDetailEO> returnOrderDetails = this.baseMapper.getByPurchaseOrderId(entity.getPurchaseOrderId());
        Double sumPlanReturnQuantity = 0d;
        if (returnOrderDetails!=null && returnOrderDetails.size()>0) {
            for(ReturnOrderDetailEO returnOrderDetail : returnOrderDetails) {
                if(returnOrderDetail.getReturnOrderDetailId().longValue() != entity.getReturnOrderDetailId().longValue()) {
                    if(returnOrderDetail.getStatus() == 0) {
                        sumPlanReturnQuantity += returnOrderDetail.getPlanReturnQuantity();
                    }
                    if(returnOrderDetail.getStatus() == 1) {
                        sumPlanReturnQuantity += returnOrderDetail.getActualReturnQuantity();
                    }
                }
            }
        }

        PurchaseOrderEO purchaseOrder = this.purchaseOrderService.getById(entity.getPurchaseOrderId());
        Double rest = purchaseOrder.getActualDeliveryQuantity() - sumPlanReturnQuantity;
        if(entity.getStatus() == 0) {
            sumPlanReturnQuantity += entity.getPlanReturnQuantity();
        }
        if(entity.getStatus() == 1) {
            sumPlanReturnQuantity += entity.getActualReturnQuantity();
        }
        if(sumPlanReturnQuantity > purchaseOrder.getActualDeliveryQuantity()) {
            throw new BusinessException("数量超出实际送货数量,剩余可退数量为" + rest +"!");
        }

        // 采购订单退货数量修改
        purchaseOrder.setReturnedQuantity(sumPlanReturnQuantity);
        if((purchaseOrder.getActualDeliveryQuantity()-sumPlanReturnQuantity) > 0) {
            purchaseOrder.setQualifiedQuantity(purchaseOrder.getActualDeliveryQuantity() - sumPlanReturnQuantity);
        }
        if(entity.getActualReturnQuantity()!=null && entity.getActualReturnQuantity()>0) {
            purchaseOrder.setStatus(1);
        }
        this.purchaseOrderService.updateById(purchaseOrder);
        // 送货计划退货数量，实际送货数量修改(目前采购订单与送货计划是一对一)
        List<DeliveryPlanEO> deliveryPlans = this.deliveryPlanService.getByPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
        if(deliveryPlans!=null && deliveryPlans.size()>0) {
            DeliveryPlanEO deliveryPlan = deliveryPlans.get(0);
            deliveryPlan.setReturnedQuantity(sumPlanReturnQuantity);
            if((deliveryPlan.getActualDeliveryQuantity()-sumPlanReturnQuantity) > 0) {
                deliveryPlan.setQualifiedQuantity(deliveryPlan.getActualDeliveryQuantity() - sumPlanReturnQuantity);
            }
            if(entity.getActualReturnQuantity()!=null && entity.getActualReturnQuantity()>0) {
                deliveryPlan.setStatus(1);
            }
            this.deliveryPlanService.updateById(deliveryPlan);
        }
        return super.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeByReturnOrderId(Long returnOrderId) throws BusinessException {
        this.baseMapper.removeByReturnOrderId(returnOrderId);
    }


    public List<ReturnOrderDetailEO> getByReturnOrderId(Long returnOrderId) {
        return this.baseMapper.getByReturnOrderId(returnOrderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusById(Long id,Integer status){
        this.baseMapper.updateStatusById(id,status);
    }

    public void updateReturnOrderStatusById(Long id,Integer status){
        this.baseMapper.updateReturnOrderStatusById(id,status);
    }

    public List<ReturnOrderDetailEO> getByReturnOrderIdsAndUserId(Long[] returnOrderIds, Long userId, boolean isFilterByActualReceiveQuantity, String fileName) throws BusinessException {
        if(returnOrderIds!=null && returnOrderIds.length==0){
            throw new BusinessException("请先选择数据！");
        }

        String sqlStr = "";
        for(Long returnOrderId : returnOrderIds) {
            sqlStr += (returnOrderId + ",");
        }
        if(!"".equals(sqlStr)) {
            sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
        } else {
            sqlStr = "(-1)";
        }
        List<ReturnOrderEO> returnOrders = this.returnOrderService.getByIds(sqlStr);
        String orderField = "";
        if(returnOrders!=null && returnOrders.size()>0) {
            for(ReturnOrderEO returnOrder : returnOrders) {
                if(fileName.equals("export_to_return_order.json")) { // 导出为采购退货单(采购退货页面)
                    if(returnOrder.getErpVoucherNo1()==null || returnOrder.getErpVoucherNo1().trim().equals("")) {
                        String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_to_return_order_" + returnOrders.get(0).getOrgId());
                        if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                            this.returnOrderService.updateErpVoucherNoByIds("erp_voucher_no1",erpVoucherNo, "(" + returnOrder.getReturnOrderId() + ")");
                        }
                    }
                    orderField = "ro.erp_voucher_no1";
                }
            }
        }

        return this.baseMapper.getByReturnOrderIds(sqlStr, userId, isFilterByActualReceiveQuantity, orderField);
    }
}
