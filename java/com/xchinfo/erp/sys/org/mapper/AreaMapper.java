package com.xchinfo.erp.sys.org.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xchinfo.erp.sys.org.entity.AreaEO;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;
import java.util.List;

/**
 * @author roman.li
 * @project wms-sys-provider
 * @date 2018/6/8 16:21
 * @update
 */
@Mapper
public interface AreaMapper extends BaseMapper<AreaEO> {

    /**
     * 查找全部数据
     *
     * @param wrapper
     * @return
     */
    @Select("select a.*, p.area_name as parent_name " +
            "from sys_area a " +
            "left outer join sys_area p on a.parent_id = p.area_id " +
            "where 1=1 ${ew.sqlSegment}")
    List<AreaEO> getList(@Param("ew") Wrapper<AreaEO> wrapper);

    @Select("select a.*, p.area_name as parent_name " +
            "from sys_area a " +
            "left outer join sys_area p on a.parent_id = p.area_id " +
            "where a.area_id = #{areaId}")
    AreaEO getById(Serializable areaId);

    @Select("select count(1) from sys_area where parent_id = #{areaId}")
    int countByParent(Long areaId);

    @Select("select count(1) from sys_area where area_code = #{areaCode}")
    int countByAreaCode(String areaCode);
}
