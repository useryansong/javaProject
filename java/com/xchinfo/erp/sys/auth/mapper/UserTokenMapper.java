package com.xchinfo.erp.sys.auth.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xchinfo.erp.sys.auth.entity.UserTokenEO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author roman.li
 * @date 2017/10/9
 * @update
 */
@Mapper
public interface UserTokenMapper extends BaseMapper<UserTokenEO> {

    /**
     * 根据用户ID查找Token
     *
     * @param userId
     * @return
     */
    @Select("select * from sys_user_token where user_id = #{userId} and login_type = #{loginType}")
    List<UserTokenEO> queryByUserId(@Param("userId") Long userId,@Param("loginType") Integer loginType);

    /**
     * 根据指定的Token删除数据
     *
     * @param userId
     */
    @Delete("delete from sys_user_token where user_id = #{userId} and login_type = #{loginType}")
    void deleteToken(@Param("userId") Long userId,@Param("loginType") Integer loginType);

    /**
     * 定制结果列表
     *
     * @param wrapper
     * @return
     */
    @Select("select ut.*, u.user_name as userName, u.name as name " +
            "from sys_user_token ut, sys_user u " +
            "where ut.user_id = u.user_id ${ew.sqlSegment} ")
    List<UserTokenEO> getList(@Param("ew") Wrapper<UserTokenEO> wrapper);

    /**
     * 通过Token查找数据
     *
     * @param token
     * @return
     */
    @Select("select * from sys_user_token where token = #{token}")
    UserTokenEO selectByToken(String token);
}
