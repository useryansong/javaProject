package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface ReceiveOrderDetailMapper extends BaseMapper<ReceiveOrderDetailEO> {


    /**
     * 根据入库单删除入库单明细
     *
     * @param receiveOrderId
     * @return
     */
    @Delete("delete from wms_receive_order_detail where receive_order_id = #{receiveOrderId}")
    Integer removeByReceiveOrder(Long receiveOrderId);

    /**
     * 根据入库单查找
     *
     * @param receiveOrderId
     * @return
     */
    @Select("select t.*,p.unit_name,w.warehouse_name,l.location_name from  wms_receive_order_detail t " +
            "left join bsc_measurement_unit p on p.unit_id = t.unit_id " +
            "left join bsc_warehouse w on w.warehouse_id = t.warehouse_id " +
            "left join bsc_warehouse_location l on l.warehouse_location_id = t.warehouse_location_id " +
            "where t.receive_order_id = #{receiveOrderId}")
    List<ReceiveOrderDetailEO> selectByReceiveOrder(Long receiveOrderId);

    List<ReceiveOrderDetailEO> getByMaterialIdsAndReceiveDate(@Param("materialIds") List<Long> materialIds, @Param("receiveDate") String receiveDate);

    @Select("select count(receive_detail_id) from wms_receive_order_detail")
    Integer countAll();
}
