package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.scm.wms.entity.InventoryDetailEO;
import com.xchinfo.erp.scm.wms.entity.InventoryEO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface InventoryMapper extends BaseMapper<InventoryEO> {
    /**
     * 更改状态
     *
     * @return
     */
    @Update("update wms_inventory i left join wms_inventory_detail d on i.inventory_id=d.inventory_id left join wms_temp_inventory ti on ti.inventory_id = i.inventory_id" +
            " set d.status=#{status} ,i.status = #{status},ti.status=#{status} where i.inventory_id =#{id}")
    boolean updateStatusById(@Param("id") Long id, @Param("status") int status);

    /***
     * 获取待盘点和已盘点的明细数据
     * @param Id
     * @return
     */
    @Select("select * from wms_inventory_detail where inventory_id = #{Id} and status in (1,2)")
    List<InventoryDetailEO> getByInventoryId(Long Id);


    /***
     * 判断出库单明细是否都已完成(是否存在未完成的)
     * @param Id
     * @return
     */
    @Select("select count(1) from wms_inventory_detail where inventory_id = #{Id} and status != 2")
    Integer selectDetailFinishCount(Long Id);

    /***
     * 查找订单下面是否存在已完成的订单明细
     * @param id
     * @return
     */
    @Select("select count(1) from wms_inventory_detail where status='2' and inventory_id = #{id}")
    Integer selectCompleteDetail(@Param("id") Long id);

    /**
     * 机构变更后删除盘点单下的所有明细
     * @param id
     * @return
     */
    @Delete("delete from wms_inventory_detail  where inventory_id = #{id}")
    boolean deleteDetailById(@Param("id") Long id);

    /***
     * 获取登录用户的机构ID
     * @param
     * @return
     */
    /*@Select("select org_id from sys_user where user_id = #{userId}")
    Long getOrgId(Long userId);*/

    @Select("select count(*) from  wms_inventory where (status = 0 or( " +
            "status = 2 and  adjust_status not in (3,4))) and org_id = #{orgId}")
    Integer selectInventory(@Param("orgId") Long orgId);

    @Insert(
        "INSERT INTO wms_inventory_detail (inventory_detail_id,inventory_id,inventory_type,inventory_month, material_id, material_code, material_name,inventory_code, " +
                " element_no,specification,unit_id,figure_number,figure_version,warehouse_id,warehouse_location_id,STATUS,amount,version,created_by,created_time,last_modified_time" +
                " ) SELECT nextval ('wms_inventory_detail') AS inventory_detail_id,#{inventoryId},1,#{Month},i.material_id,i.material_code,i.material_name,i.inventory_code,i.element_no, " +
                " i.specification,i.first_measurement_unit,i.figure_number,i.figure_version,i.main_warehouse_id,i.warehouse_location_id, 0, " +
                " (SELECT sum(IF ((s.voucher_type <= 6),s.amount,(- (1) * s.amount))) AS amount " +
                "FROM  wms_stock_account s " +
                "where (s.voucher_date <=#{beginDate} or s.voucher_date is null) and s.material_id =i.material_id " +
                ") as count,1,(SELECT user_name from sys_user where user_id = #{userId} )as created_by,now(),now() " +
                "FROM  bsc_material  i where i.org_id = #{orgId} and i.status =1"
    )
    int insertdetail (@Param("inventoryId") Long inventoryId,@Param("orgId") Long orgId,@Param("userId") Long userId,@Param("Month") String Month, @Param("beginDate") String beginDate);

    @Update("update wms_inventory_detail i left join wms_temp_inventory ti on ti.inventory_id = i.inventory_id inner join wms_inventory wi on wi.inventory_id = i.inventory_id set i.rel_amount = ( " +
            "SELECT sum( ti.amount ) from wms_temp_inventory ti where i.material_id = ti.material_id  and ti.inventory_id = i.inventory_id and ti.status=0 and wi.org_id = #{orgId} ),i.total_amount = (SELECT sum( ti.amount ) from wms_temp_inventory ti where i.material_id = ti.material_id  and ti.inventory_id = i.inventory_id and ti.status=0 and wi.org_id = #{orgId})," +
            "i.inventory_no = ti.inventory_no,i.last_modified_time = now(),i.adjust_status = 0 where  ti.status=0  and i.material_id = ti.material_id and wi.org_id = #{orgId}")
    void updateRelAcount(@Param("orgId") Long orgId);

    @Update("update wms_temp_inventory ti set ti.status = 1 where ti.inventory_id = (" +
            "select i.inventory_id from  wms_inventory i where  i.status=0 and i.org_id = #{orgId})")
    void updateTempStatus(@Param("orgId") Long orgId);

    /***
     *获取最新的库存数量并更新到盘点明细表中
     * @param orgId
     * @return
     */
    /*@Update("UPDATE wms_inventory_detail wid inner join wms_inventory wi on wi.inventory_id = wid.inventory_id SET  wid.amount = (" +
            "SELECT sum(s.begining_balance + s.purchase_amount + s.production_amount + s.outsource_in_amount + s.consumed_in_amount + s.allocation_in_amount + s.other_receive_amount - s.sale_amount - s.requisition_amount - s.outsource_out_amount - s.consumed_out_amount - s.allocation_out_amount - s.other_delivery_amount) " +
            "FROM v_wms_stock_account s WHERE s.material_id = wid.material_id) where wi.org_id=#{orgId} and wi.status=0")
    void updateNewAmount(@Param("orgId") Long orgId);*/

    @Update("UPDATE wms_inventory_detail wid " +
            "INNER JOIN wms_inventory wi ON wi.inventory_id = wid.inventory_id " +
            "SET wid.amount = (select (SELECT sum( IF (( s.voucher_type <= 6), s.amount, ( - (1) * s.amount) )) AS amount " +
            "FROM wms_stock_account s where (s.voucher_date <=( select inventory_date  from wms_inventory where inventory_id=#{id} ) or s.voucher_date is null) and s.material_id =i.material_id" +
            ") count from bsc_material i " +
            "WHERE i.material_id = wid.material_id)" +
            "WHERE wi.org_id = #{orgId}  AND wi. STATUS = 0")
    void updateNewAmount(@Param("orgId") Long orgId,@Param("id") Long id);

    /***
     *获取盘点差异物料数量并更新到盘点表中
     * @param id
     * @return
     */
    @Update("update wms_inventory set dif_material_amount =(" +
            "select count(*) from   wms_inventory_detail  where amount <> total_amount and  total_amount is not null and  inventory_id  = #{id}" +
            ") where inventory_id  = #{id}")
    void updateDifMaterialAmount(@Param("id") Long id);

    /***
     *根据盘点差异物料数量更新盘点单状态
     * @param id
     * @return
     */
    @Update("update wms_inventory wi set wi.adjust_status = (" +
            " case when wi.dif_material_amount > 0 then 0 else 4 end " +
            "),wi.last_modified_time = NOW() where wi.inventory_id = #{id}")
    void updateInventoryAdjustStatus(@Param("id") Long id);

    /***
     *查找对应机构下所有存在差值的盘点记录
     * @param orgId
     * @return
     */
    @Select("select wid.* from wms_inventory_detail wid " +
            "inner join wms_inventory win on win.inventory_id = wid.inventory_id " +
            "where win.status=0 and win.org_id=#{orgId} and wid.rel_amount is not null and wid.amount <>wid.rel_amount")
    List<InventoryDetailEO> getdifamount(@Param("orgId") Long orgId) ;

    @Update("update wms_inventory i left join wms_inventory_detail d on i.inventory_id=d.inventory_id " +
            " set d.status=2 ,i.status = 2 where i.inventory_id =  (" +
            " select t.inventory_id from ( select iv.inventory_id from  wms_inventory iv where  iv.status=0 and iv.org_id = #{orgId}) t )  ")
    void updateInventoryStatus(@Param("orgId") Long orgId);


    @Select("select * from wms_inventory where status in (0,1) and org_id = #{orgId}")
    InventoryEO selectExistIncentory(Long orgId);

    @Select("select * from wms_inventory_detail where inventory_id = #{Id} and element_no = #{elementNo}")
    InventoryDetailEO selectExistIncentoryDetail(@Param("Id") Long Id,@Param("elementNo") String elementNo);

    @Insert(
            "INSERT INTO wms_inventory_detail (inventory_detail_id,inventory_id,inventory_type, material_id, material_code, material_name,inventory_code, " +
                    " element_no,specification,unit_id,figure_number,figure_version,warehouse_id,warehouse_location_id,STATUS,amount,version,created_by,created_time,last_modified_time" +
                    " ) SELECT nextval ('wms_inventory_detail') AS inventory_detail_id,#{inventoryId},1,i.material_id,i.material_code,i.material_name,i.inventory_code,i.element_no, " +
                    " i.specification,i.first_measurement_unit,i.figure_number,i.figure_version,i.main_warehouse_id,i.warehouse_location_id, 0, " +
                    " (select  sum(s.begining_balance + s.purchase_amount + s.production_amount + s.outsource_in_amount + s.consumed_in_amount + s.allocation_in_amount + s.other_receive_amount " +
                    "  - s.sale_amount - s.requisition_amount - s.outsource_out_amount -s.consumed_out_amount -s.allocation_out_amount -s.other_delivery_amount) from v_wms_stock_account s " +
                    "  where  s.material_id = i.material_id " +
                    " ) as amount,1,(SELECT user_name from sys_user where user_id = #{userId} )as created_by,now(),now() " +
                    "FROM  bsc_material  i where i.org_id = #{orgId} and i.material_id not in ( select material_id from wms_inventory_detail where inventory_id=#{inventoryId})"
    )
    int updateNewInventory (@Param("inventoryId") Long inventoryId,@Param("orgId") Long orgId,@Param("userId") Long userId );

    @Update("update wms_inventory_detail ide " +
            "left join wms_linedge_inventory_detail lid on ide.element_no=lid.element_no " +
            "set ide.linedge_amount = lid.amount ,ide.total_amount = ide.linedge_amount+ ide.rel_amount ,ide.last_modified_time = now() " +
            "where ide.inventory_id = #{id} and ide.element_no=lid.element_no and lid.org_id = (select  org_id  from wms_inventory where inventory_id=#{id}) and lid.inventory_month =ide.inventory_month")
    void updateLinedgeAmount(@Param("id") Long id);


    @Update("update wms_inventory_detail ide " +
            "left join wms_linedge_inventory_material_assignment ima on ide.element_no=ima.element_no " +
            "set ide.linedge_amount = ima.amount ,ide.total_amount = ide.linedge_amount+ ide.rel_amount ,ide.last_modified_time = now() " +
            "where ide.inventory_id = #{id} and ide.element_no=ima.element_no and ima.org_id = (select  org_id  from wms_inventory where inventory_id = #{id}) and ima.inventory_month =ide.inventory_month ")
    void updateLinedgeAssignmentAmount(@Param("id") Long id);

    @Update("update wms_inventory_detail ide " +
            "set ide.total_amount = ide.linedge_amount ,ide.last_modified_time = now() ,ide.adjust_status = 0 " +
            "where ide.inventory_id = #{id} and ide.linedge_amount is not null and ide.rel_amount is null")
    void updateLinedgeLinedgeAmount(@Param("id") Long id);

    @Update("update wms_inventory_detail ide " +
            "set ide.total_amount = (ide.linedge_amount+ide.rel_amount) ,ide.last_modified_time = now() ,ide.adjust_status = 0 " +
            "where ide.inventory_id = #{id} and ide.linedge_amount is not null and ide.rel_amount is not null")
    void updateLinedgeTotalAmount(@Param("id") Long id);

    @Update("update wms_inventory_detail  set  rel_amount= 0 ,total_amount=0,adjust_status=0 where total_amount is  null and inventory_id=#{id}")
    void updateRelAcountToZero(@Param("id") Long id);

    @Update("update wms_inventory_detail set amount = 0 where inventory_id = #{inventoryId} and amount is null")
    void updateAmount(@Param("inventoryId") Long inventoryId);


    @Select("select * from bsc_material where element_no =#{elementNo} and org_id=#{orgId}")
    MaterialEO selectMaterialEO(@Param("elementNo") String elementNo, @Param("orgId") Long orgId);

    @Select("select * from wms_inventory where org_id = #{orgId} and inventory_date = #{inventoryDate} and status != 2")
    List<InventoryEO> selectInventoryNotFinish(@Param("orgId") Long orgId, @Param("inventoryDate") String inventoryDate);

    void updateLinedgeAmountNew(Long inventoryId, Long linedgeInventoryId);
}
