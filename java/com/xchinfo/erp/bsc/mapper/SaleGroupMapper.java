package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.SaleGroupEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SaleGroupMapper extends BaseMapper<SaleGroupEO> {

    /**
     * 查询所有实体
     *
     * @return
     */
    @Select("select * from bsc_sale_group where status = 1")
    List<SaleGroupEO> selectAll();
}
