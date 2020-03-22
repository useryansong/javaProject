package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ProcessEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface ProcessMapper extends BaseMapper<ProcessEO> {

    @Select("select p.*, pp.process_code as parent_process_code, pp.process_name as parent_process_name, m.material_code, m.material_name " +
            "from bsc_process p " +
            "left outer join bsc_process pp on p.parent_process = pp.process_id " +
            "inner join bsc_material m on p.material_id = m.material_id " +
            "where 1=1 ${ew.sqlSegment} ")
    List<ProcessEO> selectList(@Param("ew") Wrapper<ProcessEO> wrapper);

    @Select("select p.*, pp.process_code as parent_process_code, pp.process_name as parents_process_name, m.material_code, m.material_name " +
            "from bsc_process p " +
            "left outer join bsc_process pp on p.parent_process = pp.process_id " +
            "inner join bsc_material m on p.material_id = m.material_id " +
            "where p.material_id = #{materialId}")
    List<ProcessEO> selectByMaterial(Long materialId);
}
