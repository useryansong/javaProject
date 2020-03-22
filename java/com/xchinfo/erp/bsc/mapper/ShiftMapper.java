package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ShiftEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface ShiftMapper extends BaseMapper<ShiftEO> {
    /**
     * 查询所有班次
     * @return
     */
    @Select("select * from bsc_shift")
    List<ShiftEO> selectAll();
}
