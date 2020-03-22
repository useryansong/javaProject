package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ProcessMaterialEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface ProcessMaterialMapper extends BaseMapper<ProcessMaterialEO> {

    /**
     * 查找工序可用物料
     *
     * @param processId
     * @return
     */
    @Select("select pm.*, m.material_code, m.material_name " +
            "from bsc_process_material pm " +
            "inner join bsc_material m on m.material_id = pm.material_id " +
            "where pm.process_id = #{processId}")
    List<ProcessMaterialEO> selectByProcess(Long processId);

    /**
     * 删除物料
     *
     * @param processId
     * @return
     */
    @Delete("delete from bsc_process_material where process_id = #{processId}")
    Integer removeByProcessId(Long processId);
}
