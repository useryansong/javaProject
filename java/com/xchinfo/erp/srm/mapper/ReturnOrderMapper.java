package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/24
 */
@Mapper
public interface ReturnOrderMapper extends BaseMapper<ReturnOrderEO> {

    List<ReturnOrderEO> getPage(Map map);

    /**
     * 根据退货单id及状态更新退货单状态
     * @return
     */
    @Update("update srm_return_order set status = #{status} where return_order_id = #{returnOrderId}")
    int updateStatusById(@Param("returnOrderId") Long returnOrderId, @Param("status") Integer status);


    /***
     * 通过流水获取送货单数据
     * @param voucherNo
     * @return
     */
    @Select("select t.*,l.supplier_name from srm_return_order t " +
            "LEFT JOIN bsc_supplier l on t.supplier_id = l.supplier_id " +
            "where t.voucher_no = #{Id}")
    ReturnOrderEO getDetailInfoByNo(String voucherNo);


    /***
     * 获取送货中的待出库或已出货的的明细数据
     * @param Id
     * @return
     */
    @Select("select * from srm_return_order_detail where return_order_id = #{Id} and status in (0,1)")
    List<ReturnOrderDetailEO> getReturnInfoById(Long Id);


    /***
     * 判断出库单明细是否都已完成(是否存在未完成的)
     * @param Id
     * @return
     */
    @Select("select count(1) from srm_return_order_detail where return_order_id = #{Id} and status != 1")
    Integer selectDetailFinishCount(Long Id);

    @Update("update srm_return_order_detail set delivery_time = null where return_order_detail_id = #{Id}")
    Boolean updateDetailDateNull(Long Id);

    @Update("update srm_return_order set actual_return_date = null where return_order_id = #{Id}")
    Boolean updateDateNull(Long Id);


    @Select("select * from wms_delivery_order where relation_id = #{Id} and delivery_type = 5")
    DeliveryOrderEO selectDeliveryOrderCount(Long Id);


    @Select("select * from wms_delivery_order_detail where order_id = #{Id} and delivery_order_id = #{deliveryId}")
    DeliveryOrderDetailEO selectDeliveryOrderDetailCount(@Param("Id") Long Id, @Param("deliveryId") Long deliveryId);


    @Select("SELECT t.* ,l.material_id,l.material_code,l.material_name,l.inventory_code,l.element_no,l.specification,s.unit_id,l.figure_number,l.figure_version,l.warehouse_location_id,l.main_warehouse_id as warehouse_id " +
            "FROM srm_return_order_detail t " +
            "LEFT JOIN bsc_material l on l.material_id = t.material_id " +
            "LEFT JOIN bsc_measurement_unit s on t.unit_name = s.unit_name " +
            "where t.return_order_detail_id = #{Id}")
    ReturnOrderDetailEO selectReturnOrderDetailInfo(Long Id);


    @Select("select count(1) from wms_delivery_order_detail where delivery_order_id = #{Id} and status != 2")
    Integer selectDeliveryOrderDetailFinishCount(Long Id);

    /**
     * 按库位查找物料库存
     *
     * @return
     */
    List<StockAccountEO> checkStockByLocation(Map param);

    @Select("select sum(amount) from wms_stock_account where voucher_id = #{Id}")
    Double selectSumStockCount(@Param("Id") Long Id);

    @Select("select sum(amount) from wms_stock_account where voucher_id =(" +
            "select delivery_detail_id from wms_delivery_order_detail where delivery_order_id = (" +
            "select delivery_id from wms_delivery_order where relation_id = (" +
            "select return_order_id from srm_return_order_detail where return_order_detail_id = #{Id})))")
    Double selectSumStockByReturnDetailId(@Param("Id") Long Id);

    @Select("select * from srm_return_order where return_order_id in ${sqlStr}")
    List<ReturnOrderEO> getByIds(@Param("sqlStr") String sqlStr);

    @Update("update srm_return_order set ${column} = #{erpVoucherNo} where return_order_id in ${sqlStr}")
    void updateErpVoucherNoByIds(@Param("column") String column, @Param("erpVoucherNo") String erpVoucherNo, @Param("sqlStr") String sqlStr);
}
