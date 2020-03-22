package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.mes.entity.ProductionOrderEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface ProductionOrderMapper extends BaseMapper<ProductionOrderEO> {

    /**
     * 更新排场状态
     *
     * @param orderId
     * @return
     */
    @Update("update mes_production_order set status = 2 where order_id = #{orderId}")
    Integer updateStatusForSchedule(Long orderId);
}
