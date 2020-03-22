package com.xchinfo.erp.bpm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bpm.entity.WorkItemEO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Mapper
public interface WorkItemMapper extends BaseMapper<WorkItemEO> {

    /**
     * 插入参与者列表
     *
     * @param workItemId
     * @param actor
     * @return
     */
    @Insert("insert into wf_work_item_actor(work_item_id, actor) values (#{workItemId}, #{actor})")
    Integer insertActor(@Param("workItemId") Long workItemId, @Param("actor") String actor);
}
