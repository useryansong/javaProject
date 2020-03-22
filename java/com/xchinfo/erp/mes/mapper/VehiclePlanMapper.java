package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.mes.entity.VehiclePlanEO;
import com.xchinfo.erp.mes.entity.VehicleTrainPlanEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/6/20
 */
@Mapper
public interface VehiclePlanMapper extends BaseMapper<VehiclePlanEO> {

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


    @Select("select t.*,l.project_no,l.snp from cmp_material_plan t " +
            "LEFT JOIN bsc_material l on t.material_id = l.material_id " +
            "where t.plan_type = 2 and t.org_id = #{orgId} and t.week_date = #{weekDate} ")
    List<MaterialPlanEO> selectWeekPlanByDate(@Param("weekDate") Date weekDate,@Param("orgId") Long orgId);

    @Select("SELECT count(1) from cmp_vehicle_plan where delivery_date = #{weekDate} and org_id = #{orgId}")
    Integer selectExistCount(@Param("weekDate") Date weekDate,@Param("orgId") Long orgId);


    @Select("SELECT '汇总' as project_no, " +
            "SUM(t.train_number01) as train_number01,SUM(t.train_number02) as train_number02,SUM(t.train_number03) as train_number03,SUM(t.train_number04) as train_number04, " +
            "SUM(t.train_number05) as train_number05,SUM(t.train_number06) as train_number06,SUM(t.train_number07) as train_number07,SUM(t.train_number08) as train_number08, " +
            "SUM(t.train_number09) as train_number09,SUM(t.train_number10) as train_number10,SUM(t.train_number11) as train_number11,SUM(t.train_number12) as train_number12, " +
            "SUM(t.train_number13) as train_number13,SUM(t.train_number14) as train_number14,SUM(t.train_number15) as train_number15,SUM(t.train_number16) as train_number16, " +
            "SUM(t.train_number17) as train_number17,SUM(t.train_number18) as train_number18,SUM(t.train_number19) as train_number19,SUM(t.train_number20) as train_number20, " +
            "SUM(t.train_number21) as train_number21,SUM(t.train_number22) as train_number22,SUM(t.train_number23) as train_number23,SUM(t.train_number24) as train_number24, " +
            "SUM(t.train_number25) as train_number25,SUM(t.train_number26) as train_number26,SUM(t.train_number27) as train_number27,SUM(t.train_number28) as train_number28, " +
            "SUM(t.train_number29) as train_number29,SUM(t.train_number30) as train_number30,SUM(t.train_number31) as train_number31,SUM(t.train_number32) as train_number32, " +
            "SUM(t.train_number33) as train_number33,SUM(t.train_number34) as train_number34,SUM(t.train_number35) as train_number35,SUM(t.train_number36) as train_number36, " +
            "SUM(t.train_number37) as train_number37,SUM(t.train_number38) as train_number38,SUM(t.train_number39) as train_number39,SUM(t.train_number40) as train_number40 " +
            "from cmp_vehicle_plan t where t.delivery_date = #{weekDate} and t.org_id = #{orgId}")
    VehiclePlanEO selectSumCount(@Param("weekDate") Date weekDate,@Param("orgId") Long orgId);




    @Select("select t.*,l.dict_name as vehicle_type_name from cmp_vehicle_train_plan t " +
            "LEFT JOIN (select d.* " +
            "            from sys_dict d " +
            "            where d.type_id in (select dict_id from sys_dict where dict_code = 'cmp_vehicle_type' )" +
            "            and d.type = 2 and d.status=1 " +
            "            order by d.order_num asc) l on l.dict_code = t.vehicle_type " +
            " where t.train_date = #{weekDate} and t.org_id = #{orgId}")
    List<VehicleTrainPlanEO> selectTrainPlans(@Param("weekDate") Date weekDate,@Param("orgId") Long orgId);


    @Select("select t.* from cmp_vehicle_train_plan t where t.train_date = #{weekDate} and t.org_id = #{orgId} and t.train_number = #{trainNumber} limit 0,1")
    VehicleTrainPlanEO selectExistEntity(@Param("weekDate") Date weekDate,@Param("orgId") Long orgId,@Param("trainNumber") String trainNumber);


    @Select("SELECT c.*,h.main_warehouse_id as warehouse_id FROM " +
            "            (select t.material_id,sum(begining_balance + purchase_amount + production_amount + outsource_in_amount + consumed_in_amount + allocation_in_amount + other_receive_amount " +
            "                            - sale_amount - requisition_amount - outsource_out_amount -consumed_out_amount -allocation_out_amount -other_delivery_amount) as count " +
            "                           from v_wms_stock_account t group by t.material_id) as c " +
            "LEFT JOIN bsc_material h on h.material_id = c.material_id " +
            "where c.material_id = #{materialId}")
    StockAccountEO selectStockCount(Long materialId);


    @Select("select count(1) from wms_delivery_order_detail t " +
            "INNER JOIN wms_delivery_order l on l.delivery_id = t.delivery_order_id " +
            "where t.status = 2 and l.voucher_no = #{voucherNo} ")
    Integer selectExistOrder(String voucherNo);


    @Delete("DELETE FROM  wms_delivery_order_detail " +
            "where delivery_order_id in (select l.delivery_id from wms_delivery_order l where l.voucher_no = #{voucherNo})")
    boolean deleteDeliveryOrderDetail(String voucherNo);

    @Delete("delete from wms_delivery_order where voucher_no = #{voucherNo}")
    boolean deleteDeliveryOrder(String voucherNo);

    @Update("update cmp_vehicle_plan set ${headSql} where ${backSql} and delivery_date = #{date}")
    boolean updateVoucherNo(@Param("headSql") String headSql,@Param("backSql") String backSql,@Param("date") Date date);


    @Select("select DISTINCT project_no from cmp_vehicle_plan where project_no is not null and project_no !='' and delivery_date =#{date}")
    List<VehiclePlanEO> getAllProjectNo(Date date);



    @Select("select * from bsc_material where element_no = #{elementNo}  and org_id = #{orgId} LIMIT 0,1")
    MaterialEO selectMaterialInfo(@Param("elementNo") String elementNo,@Param("orgId") Long orgId);



    @Select("select t.* from bsc_customer t " +
            "INNER JOIN bsc_material_customer l on l.customer_id = t.customer_id " +
            "INNER JOIN bsc_material s on s.material_id = l.material_id " +
            "where s.material_id = #{Id} and l.is_default = 1 ")
    CustomerEO selectDefaultCustomer(Long Id);

    @Select("select * from bsc_customer where customer_name = #{customerName} ")
    CustomerEO selectCustomerInfo(String customerName);


    @Select("SELECT t.* FROM cmp_vehicle_plan t WHERE t.customer_id = #{customerId} and t.material_id = #{materialId} and t.delivery_date = #{date} and t.org_id = #{orgId}")
    VehiclePlanEO selectExistVehiclePlan(@Param("customerId") Long customerId,@Param("materialId") Long materialId,@Param("date") Date date,@Param("orgId") Long orgId);


    @Select("SELECT '架数' as project_no, " +
            "            SUM(c.train_number01) as train_number01,SUM(c.train_number02) as train_number02,SUM(c.train_number03) as train_number03,SUM(c.train_number04) as train_number04," +
            "            SUM(c.train_number05) as train_number05,SUM(c.train_number06) as train_number06,SUM(c.train_number07) as train_number07,SUM(c.train_number08) as train_number08," +
            "            SUM(c.train_number09) as train_number09,SUM(c.train_number10) as train_number10,SUM(c.train_number11) as train_number11,SUM(c.train_number12) as train_number12," +
            "            SUM(c.train_number13) as train_number13,SUM(c.train_number14) as train_number14,SUM(c.train_number15) as train_number15,SUM(c.train_number16) as train_number16," +
            "            SUM(c.train_number17) as train_number17,SUM(c.train_number18) as train_number18,SUM(c.train_number19) as train_number19,SUM(c.train_number20) as train_number20," +
            "            SUM(c.train_number21) as train_number21,SUM(c.train_number22) as train_number22,SUM(c.train_number23) as train_number23,SUM(c.train_number24) as train_number24," +
            "            SUM(c.train_number25) as train_number25,SUM(c.train_number26) as train_number26,SUM(c.train_number27) as train_number27,SUM(c.train_number28) as train_number28," +
            "            SUM(c.train_number29) as train_number29,SUM(c.train_number30) as train_number30,SUM(c.train_number31) as train_number31,SUM(c.train_number32) as train_number32," +
            "            SUM(c.train_number33) as train_number33,SUM(c.train_number34) as train_number34,SUM(c.train_number35) as train_number35,SUM(c.train_number36) as train_number36," +
            "            SUM(c.train_number37) as train_number37,SUM(c.train_number38) as train_number38,SUM(c.train_number39) as train_number39,SUM(c.train_number40) as train_number40 " +
            "from" +
            "(select " +
            "t.train_number01/t.snp as train_number01,t.train_number02/t.snp as train_number02,t.train_number03/t.snp as train_number03,t.train_number04/t.snp as train_number04," +
            "t.train_number05/t.snp as train_number05,t.train_number06/t.snp as train_number06,t.train_number07/t.snp as train_number07,t.train_number08/t.snp as train_number08," +
            "t.train_number09/t.snp as train_number09,t.train_number10/t.snp as train_number10,t.train_number11/t.snp as train_number11,t.train_number12/t.snp as train_number12," +
            "t.train_number13/t.snp as train_number13,t.train_number14/t.snp as train_number14,t.train_number15/t.snp as train_number15,t.train_number16/t.snp as train_number16," +
            "t.train_number17/t.snp as train_number17,t.train_number18/t.snp as train_number18,t.train_number19/t.snp as train_number19,t.train_number20/t.snp as train_number20," +
            "t.train_number21/t.snp as train_number21,t.train_number22/t.snp as train_number22,t.train_number23/t.snp as train_number23,t.train_number24/t.snp as train_number24," +
            "t.train_number25/t.snp as train_number25,t.train_number26/t.snp as train_number26,t.train_number27/t.snp as train_number27,t.train_number28/t.snp as train_number28," +
            "t.train_number29/t.snp as train_number29,t.train_number30/t.snp as train_number30,t.train_number31/t.snp as train_number31,t.train_number32/t.snp as train_number32," +
            "t.train_number33/t.snp as train_number33,t.train_number34/t.snp as train_number34,t.train_number35/t.snp as train_number35,t.train_number36/t.snp as train_number36," +
            "t.train_number37/t.snp as train_number37,t.train_number38/t.snp as train_number38,t.train_number39/t.snp as train_number39,t.train_number40/t.snp as train_number40 " +
            "from cmp_vehicle_plan t where t.snp is not null and t.snp !='' and t.snp != '0' and t.delivery_date = #{weekDate} and t.org_id = #{orgId})  c")
    VehiclePlanEO selectJSSumCount(@Param("weekDate") Date weekDate,@Param("orgId") Long orgId);


    @Select("select t.*,c.custom_string_field1 from wms_delivery_order t " +
            "left join bsc_customer c on c.customer_id = t.customer_id " +
            " where t.voucher_no = #{voucherNo}")
    DeliveryOrderEO getVoucherInfoByNo(String voucherNo);



    @Update("UPDATE wms_delivery_order SET delivery_time = #{trainTime},vehicle_type = #{vehicleType}  WHERE train_number = #{trainNumber} and org_id = #{orgId}  and delivery_date = #{trainDate} ")
    boolean updateDOrserTranTime(VehicleTrainPlanEO entity);


    @Select("SELECT count(1) from wms_delivery_order_detail t " +
            "INNER JOIN wms_delivery_order l on t.delivery_order_id = l.delivery_id " +
            "where t.status = 2 and l.train_number = #{trainNumber} and l.org_id = #{orgId}  and l.delivery_date = #{trainDate}")
    Integer selectFinishCount(VehicleTrainPlanEO entity);


    @Select("select count(1) from  wms_delivery_order where voucher_no = #{voucherNo} and status = 2  ")
    Integer selectExistFinishOrder(String voucherNo);

    @Select("select * from cmp_vehicle_plan where vehicle_plan_id in ${sqlStr}")
    List<VehiclePlanEO> getByVehiclePlanIds(@Param("sqlStr") String sqlStr);
}
