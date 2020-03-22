package com.xchinfo.erp.mes.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface MaterialPlanMapper extends BaseMapper<MaterialPlanEO> {

    /***
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    @Update("update cmp_material_plan set status = #{status} where serial_id = #{id}")
    boolean updateStatusById(@Param("id") Long id, @Param("status") Integer status);


    /***
     * 查询是否存在月计划
     * @param entity
     * @return
     */
    @Select("SELECT count(1) from cmp_material_plan where customer_id = #{customerId} and month_date = #{monthDate} and material_id = #{materialId} and plan_type = 1")
    Integer selectExistCount(MaterialPlanEO entity);

    /***
     * 查询存在的月计划
     * @param entity
     * @return
     */
    @Select("SELECT * from cmp_material_plan where customer_id = #{customerId} and month_date = #{monthDate} and material_id = #{materialId} and plan_type = 1")
    MaterialPlanEO selectExistMaterialPlan(MaterialPlanEO entity);

    /***
     * 查询零件号和客户的关系是否存在
     * @param customerId
     * @param elementNo
     * @return
     */
    @Select("select t.*,m.customer_name,m.customer_code,p.inventory_code,p.min_stock,p.material_code,p.material_name,p.is_product,p.is_purchase,p.element_no,p.specification," +
            "case when s.stock_count = '' or s.stock_count is null " +
            "then 0 " +
            "else s.stock_count " +
            "end as stock_count, " +
            "case when l.not_delivery_quantity='' or l.not_delivery_quantity is null " +
            "then 0 else l.not_delivery_quantity " +
            "end as in_transit_count " +
            "from bsc_material_customer t " +
            " inner join bsc_customer m on m.customer_id = t.customer_id " +
            "inner join bsc_material p on p.material_id = t.material_id " +
            "left join (select sum((begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount " +
            "            - sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount )) as stock_count,h.material_id from v_wms_stock_account h GROUP BY h.material_id) s on s.material_id = t.material_id " +
            "left join  (SELECT sum(k.delivery_quantity) as not_delivery_quantity,u.material_id FROM srm_delivery_note_detail k " +
            "           INNER JOIN srm_delivery_plan j on j.delivery_plan_id = k.delivery_plan_id " +
            "           INNER JOIN srm_purchase_order u on u.purchase_order_id = j.purchase_order_id " +
            "            where k.status = 2 GROUP BY u.material_id) l on l.material_id = t.material_id " +
            "where t.customer_id = #{customerId} and p.element_no = #{elementNo} and p.org_id = #{orgId} limit 0,1")
    MaterialCustomerEO selectCustomerMaterial(@Param("customerId") Long customerId, @Param("elementNo") String elementNo,@Param("orgId") Long orgId);

    /***
     * 查询修改后的月计划是否存在
     * @param entity
     * @return
     */
    @Select("SELECT count(1) from cmp_material_plan where customer_id = #{customerId} and month_date = #{monthDate} and material_id = #{materialId} and plan_type = 1 and serial_id != #{serialId}")
    Integer selectUpdateExistCount(MaterialPlanEO entity);


    /***
     * 查询客户物料关键是否存在
     * @param customerId
     * @param materialId
     * @return
     */
    @Select("select count(1) " +
            "from bsc_material_customer t " +
            "inner join bsc_customer m on m.customer_id = t.customer_id " +
            "inner join bsc_material p on p.material_id = t.material_id " +
            "where t.customer_id = #{customerId} and t.material_id = #{materialId} ")
    Integer selectExistCustomerMaterial(@Param("customerId") Long customerId, @Param("materialId") Long materialId);


    /**
     * 根据盘点单查找可用物料
     *
     * @return
     */
    List<MaterialPlanEO> selectMPlanCustomPage(Map param);



    /***
     * 查询是否存在日计划
     * @param entity
     * @return
     */
    @Select("SELECT * from cmp_material_plan where customer_id = #{customerId} and week_date = #{weekDate} and material_id = #{materialId} and plan_type = 2")
    MaterialPlanEO selectExistWeekCount(MaterialPlanEO entity);



    /***
     * 查询是月计划实体
     * @param entity
     * @return
     */
    @Select("SELECT * from cmp_material_plan where customer_id = #{customerId} and month_date = #{monthDate} and material_id = #{materialId} and plan_type = 1")
    MaterialPlanEO selectExistEntity(MaterialPlanEO entity);

    /***
     * 查询是否存在子计划
     * @param Id
     * @return
     */
    @Select("SELECT DISTINCT t.* from cmp_material_plan t " +
            "INNER JOIN cmp_material_plan l on l.material_id = t.material_id and l.month_date = t.month_date and l.customer_id = t.customer_id and l.plan_type = 2 " +
            "where t.plan_type = 1 and t.serial_id = #{Id}")
    MaterialPlanEO selectChildPlan(Long Id);


    /***
     *查询是否存在（采购，委外，生产等计划已发布）
     * @param Id
     * @return
     */
    @Select("SELECT count(1) from cmp_material_distribute t " +
            "INNER JOIN cmp_material_plan l on t.serial_id = l.serial_id " +
            "where t.status != 0 and t.serial_id = #{Id}")
    Integer selectCountPPOPlan(Long Id);



    /***
     * 更新关闭之前的状态
     * @param id
     * @return
     */
    @Update("update cmp_material_plan t set t.pre_status = t.status where t.serial_id = #{id}")
    boolean updatePreStatusByStatus(Long id);


    /***
     * 打开操作更新之前的状态
     * @param id
     * @return
     */
    @Update("update cmp_material_plan t set t.status = t.pre_status where t.serial_id = #{id}")
    boolean updateStatusByPreStatus(Long id);


//    @Select("SELECT t.* from cmp_material_plan t where t.material_id = #{materialId} and t.customer_id = #{customerId} and t.month_date = #{monthDate} and t.week_sum = #{weekSum} and t.org_id = #{orgId} and t.plan_type = 2")
//    List<MaterialPlanEO> getWeekDetailList(@Param("customerId") Long customerId, @Param("materialId") Long materialId,@Param("monthDate") Date monthDate,@Param("weekSum") Integer weekSum,@Param("orgId") Long orgId);

    @Select("SELECT t.* from cmp_material_plan t where t.parent_serial_id = #{parentSerialId} and t.org_id = #{orgId} and t.plan_type = 2")
    List<MaterialPlanEO> getWeekDetailList(@Param("parentSerialId") Long parentSerialId,@Param("orgId") Long orgId);


    @Select("select * from bsc_material where material_id = #{Id}")
    MaterialEO getByMaterialId(Long Id);

    @Select("SELECT DISTINCT t.* from srm_purchase_order t " +
            "INNER JOIN cmp_material_distribute l on l.serial_distribute_id = t.serial_distribute_id " +
            "INNER JOIN cmp_material_plan p on p.serial_id = l.serial_id " +
            "where p.serial_id = #{Id}")
    List<PurchaseOrderEO> selectPurchaseOrderList(Long Id);

    @Select("select count(1) from srm_delivery_plan t " +
            "INNER JOIN srm_purchase_order l on l.purchase_order_id = t.purchase_order_id " +
            "where t.purchase_order_id = #{Id} and t.type = #{type} and t.plan_delivery_date = #{date}")
    Integer selectDeliveryPlanCount(@Param("Id") Long Id,@Param("type") Integer type,@Param("date") Date date);


    /**
     * 查找对应code的数据条数
     *
     * @param code
     * @return
     */
    @Select("select count(1) from sys_code_rule where rule_code = #{code} and org_id=#{orgId}")
    int selectCountByCode(@Param("code")String code,@Param("orgId")Long orgId);

    @Select("select max(left(${colName}, #{seqLength})) from ${tableName} where org_id=#{orgId}")
    String selectMaxCode(@Param("tableName") String tableName, @Param("colName") String colName,
                         @Param("seqLength") int seqLength,@Param("orgId")Long orgId);

    @Select("select max(right(${colName}, #{seqLength})) " +
            "from ${tableName} " +
            "where left(${colName}, #{prefixLength}) = #{prefix} " +
            "and length(${colName}) = #{codeLength} and org_id=#{orgId}")
    String selectMaxCodeWithPrefix(@Param("tableName") String tableName, @Param("colName") String colName,
                                   @Param("seqLength") int seqLength, @Param("prefixLength") int prefixLength,
                                   @Param("codeLength") int codeLength, @Param("prefix") String prefix,@Param("orgId")Long orgId);


    @Select("select * from sys_code_rule where rule_code = #{code} and org_id=#{orgId}")
    CodeRuleEO selectByRuleCode(@Param("code")String code,@Param("orgId")Long orgId);


//    @Select("select t.* from srm_delivery_plan t " +
//            "INNER JOIN srm_purchase_order l on l.purchase_order_id = t.purchase_order_id " +
//            "INNER JOIN cmp_material_distribute d on l.serial_distribute_id = d.serial_distribute_id " +
//            "INNER JOIN cmp_material_plan c on c.serial_id = d.serial_id " +
//            "where  t.status != #{status} and t.plan_delivery_date = #{date} and c.serial_id = #{Id} ")
//    List<DeliveryPlanEO> selectExistCountOrderPlan(@Param("status") Integer status,@Param("Id") Long Id, @Param("date") Date date);

    @Select("select t.* from srm_delivery_plan t where t.status != 0 and t.plan_week_id = #{Id} ")
    List<DeliveryPlanEO> selectExistCountOrderPlan(Long Id);


    @Delete("delete from  srm_delivery_plan where plan_week_id = #{Id}")
    boolean deleteDeliveryPlanById(Long Id);



    @Select("select t.*,m.customer_code,m.customer_name,p.inventory_code,p.material_code,p.material_name,p.is_product,p.is_purchase,p.element_no,p.org_id,k.unit_name,p.min_stock " +
            "from bsc_material_customer t " +
            "inner join bsc_customer m on m.customer_id = t.customer_id " +
            "inner join bsc_material p on p.material_id = t.material_id " +
            "left join bsc_measurement_unit k on k.unit_id = p.first_measurement_unit " +
            "where m.customer_name = #{customerName} and p.element_no = #{elementNo} and p.org_id = #{orgId} limit 0,1")
    MaterialCustomerEO selectCustomerMaterialPlan(@Param("customerName") String customerName, @Param("elementNo") String elementNo,@Param("orgId") Long orgId);


    @Select("select sum((begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount - sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount )) as count,h.material_id " +
            "from v_wms_stock_account_simple h  " +
            "INNER JOIN bsc_material m on h.material_id = m.material_id and m.org_id = #{orgId} " +
            "GROUP BY h.material_id")
    List<StockAccountEO> selectAllStockByOrgId(Long orgId);

    /***
     * 周计划的月父节点
     * @param
     * @return
     */
    @Select("SELECT * from cmp_material_plan where customer_id = #{customerId} and month_date = #{monthDate} and material_id = #{materialId} and org_id = #{orgId} and plan_type = 3")
    MaterialPlanEO selectParernPlan(@Param("customerId") Long customerId, @Param("materialId") Long materialId,@Param("monthDate") Date monthDate,@Param("orgId") Long orgId);


    /***
     * 查询是否存在周日计划
     * @param
     * @return
     */
    @Select("SELECT count(1) from cmp_material_plan where customer_id = #{customerId} and week_date = #{weekDate} and material_id = #{materialId} and org_id = #{orgId} and plan_type = 2")
    Integer selectWeekPlanCount(@Param("customerId") Long customerId, @Param("materialId") Long materialId,@Param("weekDate") Date weekDate,@Param("orgId") Long orgId);



    @Select("SELECT t.serial_id from cmp_material_plan t where t.plan_type = 1 and t.month_date = #{monthDate} and t.status = #{status} and t.org_id = #{orgId} ")
    List<Long> selectIdList(@Param("monthDate") Date monthDate,@Param("status") Integer status,@Param("orgId") Long orgId);

    @Select("select * from cmp_material_plan where serial_id in ${sqlStr}")
    List<MaterialPlanEO> selectPlanInfoByIds(@Param("sqlStr") String sqlStr);

    @Select("select * from bsc_material where element_no = #{elementNo}  and org_id = #{orgId} LIMIT 0,1")
    MaterialEO selectMaterialInfo(@Param("elementNo") String elementNo,@Param("orgId") Long orgId);

    @Select("select t.* from bsc_customer t " +
            "INNER JOIN bsc_material_customer l on l.customer_id = t.customer_id " +
            "INNER JOIN bsc_material s on s.material_id = l.material_id " +
            "where s.material_id = #{Id} and l.is_default = 1")
    CustomerEO selectDefaultCustomer(Long Id);

    @Delete("DELETE from cmp_material_plan where status = 0 and plan_type = 1 and month_date = #{monthDate} and org_id = #{orgId}")
    boolean deleteByIdList(@Param("monthDate") Date monthDate,@Param("orgId") Long orgId);

    @Delete("DELETE from cmp_material_plan where status = 0 and plan_type = 1 and org_id = #{orgId} and serial_id in ${sqlStr} ")
    boolean deleteByIds(@Param("sqlStr") String sqlStr,@Param("orgId") Long orgId);

    @Select("select mp.*, m.material_id as temp_material_id " +
            "from cmp_material_plan mp " +
            "left join bsc_material m on mp.material_id = m.material_id " +
            "where mp.serial_id in ${sqlStr}")
    List<MaterialPlanEO> getBySerialIds(@Param("sqlStr") String sqlStr);

    @Select("select mp.* " +
            "from cmp_material_plan mp " +
            "where mp.parent_serial_id in ${sqlStr}")
    List<MaterialPlanEO> getByParentSerialIds(@Param("sqlStr") String sqlStr);

    @Update("update cmp_material_plan set status = #{status} where serial_id in ${sqlStr}")
    void updateStatusByIds(@Param("sqlStr") String sqlStr, @Param("status") Integer status);

    @Update("update cmp_material_plan set status = #{status} where parent_serial_id in ${sqlStr}")
    void updateStatusByParentSerialIds(@Param("sqlStr") String sqlStr, @Param("status") Integer status);


    List<DeliveryOrderDetailEO> selectCustomerMonthReport(Map map);
}
