package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.DeliveryWorkOrderEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@Mapper
public interface DeliveryWorkOrderMapper extends BaseMapper<DeliveryWorkOrderEO> {

    DeliveryWorkOrderEO getByOrgIdAndDetailId(@Param("orgId") Long orgId, @Param("relationId") Long relationId, @Param("type") Integer type);

    List<DeliveryWorkOrderEO> listAll(Map map);

    DeliveryWorkOrderEO getByWorkOrderId(@Param("workOrderId") String workOrderId);

    List<DeliveryWorkOrderEO> getByWorkOrderIds(@Param("workOrderIds") Set set);
}
