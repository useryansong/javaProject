package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.scm.wms.entity.AllocationDetailEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import org.apache.ibatis.annotations.*;
import org.yecat.mybatis.utils.Criteria;


import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface AllocationDetailMapper extends BaseMapper<AllocationDetailEO> {

    /**
     * 更改状态
     *
     * @return
     */
    @Update("update wms_allocation_detail a  set a.status=#{status}  where a.allocation_detail_id =#{id}")
    boolean updateStatusById(@Param("id") Long id, @Param("status") int status);



    /***
     *查找物料分页
     *
     * @return
     */
    List<StockAccountEO> getMaterialPage(Map param);


    /***
     *查找出库物料分页
     *
     * @return
     */
    List<StockAccountEO> getDeliveryMaterialPage(Map param);


    /***
     *查找生产领料出库物料分页
     *
     * @return
     */
    List<StockAccountEO> getDeliveryProductMaterialPage(Map param);

    /**
     * 查询仓库
     *
     * @return
     */

    @Select("select t.warehouse_id ,b.warehouse_name from v_wms_stock_account t " +
            "left join bsc_warehouse b on b.warehouse_id = t.warehouse_id " +
            "where t.material_id=#{id} GROUP BY t.warehouse_id")
    List<WarehouseEO> listWarehouse(@Param("id") Long id);

    /**
     * 查询库位
     *
     * @return
     */

    @Select("select t.warehouse_location_id,w.location_name from v_wms_stock_account t " +
            "left join bsc_warehouse_location w on w.warehouse_location_id = t.warehouse_location_id " +
            "where t.material_id =#{id} and t.warehouse_id =#{warehouseId} GROUP BY t.warehouse_location_id")
    List<WarehouseLocationEO> listWarehouseLocation(@Param("id") Long id, @Param("warehouseId") Long warehouseId);

    /**
     * 查询数量
     *
     * @return
     */
    @Select("select sum((begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount " +
            "- sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount )) " +
            "as count from v_wms_stock_account t where t.material_id=#{id} and t.warehouse_id =#{warehouseId} and t.warehouse_location_id =#{locationId}" )
    int getAmount(@Param("id") Long id, @Param("warehouseId") Long warehouseId, @Param("locationId") Long locationId);

    /**
     * 查询调入库位
     *
     * @return
     */

    @Select("select * from bsc_warehouse_location where warehouse_id =#{warehouseId} and status='1'")
    List<WarehouseLocationEO> getToWarehouseLocation(@Param("warehouseId") Long warehouseId);

    /**
     * 删除物料台账
     *
     * @return
     */

    @Delete("delete from wms_stock_account  where voucher_id = #{id}")
     boolean deleteStockById(@Param("id") Long warehouseId);

    /***
     *查询库存物料数量
     * @param id
     * @return
     */
    List<StockAccountEO> getMessage(@Param("id") Long id);
}
