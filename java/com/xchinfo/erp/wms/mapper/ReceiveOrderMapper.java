package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface ReceiveOrderMapper extends BaseMapper<ReceiveOrderEO> {


    /**
     * 更改状态
     *
     * @return
     */
    @Update("update wms_receive_order i left join wms_receive_order_detail d on i.receive_id=d.receive_order_id" +
            " set d.status=#{status} ,i.status = #{status} where i.receive_id =#{Id} and i.status = #{oldStatus}")
    boolean updateStatusById(@Param("Id") Long Id, @Param("status") Integer status,@Param("oldStatus") Integer oldStatus);

    /**
     * 新增时查询是否存在
     *
     * @return
     */
    @Select("select count(1) from wms_receive_order_detail where receive_order_id = #{receiveOrderId} and material_id = #{materialId}")
    Integer selectCountById(@Param("receiveOrderId") Long receiveOrderId,@Param("materialId") Long materialId);

    /**
     * 更新时查询是否存在
     *
     * @return
     */
    @Select("select count(1) from wms_receive_order_detail where receive_order_id = #{receiveOrderId} and material_id = #{materialId} and receive_detail_id != #{receiveDetailId}")
    Integer selectCountUpdateById(@Param("receiveOrderId") Long receiveOrderId,@Param("materialId") Long materialId,@Param("receiveDetailId") Long receiveDetailId);

    /***
     *查找订单下的所有订单明细插入物料台账
     * @param id
     * @return
     */
    @Select("select * from wms_receive_order_detail where receive_order_id = #{id}")
    List<ReceiveOrderDetailEO> selectDetailById(@Param("id") Long id) ;

    /***
     * 查找订单下面是否有订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_receive_order_detail where receive_order_id = #{id}")
    Integer selectDetailCountById(@Param("id") Long id);

    /**
     * 删除物料台账
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id in " +
            "(select t.receive_detail_id from wms_receive_order_detail t where t.receive_order_id=#{id}) and voucher_type in (1,2,3,6)")
    boolean deleteStockById(@Param("id") Long id);


    /***
     * 通过流水获取入库单数据
     * @param voucherNo
     * @return
     */
    @Select("SELECT t.*,o.org_name FROM  wms_receive_order t " +
            "INNER JOIN sys_org o on o.org_id = t.org_id " +
            "where t.voucher_no = #{voucherNo}")
    ReceiveOrderEO getDetailInfoByNo(String voucherNo);


    /***
     * 获取入库中的待入库或已入库货的的明细数据
     * @param Id
     * @return
     */
    @Select("select * from wms_receive_order_detail where receive_order_id = #{Id} and status in (1,2)")
    List<ReceiveOrderDetailEO> getByReceiveId(Long Id);

    /***
     * 判断入单明细是否都已完成(是否存在未完成的)
     * @param Id
     * @return
     */
    @Select("select count(1) from wms_receive_order_detail where receive_order_id = #{Id} and status != 2")
    Integer selectDetailFinishCount(Long Id);

    /***
     * 查询预计统计数量
     * @param Id
     * @return
     */
    @Select("select sum(receive_amount) from wms_receive_order_detail where receive_order_id = #{Id}")
    Double selectTotalCount(Long Id);

    /***
     * 查找订单下面是否存在已完成的订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_receive_order_detail where status='2' and receive_order_id = #{id}")
    Integer selectCompleteDetail(@Param("id") Long id);

    /**
     * 机构变更后删除入库单下的所有明细
     * @param id
     * @return
     */
    @Delete("delete from wms_receive_order_detail  where receive_order_id = #{id}")
    boolean deleteDetailById(@Param("id") Long id);

    @Delete("delete from wms_stock_account where voucher_id = #{Id} and voucher_type = #{voucherType}")
    boolean deleteStockByDetailId(@Param("Id") Long Id,@Param("voucherType") Integer voucherType);

    @Select("<script>" +
                "select rod.*,ro.receive_user_name,w.warehouse_code,w.erp_code,ro.voucher_no,substring(ro.voucher_no,3) as voucher_no_sub,ro.receive_date " +
                "<when test=\"null != userId and '' !=userId \">" +
                ",(select real_name from sys_user where user_id = #{userId}) as create_bill_name " +
                "</when> " +
                "from wms_receive_order_detail rod " +
                "left join wms_receive_order ro on rod.receive_order_id = ro.receive_id " +
                "left join bsc_warehouse w on rod.warehouse_id = w.warehouse_id " +
                "where rod.receive_order_id in " +
                "<foreach item='item' index='index' collection='receiveOrderIds' open='(' separator=',' close=')'>" +
                    "#{item}" +
                "</foreach>" +
            "</script>")
    List<ReceiveOrderDetailEO> getByReceiveOrderIds(@Param("receiveOrderIds") Long[] receiveOrderIds, @Param("userId") Long userId);

    @Select("<script>" +
            "select dod.*,u.real_name as create_bill_name,w.warehouse_code,w.erp_code,ro.receive_id as receive_order_id,ro.voucher_no,substring(ro.erp_voucher_no1,3) as voucher_no_sub, do.delivery_date " +
//            "<when test=\"null != userId and '' !=userId \">" +
//            ",(select real_name from sys_user where user_id = #{userId}) as create_bill_name " +
//            "</when> " +
            "from wms_delivery_order do " +
            "left join sys_user u on u.user_id = do.delivery_user_id " +
            "left join wms_delivery_order_detail dod on do.delivery_id = dod.delivery_order_id " +
            "left join wms_receive_order_detail rod on rod.receive_detail_id = do.relation_id " +
            "left join wms_receive_order ro on rod.receive_order_id = ro.receive_id " +
            "left join bsc_warehouse w on dod.warehouse_id = w.warehouse_id " +
            "where do.delivery_type=2 and rod.receive_order_id in " +
            "<foreach item='item' index='index' collection='receiveOrderIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<DeliveryOrderDetailEO> getDeliveryOrderDetails(@Param("receiveOrderIds") Long[] receiveOrderIds, @Param("userId") Long userId);

    @Select("select count(receive_id) from wms_receive_order")
    Integer countAll();


    @Select("select * from bsc_material where material_id = (" +
            "select material_id from wms_receive_order_detail where receive_detail_id = #{Id})")
    MaterialEO selectMaterialEO(@Param("Id") Long Id);

    /**
     * 查询调入库位
     * @return
     */
    @Select("select w.*,(SELECT warehouse_location_id FROM wms_receive_order_detail WHERE receive_detail_id = #{id}) as material_location_id from bsc_warehouse_location w " +
            "where w.org_id =(select org_id from bsc_material where material_id=(select material_id from wms_receive_order_detail where receive_detail_id = #{id})) and w.status='1'")
    List<WarehouseLocationEO> getWarehouseLocation(@Param("id") Long id);

    void addBatch(@Param("receiveOrders") List<ReceiveOrderEO> receiveOrders);

    @Delete("delete from wms_receive_order where poor_production_id in ${deleteSql}")
    void deleteByPoorProductionIds(@Param("deleteSql") String deleteSql);

    List<ReceiveOrderEO> getU8ListByReceiveOrderIds(@Param("receiveOrderIds") Long[] receiveOrderIds);

    int syncU8Update(@Param("syncResult") Map<Long, String> map);

    int updateByU8(@Param("receiveOrders") List<ReceiveOrderEO> receiveOrders);

    List<ReceiveOrderDetailEO> selectPageByViewMode(Map param);

    List<ReceiveOrderEO> getByIds(@Param("receiveOrderIds") Long[] receiveOrderIds);

    @Update("update wms_receive_order set ${column} = #{erpVoucherNo} where receive_id in ${sqlStr}")
    void updateErpVoucherNoByIds(@Param("column") String column, @Param("erpVoucherNo") String erpVoucherNo, @Param("sqlStr") String sqlStr);
}
