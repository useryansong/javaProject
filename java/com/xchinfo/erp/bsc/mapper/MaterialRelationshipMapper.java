package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialRelationshipEO;
import org.apache.ibatis.annotations.*;

import java.util.List;


/**
 * @author roman.c
 * @date 2019/4/10
 * @update
 */
@Mapper
public interface MaterialRelationshipMapper extends BaseMapper<MaterialRelationshipEO> {



    /**
     * 新增物料关系
     *
     * @return
     */
    @Insert("insert into bsc_material_relationship (parent_material_id, child_material_id) VALUES (#{parentMaterialId},#{childMaterialId})")
    Integer saveRelationship(MaterialRelationshipEO RelationshipEO);


    /**
     * 查询子物料
     *
     * @param partnerId
     * @return
     */
    @Select("select t.*,m.material_code as parentMaterialCode,m.material_name as parentMaterialName,m.element_no as parentMaterialElementNo" +
            ",m.specification as parentMaterialSpecification,c.material_code as childMaterialCode,c.material_name as childMaterialName" +
            ",c.element_no as childMaterialElementNo,c.specification as childMaterialSpecification " +
            "from bsc_material_relationship t " +
            "inner join bsc_material m on  m.material_id = t.parent_material_id " +
            "inner join bsc_material c on  c.material_id = t.child_material_id " +
            " where t.parent_material_id = #{partnerId}")
    List<MaterialRelationshipEO> selectByPartnerId(Long partnerId);

    /**
     * 查询父物料
     *
     * @param ChildId
     * @return
     */
    @Select("select t.*,m.material_code as parentMaterialCode,m.material_name as parentMaterialName,m.element_no as parentMaterialElementNo" +
            ",m.specification as parentMaterialSpecification,c.material_code as childMaterialCode,c.material_name as childMaterialName" +
            ",c.element_no as childMaterialElementNo,c.specification as childMaterialSpecification " +
            "from bsc_material_relationship t " +
            "inner join bsc_material m on m.status = '1' and m.material_id = t.parent_material_id " +
            "inner join bsc_material c on c.status = '1' and c.material_id = t.child_material_id " +
            "where t.child_material_id = #{ChildId}")
    List<MaterialRelationshipEO> selectByChildId(Long ChildId);


    /**
     * 删除物料关系
     *
     * @param
     * @return
     */
    @Delete("delete from bsc_material_relationship where parent_material_id = #{id} or child_material_id = #{id}")
    Integer removeByMaterialId(Long id);


    /**
     * 查询物料关系是否存在
     *
     * @param
     * @return
     */
    @Select("select count(1) from bsc_material_relationship where parent_material_id = #{parentMaterialId}")
    Integer selectRelationCount(MaterialRelationshipEO RelationshipEO);

    /**
     * 删除单个物料关系
     *
     * @param
     * @return
     */
    @Delete("delete from bsc_material_relationship where parent_material_id = #{parentMaterialId} and child_material_id = #{childMaterialId}")
    Boolean deleteRelation(MaterialRelationshipEO RelationshipEO);

    /**
     * 更新单个物料关系
     *
     * @param
     * @return
     */
    @Update("update bsc_material_relationship t set t.parent_material_id = #{newPId},t.child_material_id = #{newCId} where t.parent_material_id = #{oldPId} and t.child_material_id = #{oldCId}")
    Boolean updateRelation(@Param("oldPId") Long oldPId, @Param("oldCId") Long oldCId, @Param("newPId") Long newPId, @Param("newCId") Long newCId);

    @Select("select * from bsc_material_relationship where parent_material_id = #{parentMaterialId} and child_material_id = #{childMaterialId}")
    MaterialRelationshipEO get(@Param("parentMaterialId") Long parentMaterialId, @Param("childMaterialId") Long childMaterialId);
}
