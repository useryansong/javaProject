package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.StockGroupEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yuanchang
 * @date 2019/4/6
 * @update
 */
@Mapper
public interface StockGroupMapper  extends BaseMapper<StockGroupEO> {

    /**
     * 查询所有实体
     *
     * @return
     */
    @Select("select * from bsc_stock_group where status = 1")
    List<StockGroupEO> selectAll();
}
