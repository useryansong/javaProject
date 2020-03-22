package com.xchinfo.erp.bpm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bpm.entity.ProcessInstanceEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Mapper
public interface ProcessInstanceMapper extends BaseMapper<ProcessInstanceEO> {

    /**
     * 备份流程实例表
     *
     * @param instanceId
     * @return
     */
    @Insert("insert into wf_his_process_instance " +
            "select * from wf_process_instance where process_instance_id = #{instanceId}")
    Integer insertHisInstance(Long instanceId);

    /**
     * 备份工作项表
     *
     * @param instanceId
     * @return
     */
    @Insert("insert into wf_his_work_item " +
            "select * from wf_work_item where process_instance_id = #{instanceId}")
    Integer insertHistWorkItem(Long instanceId);

    /**
     * 备份参与者表
     *
     * @param instanceId
     * @return
     */
    @Insert("insert into wf_his_work_item_actor " +
            "select * from wf_work_item_actor " +
            "where work_item_id in (select work_item_id from wf_work_item where process_instance_id = #{instanceId})")
    Integer insertHisWorkItemActor(Long instanceId);

    /**
     * 根据实例删除工作项
     *
     * @param instanceId
     * @return
     */
    @Delete("delete from wf_work_item where process_instance_id = #{instanceId}")
    Integer deleteWorkItemByInstance(Long instanceId);

    /**
     * 根据实例删除工作项参与者
     *
     * @param instanceId
     * @return
     */
    @Delete("delete from wf_work_item_actor " +
            "where work_item_id in (select work_item_id from wf_work_item where process_instance_id = #{instanceId})")
    Integer deleteWorkItemActorByInstance(Long instanceId);
}
