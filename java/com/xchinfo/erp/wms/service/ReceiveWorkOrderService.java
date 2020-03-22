package com.xchinfo.erp.wms.service;

import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.mapper.MaterialMapper;
import com.xchinfo.erp.bsc.mapper.WarehouseLocationMapper;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.bsc.service.WarehouseLocationService;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveWorkOrderEO;
import com.xchinfo.erp.scm.wms.entity.WorkOrderDetailEO;
import com.xchinfo.erp.srm.mapper.ScheduleOrderMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.wms.mapper.ReceiveWorkOrderMapper;
import com.xchinfo.erp.wms.mapper.WorkOrderDetailMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.*;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@Service
public class ReceiveWorkOrderService extends BaseServiceImpl<ReceiveWorkOrderMapper, ReceiveWorkOrderEO> {

    @Autowired
    private WorkOrderDetailMapper workOrderDetailMapper;

    @Autowired
    private WarehouseLocationMapper warehouseLocationMapper;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private WorkOrderDetailService workOrderDetailService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @Autowired
    private ScheduleOrderMapper scheduleOrderMapper;

    @Autowired
    private MaterialMapper materialMapper;


    @Transactional(rollbackFor = Exception.class)
    public Result addByReceiveOrderDetail(ReceiveOrderDetailEO entity, Long orgId, Integer type, UserEO user) {
        Result result = new Result();

        ReceiveWorkOrderEO receiveWorkOrderFromDb = this.baseMapper.getByOrgIdAndDetailId(orgId, entity.getReceiveDetailId(), type);
        if(receiveWorkOrderFromDb != null) {
            receiveWorkOrderFromDb.setNotReceiveQuantity(entity.getRelReceiveAmount());
            receiveWorkOrderFromDb.setOrderDate(new Date());

            List<WorkOrderDetailEO> workOrderDetails = this.workOrderDetailMapper.getByWorkOrderIdAndType(receiveWorkOrderFromDb.getWorkOrderId(), 1);
            if(workOrderDetails==null || workOrderDetails.size()==0) {
                receiveWorkOrderFromDb.setStatus(0);
            } else {
                Double sumQuantity = 0d;
                for(WorkOrderDetailEO workOrderDetail : workOrderDetails) {
                    sumQuantity += Math.abs(workOrderDetail.getQuantity());
                }
                if(sumQuantity < receiveWorkOrderFromDb.getNotReceiveQuantity()) {
                    receiveWorkOrderFromDb.setStatus(1);
                } else {
                    receiveWorkOrderFromDb.setStatus(2);
                }
            }

            super.updateById(receiveWorkOrderFromDb);
        } else {
            ReceiveWorkOrderEO receiveWorkOrder = new ReceiveWorkOrderEO();
            receiveWorkOrder.setWorkOrderId(UUID.randomUUID().toString());
            receiveWorkOrder.setOrgId(orgId);
            receiveWorkOrder.setType(type);
            receiveWorkOrder.setRelationId(entity.getReceiveDetailId());
            receiveWorkOrder.setMaterialId(entity.getMaterialId());
            receiveWorkOrder.setElementNo(entity.getElementNo());
            receiveWorkOrder.setMaterialName(entity.getMaterialName());
            receiveWorkOrder.setOrderDate(new Date());
            receiveWorkOrder.setNotReceiveQuantity(entity.getRelReceiveAmount());
            receiveWorkOrder.setHasReceiveQuantity(0d);
            receiveWorkOrder.setStatus(0);
            receiveWorkOrder.setIsLock(0);
            receiveWorkOrder.setLockUserId(user.getUserId());

            super.save(receiveWorkOrder);
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result addDetail(WorkOrderDetailEO workOrderDetail, UserEO user) throws BusinessException {
        Result result = new Result();

        ReceiveWorkOrderEO receiveWorkOrder = this.baseMapper.getByWorkOrderId(workOrderDetail.getWorkOrderId());
        if(receiveWorkOrder != null) {
            if(receiveWorkOrder.getOrgId().longValue() != user.getOrgId().longValue()) {
                throw new BusinessException("入库工单归属机构与用户归属机构不一致!");
            }
            if(receiveWorkOrder.getStatus().intValue() == 2) {
                throw new BusinessException("入库工单已完成!");
            }
            if(receiveWorkOrder.getStatus().intValue() == 3) {
                throw new BusinessException("入库工单已关闭!");
            }
            if(receiveWorkOrder.getStatus().intValue() == 4) {
                throw new BusinessException("入库工单已取消!");
            }

            MaterialEO material = this.materialService.getById(workOrderDetail.getMaterialId());
            if(material != null) {
                if(material.getOrgId().longValue() != user.getOrgId().longValue()) {
                    throw new BusinessException("物料归属机构与用户归属机构不一致!");
                }
                if(material.getStatus().intValue() == 0) {
                    throw new BusinessException("物料已被禁用!");
                }
            } else {
                throw new BusinessException("物料不存在!");
            }

             Double rest = receiveWorkOrder.getNotReceiveQuantity() - receiveWorkOrder.getHasReceiveQuantity();
             if(workOrderDetail.getQuantity() > rest) {
                 throw new BusinessException("本次入库数量" + workOrderDetail.getQuantity() + ",超出剩余可入库数量" + rest
                         +",超出数量为" + (workOrderDetail.getQuantity() - rest) + "!");
             }

            WarehouseLocationEO warehouseLocation = this.warehouseLocationMapper.getByBarCode(workOrderDetail.getWarehouseLocationBarCode());
            if(warehouseLocation.getOrgId().longValue() != user.getOrgId().longValue()) {
                throw new BusinessException("库位归属机构与用户归属机构不一致!");
            }

            Integer sumContain = this.workOrderDetailMapper.getSumContainByWarehouseLocationIdAndType(workOrderDetail.getWarehouseLocationId(), 1);
            if(sumContain == null) {
                sumContain = 0;
            }
//            Integer restContain = warehouseLocation.getMaxContain() - sumContain;
//            if(restContain < workOrderDetail.getContain()) {
//                throw new BusinessException("本次入库容器数量" + workOrderDetail.getContain() + "超出剩余可入库容器数量" + restContain
//                        +",超出容器数量为" + (workOrderDetail.getContain() - restContain) + "!");
//            }

//            warehouseLocation.setStorageContainer(sumContain + workOrderDetail.getContain());

            Integer count = this.workOrderDetailMapper.getCountByWarehouseLocationBarCode(workOrderDetail.getWarehouseLocationBarCode());
            warehouseLocation.setStorageContainer(count);
            this.warehouseLocationService.updateById(warehouseLocation);

            List<WorkOrderDetailEO> workOrderDetails = this.workOrderDetailMapper.getByWorkOrderIdAndType(workOrderDetail.getWorkOrderId(), 1);
            if(workOrderDetails == null) {
                workOrderDetails = new ArrayList<>();
            }
            workOrderDetails.add(workOrderDetail);
            Integer status = this.workOrderDetailService.getStatus(workOrderDetails, receiveWorkOrder.getNotReceiveQuantity());
            receiveWorkOrder.setStatus(status);
            receiveWorkOrder.setHasReceiveQuantity(receiveWorkOrder.getHasReceiveQuantity() + workOrderDetail.getQuantity());
            super.updateById(receiveWorkOrder);

            workOrderDetail.setWarehouseLocationId(warehouseLocation.getWarehouseLocationId());
            workOrderDetail.setWarehouseId(warehouseLocation.getWarehouseId());
            workOrderDetail.setOperateTime(new Date());
            workOrderDetail.setOperateUserId(user.getUserId());
            workOrderDetail.setRemainingQuantity(workOrderDetail.getQuantity());
            workOrderDetail.setElementNo(material.getElementNo());
            workOrderDetail.setMaterialName(material.getMaterialName());

            this.workOrderDetailService.save(workOrderDetail);
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result addTempDetail(WorkOrderDetailEO workOrderDetail, UserEO user) throws BusinessException {
        Result result = new Result();

        MaterialEO material = this.materialService.getById(workOrderDetail.getMaterialId());
        if(material != null) {
            if(material.getOrgId().longValue() != user.getOrgId().longValue()) {
                throw new BusinessException("物料归属机构与用户归属机构不一致!");
            }
            if(material.getStatus().intValue() == 0) {
                throw new BusinessException("物料已被禁用!");
            }
        } else {
            throw new BusinessException("物料不存在!");
        }

        WarehouseLocationEO warehouseLocation = this.warehouseLocationMapper.getByBarCode(workOrderDetail.getWarehouseLocationBarCode());
        if(warehouseLocation.getOrgId().longValue() != user.getOrgId().longValue()) {
            throw new BusinessException("库位归属机构与用户归属机构不一致!");
        }

        workOrderDetail.setWarehouseId(warehouseLocation.getWarehouseId());
        workOrderDetail.setWarehouseLocationId(warehouseLocation.getWarehouseLocationId());
        workOrderDetail.setOperateTime(new Date());
        workOrderDetail.setOperateUserId(user.getUserId());
        workOrderDetail.setRemainingQuantity(workOrderDetail.getQuantity());
        workOrderDetail.setElementNo(material.getElementNo());
        workOrderDetail.setMaterialName(material.getMaterialName());
        this.workOrderDetailService.save(workOrderDetail);

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result addBatch(List<ReceiveWorkOrderEO> receiveWorkOrders, UserEO user) {
        Result result = new Result();

        String errorMsg = "";
        if(receiveWorkOrders!=null && receiveWorkOrders.size()>0) {
            String sqlStr1 = "";
            String sqlStr2 = "";
            for(ReceiveWorkOrderEO receiveWorkOrder : receiveWorkOrders) {
                sqlStr1 += (receiveWorkOrder.getRelationId() + ",");
                sqlStr2 += (receiveWorkOrder.getMaterialId() + ",");
            }

            if("".equals(sqlStr1)) {
                sqlStr1 = "(-1)";
            } else {
                sqlStr1 = "(" + sqlStr1.substring(0, sqlStr1.length()-1) + ")";
            }
            List<ScheduleOrderEO> scheduleOrders = this.scheduleOrderMapper.getScheduleOrderByIds(sqlStr1);

            if("".equals(sqlStr2)) {
                sqlStr2 = "(-1)";
            } else {
                sqlStr2 = "(" + sqlStr2.substring(0, sqlStr2.length()-1) + ")";
            }
            List<MaterialEO> materials = this.materialMapper.getByMaterialIds(sqlStr2);

            if(scheduleOrders!=null && scheduleOrders.size()>0) {
                for(ScheduleOrderEO scheduleOrder : scheduleOrders) {
                    if(scheduleOrder.getOrgId().longValue() != user.getOrgId().longValue()) {
                        errorMsg += "排产单" + scheduleOrder.getVoucherNo() + "归属机构与用户归属机构不一致!<br/>";
                    }
                }
            }
            if(!"".equals(errorMsg)) {
                throw new BusinessException(errorMsg);
            }

            Map map = new HashedMap();
            if(materials!=null && materials.size()>0) {
                for(MaterialEO material : materials) {
                    if(material.getOrgId().longValue() != user.getOrgId().longValue()) {
                        errorMsg += "物料" + material.getElementNo() + "归属机构与用户归属机构不一致!<br/>";
                    }
                    if(material.getStatus().intValue() == 0) {
                        errorMsg += "物料" + material.getElementNo() + "已被禁用!<br/>";
                    }
                    map.put(material.getMaterialId(), material);
                }
            }
            if(!"".equals(errorMsg)) {
                throw new BusinessException(errorMsg);
            }

            for(ReceiveWorkOrderEO receiveWorkOrder : receiveWorkOrders) {
                if (map.keySet().contains(receiveWorkOrder.getMaterialId())) {
                    MaterialEO material = (MaterialEO) map.get(receiveWorkOrder.getMaterialId());
                    receiveWorkOrder.setElementNo(material.getElementNo());
                    receiveWorkOrder.setMaterialName(material.getMaterialName());
                    receiveWorkOrder.setWorkOrderId(UUID.randomUUID().toString());
                }
            }
            super.saveBatch(receiveWorkOrders);
        }

        return result;
    }
}
