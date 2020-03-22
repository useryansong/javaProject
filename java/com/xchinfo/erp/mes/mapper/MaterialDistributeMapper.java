package com.xchinfo.erp.mes.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.mes.entity.MaterialDistributeEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface MaterialDistributeMapper extends BaseMapper<MaterialDistributeEO> {

    /***
     * 根据物料计划ID删除
     * @return
     */
    @Delete("delete from cmp_material_distribute  where serial_id = #{id}")
    boolean deleteBySerialId(@Param("id") Long id);


    /***
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    @Update("update cmp_material_distribute set status = #{status} where serial_distribute_id = #{id}")
    boolean updateStatusById(@Param("id") Long id, @Param("status") Integer status);



    /***
     *查询是否存在（采购订单数据）
     * @param Id
     * @return
     */
    @Select("select count(1) from srm_purchase_order t " +
            "INNER JOIN cmp_material_distribute s on t.serial_distribute_id = s.serial_distribute_id " +
            "where t.status != 0 and t.serial_distribute_id = #{id}")
    Integer selectPuchaseCount(Long Id);

    /***
     *删除采购订单数据
     * @return
     */
    @Delete("delete from srm_purchase_order where serial_distribute_id = #{Id}")
    boolean deletePuchaseById(Long Id);


    /***
     * 查询月计划是否都已完成或关闭
     * @param Id
     * @return
     */
    @Select("select count(1) from cmp_material_distribute t where t.status < 3 and t.serial_id = #{Id}")
    Integer selectPlanFinishCount(Long Id);


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



    @Select("select count(1) from  cmp_material_distribute  t where t.material_id =  #{materialId}  and t.supplier_id = #{supplierId}  and t.week_date = #{weekDate} and t.distribute_type = #{distributeType} and t.serial_distribute_id != #{Id}")
    Integer selectExistCountById(@Param("materialId") Long materialId, @Param("supplierId") Long supplierId, @Param("weekDate") Date weekDate,@Param("Id") Long Id,@Param("distributeType") Integer distributeType);

    @Select("select count(1) from  cmp_material_distribute  t where t.material_id =  #{materialId}  and t.supplier_id = #{supplierId}  and t.week_date = #{weekDate} and t.distribute_type = #{distributeType}")
    Integer selectExistCount(@Param("materialId") Long materialId, @Param("supplierId") Long supplierId, @Param("weekDate") Date weekDate,@Param("distributeType") Integer distributeType);



    @Select("select t.*,m.supplier_code,m.supplier_name,p.inventory_code,p.material_code,p.material_name,p.is_product,p.is_purchase,p.element_no,p.specification, " +
            "            case when s.stock_count = '' or s.stock_count is null " +
            "            then 0 " +
            "            else s.stock_count " +
            "            end as stock_count " +
            "            from bsc_material_supplier t " +
            "            inner join bsc_supplier m on m.supplier_id = t.supplier_id " +
            "            inner join bsc_material p on p.material_id = t.material_id " +
            "            left join (select sum((begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount " +
            "                        - sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount )) as stock_count,h.material_id from v_wms_stock_account h GROUP BY h.material_id) s on s.material_id = t.material_id " +
            "            where m.supplier_name = #{supplierName} and p.element_no = #{elementNo} limit 0,1 ")
    MaterialSupplierEO selectSupplierMaterial(@Param("supplierName") String supplierName,@Param("elementNo") String elementNo);

}
