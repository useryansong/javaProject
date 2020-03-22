package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.mes.entity.WorkingProcedureTimeEO;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import org.apache.ibatis.annotations.*;
import org.yecat.mybatis.utils.Criteria;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/9/2
 */
@Mapper
public interface ProductOrderMapper extends BaseMapper<ProductOrderEO> {

    @Select("select po.*, mp.parent_serial_id " +
            "from srm_product_order po " +
            "left join cmp_material_plan mp on po.serial_id = mp.serial_id " +
            "where po.serial_id in ${sqlStr}")
    List<ProductOrderEO> getBySerialIds(@Param("sqlStr") String sqlStr);

    @Delete("delete from srm_product_order where product_order_id in ${deleteSqlStr}")
    void deleteByProductOrderIds(@Param("deleteSqlStr") String deleteSqlStr);

    @Select("select * from srm_product_order where product_order_id in ${sqlStr}")
    List<ProductOrderEO> getByProductOrderIds(@Param("sqlStr") String sqlStr);

    List<ProductOrderEO> selectSchedulingPage(Map map);

    List<ProductOrderEO> getCounts(@Param("sqlStr") String sqlStr, @Param("voucherDate") String voucherDate);

    List<ProductOrderEO> getPlanProduceQuantitys(@Param("sqlStr") String sqlStr, @Param("planFinishDateStart") String planFinishDateStart, @Param("planFinishDateEnd") String planFinishDateEnd);

    List<ProductOrderEO> getBiwPlanProduceQuantitys(@Param("sqlStr") String sqlStr, @Param("planFinishDateStart") String planFinishDateStart, @Param("planFinishDateEnd") String planFinishDateEnd);

    List<ProductOrderEO> getPageByParentSerialId(Map map);

    List<ProductOrderEO> getProduceReportQuantitys(@Param("sqlStr") String sqlStr, @Param("planFinishDateStart") String planFinishDateStart, @Param("planFinishDateEnd") String planFinishDateEnd);

    @Select("select * from bsc_machine where machine_name =#{machineNo} and org_id=#{orgId}")
    MachineEO selectMachineEO(@Param("machineNo") String machineNo, @Param("orgId") Long orgId);

    @Select(" select  *  from  mes_working_procedure_time where working_procedure_time_id = (select max(working_procedure_time_id) from mes_working_procedure_time where element_no =#{elementNo} and working_procedure_code=#{workingProcedureCode} and org_id=#{orgId})")
    WorkingProcedureTimeEO selecProcedureTimeEOByProcedureCode(@Param("elementNo") String elementNo,@Param("workingProcedureCode") String workingProcedureCode, @Param("orgId") Long orgId);

    @Select("select * from mes_working_procedure_time where element_no =#{elementNo} and org_id=#{orgId} and working_procedure_code = (select max(working_procedure_code) from mes_working_procedure_time  where element_no =#{elementNo} and org_id=#{orgId}) ")
    WorkingProcedureTimeEO selectProcedureTimeEO(@Param("elementNo") String elementNo, @Param("orgId") Long orgId);

    @Select("select po.* from srm_product_order po inner join  srm_schedule_order so on po.product_order_id = so.product_order_id  where po.material_id =#{materialId} and po.org_id= #{orgId} and po.plan_finish_date = #{date} and so.class_order = #{classOrder} AND so.machine_id= #{machineId}")
    ProductOrderEO selectProductOrderEO(@Param("materialId") Long materialId, @Param("orgId") Long orgId, @Param("date") String date,@Param("classOrder") String classOrder,@Param("machineId") Long machineId);

    @Select("select po.* from srm_product_order po inner join  srm_schedule_order so on po.product_order_id = so.product_order_id INNER JOIN mes_working_procedure_time pt ON so.working_procedure_time_id = pt.working_procedure_time_id  where po.material_id =#{materialId} and po.org_id= #{orgId} and po.plan_finish_date = #{date} and so.class_order = #{classOrder} and pt.working_procedure_code =#{workingProcedureCode}")
    ProductOrderEO selectProductOrderEO2(@Param("materialId") Long materialId, @Param("orgId") Long orgId, @Param("date") String date,@Param("classOrder") String classOrder,@Param("workingProcedureCode") String workingProcedureCode);


    @Select("select po.* from srm_product_order po  where po.material_id =#{materialId} and po.org_id= #{orgId} and po.plan_finish_date = #{date}")
    ProductOrderEO selectProductOrderEOtemp(@Param("materialId") Long materialId, @Param("orgId") Long orgId, @Param("date") String date );

    @Select("SELECT * FROM srm_schedule_order s1 WHERE import_status=0 and s1.import_procedure_code = ( SELECT MAX(s2.import_procedure_code) FROM srm_schedule_order s2 GROUP BY s2.import_material_id ,s2.plan_product_date HAVING s1.import_material_id = s2.import_material_id and s1.plan_product_date = s2.plan_product_date)")
    List<ScheduleOrderEO> getScheduleOrderEO();

    @Update("update srm_schedule_order set product_order_id =#{productOrderId}  ,  import_status = 1  where import_status=0  and import_material_id = #{materialId} and plan_product_date = #{PlanProductDate} and org_id = #{orgId} ")
    void updateScheduleEO(@Param("materialId") Long materialId, @Param("orgId") Long orgId,@Param("productOrderId") Long productOrderId,@Param("PlanProductDate") Date PlanProductDate);

    @Update("update srm_schedule_order set check_status = 1")
    void updateAllCheckStatus();


}
