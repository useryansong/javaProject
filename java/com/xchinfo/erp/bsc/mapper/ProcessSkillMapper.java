package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ProcessSkillEO;
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
public interface ProcessSkillMapper extends BaseMapper<ProcessSkillEO> {

    /**
     * 查找工序所需技能
     *
     * @param processId
     * @return
     */
    @Select("select ps.* from bsc_process_skill ps " +
            "where ps.process_id = #{processId}")
    List<ProcessSkillEO> selectByProcess(Long processId);

    /**
     * 删除技能
     *
     * @param processId
     * @return
     */
    @Delete("delete from bsc_process_skill where process_id = #{processId}")
    Integer removeByProcessId(Long processId);
}
