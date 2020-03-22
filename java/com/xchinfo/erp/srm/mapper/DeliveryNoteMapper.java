package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/14
 */
@Mapper
public interface DeliveryNoteMapper extends BaseMapper<DeliveryNoteEO> {

    List<DeliveryNoteEO> getPage(Map map);

    /**
     * 根据送货单id及状态更新送货单状态
     * @return
     */
    @Update("update srm_delivery_note set status = #{status} where delivery_note_id = #{deliveryNoteId}")
    int updateStatusById(@Param("deliveryNoteId") Long deliveryNoteId, @Param("status") Integer status);

    /***
     * 通过流水获取送货单数据
     * @param voucherNo
     * @return
     */
    @Select("select t.*,l.supplier_name from srm_delivery_note t " +
            "LEFT JOIN bsc_supplier l on t.supplier_id = l.supplier_id " +
            "where voucher_no = #{voucherNo}")
    DeliveryNoteEO getDetailInfoByNo(String voucherNo);

    /***
     * 获取送货中的明细数据
     * @param Id
     * @return
     */
    @Select("select t.*,m.specification,m.element_no,m.inventory_code,m.material_name from srm_delivery_note_detail t " +
            "INNER JOIN srm_delivery_note l on t.delivery_note_id = l.delivery_note_id " +
            "INNER JOIN srm_delivery_plan s on s.delivery_plan_id = t.delivery_plan_id " +
            "INNER JOIN srm_purchase_order m on m.purchase_order_id = s.purchase_order_id " +
            "where t.status in (2,3) and l.delivery_note_id = #{Id}")
    List<DeliveryNoteDetailEO> getByDeliveryNoteId(Long Id);


    /***
     * 判断送货单明细是否都已完成
     * @param Id
     * @return
     */
    @Select("SELECT count(1) from  srm_delivery_note_detail where status != 3 and delivery_note_id = #{Id}")
    Integer selectNoteCount(Long Id);



    @Update("update srm_delivery_note set receive_date = null where delivery_note_id = #{Id}")
    Boolean updateDateNull(Long Id);


    @Select("select * from wms_receive_order where delivery_note_id = #{Id} and receive_type in (1,3)")
    ReceiveOrderEO selectReceiveOrderCount(Long Id);


    @Select("select * from wms_receive_order_detail where order_id = #{Id} and receive_order_id = #{receiveOrderId}")
    ReceiveOrderDetailEO selectReceiveOrderDetailCount(@Param("Id") Long Id,@Param("receiveOrderId") Long receiveOrderId);


    /***
     * 判断入单明细是否都已完成(是否存在未完成的)
     * @param Id
     * @return
     */
    @Select("select count(1) from wms_receive_order_detail where receive_order_id = #{Id} and status != 2")
    Integer selectDetailFinishCount(Long Id);


    @Select("SELECT t.*,l.material_id,l.material_code,l.material_name,l.inventory_code,l.element_no,l.specification,s.unit_id,l.figure_number,l.figure_version,l.warehouse_location_id,l.main_warehouse_id from srm_delivery_note_detail t " +
            "INNER JOIN srm_delivery_plan j on j.delivery_plan_id = t.delivery_plan_id " +
            "INNER JOIN srm_purchase_order u on u.purchase_order_id = j.purchase_order_id " +
            "LEFT JOIN bsc_material l on l.material_id = u.material_id  " +
            "LEFT JOIN bsc_measurement_unit s on u.unit_name = s.unit_name " +
            "WHERE t.delivery_note_detail_id = #{Id} ")
    DeliveryNoteDetailEO selectDeliveryNoteDetailInfo(Long Id);


    @Select("SELECT * from bsc_material where element_no = #{elementNo} and org_id = #{orgId} and status = 1 limit 0,1")
    MaterialEO selectMaterialEO(@Param("elementNo") String elementNo,@Param("orgId") Long orgId);


    @Select("select count(1) from sys_org where status=1 and parent_org_id=1 and org_id = #{orgId}")
    Integer selectInnerOrgExist(Long orgId);


    @Select("select * from bsc_customer where status = 1 and customer_name = #{customerName}")
    CustomerEO selectCustomerByName(String customerName);


    @Select("select count(1) from wms_delivery_order_detail where status = 2 and delivery_note_no = #{voucherNo}")
    Integer selectFinishDetail(String voucherNo);

    @Delete("delete from cmp_vehicle_plan where delivery_note_no = #{voucherNo}")
    boolean deleteVehiclePlanByNo(String voucherNo);

    @Delete("delete from wms_delivery_order_detail where delivery_note_no = #{voucherNo}")
    boolean deleteDeliveryDetailByNo(String voucherNo);

    @Delete("delete from wms_delivery_order where voucher_no = #{voucherNo}")
    boolean deleteDeliveryByNo(String voucherNo);

    /**
     * 查询调入库位
     * @return
     */
    @Select("select w.*,m.warehouse_location_id as material_location_id from bsc_warehouse_location w " +
            "left join bsc_material m on m.material_id=#{materialId} " +
            "where w.org_id =(select org_id from bsc_material where material_id=#{materialId}) and w.status='1'")
    List<WarehouseLocationEO> getWarehouseLocation(@Param("materialId") Long materialId);

    @Select("select * from srm_delivery_note where delivery_note_id in ${sqlStr}")
    List<DeliveryNoteEO> getByIds(@Param("sqlStr") String sqlStr);

    @Update("update srm_delivery_note set ${column} = #{erpVoucherNo} where delivery_note_id in ${sqlStr}")
    void updateErpVoucherNoByIds(@Param("column") String column, @Param("erpVoucherNo") String erpVoucherNo, @Param("sqlStr") String sqlStr);

    List<DeliveryNoteEO> getU8ListByDeliveryNoteIds(@Param("deliveryNoteIds") Long[] deliveryNoteIds);

    int syncU8Update(@Param("syncResult") Map<Long, String> map);

    int updateByU8(@Param("deliveryNotes") List<DeliveryNoteEO> deliveryNotes);

    Integer getCount(Map map);
}
