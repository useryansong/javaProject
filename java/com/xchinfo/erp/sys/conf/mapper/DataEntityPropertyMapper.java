package com.xchinfo.erp.sys.conf.mapper;

import com.xchinfo.erp.sys.conf.entity.DataEntityPropertyEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author roman.li
 * @date 2018/11/22
 * @update
 */
@Mapper
public interface DataEntityPropertyMapper extends BaseMapper<DataEntityPropertyEO> {

    /**
     * 根据Entity查询实体属性
     *
     * @param entityCode
     * @return
     */
    @Select("select * from sys_data_entity_property where entity_code = #{entityCode}")
    List<DataEntityPropertyEO> selectListByEntity(String entityCode);
}
