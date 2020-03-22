package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ShiftTypeEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface ShiftTypeMapper extends BaseMapper<ShiftTypeEO> {
    /**
     * 查询所有班组
     * @return
     */
    @Select("select * from bsc_shift_type where status = 1")
    List<ShiftTypeEO> selectAll();
}
