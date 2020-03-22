package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.mes.entity.WorkingProcedureTimeEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkingProcedureTimeMapper extends BaseMapper<WorkingProcedureTimeEO> {
    /**
     * 更改状态
     *
     * @return
     */
    @Update("update mes_working_procedure_time  set status = #{status} where working_procedure_time_id =#{id}")
    boolean updateStatusById(@Param("status") int status, @Param("id") Long id);
    @Select("select max(working_procedure_code) from mes_working_procedure_time where working_procedure_code like '${prefix}%' and org_id=#{orgId} ")
    String getMaxGxhByType(@Param("prefix") String prefix,@Param("orgId") Long orgId);

    @Select("select count(1) from mes_working_procedure_time where working_procedure_code = #{workingProcedureCode} and org_id=#{orgId} ")
    int checkRepeat(@Param("workingProcedureCode") String workingProcedureCode,@Param("orgId") Long orgId);

    List<Map> getPageList(Map map);

    int getPageListCount(Map map);

    List<Map> hasProject(Long userId);

    List<Map> hasWorkShop(Map map);

    @Update("update mes_working_procedure_time set  is_last_procedure = 0  where material_id = #{materialId} and working_procedure_time_id != #{id}")
    boolean updateOtherProcedureStatus(@Param("id") Long id,@Param("materialId") Long materialId);

}
