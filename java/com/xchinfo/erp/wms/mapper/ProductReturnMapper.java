package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.scm.wms.entity.ProductReturnDetailEO;
import com.xchinfo.erp.scm.wms.entity.ProductReturnEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author yuanchang
 * @date 2019/5/17
 * @update
 */
@Mapper
public interface ProductReturnMapper extends BaseMapper<ProductReturnEO> {
    /**
     * 更改状态
     *
     * @return
     */
    @Update("update wms_product_return i left join wms_product_return_detail d on i.return_id=d.return_id" +
            " set d.status=#{status} ,i.status = #{status} where i.return_id =#{Id} and i.status = #{oldStatus}")
    boolean updateStatusById(@Param("Id") Long Id, @Param("status") Integer status, @Param("oldStatus") Integer oldStatus);

    /**
     * 新增时查询是否存在
     *
     * @return
     */
    @Select("select count(1) from wms_product_return_detail where return_id = #{receiveOrderId} and material_id = #{materialId}")
    Integer selectCountById(@Param("receiveOrderId") Long receiveOrderId,@Param("materialId") Long materialId);

    /**
     * 更新时查询是否存在
     *
     * @return
     */
    @Select("select count(1) from wms_product_return_detail where return_id = #{receiveOrderId} and material_id = #{materialId} and return_detail_id != #{receiveDetailId}")
    Integer selectCountUpdateById(@Param("receiveOrderId") Long receiveOrderId,@Param("materialId") Long materialId,@Param("receiveDetailId") Long receiveDetailId);

    /***
     *查找订单下的所有订单明细插入物料台账
     * @param id
     * @return
     */
    @Select("select * from wms_product_return_detail where return_id = #{id}")
    List<ProductReturnDetailEO> selectDetailById(@Param("id") Long id) ;

    /***
     * 查找订单下面是否有订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_product_return_detail where return_id = #{id}")
    Integer selectDetailCountById(@Param("id") Long id);

    /**
     * 删除物料台账
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id in " +
            "(select t.return_detail_id from wms_product_return_detail t where t.return_id=#{id}) and voucher_type in (6)")
    boolean deleteStockById(@Param("id") Long id);

    /***
     * 通过流水获取退货单单数据
     * @param voucherNo
     * @return
     */
    @Select("SELECT t.* FROM  wms_product_return t where t.voucher_no = #{voucherNo}")
    ProductReturnEO getDetailInfoByNo(String voucherNo);


    /***
     * 获取入库中的待退货或已退货的的明细数据
     * @param Id
     * @return
     */
    @Select("select * from wms_product_return_detail where return_id = #{Id} and status in (1,2)")
    List<ProductReturnDetailEO> getByReturnId(Long Id);

    /***
     * 判断入单明细是否都已完成(是否存在未完成的)
     * @param Id
     * @return
     */
    @Select("select count(1) from wms_product_return_detail where return_id = #{Id} and status < 2")
    Integer selectDetailFinishCount(Long Id);

    /***
     * 查询预计统计数量
     * @param Id
     * @return
     */
    @Select("select sum(return_amount) from wms_product_return_detail where return_id = #{Id}")
    Double selectTotalCount(Long Id);

    /***
     * 查找订单下面是否存在已完成的订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_product_return_detail where status='2' and return_id = #{id}")
    Integer selectCompleteDetail(@Param("id") Long id);

    /**
     * 机构或者客户变更后删除退货单下的所有明细
     * @param id
     * @return
     */
    @Delete("delete from wms_product_return_detail  where return_id = #{id}")
    boolean deleteDetailById(@Param("id") Long id);


    @Select("select * from wms_receive_order where delivery_note_id = #{Id} and receive_type = 6")
    ReceiveOrderEO selectReceiveOrderCount(Long Id);

    @Select("select * from wms_receive_order_detail where order_id = #{Id} and receive_order_id = #{receiveOrderId}")
    ReceiveOrderDetailEO selectReceiveOrderDetailCount(@Param("Id") Long Id,@Param("receiveOrderId") Long receiveOrderId);

    /**
     * 查询调入库位
     * @return
     */
    @Select("select w.*,(SELECT warehouse_location_id FROM wms_product_return_detail WHERE return_detail_id = #{id}) as material_location_id from bsc_warehouse_location w " +
            "where w.org_id =(select org_id from bsc_material where material_id=(select material_id from wms_product_return_detail where return_detail_id = #{id})) and w.status='1'")
    List<WarehouseLocationEO> getWarehouseLocation(@Param("id") Long id);
}
