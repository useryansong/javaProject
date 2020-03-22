package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/24
 */
@Mapper
public interface ReturnOrderDetailMapper extends BaseMapper<ReturnOrderDetailEO> {

    List<ReturnOrderDetailEO> getPage(Map map);

    @Delete("delete from srm_return_order_detail where return_order_id = #{returnOrderId}")
    void removeByReturnOrderId(Long returnOrderId);

    List<ReturnOrderDetailEO> getByReturnOrderId(@Param("returnOrderId") Long returnOrderId);

    List<ReturnOrderDetailEO> getByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId);

    /**
     * 根据出库单明细id及状态更新出库单明细状态
     * @return
     */
    @Update("update srm_return_order_detail set status = #{status} where return_order_detail_id = #{returnDetailId}")
    int updateStatusById(@Param("returnDetailId") Long returnDetailId, @Param("status") Integer status);


    /**
     * 根据出库单明细是否全部完成更新出库单状态
     * @return
     */
    @Update("update srm_return_order set status = #{status} where return_order_id = (" +
            "select return_order_id from srm_return_order_detail where return_order_detail_id = #{returnDetailId} " +
            ") and (select count(1) from srm_return_order_detail where return_order_id = (select return_order_id from srm_return_order_detail where return_order_detail_id = #{returnDetailId}) and status != 1 " +
            ")=0")
    int updateReturnOrderStatusById(@Param("returnDetailId") Long returnDetailId, @Param("status") Integer status);

    List<ReturnOrderDetailEO> getByReturnOrderIds(@Param("returnOrderIds") String returnOrderIds, @Param("userId") Long userId,
                                                    @Param("isFilterByActualReceiveQuantity") Boolean isFilterByActualReceiveQuantity,
                                                    @Param("orderField") String orderField);
}
