package com.xchinfo.erp.sys.auth.mapper;

import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.auth.entity.RoleEO;

import java.io.Serializable;
import java.util.List;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEO> {

    /**
     * 根据编码统计数据条数
     *
     * @param roleCode
     * @return
     */
    @Select("select count(1) from sys_role where role_code =#{roleCode} ")
    int selectCountByCode(String roleCode);

    /**
     * 统计角色是否在授权表中使用
     *
     * @param roleId
     * @return
     */
    @Select("select count(1) from sys_user_auth where role_id = #{roleId} ")
    int selectCountAuthById(Serializable roleId);

    @Select("select menu_id from sys_role_menu where role_id = #{roleId}")
    List<Long> selectMenuByRole(Long roleId);

    @Select("select org_id from sys_role_org where role_id = #{roleId}")
    List<Long> selectOrgByRole(Long roleId);

    /**
     * 查找角色实体，包含关联数据
     *
     * @param roleId
     * @return
     */
    @Select("select * from sys_role where role_id =#{roleId} ")
    @Results(value = {
            @Result(id = true, property = "roleId", column = "role_id"),
            @Result(property = "roleCode", column = "role_code"),
            @Result(property = "roleName", column = "role_name"),
            @Result(property = "description", column = "description"),
            @Result(property = "type", column = "type"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "version", column = "version"),
            @Result(property = "menuIds", column = "role_id",
                    many = @Many(select = "com.xchinfo.erp.sys.auth.mapper.RoleMapper.selectMenuByRole")),
            @Result(property = "orgIds", column = "role_id",
                    many = @Many(select = "com.xchinfo.erp.sys.auth.mapper.RoleMapper.selectOrgByRole"))
    })
    RoleEO getById(Serializable roleId);

    /**
     * 根据授权ID查找角色列表
     *
     * @param identityId
     * @return
     */
    @Select("select * from sys_role r " +
            "where exists (select 1 from sys_user_auth ua " +
            "              where r.role_id = ua.role_id and ua.identity_id = #{identityId})")
    List<RoleEO> selectByIdentity(Long identityId);

    @Delete("delete from sys_role_menu where role_id = #{roleId}")
    int deleteOptAuth(Long roleId);

    @Insert("insert into sys_role_menu value (#{roleId}, #{menuId})")
    int updateOptAuth(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    @Delete("delete from sys_role_org where role_id = #{roleId}")
    int deleteOrgAuth(Long roleId);

    @Insert("insert into sys_role_org value (#{roleId}, #{orgId})")
    int updateOrgAuth(@Param("roleId") Long roleId, @Param("orgId")Long orgId);

//    @Select("select distinct r.* from sys_role r inner join sys_org o on r.org_id = o.org_id where r.status = 1 ")
    @Select("select distinct r.* from sys_role r where r.status = 1 ")
    List<RoleEO> selectAll(Long userId);

    @Select("select * from sys_role where status = 1 and type = #{type}")
    List<RoleEO> selectByType(@Param("type") Integer type);

    /**
     * 复制角色数据权限
     * @param fromRoleId
     * @param toRoleId
     * @return
     */
    @Insert("insert into sys_role_org (role_id, org_id) SELECT #{toRoleId} as role_id, o.org_id FROM sys_role_org o " +
            "WHERE o.role_id = #{fromRoleId} AND o.org_id NOT IN (SELECT r.org_id from sys_role_org r WHERE r.role_id = #{toRoleId})")
    int copyOrgPerm(@Param("fromRoleId") Integer fromRoleId, @Param("toRoleId")Integer toRoleId);

    /**
     * 复制角色功能权限
     * @param fromRoleId
     * @param toRoleId
     * @return
     */
    @Insert("INSERT INTO sys_role_menu (role_id, menu_id) SELECT #{toRoleId} AS role_id, o.menu_id FROM sys_role_menu o " +
            "WHERE o.role_id = #{fromRoleId} AND o.menu_id NOT IN (SELECT r.menu_id FROM sys_role_menu r WHERE r.role_id = #{toRoleId})")
    int copyOpt(@Param("fromRoleId") Integer fromRoleId, @Param("toRoleId")Integer toRoleId);
}
