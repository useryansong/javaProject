package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.ConstraintEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@Mapper
public interface ConstraintMapper extends BaseMapper<ConstraintEO> {

    /**
     * 查找所有有效数据
     *
     * @return
     */
    @Select("select * from bsc_constraint where status = 1")
    List<ConstraintEO> selectAll();
}
