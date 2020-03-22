package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryReceiveOrderEO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface SubsidiaryReceiveOrderMapper extends BaseMapper<SubsidiaryReceiveOrderEO> {


    /**
     * 更改状态
     *
     * @return
     */
    @Update("update wms_subsidiary_receive_order i left join wms_subsidiary_receive_order_detail d on i.receive_id=d.receive_order_id" +
            " set d.status=#{status} ,i.status = #{status} where i.receive_id =#{Id} and i.status = #{oldStatus}")
    boolean updateStatusById(@Param("Id") Long Id, @Param("status") Integer status, @Param("oldStatus") Integer oldStatus);

    /**
     * 新增时查询是否存在
     *
     * @return
     */
    @Select("select count(1) from wms_subsidiary_receive_order_detail where receive_order_id = #{receiveOrderId} and material_id = #{materialId}")
    Integer selectCountById(@Param("receiveOrderId") Long receiveOrderId, @Param("materialId") Long materialId);

    /**
     * 更新时查询是否存在
     *
     * @return
     */
    @Select("select count(1) from wms_subsidiary_receive_order_detail where receive_order_id = #{receiveOrderId} and material_id = #{materialId} and receive_detail_id != #{receiveDetailId}")
    Integer selectCountUpdateById(@Param("receiveOrderId") Long receiveOrderId, @Param("materialId") Long materialId, @Param("receiveDetailId") Long receiveDetailId);

    /***
     *查找订单下的所有订单明细插入物料台账
     * @param id
     * @return
     */
    @Select("select * from wms_subsidiary_receive_order_detail where receive_order_id = #{id}")
    List<SubsidiaryReceiveOrderDetailEO> selectDetailById(@Param("id") Long id) ;

    /***
     * 查找订单下面是否有订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_subsidiary_receive_order_detail where receive_order_id = #{id}")
    Integer selectDetailCountById(@Param("id") Long id);

    /**
     * 删除物料台账
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id in " +
            "(select t.receive_detail_id from wms_subsidiary_receive_order_detail t where t.receive_order_id=#{id}) and voucher_type in (4)")
    boolean deleteStockById(@Param("id") Long id);


    /***
     * 通过流水获取入库单数据
     * @param voucherNo
     * @return
     */
    @Select("SELECT t.*,o.org_name FROM  wms_subsidiary_receive_order t " +
            "INNER JOIN sys_org o on o.org_id = t.org_id " +
            "where t.voucher_no = #{voucherNo}")
    SubsidiaryReceiveOrderEO getDetailInfoByNo(String voucherNo);


    /***
     * 获取入库中的待入库或已入库货的的明细数据
     * @param Id
     * @return
     */
    @Select("select * from wms_subsidiary_receive_order_detail where receive_order_id = #{Id} and status in (1,2)")
    List<SubsidiaryReceiveOrderDetailEO> getByReceiveId(Long Id);

    /***
     * 判断入单明细是否都已完成(是否存在未完成的)
     * @param Id
     * @return
     */
    @Select("select count(1) from wms_subsidiary_receive_order_detail where receive_order_id = #{Id} and status != 2")
    Integer selectDetailFinishCount(Long Id);

    /***
     * 查询预计统计数量
     * @param Id
     * @return
     */
    @Select("select sum(receive_amount) from wms_subsidiary_receive_order_detail where receive_order_id = #{Id}")
    Double selectTotalCount(Long Id);

    /***
     * 查找订单下面是否存在已完成的订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_subsidiary_receive_order_detail where status='2' and receive_order_id = #{id}")
    Integer selectCompleteDetail(@Param("id") Long id);

    /**
     * 机构变更后删除入库单下的所有明细
     * @param id
     * @return
     */
    @Delete("delete from wms_subsidiary_receive_order_detail  where receive_order_id = #{id}")
    boolean deleteDetailById(@Param("id") Long id);
}
