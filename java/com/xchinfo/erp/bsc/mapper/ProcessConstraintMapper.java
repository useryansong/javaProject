package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ProcessConstraintEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@Mapper
public interface ProcessConstraintMapper extends BaseMapper<ProcessConstraintEO> {

    /**
     * 根据工序查找
     *
     * @param processId
     * @return
     */
    @Select("select pc.* from bsc_process_constraint pc " +
            "where pc.process_id = #{processId}")
    List<ProcessConstraintEO> selectByProcess(Long processId);

    /**
     * 删除约束
     *
     * @param processId
     * @return
     */
    @Delete("delete from bsc_process_constraint where process_id = #{processId}")
    Integer removeByProcessId(Long processId);
}
