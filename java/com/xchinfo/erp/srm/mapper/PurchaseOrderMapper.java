package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/9
 */
@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrderEO> {


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



    @Select("select t.*,m.supplier_code,m.supplier_name,p.inventory_code,p.material_code,p.material_name,p.is_product,p.is_purchase,p.element_no,p.org_id,k.unit_name " +
            "            from bsc_material_supplier t " +
            "            inner join bsc_supplier m on m.supplier_id = t.supplier_id " +
            "            inner join bsc_material p on p.material_id = t.material_id " +
            "            left join bsc_measurement_unit k on k.unit_id = p.first_measurement_unit "+
            "            where m.supplier_name = #{supplierName} and p.element_no = #{elementNo} and p.org_id = #{orgId} limit 0,1 ")
    MaterialSupplierEO selectSupplierMaterial(@Param("supplierName") String supplierName, @Param("elementNo") String elementNo,@Param("orgId") Long orgId);

    @Select("select count(1) from  cmp_material_distribute  t where t.material_id =  #{materialId}  and t.supplier_id = #{supplierId}  and t.week_date = #{weekDate} and t.distribute_type = #{distributeType}")
    Integer selectExistCount(@Param("materialId") Long materialId, @Param("supplierId") Long supplierId, @Param("weekDate") Date weekDate, @Param("distributeType") Integer distributeType);


    @Update("update srm_delivery_plan set status = #{status} where purchase_order_id = #{Id} and status = #{oldStatus} ")
    boolean updateDPlanStatus(@Param("Id") Long Id,@Param("status") Integer status,@Param("oldStatus") Integer oldStatus);

    @Select("select * from srm_delivery_plan where purchase_order_id = #{Id}")
    DeliveryPlanEO selectDeliveryPlan(Long Id);

    @Update("update srm_delivery_plan t set t.plan_delivery_quantity = #{amount} where  t.purchase_order_id = #{Id} ")
    boolean updateDPmount(@Param("Id") Long Id,@Param("amount") Double amount);

    @Select("select t.purchase_order_id from srm_purchase_order t where t.purchase_order_id in ${sqlStr} and t.status = #{status} and t.plan_arrive_date >= #{beginDate}  and t.plan_arrive_date <= #{endDate} and t.org_id = #{orgId}")
    List<Long> selectOrderInfoByIds(@Param("sqlStr") String sqlStr,@Param("status") Integer status,@Param("beginDate") Date beginDate,@Param("endDate") Date endDate,@Param("orgId") Long orgId);

    @Select("select t.purchase_order_id from srm_purchase_order t where t.status = #{status} and t.plan_arrive_date >= #{beginDate}  and t.plan_arrive_date <= #{endDate} and t.org_id = #{orgId} and t.type = #{type}")
    List<Long> selectAllOrderId(@Param("status") Integer status,@Param("beginDate") Date beginDate,@Param("endDate") Date endDate,@Param("orgId") Long orgId,@Param("type") Integer type);


    @Select("select t.*,m.supplier_code,m.supplier_name,p.inventory_code,p.material_code,p.material_name,p.is_product,p.is_purchase,p.element_no,p.org_id,k.unit_name, " +
            "            case when s.stock_count = '' or s.stock_count is null " +
            "            then 0 " +
            "            else s.stock_count " +
            "            end as stock_count " +
            "            from bsc_material_supplier t " +
            "            inner join bsc_supplier m on m.supplier_id = t.supplier_id " +
            "            inner join bsc_material p on p.material_id = t.material_id " +
            "            left join bsc_measurement_unit k on k.unit_id = p.first_measurement_unit "+
            "            left join (select sum((begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount " +
            "                        - sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount )) as stock_count,h.material_id from v_wms_stock_account h GROUP BY h.material_id) s on s.material_id = t.material_id " +
            "            where t.is_default = 1 and p.element_no = #{elementNo} and p.org_id = #{orgId} limit 0,1 ")
    MaterialSupplierEO selectDefaultSupplier(@Param("elementNo") String elementNo,@Param("orgId") Long orgId);


    @Select("select * from bsc_material where element_no = #{elementNo} and status=1 and org_id = #{orgId} limit 0,1")
    MaterialEO selectMaterialInfo(@Param("elementNo") String elementNo,@Param("orgId") Long orgId);


    List<PurchaseOrderEO> selectNewPage(Map map);

    @Select("select po.*, mp.parent_serial_id " +
            "from srm_purchase_order po " +
            "left join cmp_material_plan mp on po.serial_id = mp.serial_id " +
            "where po.serial_id in ${sqlStr}")
    List<PurchaseOrderEO> getBySerialIds(@Param("sqlStr") String sqlStr);

    @Delete("delete from srm_purchase_order where purchase_order_id in ${deleteSqlStr}")
    void deleteByPurchaseOrderIds(@Param("deleteSqlStr") String deleteSqlStr);

    @Select("select po.* " +
            "from srm_purchase_order po " +
            "where po.purchase_order_id in ${sqlStr}")
    List<PurchaseOrderEO> getByPurchaseOrderIds(String sqlStr);


    List<DeliveryNoteDetailEO> selectSupplierMonthReport(Map map);

    List<PurchaseOrderEO> getPageByParentSerialId(Map map);
}
