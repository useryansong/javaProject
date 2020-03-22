package com.xchinfo.erp.hrms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.hrms.entity.EmployeeTrainingEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@Mapper
public interface EmployeeTrainingMapper extends BaseMapper<EmployeeTrainingEO> {

    /**
     * 根据员工查找
     *
     * @param employeeId
     * @return
     */
    @Select("select * from hr_emp_training where employee_id = #{value}")
    List<EmployeeTrainingEO> getByEmployee(Long employeeId);

    /**
     * 根据员工删除
     *
     * @param employeeId
     * @return
     */
    @Delete("delete from hr_emp_training where employee_id = #{value}")
    int deleteByEmployee(Long employeeId);
}
