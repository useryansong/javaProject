package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.BusinessAgentEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yuanchang
 * @date 2019/4/11
 * @update
 */
@Mapper
public interface BusinessAgentMapper  extends BaseMapper<BusinessAgentEO> {
    /**
     * 查询所有实体
     *
     * @return
     */
    @Select("select * from bsc_business_group where status = 1")
    List<BusinessAgentEO> selectAll();
}
