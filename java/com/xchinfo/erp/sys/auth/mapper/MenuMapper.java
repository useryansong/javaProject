package com.xchinfo.erp.sys.auth.mapper;

import com.xchinfo.erp.sys.auth.entity.MenuEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;
import java.util.List;

/**
 * @author roman.li
 * @project wms-sys-provider
 * @date 2018/5/11 14:02
 * @update
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuEO> {

    /**
     * 查找用户的菜单项
     *
     * @param userId
     * @return
     */
    @Select("select m.* from sys_menu m " +
            "inner join sys_role_menu rm on m.menu_id = rm.menu_id " +
            "where rm.role_id in (select ua.role_id from sys_user_auth ua where ua.user_id = #{userId}) " +
            "      and m.type in (0, 1) and m.status = 1 order by sort ")
    List<MenuEO> getUserMenuList(Long userId);

    /**
     * 根据父菜单统计菜单个数
     *
     * @param menuId
     * @return
     */
    @Select("select count(1) from sys_menu where parent_id = #{menuId}")
    Integer countByParentMenu(Long menuId);

    @Select("select m.*, p.name as parent_name " +
            "from sys_menu m " +
            "left outer join sys_menu p on m.parent_id = p.menu_id " +
            "where m.menu_id = #{menuId}")
    MenuEO getById(Serializable menuId);


    /**
     * 查找用户App的菜单项
     *
     * @param userId
     * @return
     */
    @Select("select DISTINCT m.* from sys_menu m " +
            "            inner join sys_role_menu rm on m.menu_id = rm.menu_id " +
            "            where rm.role_id in (select ua.role_id from sys_user_auth ua where ua.user_id = #{userId}) " +
            "            and m.type = 4 and m.url like '%mobile%' order by sort")
    List<MenuEO> getUserAppMenuList(Long userId);
}
