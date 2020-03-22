package com.xchinfo.erp.sys.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.auth.entity.DataAuthRuleEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/24
 * @update
 */
@Mapper
public interface DataAuthRuleMapper extends BaseMapper<DataAuthRuleEO> {

    /**
     * 根据实体查找规则
     *
     * @param dataEntry
     * @return
     */
    @Select("select * from sys_data_auth_rule where data_entry = #{dataEntry}")
    List<DataAuthRuleEO> selectAll(String dataEntry);
}
