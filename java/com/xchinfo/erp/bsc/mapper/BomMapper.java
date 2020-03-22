package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.BomEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

/**
 * @author zhongy
 * @date 2019/4/10
 * @update
 */
@Mapper
public interface BomMapper  extends BaseMapper<BomEO> {

    /**
     * 查询所有bom
     *
     * @return
     */
    @Select("select * from bsc_bom bb " +
            "inner join sys_org o on o.org_id = bb.org_id " +
//            "inner join sys_role_org ro on ro.org_id = o.org_id " +
//            "inner join sys_user_auth ua on ua.role_id = ro.role_id " +
            "inner join v_user_perm_org upo on upo.org_id = o.org_id " +
            "where upo.user_id = #{userId}")
    List<BomEO> selectAll(Long userId);

    @Select("select bb.bom_id,bb.org_id,bm.is_outside,bb.material_id,bm.material_name,bm.first_measurement_unit as unit_id,mu.unit_name," +
            "bb.parent_bom_id,bb.amount,bb.pick_rule,bm.figure_version,bb.material_model,bb.substitute_material_model,bm.figure_number," +
            "bb.weight,bm.element_no,bb.ebom_element_no,bm.is_product,bm.is_purchase,bb.status,bb.memo,bb.version,bb.created_by," +
            "bb.created_time,bb.last_modified_by,bb.last_modified_time,bb.release_status,bm.project_no," +
            "bm.material_code,bm.inventory_code,bm.material_name as text,bm.specification,bm.supplier_id,bm.main_warehouse_id,w.erp_code " +
            "from bsc_bom bb " +
            "left join bsc_material bm on bb.material_id = bm.material_id " +
            "left join bsc_measurement_unit mu on mu.unit_id = bm.first_measurement_unit " +
            "left join bsc_warehouse w on bm.main_warehouse_id = w.warehouse_id " +
            "where bb.parent_bom_id = #{parentBomId} ")
    List<BomEO> getListByParentBomId(Long parentBomId);

    /**
     * 根据Bom清单id及状态更新状态
     * @return
     */
    @Update("update bsc_bom set status = #{status} where bom_id = #{bomId}")
    int updateStatusById(@Param("bomId") Long bomId, @Param("status") Integer status);

    @Select("select bb.bom_id,bb.org_id,bm.is_outside,bb.material_id,bm.material_name,bm.first_measurement_unit as unit_id,mu.unit_name," +
            "bb.parent_bom_id,bb.amount,bb.pick_rule,bm.figure_version,bb.material_model,bb.substitute_material_model,bm.figure_number," +
            "bb.weight,bm.element_no,bb.ebom_element_no,bm.is_product,bm.is_purchase,bb.status,bb.memo,bb.version,bb.created_by," +
            "bb.created_time,bb.last_modified_by,bb.last_modified_time,bb.release_status,bm.project_no," +
            "bm.material_code,bm.inventory_code,bm.material_name as text,bm.specification,bm.supplier_id,bm.main_warehouse_id " +
            "from bsc_bom bb " +
            "left join bsc_material bm on bb.material_id = bm.material_id " +
            "left join bsc_measurement_unit mu on mu.unit_id = bm.first_measurement_unit " +
            "where bb.element_no = #{elementNo} and bb.org_id = #{orgId} ")
    List<BomEO> getByElementNoAndOrg(@Param("elementNo") String elementNo, @Param("orgId") Long orgId);

    @Update("update bsc_bom b " +
            "inner join bsc_material m on b.material_id = m.material_id and b.org_id = m.org_id " +
            "set b.element_no = m.element_no " +
            "where b.material_id = #{materialId} and b.org_id = #{orgId}")
    int synchElementNoUpdateMaterial(@Param("materialId") Long materialId, @Param("orgId") Long orgId);

    @Update("update bsc_bom b " +
            "inner join bsc_material m on b.material_id = m.material_id and b.org_id = m.org_id " +
            "set b.is_product = m.is_product " +
            "where b.material_id = #{materialId} and b.org_id = #{orgId}")
    int synchIsProductUpdateMaterial(@Param("materialId") Long materialId, @Param("orgId") Long orgId);

    @Update("update bsc_bom b " +
            "inner join bsc_material m on b.material_id = m.material_id and b.org_id = m.org_id " +
            "set b.is_purchase = m.is_purchase " +
            "where b.material_id = #{materialId} and b.org_id = #{orgId}")
    int synchIsPurchaseUpdateMaterial(@Param("materialId") Long materialId, @Param("orgId") Long orgId);

    @Update("update bsc_bom b " +
            "inner join bsc_material m on b.element_no=m.element_no and b.org_id=m.org_id " +
            "set b.material_id=m.material_id " +
            "where b.material_id=0 and b.element_no = #{elementNo} and b.org_id = #{orgId}")
    void synchAddMaterial(@Param("elementNo") String elementNo, @Param("orgId") Long orgId);

    @Select("select bb.bom_id,bb.org_id,bb.material_id,bm.material_name,mu.unit_name," +
            "bb.amount,bb.element_no,bm.is_product,bm.is_purchase," +
            "bm.material_code,bm.inventory_code,bm.specification,bm.supplier_id " +
            "from bsc_bom bb " +
            "left join bsc_material bm on bb.material_id = bm.material_id " +
            "left join bsc_measurement_unit mu on mu.unit_id = bm.first_measurement_unit " +
            "where bb.material_id = #{materialId} ")
    List<BomEO> getByMaterialId(Long materialId);

    boolean addBatch(@Param("boms") List<BomEO> boms);

    @Select("select * from bsc_bom where material_id = #{materialId} and parent_bom_id = 0")
    BomEO getParentByMaterialId(@Param("materialId") Long materialId);

    List<BomEO> getByMaterialIds(@Param("materialIds") Set set);
}
