package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ProcessToolEO;
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
public interface ProcessToolMapper extends BaseMapper<ProcessToolEO> {

    /**
     * 查找工序可用工具
     *
     * @param processId
     * @return
     */
    @Select("select pt.*, t.tool_code, t.tool_name " +
            "from bsc_process_tool pt " +
            "inner join bsc_tool t on t.tool_id = pt.tool_id " +
            "where pt.process_id = #{processId}")
    List<ProcessToolEO> selectByProcess(Long processId);

    /**
     * 删除工具
     *
     * @param processId
     * @return
     */
    @Delete("delete from bsc_process_tool where process_id = #{processId}")
    Integer removeByProcessId(Long processId);
}
