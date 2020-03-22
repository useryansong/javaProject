package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ProjectEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProjectMapper extends BaseMapper<ProjectEO> {
    /**
     * 更改状态
     *
     * @return
     */
    @Update("update bsc_project  set status = #{status} where project_id =#{id}")
    boolean updateStatusById(@Param("status") int status, @Param("id") Long id);
}
