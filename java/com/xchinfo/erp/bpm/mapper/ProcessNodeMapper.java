package com.xchinfo.erp.bpm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bpm.entity.ProcessNodeEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Mapper
public interface ProcessNodeMapper extends BaseMapper<ProcessNodeEO> {

    /**
     * 查找开始节点
     *
     * @param processId
     * @return
     */
    @Select("select * from wf_process_node where process_id = #{processId} and node_type = 1")
    ProcessNodeEO selectStartNode(Long processId);

    /**
     * 根据Key查找信息
     *
     * @param nodeKey
     * @return
     */
    @Select("select * from wf_process_node where node_key = #{nodeKey}")
    ProcessNodeEO selectbyKey(String nodeKey);
}
