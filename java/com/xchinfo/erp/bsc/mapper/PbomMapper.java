package com.xchinfo.erp.bsc.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.PbomEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PbomMapper extends BaseMapper<PbomEO> {


    @Select("select material_id,material_name,element_no,project_no from bsc_material where org_id = #{orgId} and status = 1")
    List<MaterialEO> selectAllMaterial(Long orgId);

    @Delete("delete from bsc_pbom where element_no in ${strSql} and org_id = #{orgId}")
    boolean deleteByMainMaterilId(@Param("strSql") String strSql,@Param("orgId") Long orgId);


    boolean addBatch(@Param("emps")List<PbomEO> emps);
}
