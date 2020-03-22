package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.AdjustmentEO;
import com.xchinfo.erp.scm.wms.entity.MaterialReceiveEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface MaterialReceiveMapper extends BaseMapper<MaterialReceiveEO> {

    /***
     * 通过出库订单明细ID获取原材料领料表
     * @param id
     * @return
     */
    @Select("SELECT t.* FROM  wms_original_material_receive t where t.delivery_detail_id = #{id}")
    MaterialReceiveEO getByDeliveryDetailId(Long id);

    @Delete("DELETE  FROM  wms_original_material_receive  where delivery_detail_id = #{id}")
    void removeByDetailId(Long id);

    @Delete("DELETE  FROM  wms_original_material_receive  where delivery_detail_id in (" +
            "select delivery_detail_id from wms_delivery_order_detail where delivery_order_id =#{id})")
    void removeByOrderId(Long id);

}
