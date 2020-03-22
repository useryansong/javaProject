package com.xchinfo.erp.sys.org.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.org.entity.OrgEO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@Mapper
public interface OrgMapper extends BaseMapper<OrgEO> {

    /**
     * 根据编码查找数据条数
     *
     * @param orgCode
     * @return
     */
    @Select("select count(1) from sys_org where org_code = #{orgCode}")
    int countByOrgCode(String orgCode);

    /**
     * 根据上级机构查找数据条数
     *
     * @param orgId
     * @return
     */
    @Select("select count(1) from sys_org where parent_org_id = #{orgId}")
    int countByParentOrg(Long orgId);

    /**
     * 根据机构统计用户授权个数
     *
     * @param orgId
     * @return
     */
    @Select("select count(1) from sys_identity where org_id = #{orgId}")
    int countUserByOrg(Long orgId);

    @Select("select o.*, p.org_code as parent_org_code, p.org_name as parent_org_name " +
            "from sys_org o " +
            "left outer join sys_org p on o.parent_org_id = p.org_id " +
            "where 1=1 ${ew.sqlSegment} ")
    List<OrgEO> getList(@Param("ew") Wrapper<OrgEO> wrapper);

    @Select("select o.*, p.org_code as parent_org_code, p.org_name as parent_org_name " +
            "from sys_org o " +
            "left outer join sys_org p on o.parent_org_id = p.org_id " +
            "where o.status=1")
    List<OrgEO> selectTreeList();

    @Select("select o.*, p.org_code as parent_org_code, p.org_name as parent_org_name " +
            "from sys_org o " +
            "left outer join sys_org p on o.parent_org_id = p.org_id " +
            "where o.status=1 and o.org_type = #{type}")
    List<OrgEO> selectOrgTreeList(Integer type);

    /**
     * 设置输出数据
     *
     * @param orgId
     * @return
     */
    @Select("select o.*, p.org_code as parent_org_code, p.org_name as parent_org_name " +
            "from sys_org o " +
            "left outer join sys_org p on o.parent_org_id = p.org_id " +
            "where o.org_id = #{orgId} ")
    OrgEO getById(Serializable orgId);

    /***
     * 校验用户机构权限
     * @param orgId
     * @param userId
     * @return
     */
    @Select("select count(1) from sys_org o " +
            "INNER JOIN v_user_perm_org x on x.org_id = o.org_id " +
            "where x.user_id = #{userId} and o.org_id = #{orgId}")
    Integer checkUserPermissions(@Param("orgId") Long orgId,@Param("userId") Long userId);


    @Select("select DISTINCT o.*, p.org_code as parent_org_code, p.org_name as parent_org_name " +
            "from sys_org o " +
            "INNER JOIN v_user_perm_org x on x.org_id = o.org_id " +
            "left outer join sys_org p on o.parent_org_id = p.org_id " +
            "where o.status=1  and x.user_id = #{userId} and o.org_type = 1 order by o.org_id asc ")
    List<OrgEO> selectPermissionsTreeSelectList(Long userId);

    @Select("select DISTINCT o.*, p.org_code as parent_org_code, p.org_name as parent_org_name " +
            "from sys_org o " +
            "INNER JOIN v_user_perm_org x on x.org_id = o.org_id " +
            "left outer join sys_org p on o.parent_org_id = p.org_id " +
            "where o.status=1  and x.user_id = #{userId} order by o.org_id asc ")
    List<OrgEO> selectPermissionsTreeSelectListAll(Long userId);

    @Select("select DISTINCT o.*, p.org_code as parent_org_code, p.org_name as parent_org_name " +
            "from sys_org o " +
            "left outer join sys_org p on o.parent_org_id = p.org_id " +
            "where o.status=1  order by o.org_id asc ")
    List<OrgEO> selectPermissionsTreeSelectListAllNew(Long userId);

    @Select("select o.*from sys_org o where o.parent_org_id = #{orgId}")
    List<OrgEO> getSonOrgList(Long orgId);

    @Select("select o.* " +
            "from sys_org o " +
            "where o.status=1")
    List<OrgEO> listAll();

    @Select("select DISTINCT o.* from sys_org o " +
            "  INNER JOIN sys_user u on o.org_id = u.org_id " +
            "  where o.status=1  and u.user_id = #{userId} and o.org_type = 1 order by o.org_id asc")
    List<OrgEO> selectUserOrg(Long userId);

    @Select("select o.* " +
            "from sys_org o " +
            "where o.status=1 and o.parent_org_id=1")
    List<OrgEO> selectBySupplier();

    @Select("select DISTINCT o.* " +
            "from sys_org o " +
            "inner join srm_delivery_plan dp on dp.org_id = o.org_id " +
            "where o.status=1 and o.parent_org_id=1 and dp.supplier_id = #{supplierId}")
    List<OrgEO> selectBySupplierDeliveryNote(Long supplierId);

    int deleteByIds(Map map);

    int insertByHR(OrgEO orgEO);

    OrgEO selectByIdOne(Long id);

    @Update("update sys_org set start_safe_production_date=#{startDate} where org_id = #{orgId}")
    int setStartSafeProductionDate(@Param("startDate") String startDate,@Param("orgId") Long orgId);

    List<Map> selectSafeProductionDays();


}
