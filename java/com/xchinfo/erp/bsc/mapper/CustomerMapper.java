package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface CustomerMapper extends BaseMapper<CustomerEO> {

    /**
     * 查询所有客户
     *
     * @return
     */
    @Select("select * from bsc_customer where status = 1")
    List<CustomerEO> selectAll();

    /**
     * 查询客户名称是否存在
     *
     * @return
     */
    Integer selectCountByName(CustomerEO customerEO);


    /**
     * 根据ID更新状态
     *
     * @return
     */
    @Update("update bsc_customer set status = #{status} where customer_id = #{Id}")
    boolean updateStatusById(@Param("Id") Long Id, @Param("status") Integer status);

    /**
     * 根据ID更新系统账号状态
     *
     * @return
     */
    @Update("update bsc_customer set account_status = #{status} where customer_id = #{Id}")
    boolean updateAccountStatusById(@Param("Id") Long Id, @Param("status") Integer status);


    List<CustomerEO> selectPageNew(Map map);

    @Select("select DISTINCT u.* from sys_user u " +
            "left JOIN sys_user_auth x on x.user_id = u.user_id " +
            "left JOIN sys_role_menu rm on x.role_id = rm.role_id " +
            "LEFT JOIN sys_menu m on m.menu_id = rm.menu_id " +
            "where m.permissions = #{permissions}")
    List<Map> getApprovers(@Param("permissions") String permissions);

    @Update("update bsc_customer set status = 3 , final_approver_id = #{approverId} ,final_approver = #{approver} " +
            "where customer_id = #{customerId}")
    boolean submit(@Param("approverId") Long approverId, @Param("approver") String approver, @Param("customerId") Long customerId);

    @Update("update bsc_customer set status = #{result} " +
            "where customer_id = #{customerId}")
    boolean approve(@Param("result") Long result, @Param("customerId") Long customerId);

    @Update("update bsc_partner_address set status = 3 , final_approver_id = #{approverId} ,final_approver = #{approver} " +
            "where address_id = #{addressId}")
    boolean addressSubmit(@Param("approverId") Long approverId, @Param("approver") String approver, @Param("addressId") Long addressId);


    @Update("update bsc_partner_address set status = #{result} where address_id = #{addressId}")
    boolean approveAddress(@Param("result") Long result, @Param("addressId") Long addressId);

    @Select("select * from bsc_customer where customer_name = #{customerName} and status = 1")
    CustomerEO getByCustomerName(@Param("customerName") String customerName);
}
