package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface SubsidiaryDeliveryOrderDetailMapper extends BaseMapper<SubsidiaryDeliveryOrderDetailEO> {
    /**
     * 根据出库单查找出库单明细
     *
     * @param deliveryId
     * @return
     */
    @Select("select dod.*, w.warehouse_name, wl.location_name as warehouse_location_name, mu.unit_name " +
            "from wms_subsidiary_delivery_order_detail dod " +
            "left join bsc_warehouse w on dod.warehouse_id = w.warehouse_id " +
            "left join bsc_warehouse_location wl on dod.warehouse_location_id = wl.warehouse_location_id " +
            "left join bsc_measurement_unit mu on dod.unit_id = mu.unit_id " +
            "where dod.delivery_order_id = #{deliveryId}")
    List<SubsidiaryDeliveryOrderDetailEO> selectByDelivery(Long deliveryId);

    /**
     * 根据出库单删除
     *
     * @param deliveryId
     * @return
     */
    @Delete("delete from wms_subsidiary_delivery_order_detail where delivery_order_id = #{deliveryId}")
    int deleteByDelivery(Long deliveryId);

    /**
     * 根据出库单明细id及状态更新出库单明细状态
     * @return
     */
    @Update("update wms_subsidiary_delivery_order_detail set status = #{status} where delivery_detail_id = #{deliveryDetailId}")
    int updateStatusById(@Param("deliveryDetailId") Long deliveryDetailId, @Param("status") Integer status);

    /**
     * 删除物料台账
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id = #{id} and voucher_type in (7,8,9,10,12)")
    boolean deleteStockById(@Param("id") Long id);

    /**
     * 根据明细单ID查找主表数据
     * @param id
     * @return
     */
    @Select("select * from   wms_subsidiary_delivery_order where delivery_id = " +
            "(select delivery_order_id from wms_subsidiary_delivery_order_detail where delivery_detail_id = #{id})")
    SubsidiaryDeliveryOrderEO selectDeliveryById(Long id);

    /***
     *查询库存物料数量
     * @param id
     * @return
     */
    List<StockAccountEO> getMessage(@Param("id") Long id);

    /**
     * 查询所有销售出库明细
     *
     * @return
     */
    @Select("select * from wms_subsidiary_delivery_order_detail t where t.delivery_order_id in (" +
            "select delivery_id from wms_subsidiary_delivery_order where delivery_type ='1') and t.status='1'")
    List<SubsidiaryDeliveryOrderDetailEO> getDeliveryDetailId();

    List<SubsidiaryDeliveryOrderDetailEO> getByDeliveryOrderId(@Param("deliveryOrderId") Long deliveryOrderId);
    
    /**
     * 查找待出库记录分页
     *
     * @return
     */
    List<SubsidiaryDeliveryOrderDetailEO> selectRendererPage(Map param);
}
