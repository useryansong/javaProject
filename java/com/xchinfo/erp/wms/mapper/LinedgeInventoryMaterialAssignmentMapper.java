package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryDetailEO;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryMaterialAssignmentEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface LinedgeInventoryMaterialAssignmentMapper extends BaseMapper<LinedgeInventoryMaterialAssignmentEO> {

    List<LinedgeInventoryMaterialAssignmentEO> checkPbomMaterialPage(Map param);


    List<LinedgeInventoryMaterialAssignmentEO> listexportDateByInventory(Long inventoryId);

    List<LinedgeInventoryMaterialAssignmentEO> listexportDateByInventoryAll(Long inventoryId);

    @Select("select * " +
            "from wms_linedge_inventory_material_assignment " +
            "where linedge_inventory_id = #{linedgeInventoryId} " +
            "and inventory_linedge_detail_id = #{inventoryLinedgeDetailId} " +
            "and material_id = #{materialId} " +
            "and status = #{status}")
    List<LinedgeInventoryMaterialAssignmentEO> getList(@Param("linedgeInventoryId") Long linedgeInventoryId,
                                                          @Param("inventoryLinedgeDetailId") Long inventoryLinedgeDetailId,
                                                          @Param("materialId") Long materialId,
                                                          @Param("status") Integer status);
}
