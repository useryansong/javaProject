package com.xchinfo.erp.bpm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bpm.entity.ProcessTransitionEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Mapper
public interface ProcessTransitionMapper extends BaseMapper<ProcessTransitionEO> {

    /**
     * 根据上一节点查找关联的节点
     *
     * @param fromNode
     * @return
     */
    @Select("select * from wf_process_transition where from_node = #{fromNode}")
    List<ProcessTransitionEO> selectByFromNode(Long fromNode);
}
