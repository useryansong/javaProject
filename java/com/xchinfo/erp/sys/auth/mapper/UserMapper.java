package com.xchinfo.erp.sys.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import com.xchinfo.erp.sys.auth.entity.UserEO;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author Yansong Shi
 * @date 2017/10/9
 * @update
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEO> {

    /**
     * 根据用户名查找用户
     *
     * @param userName
     * @return
     */
    @Select("select u.*,o.org_name from sys_user u  " +
            "LEFT JOIN sys_org o on o.org_id = u.org_id " +
            "where u.user_name = #{userName}")
    UserEO queryByUserName(String userName);

    @Select("select * from sys_user where email = #{email}")
    UserEO queryByEmail(String email);

    @Select("select * from sys_user where mobile = #{mobile}")
    UserEO queryByMobile(String mobile);

    /**
     * 查找用户的功能列表
     *
     * @param userId
     * @return
     */
    @Select("select DISTINCT m.permissions from sys_role_menu rm " +
            "inner join sys_menu m on rm.menu_id = m.menu_id " +
            "where rm.role_id in (select ua.role_id from sys_user_auth ua where ua.user_id = #{userId})" +
            "      and m.permissions is not null")
    List<String> queryUserPerms(Long userId);

    /**
     * 更新用户状态
     *
     * @param userId
     * @param status
     * @return
     */
    @Update("update sys_user set status = #{status} where user_id = #{userId}")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 检查用户名是否被使用
     *
     * @param userName
     * @return
     */
    @Select("select count(1) from sys_user where user_name = #{userName}")
    int countByUserName(String userName);

    /**
     * 查询用户角色信息
     *
     * @param userId
     * @return
     */
    @Select("select role_id from sys_user_auth where user_id = #{userId}")
    List<Long> selectUserRoleIds(Long userId);

    /**
     * 删除用户授权角色清单
     *
     * @param userId
     * @return
     */
    @Delete("delete from sys_user_auth where user_id = #{userId}")
    int deleteUserAuth(Long userId);

    /**
     * 插入用户授权角色清单
     *
     * @param userId
     * @param roleId
     * @return
     */
    @Insert("insert into sys_user_auth(user_id, role_id) values (#{userId}, #{roleId})")
    int insertUserAuth(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户授权机构清单
     *
     * @param userId
     * @return
     */
    @Delete("delete from sys_user_org where user_id = #{userId}")
    int deleteUserOrg(Long userId);

    /**
     * 插入用户授权机构清单
     *
     * @param userId
     * @param orgId
     * @return
     */
    @Insert("insert into sys_user_org(user_id, org_id) values (#{userId}, #{orgId})")
    int insertUserOrg(@Param("userId") Long userId, @Param("orgId") Long orgId);

    @Select("select r.* from sys_role_org r " +
            "where r.role_id in (select ua.role_id from sys_user_auth ua where ua.user_id = #{userId});")
    List<Long> getUserOrgs(Long userId);

    @Select("select r.org_id from sys_user_org r where r.user_id = #{userId}")
    List<Long> getUserOrgsData(Long userId);

    /**
     * 查询所有实体
     *
     * @return
     */
    @Select("select * from sys_user where status = 1")
    List<UserEO> selectAll();

    /**
     * 查询用户的角色清单
     *
     * @param userId
     * @return
     */
    @Select("select distinct r.role_code " +
            "from sys_user_auth ua " +
            "inner join sys_role r on ua.role_id = r.role_id " +
            "where user_id = #{userId}")
    Set<String> getUserRoles(Long userId);

    /**
     * 更新供应商账号状态
     *
     * @param supplierCode
     * @return
     */
    @Update("update bsc_supplier set account_status = #{status} where supplier_code = #{supplierCode}")
    int updateSupplierAccountStatus(@Param("supplierCode") String supplierCode, @Param("status") Integer status);

    /**
     * 检查用户是否有权限处理数据
     *
     * @param entity
     * @param idField
     * @param id
     * @param user
     * @return
     */
    @Select("select count(1) " +
            "from ${entity} " +
            "where ${idField} = #{id} " +
            "and (created_by = #{user} or last_modified_by = #{user})")
    int checkAuditableInfo(@Param("entity") String entity,
                           @Param("idField") String idField,
                           @Param("id") Serializable id,
                           @Param("user") String user);




    @Update("update sys_user set inner_org = null where user_id = #{userId}")
    boolean setInnerOrgNull(Long userId);
}
