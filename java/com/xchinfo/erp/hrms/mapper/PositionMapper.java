package com.xchinfo.erp.hrms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.hrms.entity.PositionEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@Mapper
public interface PositionMapper extends BaseMapper<PositionEO> {

    /**
     * 查询员工职位
     *
     * @param positionId
     * @return
     */
    @Select("select count(1) from hr_employee where position_id = #{value}")
    Integer countFromEmployee(Long positionId);

    List<Map> getPositionList(Map map);

    int getPositionListCount(Map map);

    int deleteByIds(Map map);

    int insertByHR(PositionEO positionEO);

    PositionEO selectByIdOne(Long id);
}
