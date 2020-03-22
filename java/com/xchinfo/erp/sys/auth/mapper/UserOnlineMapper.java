package com.xchinfo.erp.sys.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.auth.entity.UserOnlineEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/26
 * @update
 */
@Mapper
public interface UserOnlineMapper extends BaseMapper<UserOnlineEO> {

    /**
     * 根据到期时间查找
     *
     * @param expiredDate
     * @return
     */
    @Select("select * from sys_user_online where last_access_time <= #{expiredDate}")
    List<UserOnlineEO> selectByExpired(Date expiredDate);
}
