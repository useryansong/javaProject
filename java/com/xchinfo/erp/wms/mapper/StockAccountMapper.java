package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface StockAccountMapper extends BaseMapper<StockAccountEO> {

    /**
     *
     *
     * @return
     */

    List<StockAccountEO> selectPageByViewMode(Map param);

    @Select("select sum(case when ac.voucher_type in(1,2,3,4,5,6) then amount end) as countInAmount,sum(case when ac.voucher_type in(7,8,9,10,11,12)" +
            " then amount end) as CountOutAmount from wms_stock_account ac where ac.material_id=#{id} ")
    StockAccountEO countNumById(@Param("id") Long id);

    @Select("select * from wms_stock_account where material_id = #{materialId} and warehouse_id = #{warehouseId} and voucher_type=0 limit 0,1")
    StockAccountEO getByMaterialIdAndWarehouseId(@Param("materialId") Long materialId, @Param("warehouseId") Long warehouseId);

    /**
     * 查看详情时按库位查找物料库存
     *
     * @return
     */
    List<StockAccountEO> checkStockByLocation(Map param);

    /**
     * 按库位查找物料库存
     *
     * @return
     */
    List<StockAccountEO> locationPage(Map param);

    void addBatch(@Param("stockAccounts") List<StockAccountEO> stockAccounts);

    @Delete("delete from wms_stock_account where account_id in ${deleteSql}")
    void deleteByIds(@Param("deleteSql") String deleteSql);

    @Insert("INSERT into  wms_usedage_temp  (usedage_id,voucher_date,material_id) " +
            "select  nextval ('wms_usedage_temp') AS usedage_id,max(t.voucher_date),t.material_id  from wms_stock_account t " +
            "where t.material_id not in ( select material_id  from wms_usedage_temp ) and t.voucher_date is not null GROUP BY t.material_id ")
    void insertSynchro();

    @Update("update wms_usedage_temp ut LEFT JOIN " +
            "(select t.material_id,max(t.voucher_date) as voucher_date  from wms_stock_account t GROUP BY t.material_id) a on a.material_id = ut.material_id " +
            "set  ut.voucher_date = a.voucher_date")
    void updateSynchro();

    List<StockAccountEO> getByMaterialIdsAndMonth(@Param("materialIds") List<Long> materialIds, @Param("month") String month);
}
