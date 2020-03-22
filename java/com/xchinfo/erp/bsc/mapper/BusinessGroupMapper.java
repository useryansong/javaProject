package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.BusinessGroupEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yuanchang
 * @date 2019/4/11
 * @update
 */
@Mapper
public interface BusinessGroupMapper  extends BaseMapper<BusinessGroupEO> {
    /**
     * 查询所有实体
     *
     * @return
     */
    @Select("select * from bsc_business_group where status = 1")
    List<BusinessGroupEO> selectAll();

    /**
     * 根据组类型查询所有实体
     *
     * @return
     */
    @Select("select * from bsc_business_group where status = 1 and group_type = #{groupType}")
    List<BusinessGroupEO> listByGroupType(Integer groupType);
}
