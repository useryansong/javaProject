package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ProcessMachineEO;
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
public interface ProcessMachineMapper extends BaseMapper<ProcessMachineEO> {

    /**
     * 查找工序可用设备
     *
     * @param processId
     * @return
     */
    @Select("select pm.*, m.machine_code, m.machine_name " +
            "from bsc_process_machine pm " +
            "inner join bsc_machine m on m.machine_id = pm.machine_id " +
            "where pm.process_id = #{processId}")
    List<ProcessMachineEO> selectByProcess(Long processId);

    /**
     * 删除设备
     *
     * @param processId
     * @return
     */
    @Delete("delete from bsc_process_machine where process_id = #{processId}")
    Integer removeByProcessId(Long processId);
}
