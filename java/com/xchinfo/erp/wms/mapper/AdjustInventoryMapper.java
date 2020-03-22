package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.AdjustInventoryEO;
import com.xchinfo.erp.scm.wms.entity.InventoryDetailEO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdjustInventoryMapper extends BaseMapper<AdjustInventoryEO> {

    @Update("update  wms_inventory_detail  set  adjust_status = 1 ,adjust_user_id = #{userId},adjust_user_name = #{userName} where inventory_detail_id =  #{inventoryDetailId}")
    void updateInventoryDetailAdjustStatus1(@Param("inventoryDetailId") Long inventoryDetailId,@Param("userId") Long userId,@Param("userName") String userName);

    @Update("update  wms_inventory  set  adjust_status =#{status} where inventory_id =  #{inventoryId}")
    void updateInventoryAdjustStatus(@Param("inventoryId") Long inventoryId,@Param("status") int status);

    @Select("select count(*) from  wms_inventory_detail where inventory_id = #{inventoryId} and adjust_status = 1 ")
    Double getwaitAdjustAmount(@Param("inventoryId") Long inventoryId);

    @Select("select count(*) from  wms_inventory_detail where inventory_id = #{inventoryId} and adjust_status = 2 ")
    Double getDoneAdjustAmount(@Param("inventoryId") Long inventoryId);

    @Select("select * from wms_inventory_detail where  amount != total_amount and total_amount is not null and adjust_status = 0 and inventory_id =  #{inventoryId}")
    List<InventoryDetailEO> getInventoryDetailEOs(@Param("inventoryId") Long inventoryId);

    @Update("update  wms_inventory_detail de left join wms_adjust_inventory ai  on ai.inventory_detail_id = de.inventory_detail_id" +
            " set  de.adjust_status = 0 ,de.adjust_user_id = #{userId},de.adjust_user_name = #{userName} where ai.adjust_id = #{AdjustId}")
    void updateInventoryDetailAdjustStatus0(@Param("AdjustId") Long AdjustId,@Param("userId") Long userId,@Param("userName") String userName);

    @Select("select ai.*,de.material_name,de.inventory_code,de.material_code,de.specification,de.unit_id,de.figure_number,de.figure_version,de.warehouse_id,de.warehouse_location_id from wms_adjust_inventory ai " +
            "left join wms_inventory_detail de  on ai.inventory_detail_id = de.inventory_detail_id " +
            "where ai.status=0 and ai.adjust_id = #{adjustId} ")
    AdjustInventoryEO getadjustEO(@Param("adjustId") Long adjustId);

    @Update("update  wms_inventory_detail  set  adjust_status = 2  where inventory_detail_id =  #{inventoryDetailId}")
    void updateInventoryDetailAdjustStatus2(@Param("inventoryDetailId") Long inventoryDetailId);

    @Update("update  wms_adjust_inventory ai " +
            "left join wms_stock_account sa  on ai.inventory_detail_id = sa.voucher_id and ai.element_no=sa.element_no " +
            "set ai.status = 1 ,ai.account_id=sa.account_id where ai.adjust_id = #{adjustId}")
    void updateAdjustStatus(@Param("adjustId") Long adjustId,@Param("status") int status);

    @Select("select ai.*,de.material_name,de.inventory_code,de.material_code,de.specification,de.unit_id,de.figure_number,de.figure_version,de.warehouse_id,de.warehouse_location_id from wms_adjust_inventory ai " +
            "left join wms_inventory_detail de  on ai.inventory_detail_id = de.inventory_detail_id " +
            "where ai.status=0 and de.inventory_id = #{inventoryId} ")
    List<AdjustInventoryEO> getAdjustInventoryEOs(@Param("inventoryId") Long inventoryId);

    @Update("update  wms_adjust_inventory set status = #{status} ,account_id=null where account_id = #{accountId}")
    void deleteAdjustStatus(@Param("accountId") Long accountId,@Param("status") int status);

    @Update("update  wms_inventory_detail de " +
            "left join wms_adjust_inventory ai on  de.inventory_detail_id = ai.inventory_detail_id " +
            " set  de.adjust_status = #{status}  where ai.account_id = #{accountId}")
    void deleteInventoryDetailAdjustStatus(@Param("accountId") Long accountId,@Param("status") int status);

    @Delete("delete from  wms_stock_account where account_id in  (" +
            "select account_id from wms_adjust_inventory where inventory_id = #{inventoryId})")
    void deleteAllStock(@Param("inventoryId") Long inventoryId);

    @Update("update  wms_inventory_detail de " +
            "left join wms_adjust_inventory ai on  de.inventory_detail_id = ai.inventory_detail_id  " +
            " set  de.adjust_status = #{status} where ai.inventory_id = #{inventoryId}")
    void deleteAllInventoryDetailStatus(@Param("inventoryId") Long inventoryId,@Param("status") int status);

    @Update("update  wms_adjust_inventory set status = #{status} ,account_id=null where inventory_id = #{inventoryId}")
    void deleteAllAdjustStatus(@Param("inventoryId") Long inventoryId,@Param("status") int status);

    @Update("update wms_inventory set adjust_status = 3 where inventory_id=#{inventoryId}")
    void updateInventoryStatus(@Param("inventoryId") Long inventoryId);
}
