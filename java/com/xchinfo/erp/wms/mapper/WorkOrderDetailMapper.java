package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.WorkOrderDetailEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@Mapper
public interface WorkOrderDetailMapper extends BaseMapper<WorkOrderDetailEO> {

    List<WorkOrderDetailEO> getByWorkOrderIdAndType(@Param("workOrderId") String workOrderId, @Param("type") Integer type);

    WorkOrderDetailEO getByMaterialId(@Param("materialId") Long materialId);

    List<WorkOrderDetailEO> getAllByMaterialIdAndType(@Param("materialId") Long materialId, @Param("type") Integer type);

    Integer getSumContainByWarehouseLocationIdAndType(@Param("warehouseLocationId") Long warehouseLocationId, @Param("type") Integer type);

    Double getByMaterialIdAndWarehouseLocationId(@Param("materialId") Long materialId, @Param("warehouseLocationId") Long warehouseLocationId);

    List<WorkOrderDetailEO> getPage(Map map);

    List<WorkOrderDetailEO> getAmountPage(Map map);

    Integer getCountByWarehouseLocationBarCode(@Param("warehouseLocationBarCode") String warehouseLocationBarCode);

    List<WorkOrderDetailEO> getByWarehouseLocationBarCode(@Param("warehouseLocationBarCode") String warehouseLocationBarCode);
}
