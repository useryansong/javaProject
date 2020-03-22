package com.xchinfo.erp.srm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.srm.mapper.DeliveryNoteDetailMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/14
 */
@Service
public class DeliveryNoteDetailService extends BaseServiceImpl<DeliveryNoteDetailMapper, DeliveryNoteDetailEO> {

    @Autowired
    @Lazy
    public DeliveryPlanService deliveryPlanService;

    @Autowired
    @Lazy
    public DeliveryNoteService deliveryNoteService;

    @Autowired
    @Lazy
    public MaterialService materialService;

    @Autowired
    @Lazy
    public BusinessCodeGenerator businessCodeGenerator;


    // 根据实体的deliveryPlanId与deliveryNoteId字段查询实体
    private DeliveryNoteDetailEO getEntity(Long deliveryPlanId, Long deliveryNoteId){
        QueryWrapper<DeliveryNoteDetailEO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delivery_plan_id", deliveryPlanId);
        queryWrapper.eq("delivery_note_id", deliveryNoteId);
        DeliveryNoteDetailEO deliveryNoteDetail = this.baseMapper.selectOne(queryWrapper);
        return deliveryNoteDetail;
    }


    @Transactional(rollbackFor = Exception.class)
    public String addBatch(Long[] deliveryPlanIds, Long deliveryNoteId) throws BusinessException {
        if(deliveryPlanIds==null || deliveryPlanIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        // 判断送货计划的归属机构与选择的归属机构是否一致
        String alertMsg = "";
        DeliveryNoteEO deliveryNote = this.deliveryNoteService.getById(deliveryNoteId);
        for(Long deliveryPlanId : deliveryPlanIds) {
            DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(deliveryPlanId);
            if(deliveryPlan.getOrgId().longValue() != deliveryNote.getOrgId().longValue()) {
                alertMsg += ("订单【" + deliveryPlan.getVoucherNo() + "】的归属机构与选择的归属机构不一致!<br/>");
            }
        }

        if(!"".equals(alertMsg)) {
            throw new BusinessException(alertMsg);
        }


        int sum = 0;
        String errorMsg = "";
        Double totalDeliveryQuantity = 0d;
        for(Long deliveryPlanId : deliveryPlanIds){
            DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(deliveryPlanId);
            DeliveryNoteDetailEO deliveryNoteDetailFromDb = getEntity(deliveryPlanId, deliveryNoteId);
            if(deliveryNoteDetailFromDb != null){
                errorMsg += "添加["+ deliveryPlan.getVoucherNo() +"]订单到送货单时出错:当前送货单已经添加了此订单!<br/>";
            }else{
                Map map = new HashMap();
                map.put("deliveryPlanId", deliveryPlanId);
                List<DeliveryNoteDetailEO> deliveryNoteDetails = this.getList(map);
                Double hasDeliveryQuantity = 0d;
                if(deliveryNoteDetails!=null && deliveryNoteDetails.size()>0) {
                    for(DeliveryNoteDetailEO deliveryNoteDetail : deliveryNoteDetails) {
                        if(deliveryNoteDetail.getStatus() == 3) {
                            hasDeliveryQuantity += deliveryNoteDetail.getActualReceiveQuantity();
                        } else {
                            hasDeliveryQuantity += deliveryNoteDetail.getDeliveryQuantity();
                        }
                    }
                }

                if(hasDeliveryQuantity < (deliveryPlan.getPlanDeliveryQuantity() + deliveryPlan.getReturnedQuantity())) {
                    DeliveryNoteDetailEO deliveryNoteDetail = new DeliveryNoteDetailEO();
                    deliveryNoteDetail.setDeliveryNoteId(deliveryNoteId);
                    deliveryNoteDetail.setDeliveryPlanId(deliveryPlanId);
                    deliveryNoteDetail.setDeliveryQuantity(deliveryPlan.getPlanDeliveryQuantity() - hasDeliveryQuantity);
                    deliveryNoteDetail.setActualReceiveQuantity(Double.valueOf(0));
                    deliveryNoteDetail.setQualifiedQuantity(Double.valueOf(0));
                    deliveryNoteDetail.setNotQualifiedQuantity(Double.valueOf(0));
                    deliveryNoteDetail.setStatus(0);
                    MaterialEO material = this.materialService.getById(deliveryPlan.getMaterialId());
                    Integer snp = 0;
                    if(material != null) {
                        snp = material.getSnp()==null?0:material.getSnp().intValue();
                    }
                    deliveryNoteDetail.setBqNumber(snp);
                    boolean flag = super.save(deliveryNoteDetail);
                    if(flag) {
                        sum += 1;
                        totalDeliveryQuantity += deliveryPlan.getPlanDeliveryQuantity();
                    }
                } else {
                    errorMsg += "添加["+ deliveryPlan.getVoucherNo() +"]订单到送货单时出错:超出送货单数量上限!<br/>";
                }
            }
        }

        // 修改送货单总送货数量
        if(totalDeliveryQuantity.doubleValue()>0) {
            deliveryNote.setTotalDeliveryQuantity(deliveryNote.getTotalDeliveryQuantity() + totalDeliveryQuantity);
            this.deliveryNoteService.updateById(deliveryNote);
        }

        String successMsg = "已添加" + sum + "项<br/>";
        return successMsg + errorMsg;
    }


    public List<DeliveryNoteDetailEO> getList(Map map) {
        List<DeliveryNoteDetailEO> list = this.baseMapper.getList(map);
        // 计算剩余未送数量设置到对应得实体中
        for(int i=0; i<list.size(); i++) {
            DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(list.get(i).getDeliveryPlanId());
            Double hasDeliveryQuantity = 0d;
            Map newMap = new HashMap();
            newMap.put("deliveryPlanId", list.get(i).getDeliveryPlanId());
            List<DeliveryNoteDetailEO> deliveryNoteDetails = this.baseMapper.getList(newMap);
            if(deliveryNoteDetails!=null && deliveryNoteDetails.size()>0) {
                for(DeliveryNoteDetailEO deliveryNoteDetail : deliveryNoteDetails) {
                    if(deliveryNoteDetail.getStatus() == 3) {
                        hasDeliveryQuantity += deliveryNoteDetail.getActualReceiveQuantity();
                    } else {
                        hasDeliveryQuantity += deliveryNoteDetail.getDeliveryQuantity();
                    }
                }
            }
            list.get(i).setNotDeliveryQuantity(deliveryPlan.getPlanDeliveryQuantity()-hasDeliveryQuantity);
        }
        return list;
    }


    public String updateBatch(DeliveryNoteDetailEO[] deliveryNoteDetails) throws BusinessException {
        if(deliveryNoteDetails!=null && deliveryNoteDetails.length==0){
            throw new BusinessException("无数据!");
        }

        int sum = 0;
        String errorMsg = "";
        Double totalDeliveryQuantity = 0d;
        for(DeliveryNoteDetailEO deliveryNoteDetail : deliveryNoteDetails) {
            // 计算送货单明细对应的送货计划数量是否超出
            DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(deliveryNoteDetail.getDeliveryPlanId());
            Map map = new HashMap();
            map.put("deliveryPlanId", deliveryNoteDetail.getDeliveryPlanId());
            List<DeliveryNoteDetailEO> list = this.baseMapper.getList(map);
            Double hasDeliveryQuantity = 0d;
            if(list!=null && list.size()>0) {
                for(int i=0; i<list.size(); i++) {
                    DeliveryNoteDetailEO temp = list.get(i);
                    if(temp.getDeliveryNoteDetailId().longValue() != deliveryNoteDetail.getDeliveryNoteDetailId().longValue()) {
                        if(temp.getStatus() == 3) {
                            hasDeliveryQuantity += temp.getActualReceiveQuantity();
                        } else {
                            hasDeliveryQuantity += temp.getDeliveryQuantity();
                        }
                    }
                }
            }

            hasDeliveryQuantity += deliveryNoteDetail.getDeliveryQuantity();
            if(hasDeliveryQuantity > (deliveryPlan.getPlanDeliveryQuantity() + deliveryPlan.getReturnedQuantity())) {
                errorMsg += "保存["+ deliveryNoteDetail.getVoucherNo() +"]订单出错:超出送货单数量上限!<br/>";
            } else {
                boolean flag = super.updateById(deliveryNoteDetail);
                if(flag) {
                    sum += 1;
                    totalDeliveryQuantity += deliveryNoteDetail.getPlanDeliveryQuantity();
                }
            }
        }

        // 修改送货单总送货数量
        if(totalDeliveryQuantity.doubleValue()>0) {
            DeliveryNoteEO deliveryNote = this.deliveryNoteService.getById(deliveryNoteDetails[0].getDeliveryNoteId());
            deliveryNote.setTotalDeliveryQuantity(totalDeliveryQuantity);
            this.deliveryNoteService.updateById(deliveryNote);
        }

        String successMsg = "已添加" + sum + "项<br/>";
        return successMsg + errorMsg;
    }


    public void removeByDeliveryNoteId(Long deliveryNoteId) {
        this.baseMapper.removeByDeliveryNoteId(deliveryNoteId);
    }


    public List<DeliveryNoteDetailEO> getByDeliveryNoteId(Long deliveryNoteId) {
        List<DeliveryNoteDetailEO>  list = this.baseMapper.getByDeliveryNoteId(deliveryNoteId, null, null);
        if(list!=null && list.size()>0) {
            // 计算剩余未送数量设置到对应得实体中
            for(int i=0; i<list.size(); i++) {
                DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(list.get(i).getDeliveryPlanId());
                Double hasDeliveryQuantity = 0d;
                Map newMap = new HashMap();
                newMap.put("deliveryPlanId", list.get(i).getDeliveryPlanId());
                List<DeliveryNoteDetailEO> deliveryNoteDetails = this.baseMapper.getList(newMap);
                if(deliveryNoteDetails!=null && deliveryNoteDetails.size()>0) {
                    for(DeliveryNoteDetailEO deliveryNoteDetail : deliveryNoteDetails) {
                        if(deliveryNoteDetail.getStatus() == 3) {
                            hasDeliveryQuantity += deliveryNoteDetail.getActualReceiveQuantity();
                        } else {
                            hasDeliveryQuantity += deliveryNoteDetail.getDeliveryQuantity();
                        }
                    }
                }
                list.get(i).setNotDeliveryQuantity(deliveryPlan.getPlanDeliveryQuantity()-hasDeliveryQuantity);
            }
        }

        return list;
    }


    public void updateStatusById(Long deliveryNoteDetailId, Integer status) {
        this.baseMapper.updateStatusById(deliveryNoteDetailId, status);
    }

    @Override
    public boolean removeByIds(Serializable[] deliveryNoteDetailIds) {
        if(deliveryNoteDetailIds!=null && deliveryNoteDetailIds.length==0){
            throw new BusinessException("无数据!");
        }

        for(Serializable deliveryNoteDetailId : deliveryNoteDetailIds) {
            DeliveryNoteDetailEO deliveryNoteDetail = super.getById(deliveryNoteDetailId);
            // 修改送货单总送货数量
            DeliveryNoteEO deliveryNote = this.deliveryNoteService.getById(deliveryNoteDetail.getDeliveryNoteId());
            DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(deliveryNoteDetail.getDeliveryPlanId());
            deliveryNote.setTotalDeliveryQuantity(deliveryNote.getTotalDeliveryQuantity() - deliveryPlan.getPlanDeliveryQuantity());
            this.deliveryNoteService.updateById(deliveryNote);
        }

        return super.removeByIds(deliveryNoteDetailIds);
    }


    public List<DeliveryNoteDetailEO> getByDeliveryNoteIds(Long[] deliveryNoteIds) {
        if(deliveryNoteIds!=null && deliveryNoteIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        List<DeliveryNoteDetailEO> deliveryNoteDetails = new ArrayList<>();
        for(Long deliveryNoteId : deliveryNoteIds) {
            List<DeliveryNoteDetailEO> list = this.getByDeliveryNoteId(deliveryNoteId);
            if(list!=null && list.size()>0) {
                deliveryNoteDetails.addAll(list);
            }
        }
        return deliveryNoteDetails;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<DeliveryNoteDetailEO> getByDeliveryNoteIdsAndUserId(Long[] deliveryNoteIds, Long userId, Boolean isFilterByActualReceiveQuantity, String fileName) throws BusinessException {
        if(deliveryNoteIds!=null && deliveryNoteIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        String sqlStr = "";
        for(Long deliveryNoteId : deliveryNoteIds) {
            sqlStr += (deliveryNoteId + ",");
        }
        if(!"".equals(sqlStr)) {
            sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
        } else {
            sqlStr = "(-1)";
        }
        List<DeliveryNoteEO> deliveryNotes = this.deliveryNoteService.getByIds(sqlStr);
//        String updateSqlStr1 = "";
//        String updateSqlStr2 = "";
//        String updateSqlStr3 = "";
//        String updateSqlStr4 = "";
        String orderField = "";
        if(deliveryNotes!=null && deliveryNotes.size()>0) {
            for(DeliveryNoteEO deliveryNote : deliveryNotes) {
                if(fileName.equals("receive_purchase_export.json")) { // 导出(采购收货页面)
                    if(deliveryNote.getErpVoucherNo1()==null || deliveryNote.getErpVoucherNo1().trim().equals("")) {
//                        updateSqlStr1 += (deliveryNote.getDeliveryNoteId() + ",");
                        String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("receive_purchase_export_" + deliveryNotes.get(0).getOrgId());
                        if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                            this.deliveryNoteService.updateErpVoucherNoByIds("erp_voucher_no1", erpVoucherNo, "(" + deliveryNote.getDeliveryNoteId() + ")");
                        }
                    }
                    orderField = "dn.erp_voucher_no1";
                }
                if(fileName.equals("export_to_purchase_order.json")) { // 导出为采购订单(采购收货页面))
                    if(deliveryNote.getErpVoucherNo2()==null || deliveryNote.getErpVoucherNo2().trim().equals("")) {
//                        updateSqlStr2 += (deliveryNote.getDeliveryNoteId() + ",");
                        String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_to_purchase_order_" + deliveryNotes.get(0).getOrgId());
                        if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                            this.deliveryNoteService.updateErpVoucherNoByIds("erp_voucher_no2",erpVoucherNo, "(" + deliveryNote.getDeliveryNoteId() + ")");
                        }
                    }
                    orderField = "dn.erp_voucher_no2";
                }
                if(fileName.equals("receive_outside_export.json")) { // 导出(委外收货页面)
                    if(deliveryNote.getErpVoucherNo3()==null || deliveryNote.getErpVoucherNo3().trim().equals("")) {
//                        updateSqlStr3 += (deliveryNote.getDeliveryNoteId() + ",");
                        String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("receive_outside_export_" + deliveryNotes.get(0).getOrgId());
                        if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                            this.deliveryNoteService.updateErpVoucherNoByIds("erp_voucher_no3", erpVoucherNo, "(" + deliveryNote.getDeliveryNoteId() + ")");
                        }
                    }
                    orderField = "dn.erp_voucher_no3";
                }
                if(fileName.equals("export_to_outside_order.json")) { // 导出为委外出库(委外收货页面)
                    if(deliveryNote.getErpVoucherNo4()==null || deliveryNote.getErpVoucherNo4().trim().equals("")) {
//                        updateSqlStr4 += (deliveryNote.getDeliveryNoteId() + ",");
                        String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_to_outside_order_" + deliveryNotes.get(0).getOrgId());
                        if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                            this.deliveryNoteService.updateErpVoucherNoByIds("erp_voucher_no4", erpVoucherNo, "(" + deliveryNote.getDeliveryNoteId() + ")");
                        }
                    }
                    orderField = "dn.erp_voucher_no4";
                }
            }
        }
//        if(!"".equals(updateSqlStr1)) {
//            updateSqlStr1 = "(" + updateSqlStr1.substring(0, updateSqlStr1.length()-1) + ")";
//            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("receive_purchase_export_" + deliveryNotes.get(0).getOrgId());
//            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
//                this.deliveryNoteService.updateErpVoucherNoByIds("erp_voucher_no1", erpVoucherNo, updateSqlStr1);
//            }
//        }
//        if(!"".equals(updateSqlStr2)) {
//            updateSqlStr2 = "(" + updateSqlStr2.substring(0, updateSqlStr2.length()-1) + ")";
//            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_to_purchase_order_" + deliveryNotes.get(0).getOrgId());
//            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
//                this.deliveryNoteService.updateErpVoucherNoByIds("erp_voucher_no2",erpVoucherNo, updateSqlStr2);
//            }
//        }
//        if(!"".equals(updateSqlStr3)) {
//            updateSqlStr3 = "(" + updateSqlStr3.substring(0, updateSqlStr3.length()-1) + ")";
//            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("receive_outside_export_" + deliveryNotes.get(0).getOrgId());
//            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
//                this.deliveryNoteService.updateErpVoucherNoByIds("erp_voucher_no3", erpVoucherNo, updateSqlStr3);
//            }
//        }
//        if(!"".equals(updateSqlStr4)) {
//            updateSqlStr4 = "(" + updateSqlStr4.substring(0, updateSqlStr4.length()-1) + ")";
//            String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_to_outside_order_" + deliveryNotes.get(0).getOrgId());
//            if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
//                this.deliveryNoteService.updateErpVoucherNoByIds("erp_voucher_no4", erpVoucherNo, updateSqlStr4);
//            }
//        }

//        List<DeliveryNoteDetailEO> deliveryNoteDetails = new ArrayList<>();
//        for(int i=deliveryNoteIds.length; i>0; i--) {
//            List<DeliveryNoteDetailEO> list = this.baseMapper.getByDeliveryNoteId(deliveryNoteIds[i-1], userId, isFilterByActualReceiveQuantity);
//            if(list!=null && list.size()>0) {
//                deliveryNoteDetails.addAll(list);
//            }
//        }

//        List<DeliveryNoteDetailEO> list = this.baseMapper.getByDeliveryNoteIds(sqlStr, userId, isFilterByActualReceiveQuantity);
//        return deliveryNoteDetails;

        return this.baseMapper.getByDeliveryNoteIds(sqlStr, userId, isFilterByActualReceiveQuantity, orderField);
    }

    public List<DeliveryNoteDetailEO> getPage(Map map) {
        return this.baseMapper.getPage(map);
    }

    public Integer getCount(Map map) {
        return this.baseMapper.getCount(map);
    }
}
