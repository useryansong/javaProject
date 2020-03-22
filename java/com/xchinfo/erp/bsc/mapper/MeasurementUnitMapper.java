package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MeasurementUnitEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface MeasurementUnitMapper extends BaseMapper<MeasurementUnitEO> {

    /**
     * 查找所有有效计量单位
     *
     * @return
     */
    @Select("select * from bsc_measurement_unit where status = 1")
    List<MeasurementUnitEO> selectAll();

    /**
     * 统计计量是否被使用
     *
     * @return
     */
    @Select("select DISTINCT t.* from bsc_measurement_unit t " +
            " inner join bsc_material m on m.first_measurement_unit = t.unit_id or m.second_measurement_unit = t.unit_id or m.size_unit = t.unit_id " +
            " where unit_id = #{unitId} ")
    MeasurementUnitEO findCountById(Long unitId);
}
