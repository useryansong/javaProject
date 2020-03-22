package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.BomEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.mes.entity.PoorProductionEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface PoorProductionMapper extends BaseMapper<PoorProductionEO> {
    /**
     * 更改状态
     *
     * @return
     */
    @Update("update mes_poor_production  set status = #{status} where poor_production_id =#{id}")
    boolean updateStatusById(@Param("status") int status, @Param("id") Long id);

    List<Map> getDataByIds(Map map);

    @Select("select bb.bom_id,bb.org_id,bb.material_id,bm.material_name,bm.first_measurement_unit as unit_id,\n" +
            "            bb.parent_bom_id,bb.amount,bm.figure_version,bb.material_model,bb.substitute_material_model,bm.figure_number,\n" +
            "            bb.weight,bb.element_no,bb.ebom_element_no,bm.is_product,bm.is_purchase,bb.status,bb.memo,bb.version,bb.created_by,\n" +
            "            bb.created_time,bb.last_modified_by,bb.last_modified_time,bb.release_status,bm.project_no,\n" +
            "            bm.material_code,bm.inventory_code,bm.material_name as text,bm.specification\n" +
            "        from bsc_bom bb\n" +
            "        left join bsc_material bm on bb.material_id = bm.material_id" +
            " where bb.org_id=#{orgId} and bb.element_no = #{elementNo}")
    List<BomEO> getBom(@Param("orgId") Long orgId, @Param("elementNo") String elementNo);

    @Select("select pp.*, m.main_warehouse_id, m.inventory_code, m.specification " +
            "from mes_poor_production pp " +
            "left join bsc_material m on pp.material_id = m.material_id " +
            "where pp.poor_production_id in ${sqlStr}")
    List<PoorProductionEO> getByIds(@Param("sqlStr") String sqlStr);

    @Update("update mes_poor_production set status = #{status} where poor_production_id in ${sqlStr}")
    void updateStatusByIds(@Param("sqlStr") String sqlStr, @Param("status") int status);

    @Update("update mes_poor_production set account_id = #{accountId} where poor_production_id = #{poorProductionId}")
    void updateAccountIdById(@Param("accountId") Long accountId, @Param("poorProductionId") Long poorProductionId);

    @Update("update mes_poor_production set account_id = null where poor_production_id in ${sqlStr}")
    void clearAccountIdByIds(@Param("sqlStr") String sql);

    @Update("update mes_poor_production set ${column} = #{erpVoucherNo} where poor_production_id in ${sqlStr}")
    void updateErpVoucherNoByIds(@Param("column") String column, @Param("erpVoucherNo") String erpVoucherNo, @Param("sqlStr") String sqlStr);

    List<PoorProductionEO> listAll(Map map);
}
