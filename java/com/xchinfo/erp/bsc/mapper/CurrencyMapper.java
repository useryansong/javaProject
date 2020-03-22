package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.CurrencyEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/5
 * @update
 */
@Mapper
public interface CurrencyMapper extends BaseMapper<CurrencyEO> {

    /**
     * 查询所有实体
     *
     * @return
     */
    @Select("select * from bsc_currency where status = 1")
    List<CurrencyEO> selectAll();
}
