package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.WarehouseAreaEO;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/11
 * @update
 */
@Mapper
public interface WarehouseMapper extends BaseMapper<WarehouseEO> {
    /**
     * 查询所有实体
     *
     * @return
     */
    @Select("select distinct w.* from bsc_warehouse w inner join sys_org o on w.org_id = o.org_id and w.org_id = #{orgId} " +
            "inner join v_user_perm_org x on x.org_id = o.org_id  where w.status = 1 and x.user_id = #{userId}")
    List<WarehouseEO> selectAll(@Param("userId") Long userId,@Param("orgId") Long orgId );



    /**
     * 更改状态
     *
     * @return
     */
    @Update("update bsc_warehouse  set status = #{status} where warehouse_id =#{id}")
    boolean updateStatusById(@Param("id") Long id, @Param("status") int status);

    /**
     * 查询客户名称是否存在
     *
     * @return
     */
    Integer selectCountByName(WarehouseEO warehouseEO);

    /**
     * 根据条码查询仓库信息
     * @return
     */
    @Select("select * from bsc_warehouse where bar_code = #{barCode}")
    WarehouseEO getByBarCode(String barCode);

    /**
     * 仓库编码校验重复
     * @param orgId
     * @param warehouseCode
     * @return
     */
    @Select("select count(1) from bsc_warehouse where org_id=#{orgId} and warehouse_code = #{warehouseCode}")
    int checkWarehouseCode(@Param("orgId") Long orgId,@Param("warehouseCode") String warehouseCode);

    /**
     * erp编码校验重复
     * @param orgId
     * @param erpCode
     * @return
     */
    @Select("select count(1) from bsc_warehouse where org_id=#{orgId} and erp_code = #{erpCode}")
    int checkErpCode(@Param("orgId") Long orgId,@Param("erpCode") String erpCode);

    /**
     * 条码校验重复
     * @param orgId
     * @param barCode
     * @return
     */
    @Select("select count(1) from bsc_warehouse where org_id=#{orgId} and bar_code = #{barCode}")
    int checkBarCode(@Param("orgId") Long orgId,@Param("barCode") String barCode);

    /**
     * 更改仓库下面库区的条码
     * @param warehouseId
     * @param barCode
     * @return
     */
    @Update("update bsc_warehouse_area set bar_code = replace(bar_code,#{oldBarCode},#{barCode}) where warehouse_id = #{warehouseId}")
    int updateBarCodeForWarehouseArea(@Param("warehouseId") Long warehouseId,@Param("barCode") String barCode,@Param("oldBarCode") String oldBarCode);

    /**
     * 更改仓库下面库位的条码
     * @param warehouseId
     * @param barCode
     * @return
     */
    @Update("update bsc_warehouse_location set bar_code = replace(bar_code,#{oldBarCode},#{barCode}) where warehouse_id = #{warehouseId}")
    int updateBarCodeForWarehouseLocation(@Param("warehouseId") Long warehouseId,@Param("barCode") String barCode,@Param("oldBarCode") String oldBarCode);

    List<WarehouseEO> getList(Long userId);
}
