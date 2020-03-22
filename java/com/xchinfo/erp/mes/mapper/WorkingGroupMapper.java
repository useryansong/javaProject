package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.mes.entity.WorkingGroupEO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

@Mapper
public interface WorkingGroupMapper extends BaseMapper<WorkingGroupEO> {
    /**
     * 更改状态
     *
     * @return
     */
    @Update("update mes_working_group set status = #{status} where id =#{id}")
    boolean updateStatusById(@Param("status") int status, @Param("id") Long id);

    int removeWorkingGroupEmployeeByIds(Map map);
    @Insert("insert into mes_working_group_employee(working_group_id, employee_id) values (#{workingGroupId}, #{employeeId})")
    int addWorkingGroupEmployeeByIds(@Param("workingGroupId") Integer workingGroupId, @Param("employeeId") Integer employeeId);

    @Insert("delete from mes_working_group_employee where working_group_id = #{workingGroupId}")
    int deleteWorkingGroupEmployeeByIWworkingGroupId(Long workingGroupId);
}
