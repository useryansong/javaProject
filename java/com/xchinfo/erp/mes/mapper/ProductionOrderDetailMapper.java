package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.mes.entity.ProductionOrderDetailEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface ProductionOrderDetailMapper extends BaseMapper<ProductionOrderDetailEO> {

    /**
     * 根据订单删除订单明细
     *
     * @param orderId
     * @return
     */
    @Delete("delete from mes_production_order_detail where order_id = #{orderId}")
    Integer removeByOrder(Long orderId);

    /**
     * 根据订单查找
     *
     * @param orderId
     * @return
     */
    @Select("select od.*, m.material_code, m.material_name, m.specification " +
            "from mes_production_order_detail od " +
            "inner join bsc_material m on od.material_id = m.material_id " +
            "where od.order_id = #{orderId}")
    List<ProductionOrderDetailEO> selectByOrder(Long orderId);
}
