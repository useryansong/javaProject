package com.xchinfo.erp.sys.auth.mapper;

import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.auth.entity.IdentityEO;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@Mapper
public interface IdentityMapper extends BaseMapper<IdentityEO> {

    @Select("select i.*, o.org_name as org_name " +
            "from sys_identity i " +
            "inner join sys_org o on o.org_id = i.org_id where i.identity_id = #{identityId}")
    IdentityEO getById(Long identityId);

    /**
     * 根据用户获取授权信息
     *
     * @param userId
     * @return
     */
    @Select("select i.*, o.org_name as org_name " +
            "from sys_identity i " +
            "inner join sys_org o on o.org_id = i.org_id where i.user_id = #{userId}")
    IdentityEO selectByUserId(Long userId);

    /**
     * 删除用户授权角色清单
     *
     * @param identityId
     * @return
     */
    @Delete("delete from sys_user_auth where identity_id = #{identityId}")
    int deleteUserAuth(Long identityId);

    /**
     * 插入用户授权角色清单
     *
     * @param identityId
     * @param roleId
     * @return
     */
    @Insert("insert into sys_user_auth(identity_id, role_id) values (#{identityId}, #{roleId})")
    int insertUserAuth(@Param("identityId") Long identityId, @Param("roleId") Long roleId);
}
