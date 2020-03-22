package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.scm.wms.entity.AdjustInventoryEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
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
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrderEO> {

    /**
     * 查询所有出库单
     * @return
     */
    @Select("select m.* from wms_delivery_order m inner join sys_org o on m.org_id = o.org_id " +
            " inner join v_user_perm_org x on x.org_id = o.org_id  where  x.user_id = #{userId}" )
    List<DeliveryOrderEO> selectAll(Long userId);

/*    *//**
     * 根据出库单id及状态更新出库单状态
     * @return
     *//*
    @Update("update wms_delivery_order set status = #{status} where delivery_id = #{deliveryId}")
    int updateStatusById(@Param("deliveryId") Long deliveryId, @Param("status") Integer status);*/

    /**
     * 更改状态
     *
     * @return
     */
    @Update("update wms_delivery_order i left join wms_delivery_order_detail d on i.delivery_id=d.delivery_order_id" +
            " set d.status=#{status} ,i.status = #{status} where i.delivery_id =#{Id} and i.status = #{oldStatus}")
    boolean updateStatusById(@Param("Id") Long Id, @Param("status") Integer status,@Param("oldStatus") Integer oldStatus);


    /**
     * 删除物料台账
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id in " +
            "(select t.delivery_detail_id from wms_delivery_order_detail t where t.delivery_order_id=#{id}) and voucher_type in (7,8,9,12)")
    boolean deleteStockById(@Param("id") Long id);

    /***
     * 查找订单下面是否有订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_delivery_order_detail where delivery_order_id = #{id}")
    Integer selectDetailCountById(@Param("id") Long id);

    /***
     * 查找订单下面是否存在已完成的订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_delivery_order_detail where status='2' and delivery_order_id = #{id}")
    Integer selectCompleteDetail(@Param("id") Long id);

    /***
     *查找订单下的所有新建状态的订单明细插入物料台账
     * @param id
     * @return
     */
    @Select("select * from wms_delivery_order_detail where status='0' and delivery_order_id = #{id}")
    List<DeliveryOrderDetailEO> selectDetailById(@Param("id") Long id) ;


    @Select("select * from wms_delivery_order_detail where delivery_detail_id = #{Id}")
    DeliveryOrderDetailEO selectDetailInfoById(@Param("Id") Long Id);

    /***
     *查询库存物料数量
     * @param id
     * @return
     */
    List<StockAccountEO> getMessage(@Param("id") Long id);



    /***
     * 通过流水获取送货单数据
     * @param voucherNo
     * @return
     */
    @Select("SELECT t.*,o.org_name FROM  wms_delivery_order t " +
            "LEFT JOIN sys_org o on o.org_id = t.org_id " +
            "where t.voucher_no = #{voucherNo}")
    DeliveryOrderEO getDetailInfoByNo(String voucherNo);


    /***
     * 获取送货中的待出库或已出货的的明细数据
     * @param Id
     * @return
     */
    @Select("select * from wms_delivery_order_detail where delivery_order_id = #{Id} and status in (1,2)")
    List<DeliveryOrderDetailEO> getByDeliveryId(Long Id);


    /***
     * 判断出库单明细是否都已完成(是否存在未完成的)
     * @param Id
     * @return
     */
    @Select("select count(1) from wms_delivery_order_detail where delivery_order_id = #{Id} and status != 2")
    Integer selectDetailFinishCount(Long Id);

    /***
     * 查询预计统计数量
     * @param Id
     * @return
     */
    @Select("select sum(delivery_amount) from wms_delivery_order_detail where delivery_order_id = #{Id}")
    Double selectTotalCount(Long Id);

    List<DeliveryOrderEO> getList(Map map);

    /**
     * 机构变更后删除出库单下的所有明细
     * @param id
     * @return
     */
    @Delete("delete from wms_delivery_order_detail  where delivery_order_id = #{id}")
    boolean deleteDetailById(@Param("id") Long id);



    @Delete("delete from wms_stock_account where voucher_id = #{Id} and voucher_type = #{voucherType}")
    boolean deleteStockByDetailId(@Param("Id") Long Id,@Param("voucherType") Integer voucherType);


//    @Select("SELECT * FROM " +
//            "(select t.material_id,t.warehouse_location_id,sum(begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount " +
//            "                - sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount) as count " +
//            "                from v_wms_stock_account_simple t group by t.material_id) as c where c.material_id = #{materialId}")
    @Select("select t.material_id,t.warehouse_location_id,t.amount as count " +
            "from v_wms_stock_account_total t " +
            "where t.material_id = #{materialId}")
    List<StockAccountEO> selectStockCount(Long materialId);


    @Select("select * from wms_delivery_order where delivery_type = 2 and relation_id = #{Id}")
    DeliveryOrderEO selectByDetailId(Long Id);

    @Select("select * from bsc_material where material_id = #{materialId}")
    MaterialEO getMaterialById(Long materialId);



    /**
     * 删除生产领料库存
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id in " +
            "(select t.delivery_detail_id from wms_delivery_order_detail t where t.delivery_order_id=#{id}) and voucher_type = 8")
    boolean deleteSCStockById(@Param("id") Long id);

    @Update("UPDATE cmp_vehicle_plan set ${strSql}")
    boolean updateVehiclePlanByNo(@Param("strSql") String strSql);

    List<DeliveryOrderEO> getPage(Map map);


    /**
     * 按库位查找物料库存
     *
     * @return
     */
    List<StockAccountEO> checkStockByLocation(Map param);

    @Select("select sum(amount) from wms_stock_account where voucher_id = #{Id} and voucher_type = #{voucherType}")
    Double selectSumStockCount(@Param("Id") Long Id,@Param("voucherType") Integer voucherType);


    @Select("select * from bsc_material where element_no = #{elementNo} and org_id = #{orgId} and status=1 limit 0,1")
    MaterialEO selectMaterialInfo(@Param("elementNo") String elementNo,@Param("orgId") Long orgId);

//    @Select("SELECT c.count FROM " +
//            "(select t.material_id,sum(begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount " +
//            "                - sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount) as count " +
//            "                from v_wms_stock_account_simple t) as c where c.material_id = #{materialId}")
    @Select("select amount as count from v_wms_stock_account_total where material_id = #{materialId}")
    Double getStockCountByMaterialId(Long materialId);

    @Select("select * from wms_delivery_order where delivery_id in ${sqlStr}")
    List<DeliveryOrderEO> getByIds(@Param("sqlStr") String sqlStr);

    @Update("update wms_delivery_order set ${column} = #{erpVoucherNo} where delivery_id in ${sqlStr}")
    void updateErpVoucherNoByIds(@Param("column") String column, @Param("erpVoucherNo") String erpVoucherNo, @Param("sqlStr") String sqlStr);

    int selectStockByCondition(@Param("voucherId")Long voucherId, @Param("materialId")Long materialId, @Param("voucherType")Integer voucherType, @Param("warehouseId")Long warehouseId, @Param("warehouseLocationId")Long warehouseLocationId,@Param("amount")Double amount);

    List<DeliveryOrderEO> getU8ListByReceiveOrderIds(@Param("receiveOrderIds") Long[] receiveOrderIds);

    List<DeliveryOrderEO> getU8ListByDeliveryOrderIds(@Param("deliveryOrderIds") Long[] deliveryOrderIds);

    int syncU8Update(@Param("syncResult") Map<Long, String> map);

    int updateByU8(@Param("deliveryOrders") List<DeliveryOrderEO> deliveryOrders);

    int syncU8PdUpdate(@Param("syncPdResult") Map<Long, String> map);

    int updateByPdU8(@Param("deliveryOrders") List<DeliveryOrderEO> deliveryOrders);
}
