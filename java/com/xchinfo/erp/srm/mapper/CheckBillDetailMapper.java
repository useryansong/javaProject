package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.CheckBillDetailEO;
import com.xchinfo.erp.scm.srm.entity.CheckBillEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yuanchang
 * @date 2019/10/18
 */
@Mapper
public interface CheckBillDetailMapper extends BaseMapper<CheckBillDetailEO> {
    /**
     * 根据对账单删除对账单明细
     *
     * @param receiveOrderId
     * @return
     */
    @Delete("delete from srm_check_bill_detail where check_id = #{receiveOrderId}")
    Integer removeByReceiveOrder(Long receiveOrderId);

    /**
     * 根据对账单查找
     *
     * @param receiveOrderId
     * @return
     */
    @Select("select t.*,p.unit_name,ma.project_no from  srm_check_bill_detail t " +
            "left join bsc_measurement_unit p on p.unit_id = t.unit_id " +
            "left join bsc_material ma on ma.element_no = t.element_no and ma.org_id = t.org_id " +
            "where t.check_id = #{receiveOrderId}")
    List<CheckBillDetailEO> selectByReceiveOrder(Long receiveOrderId);

    /**
     * 根据入库单明细查找对账单明细
     *
     * @return
     */
    @Select("select * from  srm_check_bill_detail  where receive_detail_id = #{receiveDetailId} and check_id =#{checkId}")
    CheckBillDetailEO  getByReceiveDetailId(@Param("receiveDetailId")Long receiveDetailId,@Param("checkId")Long checkId);

    /**
     * 根据出库单明细查找对账单明细
     *
     * @return
     */
    @Select("select * from  srm_check_bill_detail  where delivery_detail_id = #{deliveryDetailId} and check_id =#{checkId}")
    CheckBillDetailEO  getByDeliveryDetailId(@Param("deliveryDetailId")Long deliveryDetailId,@Param("checkId")Long checkId);

}
