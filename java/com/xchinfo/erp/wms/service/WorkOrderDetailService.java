package com.xchinfo.erp.wms.service;

import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.mapper.MaterialMapper;
import com.xchinfo.erp.bsc.service.WarehouseLocationService;
import com.xchinfo.erp.scm.wms.entity.WorkOrderDetailEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.wms.mapper.WorkOrderDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.*;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@Service
public class WorkOrderDetailService extends BaseServiceImpl<WorkOrderDetailMapper, WorkOrderDetailEO> {

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @Autowired
    private MaterialMapper materialMapper;

    static Set<Long> lockSet = new HashSet<>();/** 防止页面发送多条相同的请求导致重复入库，新增一个程序锁 */


    public List<WorkOrderDetailEO> getPage(Map map) {
        return this.baseMapper.getPage(map);
    }

    public List<WorkOrderDetailEO> getAmountPage(Map map) {
        return this.baseMapper.getAmountPage(map);
    }

    public Integer getStatus(List<WorkOrderDetailEO> workOrderDetails, Double notQuantity) {
        Integer status;
        Double sumQuantity = 0d;
        for(WorkOrderDetailEO workOrderDetail : workOrderDetails) {
            sumQuantity += Math.abs(workOrderDetail.getQuantity());
        }

        if(sumQuantity != 0) {
            if(sumQuantity < notQuantity) {
                status = 1;
            } else {
                status = 2;
            }
        } else {
            status = 0;
        }

        return status;
    }

    public WorkOrderDetailEO getWorkOrderDetails(Long materialId) throws BusinessException {
        WorkOrderDetailEO workOrderDetail = this.baseMapper.getByMaterialId(materialId);
        if(workOrderDetail != null) {
            List<WorkOrderDetailEO> workOrderDetails = this.baseMapper.getAllByMaterialIdAndType(materialId, 1);
            workOrderDetail.setRecords(workOrderDetails);
        }

        return workOrderDetail;
    }

    @Transactional(rollbackFor = Exception.class)
    public void transfer(WorkOrderDetailEO workOrderDetail, UserEO user) throws BusinessException {
        try {
            if(lockSet.contains(workOrderDetail.getWorkOrderDetailId())){
                throw new BusinessException("当前数据正在操作中,无法操作请刷新后重试!");
            } else {
                lockSet.add(workOrderDetail.getWorkOrderDetailId());

                MaterialEO material = this.materialMapper.getByElementNoAndOrgId(workOrderDetail.getElementNo(), user.getOrgId());
                if(material == null) {
                    throw new BusinessException("物料不存在,请检查!");
                }

                WorkOrderDetailEO outWorkOrderDetail = this.getById(workOrderDetail.getWorkOrderDetailId());
                WarehouseLocationEO outLocation = this.warehouseLocationService.getById(outWorkOrderDetail.getWarehouseLocationId());
                WarehouseLocationEO inLocation = this.warehouseLocationService.getById(workOrderDetail.getInLocationId());
                if(outLocation == null) {
                    throw new BusinessException("移出库位不存在,请检查!");
                }
                if(inLocation == null) {
                    throw new BusinessException("移入库位不存在,请检查!");
                }

                if(outLocation.getOrgId().longValue() != user.getOrgId().longValue()) {
                    throw new BusinessException("移出库位归属机构与用户归属机构不一致!");
                }
                if(inLocation.getOrgId().longValue() != user.getOrgId().longValue()) {
                    throw new BusinessException("移入库位归属机构与用户归属机构不一致!");
                }

                if(outLocation.getOrgId().longValue() != inLocation.getOrgId().longValue()) {
                    throw new BusinessException("移出库位归属机构与移入库位归属机构不一致!");
                }

//        Double amount = this.baseMapper.getByMaterialIdAndWarehouseLocationId(material.getMaterialId(), workOrderDetail.getOutLocationId());
                Double remainingQuantity = outWorkOrderDetail.getRemainingQuantity();
                if(remainingQuantity < workOrderDetail.getTransferQuantity()) {
                    throw new BusinessException("移出库位的库存不足,剩余数量为" + remainingQuantity + "!");
                }

                // 更新出库明细的剩余数量
                outWorkOrderDetail.setRemainingQuantity(remainingQuantity - workOrderDetail.getTransferQuantity());
                this.updateById(outWorkOrderDetail);

                // 入库工单明细
                WorkOrderDetailEO inWod = new WorkOrderDetailEO();
                inWod.setMaterialId(material.getMaterialId());
                inWod.setElementNo(material.getElementNo());
                inWod.setMaterialName(material.getMaterialName());
                inWod.setWarehouseLocationId(workOrderDetail.getInLocationId());
                inWod.setWarehouseId(inLocation.getWarehouseId());
                inWod.setWarehouseLocationBarCode(inLocation.getBarCode());
                inWod.setType(1);
                inWod.setQuantity(workOrderDetail.getTransferQuantity());
                inWod.setContain(0);
                inWod.setSnp(0);
                inWod.setOperateTime(outWorkOrderDetail.getOperateTime());
                inWod.setOperateUserId(user.getUserId());
                inWod.setRemainingQuantity(workOrderDetail.getTransferQuantity());
                inWod.setRemark("由"+outLocation.getBarCode()+"库位转入"+workOrderDetail.getTransferQuantity());
                inWod.setTransferOutDetailId(workOrderDetail.getWorkOrderDetailId());
                inWod.setOperationType(5);

                // 出库工单明细
                WorkOrderDetailEO outWod = new WorkOrderDetailEO();
                outWod.setMaterialId(material.getMaterialId());
                outWod.setElementNo(material.getElementNo());
                outWod.setMaterialName(material.getMaterialName());
                outWod.setWarehouseLocationId(outWorkOrderDetail.getWarehouseLocationId());
                outWod.setWarehouseId(outLocation.getWarehouseId());
                outWod.setWarehouseLocationBarCode(outLocation.getBarCode());
                outWod.setType(2);
                outWod.setQuantity(workOrderDetail.getTransferQuantity());
                outWod.setContain(0);
                outWod.setSnp(0);
                outWod.setOperateTime(new Date());
                outWod.setOperateUserId(user.getUserId());
                outWod.setRemainingQuantity(0d);
                outWod.setRemark("转出"+workOrderDetail.getTransferQuantity()+"到"+inLocation.getBarCode()+"库位");
                outWod.setOperationType(6);

                List<WorkOrderDetailEO> list = new ArrayList<>();
                list.add(inWod);
                list.add(outWod);
                this.saveBatch(list);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            lockSet.remove(workOrderDetail.getWorkOrderDetailId());
        }
    }

    public List<WorkOrderDetailEO> getByWarehouseLocationBarCode(String warehouseLocationBarCode) {
        return this.baseMapper.getByWarehouseLocationBarCode(warehouseLocationBarCode);
    }
}
