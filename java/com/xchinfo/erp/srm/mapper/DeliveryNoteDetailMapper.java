package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/14
 */
@Mapper
public interface DeliveryNoteDetailMapper extends BaseMapper<DeliveryNoteDetailEO> {

    List<DeliveryNoteDetailEO> getList(Map map);

    @Delete("delete from srm_delivery_note_detail where delivery_note_id = #{deliveryNoteId}")
    void removeByDeliveryNoteId(Long deliveryNoteId);

    List<DeliveryNoteDetailEO> getByDeliveryNoteId(@Param("deliveryNoteId") Long deliveryNoteId, @Param("userId") Long userId, @Param("isFilterByActualReceiveQuantity") Boolean isFilterByActualReceiveQuantity);

    /**
     * 根据送货单明细id及状态更新送货单明细状态
     * @return
     */
    @Update("update srm_delivery_note_detail set status = #{status} where delivery_note_detail_id = #{deliveryNoteDetailId}")
    void updateStatusById(@Param("deliveryNoteDetailId") Long deliveryNoteDetailId, @Param("status") Integer status);

    List<DeliveryNoteDetailEO> getByDeliveryNoteIds(@Param("deliveryNoteIds") String deliveryNoteIds, @Param("userId") Long userId,
                                                    @Param("isFilterByActualReceiveQuantity") Boolean isFilterByActualReceiveQuantity,
                                                    @Param("orderField") String orderField);

    List<DeliveryNoteDetailEO> getPage(Map map);

    Integer getCount(Map map);
}
