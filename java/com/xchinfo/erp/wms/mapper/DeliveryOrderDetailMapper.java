package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.MaterialReceiveEO;
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
public interface DeliveryOrderDetailMapper extends BaseMapper<DeliveryOrderDetailEO> {
    /**
     * 根据出库单查找出库单明细
     *
     * @param deliveryId
     * @return
     */
    @Select("select dod.*,ma.project_no,ma.snp, w.warehouse_name, wl.location_name as warehouse_location_name, mu.unit_name, " +
            "mr.stamping_material_name,mr.stamping_element_no,mr.inventory_coding,mr.material_name as original_material_name ,mr.element_no as original_element_no,mr.inventory_code as original_inventory_code " +
            "from wms_delivery_order_detail dod " +
            "left join bsc_warehouse w on dod.warehouse_id = w.warehouse_id " +
            "left join bsc_warehouse_location wl on dod.warehouse_location_id = wl.warehouse_location_id " +
            "left join bsc_measurement_unit mu on dod.unit_id = mu.unit_id " +
            "left join bsc_material ma on ma.material_id = dod.material_id " +
            "left join wms_original_material_receive mr on mr.delivery_detail_id= dod.delivery_detail_id " +
            "where dod.delivery_order_id = #{deliveryId}")
    List<DeliveryOrderDetailEO> selectByDelivery(Long deliveryId);

    /**
     * 根据出库单删除
     *
     * @param deliveryId
     * @return
     */
    @Delete("delete from wms_delivery_order_detail where delivery_order_id = #{deliveryId}")
    int deleteByDelivery(Long deliveryId);

    /**
     * 根据出库单明细id及状态更新出库单明细状态
     * @return
     */
    @Update("update wms_delivery_order_detail set status = #{status} where delivery_detail_id = #{deliveryDetailId}")
    int updateStatusById(@Param("deliveryDetailId") Long deliveryDetailId, @Param("status") Integer status);

    /**
     * 删除物料台账
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id = #{id} and voucher_type in (7,8,9,12)")
    boolean deleteStockById(@Param("id") Long id);

    /**
     * 根据明细单ID查找主表数据
     * @param id
     * @return
     */
    @Select("select * from   wms_delivery_order where delivery_id = " +
            "(select delivery_order_id from wms_delivery_order_detail where delivery_detail_id = #{id})")
    DeliveryOrderEO selectDeliveryById(Long id);

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
    @Select("select * from wms_delivery_order_detail t where t.delivery_order_id in (" +
            "select delivery_id from wms_delivery_order where delivery_type ='1') and t.status='1'")
    List<DeliveryOrderDetailEO> getDeliveryDetailId();

    List<DeliveryOrderDetailEO> getByDeliveryOrderId(@Param("deliveryOrderId") Long deliveryOrderId, @Param("userId") Long userId);
    /**
     * 查找待出库记录分页
     *
     * @return
     */
    List<DeliveryOrderDetailEO> selectRendererPage(Map param);

    List<DeliveryOrderDetailEO> getByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId);

    @Delete("delete from wms_delivery_order_detail where delivery_order_id = #{deliveryOrderId}")
    void removeByDeliveryOrderId(Long deliveryOrderId);

    @Update("update wms_delivery_order_detail set status = #{status} where delivery_order_id = #{deliveryOrderId}")
    void updateStatusByDeliveryOrderId(@Param("status") Integer status, @Param("deliveryOrderId") Long deliveryOrderId);

    @Select("select delivery_order_id, sum(delivery_amount) as delivery_amount " +
            "from wms_delivery_order_detail " +
            "where delivery_order_id in ${sqlStr} " +
            "group by delivery_order_id ")
    List<DeliveryOrderDetailEO> getSumDeliveryAmountByDeliveryOrderIds(@Param("sqlStr") String sqlStr);

    /**
     * 根据出库单明细是否全部完成更新出库单状态
     * @return
     */
    @Update("UPDATE wms_delivery_order SET STATUS =  #{status} " +
            "WHERE delivery_id = (SELECT delivery_order_id FROM wms_delivery_order_detail WHERE delivery_detail_id = #{deliveryDetailId}) " +
            "AND (SELECT count(1) FROM wms_delivery_order_detail WHERE delivery_order_id = (SELECT delivery_order_id " +
            "FROM wms_delivery_order_detail WHERE delivery_detail_id = #{deliveryDetailId})AND STATUS != 2 ) = 0")
    int updateDeliveryOrderStatusById(@Param("deliveryDetailId") Long deliveryDetailId, @Param("status") Integer status);

    /**
     * 查找原材料表物料信息
     * @return
     */
    @Select(" select material_id,material_name,element_no,inventory_code from bsc_material where status=1 and inventory_code = ( " +
            "select Inventory_coding from  mes_stamping_material_consumption_quota where stamping_material_consumption_quota_id=#{stampingId}) " +
            " and org_id = ( select org_id from  mes_stamping_material_consumption_quota where stamping_material_consumption_quota_id=#{stampingId})")
    MaterialReceiveEO selectorigina(Long stampingId);

    /**
     * 查找冲压耗用表物料信息
     * @return
     */
    @Select(" select material_id,material_name,element_no from bsc_material where status=1 and element_no = ( " +
            "select element_no from  mes_stamping_material_consumption_quota where stamping_material_consumption_quota_id=#{stampingId})" +
            "and org_id = ( select org_id from  mes_stamping_material_consumption_quota where stamping_material_consumption_quota_id=#{stampingId})")
    MaterialReceiveEO selectstamping(Long stampingId);


    List<DeliveryOrderDetailEO> getByDeliveryOrderIds(@Param("deliveryOrderIds") String deliveryOrderIds, @Param("userId") Long userId, @Param("orderField") String orderField);

    List<DeliveryOrderDetailEO> getByMaterialIdsAndDeliveryDate(@Param("materialIds") List<Long> materialIds, @Param("deliveryDate") String deliveryDate);

    DeliveryOrderDetailEO getByDeliveryOrderDetailId(@Param("deliveryOrderDetailId") Long deliveryOrderDetailId);

    List<DeliveryOrderDetailEO> getFromDeliveryOrderIds(@Param("deliveryOrderIds") Long[] deliveryOrderIds, @Param("deliveryType") Integer deliveryType);
}
