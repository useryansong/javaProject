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
public interface SubsidiaryDeliveryOrderMapper extends BaseMapper<SubsidiaryDeliveryOrderEO> {
    /**
     * 查询所有出库单
     * @return
     */
    @Select("select * from wms_subsidiary_delivery_order")
    List<SubsidiaryDeliveryOrderEO> selectAll();
    

    /**
     * 更改状态
     *
     * @return
     */
    @Update("update wms_subsidiary_delivery_order i left join wms_subsidiary_delivery_order_detail d on i.delivery_id=d.delivery_order_id" +
            " set d.status=#{status} ,i.status = #{status} where i.delivery_id =#{Id} and i.status = #{oldStatus}")
    boolean updateStatusById(@Param("Id") Long Id, @Param("status") Integer status, @Param("oldStatus") Integer oldStatus);


    /**
     * 删除物料台账
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id in " +
            "(select t.delivery_detail_id from wms_subsidiary_delivery_order_detail t where t.delivery_order_id=#{id}) and voucher_type in (10)")
    boolean deleteStockById(@Param("id") Long id);

    /***
     * 查找订单下面是否有订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_subsidiary_delivery_order_detail where delivery_order_id = #{id}")
    Integer selectDetailCountById(@Param("id") Long id);

    /***
     * 查找订单下面是否存在已完成的订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_subsidiary_delivery_order_detail where status='2' and delivery_order_id = #{id}")
    Integer selectCompleteDetail(@Param("id") Long id);

    /***
     *查找订单下的所有订单明细插入物料台账
     * @param id
     * @return
     */
    @Select("select * from wms_subsidiary_delivery_order_detail where status='0' and delivery_order_id = #{id}")
    List<SubsidiaryDeliveryOrderDetailEO> selectDetailById(@Param("id") Long id) ;

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
    @Select("SELECT t.*,o.org_name FROM  wms_subsidiary_delivery_order t " +
            "LEFT JOIN sys_org o on o.org_id = t.org_id " +
            "where t.voucher_no = #{voucherNo}")
    SubsidiaryDeliveryOrderEO getDetailInfoByNo(String voucherNo);


    /***
     * 获取送货中的待出库或已出货的的明细数据
     * @param Id
     * @return
     */
    @Select("select * from wms_subsidiary_delivery_order_detail where delivery_order_id = #{Id} and status in (1,2)")
    List<SubsidiaryDeliveryOrderDetailEO> getByDeliveryId(Long Id);


    /***
     * 判断出库单明细是否都已完成(是否存在未完成的)
     * @param Id
     * @return
     */
    @Select("select count(1) from wms_subsidiary_delivery_order_detail where delivery_order_id = #{Id} and status != 2")
    Integer selectDetailFinishCount(Long Id);

    /***
     * 查询预计统计数量
     * @param Id
     * @return
     */
    @Select("select sum(delivery_amount) from wms_subsidiary_delivery_order_detail where delivery_order_id = #{Id}")
    Double selectTotalCount(Long Id);

    List<SubsidiaryDeliveryOrderEO> getList(Map map);

    /**
     * 机构变更后删除入库单下的所有明细
     * @param id
     * @return
     */
    @Delete("delete from wms_subsidiary_delivery_order_detail  where delivery_order_id = #{id}")
    boolean deleteDetailById(@Param("id") Long id);
}
