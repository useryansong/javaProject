package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.ReceiveWorkOrderEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhongy
 * @date 2019/12/12
 */
@Mapper
public interface ReceiveWorkOrderMapper extends BaseMapper<ReceiveWorkOrderEO> {

    ReceiveWorkOrderEO getByOrgIdAndDetailId(@Param("orgId") Long orgId, @Param("relationId") Long relationId, @Param("type") Integer type);

    ReceiveWorkOrderEO getByWorkOrderId(@Param("workOrderId") String workOrderId);
}
