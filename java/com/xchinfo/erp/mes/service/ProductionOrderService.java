package com.xchinfo.erp.mes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.mes.entity.ProductionOrderDetailEO;
import com.xchinfo.erp.mes.entity.ProductionOrderEO;
import com.xchinfo.erp.mes.mapper.ProductionOrderDetailMapper;
import com.xchinfo.erp.mes.mapper.ProductionOrderMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class ProductionOrderService extends BaseServiceImpl<ProductionOrderMapper, ProductionOrderEO> {

    @Autowired
    private ProductionOrderDetailMapper productionOrderDetailMapper;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Override
    public IPage<ProductionOrderEO> selectPage(Criteria criteria) {
        IPage<ProductionOrderEO> orders = super.selectPage(criteria);

        for (ProductionOrderEO order : orders.getRecords()){
            List<ProductionOrderDetailEO> details = this.productionOrderDetailMapper.selectByOrder(order.getOrderId());
            order.setDetails(details);
        }

        return orders;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ProductionOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("mes_production_order", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        entity.setOrderNo(code);

        // 先保存订单对象
        if (!retBool(this.baseMapper.insert(entity)))
            throw new BusinessException("保存订单失败！");

        for (ProductionOrderDetailEO orderDetail : entity.getDetails()){
            orderDetail.setOrderId(entity.getOrderId());

            if (!retBool(this.productionOrderDetailMapper.insert(orderDetail)))
                throw new BusinessException("保存订单失败！");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) throws BusinessException {
        // 删除订单明细
        if (!retBool(this.productionOrderDetailMapper.removeByOrder((Long) id)))
            throw new BusinessException("删除订单失败！");

        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        // 删除订单明细
        for (Serializable id : idList){
            if (!retBool(this.productionOrderDetailMapper.removeByOrder((Long) id)))
                throw new BusinessException("删除订单失败！");
        }

        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ProductionOrderEO entity) throws BusinessException {
        // 更新订单明细
        for (ProductionOrderDetailEO orderDetail : entity.getDetails()){
            orderDetail.setOrderId(entity.getOrderId());

            if (!retBool(this.productionOrderDetailMapper.updateById(orderDetail)))
                throw new BusinessException("保存订单失败！");
        }

        return super.updateById(entity);
    }

    @Override
    public ProductionOrderEO getById(Serializable id) throws BusinessException{
        ProductionOrderEO order = this.baseMapper.selectById(id);

        List<ProductionOrderDetailEO> details = this.productionOrderDetailMapper.selectByOrder((Long) id);

        order.setDetails(details);

        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusForSchedule(Long[] ids) throws BusinessException {
        for (Long orderId : ids){
            this.baseMapper.updateStatusForSchedule(orderId);
        }
    }

    public List<ProductionOrderDetailEO> listDetailsByOrder(Long orderId) {
        return this.productionOrderDetailMapper.selectByOrder(orderId);
    }
}
