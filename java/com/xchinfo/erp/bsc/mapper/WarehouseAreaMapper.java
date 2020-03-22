package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.WarehouseAreaEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author zhongy
 * @date 2019/4/16
 * @update
 */
@Mapper
public interface WarehouseAreaMapper extends BaseMapper<WarehouseAreaEO> {
    /**
     * 查询所有库区
     * @return
     */
    @Select("select distinct wa.* " +
            "from bsc_warehouse_area wa " +
            "inner join sys_org o on o.org_id = wa.org_id " +
//            "inner join sys_role_org ro on ro.org_id = o.org_id " +
//            "inner join sys_user_auth ua on ua.role_id = ro.role_id " +
            "inner join v_user_perm_org upo on upo.org_id = o.org_id " +
            "where wa.status = 1 and upo.user_id = #{userId}")
    List<WarehouseAreaEO> selectAll(Long userId);

    /**
     * 根据库区id及状态更新库区状态
     * @return
     */
    @Update("update bsc_warehouse_area set status = #{status} where warehouse_area_id = #{warehouseAreaId}")
    int updateStatusById(@Param("warehouseAreaId") Long warehouseAreaId, @Param("status") Integer status);

    /**
     * 根据库区名称查询库区信息
     * @return
     */
    @Select("select * from bsc_warehouse_area where area_name = #{areaName}")
    WarehouseAreaEO selectByName(String areaName);

    /**
     * 根据条码查询库区信息
     * @return
     */
    @Select("select * from bsc_warehouse_area where bar_code = #{barCode}")
    WarehouseAreaEO getByBarCode(String barCode);

    /**
     * 库区编码校验重复
     * @param orgId
     * @param warehouseId
     * @param areaCode
     * @return
     */
    @Select("select count(1) from bsc_warehouse_area where org_id=#{orgId} and warehouse_id = #{warehouseId} " +
            " and area_code = #{areaCode}")
    int checkAreaCode(@Param("orgId") Long orgId,@Param("warehouseId") Long warehouseId,@Param("areaCode") String areaCode);

    /**
     * 更改库区下面库位的条码
     * @param warehouseAreaId
     * @param barCode
     * @return
     */
    @Update("update bsc_warehouse_location set bar_code = replace(bar_code,#{oldBarCode},#{barCode}) where warehouse_area_id = #{warehouseAreaId}")
    int updateBarCodeForWarehouseLocation(@Param("warehouseAreaId") Long warehouseAreaId,@Param("barCode") String barCode,@Param("oldBarCode") String oldBarCode);
}
