package com.xchinfo.erp.srm.service;

import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.srm.mapper.DeliveryPlanMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @author zhongye
 * @date 2019/5/11
 */
 @Service
public class DeliveryPlanService extends BaseServiceImpl<DeliveryPlanMapper, DeliveryPlanEO> {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;


    @Transactional(rollbackFor = Exception.class)
    public DeliveryPlanEO saveEntity(DeliveryPlanEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        // 生成业务编码
        String voucherNo = this.businessCodeGenerator.generateNextCode("srm_delivery_plan", entity,user.getOrgId());
        AssertUtils.isBlank(voucherNo);
        entity.setVoucherNo(voucherNo);

        entity.setStatus(entity.getStatus()==null?0:entity.getStatus());
        entity.setIsSupplierConfirm(0);
        entity.setActualDeliveryQuantity(Double.valueOf(0));
        entity.setActualReceiveQuantity(Double.valueOf(0));
        entity.setQualifiedQuantity(Double.valueOf(0));
        entity.setNotQualifiedQuantity(Double.valueOf(0));
        entity.setReturnedQuantity(Double.valueOf(0));
        entity.setIntransitQuantity(Double.valueOf(0));
        entity.setCreatedTime(new Date());
        // 保存送货计划对象
        this.baseMapper.insert(entity);
        return entity;
    }


    public List<DeliveryPlanEO> getByPurchaseOrderId(Long purchaseOrderId) {
        return this.baseMapper.getByPurchaseOrderId(purchaseOrderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void supplierConfirm(Long[] ids, Long userId, String userName) throws BusinessException {
        if(ids==null || ids.length==0){
            throw new BusinessException("请选择数据!");
        }

        for(Long id : ids){
            DeliveryPlanEO deliveryPlan = super.getById(id);
            if(deliveryPlan.getIsSupplierConfirm() == 0){
                deliveryPlan.setIsSupplierConfirm(1);
                deliveryPlan.setSupplierConfirmTime(new Date());
                deliveryPlan.setSupplierConfirmUserId(userId);
                deliveryPlan.setSupplierConfirmUserName(userName);
                this.updateById(deliveryPlan);
            }
        }
    }
}
