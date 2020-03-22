package com.xchinfo.erp.bpm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bpm.entity.ProcessDefinitionEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Mapper
public interface ProcessDefinitionMapper extends BaseMapper<ProcessDefinitionEO> {

    /**
     * 根据Key查找
     *
     * @param key
     * @return
     */
    @Select("select * from wf_process_definition where process_key = #{key}")
    ProcessDefinitionEO selectBykey(String key);
}
