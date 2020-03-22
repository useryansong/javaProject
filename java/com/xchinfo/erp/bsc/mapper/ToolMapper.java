package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ToolEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface ToolMapper extends BaseMapper<ToolEO> {
    /**
     * 查询所有实体
     *
     * @return
     */
    @Select("select * from bsc_tool where status = 1")
    List<ToolEO> selectAll();
}
