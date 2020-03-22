package com.xchinfo.erp.hrms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.hrms.entity.EmployeeEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<EmployeeEO> {

    /**
     * 启用用户
     *
     * @param employeeId
     */
    @Update("update hr_employee set status = 1 where employee_id = #{value}")
    int enableEmployee(Long employeeId);

    List<Map> getEmployeeList(Map map);

    int getEmployeeListCount(Map map);

    /**
     * 查找所有在职员工
     *
     * @return
     */
    @Select("select e.* from hr_employee e  " +
            "inner join sys_user o on e.root_org_id = o.org_id " +
            "where e.inservice = '1' and o.user_id = #{userId}")
    List<EmployeeEO> selectAll( Long userId);


    int deleteByIds(Map map);

    int insertByHR(EmployeeEO employeeEO);

    EmployeeEO selectByIdOne(Long id);
}
