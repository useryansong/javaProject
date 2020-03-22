package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/10
 * @update
 */
@Mapper
public interface MaterialSupplierMapper extends BaseMapper<MaterialSupplierEO> {

    /**
     * 查询所有供应商物料
     *
     * @return
     */
    @Select("select * from bsc_material_supplier")
    List<MaterialSupplierEO> selectAll();

    /**
     *设置默认
     *
     * @return
     */
    @Update("update bsc_material_supplier set is_default = 1 where material_supplier_id = #{materialSupplierId}")
    boolean updateStatusById(Long materialSupplierId);


    /**
     *设置其他为非默认
     *
     * @return
     */
    @Update("update bsc_material_supplier set is_default = 0 where material_supplier_id != #{materialSupplierId} and material_id = #{materialId}")
    boolean updateOtherStatusById(@Param("materialSupplierId") Long materialSupplierId, @Param("materialId") Long materialId);

    /**
     *查询数据统计
     *
     * @return
     */
    @Select("select count(1) from bsc_material_supplier where material_id = #{materialId}")
    Integer selectSupplierMaterilaCount(Long materialId);

    /**
     *查询数据是否存在
     *
     * @return
     */
    @Select("select count(1) from bsc_material_supplier where material_id = #{materialId} and supplier_id = #{supplierId}")
    Integer selectIsExistCount(MaterialSupplierEO entity);


    /**
     *查询更新后的数据是否存在
     *
     * @return
     */
    @Select("select count(1) from bsc_material_supplier where material_id = #{materialId} and supplier_id = #{supplierId} and material_supplier_id != #{materialSupplierId}")
    Integer selectUpdateIsExistCount(MaterialSupplierEO entity);

    @Select("select * from bsc_material_supplier where material_id = #{materialId} and supplier_id = #{supplierId}")
    MaterialSupplierEO getByMaterialIdAndSupplierId(@Param("materialId") Long materialId, @Param("supplierId") Long supplierId);
}
