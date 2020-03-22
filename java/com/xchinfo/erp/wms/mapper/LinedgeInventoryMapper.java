package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.PbomEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.mes.entity.StampingMaterialConsumptionQuotaEO;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryDetailEO;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.scm.wms.entity.TempInventoryEO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface LinedgeInventoryMapper extends BaseMapper<LinedgeInventoryEO> {


    @Select("SELECT * FROM  bsc_material  where element_no= #{elementNo} and org_id = #{orgId} and status=1")
    MaterialEO getMaterialEO(@Param("elementNo")String elementNo, @Param("orgId")Long orgId);

    @Select("SELECT * FROM  bsc_warehouse_location  where bar_code= #{locationBarCode} and org_id = #{orgId}")
    WarehouseLocationEO getLocationId(@Param("locationBarCode")String locationBarCode, @Param("orgId")Long orgId);

    @Select("SELECT count(*) FROM  wms_linedge_inventory  where inventory_month= #{inventoryMonth} and org_id = #{orgId} and status = 1")
    int getCount(@Param("inventoryMonth")String inventoryMonth, @Param("orgId")Long orgId);

    @Select("SELECT * FROM  wms_linedge_inventory  where inventory_month= #{inventoryMonth} and org_id = #{orgId} and status = 2")
    LinedgeInventoryEO getLinedgeInventory(@Param("inventoryMonth")String inventoryMonth, @Param("orgId")Long orgId);

    @Delete("delete FROM  wms_linedge_inventory_detail  where linedge_inventory_id= #{linedgeInventoryId}")
    void removeByLinedgeInventoryId(Long linedgeInventoryId);

    /**
     * 更改状态
     *
     * @return
     */
    @Update("update wms_linedge_inventory i left join wms_linedge_inventory_detail d on i.linedge_inventory_id=d.linedge_inventory_id" +
            " set d.status=#{status} ,i.status = #{status} where i.linedge_inventory_id =#{Id} and i.status = #{oldStatus}")
    boolean updateStatusById(@Param("Id") Long Id, @Param("status") Integer status,@Param("oldStatus") Integer oldStatus);

    @Select("SELECT * FROM  wms_linedge_inventory_detail  where linedge_inventory_id= #{linedgeInventoryId}")
    List<LinedgeInventoryDetailEO> getlinedgeInventoryDetails(Long linedgeInventoryId);

    @Select("select p.* " +
            "from bsc_pbom p " +
            "inner join bsc_material m on p.material_id = m.material_id and m.status = 1 " +
            "inner join bsc_material mc on p.child_material_id = mc.material_id and mc.status = 1 " +
            "where p.material_id= #{materialId} and p.org_id = #{orgId}")
    List<PbomEO> getPbomEOs(@Param("materialId")Long materialId, @Param("orgId")Long orgId);

    @Select("SELECT * FROM  mes_stamping_material_consumption_quota  where element_no= #{elementNo} and org_id = #{orgId}")
    StampingMaterialConsumptionQuotaEO getConsumptionByElement(@Param("elementNo")String elementNo, @Param("orgId")Long orgId);

    @Delete("delete FROM  wms_linedge_inventory_material_assignment  where linedge_inventory_id= #{linedgeInventoryId}")
    void deleteMaterialAssignmentBylinedgeInventoryId(Long linedgeInventoryId);

    @Select("select count(*) from  wms_linedge_inventory where inventory_date = #{inventoryDate} and org_id = #{orgId}")
    Integer selectInventory(@Param("orgId") Long orgId,@Param("inventoryDate")String inventoryMonth);

    @Select("SELECT * FROM  wms_linedge_inventory  where org_id = #{orgId} and status = 0")
    LinedgeInventoryEO getLinedgeInventoryByOrgId(@Param("orgId")Long orgId);

    @Update("update wms_temp_inventory  set status = 1  where inventory_id =#{Id} and status = 0")
    boolean updateTempStatus(@Param("Id") Long Id);

    @Select("select material_id,sum(amount) as amount,location_id,inventory_no  from wms_temp_inventory where inventory_id= #{Id}  and status=0  GROUP BY  material_id")
    List<TempInventoryEO> getTempInventory(@Param("Id") Long Id);

    @Select("select * from wms_temp_inventory where inventory_id= #{linedgeInventoryId}   and  material_id=#{materialId}  and location_id = #{locationId} and status = 0")
    TempInventoryEO getTempInventoryByMaterialAndLocationId(@Param("linedgeInventoryId") Long linedgeInventoryId,@Param("materialId") Long materialId,@Param("locationId") Long locationId);

    @Select("select * from wms_linedge_inventory where inventory_id = #{inventoryId}")
    LinedgeInventoryEO getLinedgeInventoryByInventoryId(@Param("inventoryId") Long inventoryId);
}
