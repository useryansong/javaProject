package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.scm.wms.entity.AdjustInventoryEO;
import com.xchinfo.erp.scm.wms.entity.InventoryDetailEO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface InventoryDetailMapper extends BaseMapper<InventoryDetailEO> {

    /**
     * 查找盘点单明细
     *
     * @param inventoryId
     * @return
     */
    @Select("select t.*,p.unit_name,w.warehouse_name,l.location_name as warehouseLocationName from  wms_inventory_detail t " +
            "left join bsc_measurement_unit p on p.unit_id = t.unit_id " +
            "left join bsc_warehouse w on w.warehouse_id = t.warehouse_id " +
            "left join bsc_warehouse_location l on l.warehouse_location_id = t.warehouse_location_id " +
            "where t.inventory_id  = #{inventoryId }")
    List<InventoryDetailEO> selectByInventory(Long inventoryId);

    /**
     * 查找需导出盘点单明细
     *
     * @param inventoryId
     * @return
     */
    @Select("SET @counter=-2;select @counter:=@counter+1 as sqeNo,t.*,p.unit_name,w.warehouse_name,l.location_name ,CASE t.adjust_status WHEN 0 THEN '未加入调账表' WHEN 1 THEN '待调账' WHEN 2 THEN '已调账'END AS export_adjust_status,CASE t.status WHEN 0 THEN '新建'  WHEN 2 THEN '已完成'END AS export_status,ai.adj_amount,ai.remark as adjust_remark,SUBSTR(ai.inventory_date ,1,7) as inventory_date, " +
            " org.full_name from  wms_inventory_detail t " +
            " left join bsc_measurement_unit p on p.unit_id = t.unit_id " +
            " left join bsc_warehouse w on w.warehouse_id = t.warehouse_id " +
            " left join bsc_warehouse_location l on l.warehouse_location_id = t.warehouse_location_id " +
            " left join wms_adjust_inventory ai on ai.inventory_detail_id = t.inventory_detail_id " +
            " LEFT JOIN sys_org org ON ai.org_id = org.org_id " +
            " where  t.rel_amount is not null and t.rel_amount!=t.amount and t.inventory_id= #{inventoryId}")
    List<InventoryDetailEO> listexportDateByInventory(Long inventoryId);


    /**
     * 根据盘点单删除盘点单明细
     *
     * @param inventoryId
     * @return
     */
    @Delete("delete from wms_inventory_detail where inventory_id  = #{inventoryId}")
    Integer removeByInventoryId(Long inventoryId);



    List<MaterialEO> selectMaterialByInventory(@Param("id")Long id,@Param("warehouseType") String warehouseType,@Param("materialName") String materialName);

    /**
     * 根据盘点单查找可用物料
     *
     * @return
     */
    /*List<MaterialEO> selectInventoryPage(Map param);*/

    /***
     *
     * @param materialId
     * @param warehouseId
     * @param warehouseLocationId
     * @return
     */
    @Select("select sum((begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount" +
            " - sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount)) as amount" +
            " from v_wms_stock_account s where s.material_id = #{materialId} and s.warehouse_id =#{warehouseId}  and s.warehouse_location_id =#{warehouseLocationId}")
    Double getMaterialCount(@Param("materialId") Long materialId,@Param("warehouseId") Long warehouseId,@Param("warehouseLocationId") Long warehouseLocationId);


    /**
     * 待加入调节表分页查找
     *
     * @return
     */
    List<InventoryDetailEO> waitingJoinPage(Map param);

    /**
     * 待调节物料信息分页查找
     *
     * @return
     */
    List<AdjustInventoryEO> waitingAdjustPage(Map param);

    /**
     * 已调节物料信息分页查找
     *
     * @return
     */
    List<AdjustInventoryEO> doneAdjustPage(Map param);

    List<InventoryDetailEO> getByInventoryId(@Param("inventoryId") Long inventoryId);
}
