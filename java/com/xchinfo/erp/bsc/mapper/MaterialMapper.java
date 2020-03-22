package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.bsc.entity.ProjectEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface MaterialMapper extends BaseMapper<MaterialEO> {

    /**
     * 查找所有物料
     *
     * @return
     */
    @Select("select m.* from bsc_material m inner join sys_org o on m.org_id = o.org_id " +
            "  INNER JOIN v_user_perm_org x on x.org_id = m.org_id  where m.status = 1 and x.user_id = #{userId}")
    List<MaterialEO> selectAll(Long userId);


    /**
     * 查找数量统计
     *
     * @return
     */
    List<MaterialEO> selectDuplicateNo(MaterialEO materialEO);



    /**
     * 根据ID更新状态
     *
     * @return
     */
    @Update("update bsc_material set status = #{status} where material_id = #{Id}")
    boolean updateStatusById(@Param("Id") Long Id, @Param("status") Integer status);

    /**
     * 根据ID更新是否存在子节点
     *
     * @return
     */
    @Update("update bsc_material set is_exist_child  = #{isExistChild} where material_id = #{Id}")
    boolean updateIsExistChildById(@Param("Id") Long Id, @Param("isExistChild") Integer isExistChild);


    /**
     * 根据盘点单查找可用物料
     *
     * @return
     */
    List<MaterialEO> selectInventoryPage(Map param);

    /**
     * 根据物料编码获取物料信息
     *
     * @return
     */
    @Select("select * from bsc_material where material_code = #{materialCode}")
    MaterialEO getByMaterialCode(String materialCode);

    /***
     * 查询是否存在物料台账记录
     * @param Id
     * @return
     */
    @Select("select count(1) from wms_stock_account where material_id = #{Id}")
    Integer selectCountStock(Long Id);


    /**
     *查询数据是否存在
     *
     * @return
     */
    @Select("select * from bsc_material_supplier where material_id = #{materialId} and supplier_id = #{supplierId}")
    MaterialSupplierEO selectIsExistCount(MaterialSupplierEO entity);

    @Select("select m.*,w.erp_code " +
            "from bsc_material m " +
            "left join bsc_warehouse w on m.main_warehouse_id = w.warehouse_id " +
            "where m.status = 1 and m.element_no = #{elementNo} and m.org_id = #{orgId}")
    MaterialEO                                                        getByElementNoAndOrgId(@Param("elementNo") String elementNo, @Param("orgId") Long orgId);

    @Select("select m.*,w.erp_code " +
            "from bsc_material m " +
            "inner join bsc_material_relationship mr on m.material_id = mr.child_material_id " +
            "left join bsc_warehouse w on m.main_warehouse_id = w.warehouse_id " +
            "where mr.parent_material_id = #{materialId}")
    MaterialEO getSonMaterial(@Param("materialId") Long materialId);

    @Select("select * from bsc_material m " +
            "inner join bsc_material_relationship mr on m.material_id = mr.parent_material_id " +
            "where mr.child_material_id = #{materialId}")
    MaterialEO getParentMaterial(Long materialId);

    @Select("select * from bsc_project p where p.project_name = #{projectName} and p.org_id = #{orgId}")
    List<ProjectEO>  getProject(@Param("projectName") String projectName, @Param("orgId") Long orgId);

    @Select("select * from bsc_material where org_id = #{orgId} and status = 1")
    List<MaterialEO> selectAllMaterialByOrgId(@Param("orgId") Long orgId);

    @Select("select m.*, wl.location_name " +
            "from bsc_material m " +
            "left join bsc_warehouse_location wl on wl.warehouse_location_id = m.warehouse_location_id " +
            "where m.material_id in ${sqlStr} ")
    List<MaterialEO> getByMaterialIds(@Param("sqlStr") String sqlStr);

    List<MaterialEO> getSendReceiveStore(Map map);

    @Select("select amount from v_wms_stock_account_total where material_id = #{materialId}")
    Double getStockByMaterialId(@Param("materialId") Long materialId);
}
