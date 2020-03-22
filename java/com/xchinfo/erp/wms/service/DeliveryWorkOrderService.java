package com.xchinfo.erp.wms.service;

import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.mapper.MaterialMapper;
import com.xchinfo.erp.bsc.mapper.WarehouseLocationMapper;
import com.xchinfo.erp.bsc.service.MachineService;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.bsc.service.WarehouseLocationService;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryWorkOrderEO;
import com.xchinfo.erp.scm.wms.entity.WorkOrderDetailEO;
import com.xchinfo.erp.srm.service.ScheduleOrderService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.wms.mapper.DeliveryWorkOrderMapper;
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
public class DeliveryWorkOrderService extends BaseServiceImpl<DeliveryWorkOrderMapper, DeliveryWorkOrderEO> {

    @Autowired
    private WorkOrderDetailMapper workOrderDetailMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private MachineService machineService;

    @Autowired
    private ScheduleOrderService scheduleOrderService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private WorkOrderDetailService workOrderDetailService;

    @Autowired
    private WarehouseLocationMapper warehouseLocationMapper;

    @Autowired
    private WarehouseLocationService warehouseLocationService;


    @Transactional(rollbackFor = Exception.class)
    public Result addByDeliveryOrderDetail(DeliveryOrderDetailEO entity, Long orgId, Integer type, UserEO user) {
        Result result = new Result();

        DeliveryWorkOrderEO deliveryWorkOrderFromDb = this.baseMapper.getByOrgIdAndDetailId(orgId, entity.getDeliveryDetailId(), type);
        if (deliveryWorkOrderFromDb != null) {
            deliveryWorkOrderFromDb.setNotDeliveryQuantity(entity.getRelDeliveryAmount());
            deliveryWorkOrderFromDb.setOrderDate(new Date());

            List<WorkOrderDetailEO> workOrderDetails = this.workOrderDetailMapper.getByWorkOrderIdAndType(deliveryWorkOrderFromDb.getWorkOrderId(), 2);
            if (workOrderDetails==null || workOrderDetails.size()==0) {
                deliveryWorkOrderFromDb.setStatus(0);
            } else {
                Double sumQuantity = 0d;
                for (WorkOrderDetailEO workOrderDetail : workOrderDetails) {
                    sumQuantity += Math.abs(workOrderDetail.getQuantity());
                }
                if (sumQuantity < deliveryWorkOrderFromDb.getNotDeliveryQuantity()) {
                    deliveryWorkOrderFromDb.setStatus(1);
                } else {
                    deliveryWorkOrderFromDb.setStatus(2);
                }
            }

            super.updateById(deliveryWorkOrderFromDb);
        } else {
            DeliveryWorkOrderEO deliveryWorkOrder = new DeliveryWorkOrderEO();
            deliveryWorkOrder.setWorkOrderId(UUID.randomUUID().toString());
            deliveryWorkOrder.setOrgId(orgId);
            deliveryWorkOrder.setType(type);
            deliveryWorkOrder.setRelationId(entity.getDeliveryDetailId());
            deliveryWorkOrder.setMaterialId(entity.getMaterialId());
            deliveryWorkOrder.setElementNo(entity.getElementNo());
            deliveryWorkOrder.setMaterialName(entity.getMaterialName());
            deliveryWorkOrder.setOrderDate(new Date());
            deliveryWorkOrder.setNotDeliveryQuantity(entity.getRelDeliveryAmount());
            deliveryWorkOrder.setHasDeliveryQuantity(0d);
            deliveryWorkOrder.setStatus(0);
            deliveryWorkOrder.setIsLock(0);
            deliveryWorkOrder.setLockUserId(user.getUserId());

            super.save(deliveryWorkOrder);
        }

        return result;
    }

    public List<DeliveryWorkOrderEO> listAll(Map map) {
        return this.baseMapper.listAll(map);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatch(List<DeliveryWorkOrderEO> deliveryWorkOrders, UserEO user) throws BusinessException {
        if (deliveryWorkOrders != null && deliveryWorkOrders.size() > 0) {

            if (deliveryWorkOrders.get(0).getRelationId()>0){
                ScheduleOrderEO scheduleOrder = this.scheduleOrderService.getById(deliveryWorkOrders.get(0).getRelationId());
                if (scheduleOrder.getOrgId().longValue() != user.getOrgId().longValue()) {
                    throw new BusinessException("排产单与用户归属机构不一致!<br/>");
                }
            }


//            MachineEO machine = this.machineService.getById(scheduleOrder.getMachineId());
//            if(machine != null) {
//                if (machine.getOrgId().longValue() != user.getOrgId().longValue()) {
//                    throw new BusinessException("设备:" + machine.getMachineName() + "与用户归属机构不一致!<br/>");
//                }
//            }

            String sqlStr = "";
            String errorMsg = "";
            for (DeliveryWorkOrderEO deliveryWorkOrder : deliveryWorkOrders) {
                if (deliveryWorkOrder.getOrgId().longValue() != user.getOrgId().longValue()) {
                    errorMsg += "出库工单:" + deliveryWorkOrder.getElementNo() + "与用户归属机构不一致!<br/>";
                }
                sqlStr += (deliveryWorkOrder.getMaterialId() + ",");
            }
            if (!errorMsg.equals("")) {
                throw new BusinessException(errorMsg);
            }

            if ("".equals(sqlStr)) {
                sqlStr = "(-1)";
            } else {
                sqlStr = "(" + sqlStr.substring(0, sqlStr.length() - 1) + ")";
            }
            List<MaterialEO> materials = this.materialMapper.getByMaterialIds(sqlStr);
            Map map = new HashedMap();
            if (materials != null && materials.size() > 0) {
                for (MaterialEO material : materials) {
                    if (material.getOrgId().longValue() != user.getOrgId().longValue()) {
                        errorMsg += "物料:" + material.getElementNo() + "与用户归属机构不一致!<br/>";
                    }
                    if (material.getStatus().intValue() == 0) {
                        errorMsg += "物料:" + material.getElementNo() + "被禁用!<br/>";
                    }

                    map.put(material.getMaterialId(), material);
                }
            }
            if (!errorMsg.equals("")) {
                throw new BusinessException(errorMsg);
            }

            for (DeliveryWorkOrderEO deliveryWorkOrder : deliveryWorkOrders) {
                if (map.keySet().contains(deliveryWorkOrder.getMaterialId())) {
                    MaterialEO material = (MaterialEO) map.get(deliveryWorkOrder.getMaterialId());
                    deliveryWorkOrder.setElementNo(material.getElementNo());
                    deliveryWorkOrder.setMaterialName(material.getMaterialName());
                    deliveryWorkOrder.setHasDeliveryQuantity(0d);
                    deliveryWorkOrder.setWorkOrderId(UUID.randomUUID().toString());
//                    if(machine != null) { // 暂时不用设备表对应值，直接使用传递进来的值，注释掉代码
//                        deliveryWorkOrder.setDeliveryWorkarea(machine.getStorageMaterialArea());
//                        deliveryWorkOrder.setDeliveryWorkroom(machine.getLocation());
//                    }
                }
            }
            super.saveBatch(deliveryWorkOrders);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result addDetail(WorkOrderDetailEO workOrderDetail, UserEO user) throws BusinessException {
        Result result = new Result();

        DeliveryWorkOrderEO deliveryWorkOrder = this.baseMapper.getByWorkOrderId(workOrderDetail.getWorkOrderId());
        if(deliveryWorkOrder != null) {
            if(deliveryWorkOrder.getOrgId().longValue() != user.getOrgId().longValue()) {
                throw new BusinessException("出库工单归属机构与用户归属机构不一致!");
            }
            if(deliveryWorkOrder.getStatus().intValue() == 2) {
                throw new BusinessException("出库工单已完成!");
            }
            if(deliveryWorkOrder.getStatus().intValue() == 3) {
                throw new BusinessException("出库工单已关闭!");
            }
            if(deliveryWorkOrder.getStatus().intValue() == 4) {
                throw new BusinessException("出库工单已取消!");
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

//            Double rest = deliveryWorkOrder.getNotDeliveryQuantity() - deliveryWorkOrder.getHasDeliveryQuantity();
//            if(workOrderDetail.getQuantity() > rest) {
//                throw new BusinessException("本次出库数量" + workOrderDetail.getQuantity() + "超出剩余可出库数量" + rest
//                        +",超出数量为" + (workOrderDetail.getQuantity() - rest) + "!");
//            }

            WorkOrderDetailEO workOrderDetailFromDb = this.workOrderDetailService.getById(workOrderDetail.getWorkOrderDetailId());
            if(workOrderDetailFromDb.getRemainingQuantity() < workOrderDetail.getQuantity()) {
                throw new BusinessException("剩余数量" + workOrderDetailFromDb.getRemainingQuantity() + "不足,无法出库!");
            } else {
                List<WorkOrderDetailEO> workOrderDetails = this.workOrderDetailMapper.getByWorkOrderIdAndType(workOrderDetail.getWorkOrderId(), 2);
                if(workOrderDetails == null) {
                    workOrderDetails = new ArrayList<>();
                }
                workOrderDetails.add(workOrderDetail);
                Integer status = this.workOrderDetailService.getStatus(workOrderDetails, deliveryWorkOrder.getNotDeliveryQuantity());
                deliveryWorkOrder.setStatus(status);
                deliveryWorkOrder.setHasDeliveryQuantity(deliveryWorkOrder.getHasDeliveryQuantity() + workOrderDetail.getQuantity());
                super.updateById(deliveryWorkOrder);

                workOrderDetailFromDb.setRemainingQuantity(workOrderDetailFromDb.getRemainingQuantity() - workOrderDetail.getQuantity());
                this.workOrderDetailService.updateById(workOrderDetailFromDb);

                WarehouseLocationEO warehouseLocation = this.warehouseLocationMapper.getByBarCode(workOrderDetail.getWarehouseLocationBarCode());
                Integer restContain = warehouseLocation.getStorageContainer() - workOrderDetail.getContain();
                if(restContain.intValue() < 0) {
                    restContain = 0;
                }

//                warehouseLocation.setStorageContainer(restContain);

                Integer count = this.workOrderDetailMapper.getCountByWarehouseLocationBarCode(workOrderDetail.getWarehouseLocationBarCode());
                warehouseLocation.setStorageContainer(count);
                this.warehouseLocationService.updateById(warehouseLocation);

                workOrderDetail.setWorkOrderDetailId(null);
                workOrderDetail.setRemainingQuantity(0d);
                workOrderDetail.setElementNo(material.getElementNo());
                workOrderDetail.setMaterialName(material.getMaterialName());
                workOrderDetail.setOperateTime(new Date());
                workOrderDetail.setOperateUserId(user.getUserId());
                workOrderDetail.setWarehouseLocationId(warehouseLocation.getWarehouseLocationId());
                workOrderDetail.setWarehouseId(warehouseLocation.getWarehouseId());
                this.workOrderDetailService.save(workOrderDetail);
            }
        }

        result.setData(deliveryWorkOrder);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result addTempDetail(WorkOrderDetailEO workOrderDetail, UserEO user) {
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

        WorkOrderDetailEO workOrderDetailFromDb = this.workOrderDetailService.getById(workOrderDetail.getWorkOrderDetailId());
        if(workOrderDetailFromDb.getRemainingQuantity() < workOrderDetail.getQuantity()) {
            throw new BusinessException("剩余数量" + workOrderDetailFromDb.getRemainingQuantity() + "不足,无法出库!");
        } else {
            workOrderDetailFromDb.setRemainingQuantity(workOrderDetailFromDb.getRemainingQuantity() - workOrderDetail.getQuantity());
            this.workOrderDetailService.updateById(workOrderDetailFromDb);

            WarehouseLocationEO warehouseLocation = this.warehouseLocationMapper.getByBarCode(workOrderDetail.getWarehouseLocationBarCode());
            Integer restContain = warehouseLocation.getStorageContainer() - workOrderDetail.getContain();
            if(restContain.intValue() < 0) {
                restContain = 0;
            }
//            warehouseLocation.setStorageContainer(restContain);

            Integer count = this.workOrderDetailMapper.getCountByWarehouseLocationBarCode(workOrderDetail.getWarehouseLocationBarCode());
            warehouseLocation.setStorageContainer(count);
            this.warehouseLocationService.updateById(warehouseLocation);

            workOrderDetail.setWorkOrderDetailId(null);
            workOrderDetail.setRemainingQuantity(0d);
            workOrderDetail.setElementNo(material.getElementNo());
            workOrderDetail.setMaterialName(material.getMaterialName());
            workOrderDetail.setOperateTime(new Date());
            workOrderDetail.setOperateUserId(user.getUserId());
            workOrderDetail.setWarehouseLocationId(warehouseLocation.getWarehouseLocationId());
            workOrderDetail.setWarehouseId(warehouseLocation.getWarehouseId());
            this.workOrderDetailService.save(workOrderDetail);
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result closeByWorkOrderId(String workOrderId, UserEO user) throws BusinessException {
        Result result = new Result();

        DeliveryWorkOrderEO deliveryWorkOrder = this.baseMapper.getByWorkOrderId(workOrderId);
        if(deliveryWorkOrder != null) {
            if(deliveryWorkOrder.getOrgId().longValue() != user.getOrgId().longValue()) {
                throw new BusinessException("出库工单归属机构与用户归属机构不一致!");
            }

            if(deliveryWorkOrder.getStatus().intValue()==0 && deliveryWorkOrder.getHasDeliveryQuantity().doubleValue()==0) {
                super.removeById(deliveryWorkOrder);
            }

            deliveryWorkOrder.setStatus(3);
            super.updateById(deliveryWorkOrder);
        } else {
            throw new BusinessException("出库工单不存在!");
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result urgeByWorkOrderId(String workOrderId, UserEO user) throws BusinessException {
        Result result = new Result();

        DeliveryWorkOrderEO deliveryWorkOrder = this.baseMapper.getByWorkOrderId(workOrderId);
        if(deliveryWorkOrder != null) {
            if(deliveryWorkOrder.getOrgId().longValue() != user.getOrgId().longValue()) {
                throw new BusinessException("出库工单归属机构与用户归属机构不一致!");
            }

            if(deliveryWorkOrder.getStatus().intValue() == 2) {
                throw new BusinessException("出库工单已完成!");
            }
            if(deliveryWorkOrder.getStatus().intValue() == 3) {
                throw new BusinessException("出库工单已关闭!");
            }
            if(deliveryWorkOrder.getStatus().intValue() == 4) {
                throw new BusinessException("出库工单已取消!");
            }

            deliveryWorkOrder.setUrgeTimes(deliveryWorkOrder.getUrgeTimes()+1);
            deliveryWorkOrder.setLastUrgeTime(new Date());
            super.updateById(deliveryWorkOrder);
        } else {
            throw new BusinessException("出库工单不存在!");
        }

        return result;
    }
}
