package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
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
public interface WarehouseLocationMapper extends BaseMapper<WarehouseLocationEO> {
    /**
     * 查询所有库位
     * @return
     */
    @Select("select distinct wl.* " +
            "from bsc_warehouse_location wl " +
            "inner join sys_org o on o.org_id = wl.org_id " +
//            "inner join sys_role_org ro on ro.org_id = o.org_id " +
//            "inner join sys_user_auth ua on ua.role_id = ro.role_id " +
            "inner join v_user_perm_org upo on upo.org_id = o.org_id " +
            "where wl.status = 1 and upo.user_id = #{userId}")
    List<WarehouseLocationEO> selectAll(Long userId);

    /**
     * 根据库位名称查询库位信息
     * @return
     */
    @Select("select * from bsc_warehouse_location where location_name = #{locationName}")
    WarehouseLocationEO selectByName(String locationName);

    /**
     * 根据库位id及状态更新库位状态
     * @return
     */
    @Update("update bsc_warehouse_location set status = #{status} where warehouse_location_id = #{warehouseLocationId}")
    int updateStatusById(@Param("warehouseLocationId") Long warehouseLocationId, @Param("status") Integer status);

    /**
     * 根据条码查询库位信息
     * @return
     */
    @Select("select * from bsc_warehouse_location where bar_code = #{barCode}")
    WarehouseLocationEO getByBarCode(String barCode);

    /**
     * 库位编码校验重复
     * @param orgId
     * @param warehouseId
     * @param warehouseAreaId
     * @param locationCode
     * @return
     */
    @Select("select count(1) from bsc_warehouse_location where org_id=#{orgId} and warehouse_id = #{warehouseId} " +
            "and warehouse_area_id = #{warehouseAreaId} and location_code = #{locationCode}")
    int checkLocationCode(@Param("orgId") Long orgId,@Param("warehouseId") Long warehouseId,
                          @Param("warehouseAreaId") Long warehouseAreaId,@Param("locationCode") String locationCode);
}
