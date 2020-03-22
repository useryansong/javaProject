package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * @author zhongye
 * @date 2019/5/11
 */
@Mapper
public interface DeliveryPlanMapper extends BaseMapper<DeliveryPlanEO> {

    @Select("select * from srm_delivery_plan where purchase_order_id = #{purchaseOrderId}")
    List<DeliveryPlanEO> getByPurchaseOrderId(Long purchaseOrderId);
}
