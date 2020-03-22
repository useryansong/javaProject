package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderEO;
import com.xchinfo.erp.wms.mapper.SubsidiaryDeliveryOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.SubsidiaryDeliveryOrderMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class SubsidiaryDeliveryOrderDetailService extends BaseServiceImpl<SubsidiaryDeliveryOrderDetailMapper, SubsidiaryDeliveryOrderDetailEO> {

    @Autowired
    private SubsidiaryDeliveryOrderMapper subsidiaryDeliveryOrderMapper;


    @Transactional(rollbackFor = Exception.class)
    public boolean removeByDelivery(Long deliveryId) {
        return retBool(this.baseMapper.deleteByDelivery(deliveryId));
    }


    /*private SubsidiaryDeliveryOrderDetailEO setByMaterialId(Long materialId, SubsidiaryDeliveryOrderDetailEO entity){
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
    }

    // 通过传递的参数获取数据库的实体
    private SubsidiaryDeliveryOrderDetailEO getEntity(SubsidiaryDeliveryOrderDetailEO entity){
        QueryWrapper<SubsidiaryDeliveryOrderDetailEO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delivery_order_id", entity.getDeliveryOrderId());
        queryWrapper.eq("material_id", entity.getMaterialId());
        SubsidiaryDeliveryOrderDetailEO deliveryOrderDetail = this.baseMapper.selectOne(queryWrapper);
        return deliveryOrderDetail!=null?deliveryOrderDetail:null;
    }*/

    @Override
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(SubsidiaryDeliveryOrderDetailEO entity) throws BusinessException {
        /*SubsidiaryDeliveryOrderDetailEO deliveryOrderDetail = getEntity(entity);
        if(deliveryOrderDetail != null){
            throw new BusinessException("请不要重复添加数据!");
        }
        entity = this.setByMaterialId(entity.getMaterialId(), entity);*/
        return super.save(entity);
    }

    // 判断所有数据是否为指定状态
    private boolean isWantedStatus(Long[] deliveryDetailIds, Integer status){
        if(deliveryDetailIds!=null && deliveryDetailIds.length>0){
            for(int i=1; i<deliveryDetailIds.length; i++){
                SubsidiaryDeliveryOrderDetailEO deliveryOrderDetail = super.getById(deliveryDetailIds[i]);
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
                SubsidiaryDeliveryOrderDetailEO detailEO = this.getById(id);
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
                SubsidiaryDeliveryOrderEO SubsidiaryDeliveryOrderEO = this.subsidiaryDeliveryOrderMapper.selectById(detailEO.getDeliveryOrderId());
                if(status == 2 && (SubsidiaryDeliveryOrderEO.getStatus() == 0 || SubsidiaryDeliveryOrderEO.getStatus() == 2)){
                    throw new BusinessException("出库单状态为新建或已完成状态、不能进行取料操作,请确认!");
                }

                Integer finishCount = this.subsidiaryDeliveryOrderMapper.selectDetailFinishCount(SubsidiaryDeliveryOrderEO.getDeliveryId());
                if(finishCount > 0){
                    SubsidiaryDeliveryOrderEO.setStatus(1);
                }else {
                    SubsidiaryDeliveryOrderEO.setStatus(2);
                }

                this.subsidiaryDeliveryOrderMapper.updateById(SubsidiaryDeliveryOrderEO);
            }
        }
    }


    public List<SubsidiaryDeliveryOrderDetailEO> getDeliveryDetailId() {
        return this.baseMapper.getDeliveryDetailId();
    }





    public List<SubsidiaryDeliveryOrderDetailEO> getByDeliveryOrderId(Long deliveryOrderId) throws BusinessException {
        return this.baseMapper.getByDeliveryOrderId(deliveryOrderId);
    }


    public List<SubsidiaryDeliveryOrderDetailEO> getByDeliveryOrderIds(Long[] deliveryOrderIds) throws BusinessException {
        if(deliveryOrderIds!=null && deliveryOrderIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        List<SubsidiaryDeliveryOrderDetailEO> deliveryOrderDetails = new ArrayList<>();
        for(Long deliveryOrderId : deliveryOrderIds) {
            List<SubsidiaryDeliveryOrderDetailEO> list = this.getByDeliveryOrderId(deliveryOrderId);
            if(list!=null && list.size()>0) {
                deliveryOrderDetails.addAll(list);
            }
        }
        return deliveryOrderDetails;
    }


    public IPage<SubsidiaryDeliveryOrderDetailEO> selectRendererPage(Criteria criteria) {
        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<SubsidiaryDeliveryOrderDetailEO> wrapper = new QueryWrapper<SubsidiaryDeliveryOrderDetailEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<SubsidiaryDeliveryOrderDetailEO> totalList = this.baseMapper.selectRendererPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<SubsidiaryDeliveryOrderDetailEO> list = this.baseMapper.selectRendererPage(param);

        IPage<SubsidiaryDeliveryOrderDetailEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;
    }
}
