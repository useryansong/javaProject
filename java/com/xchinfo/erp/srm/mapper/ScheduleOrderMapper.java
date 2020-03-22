package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/9/11
 */
@Mapper
public interface ScheduleOrderMapper extends BaseMapper<ScheduleOrderEO> {

    @Select("select wpt.working_procedure_time_id, wpt.working_procedure_code,wpt.working_procedure_name," +
            "d.dict_name as working_procedure_type_name,o.org_name as workshop_name,m.machine_id,m.machine_code,wpt.ct," +
            "   (select " +
            "       ifnull(sum(so.plan_produce_quantity),0) " +
            "   from srm_schedule_order so " +
            "   where so.product_order_id = #{productOrderId} " +
            "   and so.working_procedure_time_id = wpt.working_procedure_time_id) AS sum_plan_produce_quantity " +
            "from mes_working_procedure_time wpt " +
            "left join sys_dict d on wpt.working_procedure_type = d.dict_id " +
            "left join sys_org o on wpt.workshop_id = o.org_id " +
            "left join bsc_machine m on wpt.machine_id = m.machine_id " +
            "where wpt.element_no = #{elementNo} and wpt.org_id = #{orgId} " +
            "order by wpt.working_procedure_code asc ")
    List<ScheduleOrderEO> getWorkingProcedureTime(@Param("elementNo") String elementNo, @Param("orgId") Long orgId, @Param("productOrderId") Long productOrderId);

    @Select("select * from srm_schedule_order " +
            "where product_order_id = #{productOrderId} and working_procedure_time_id = #{workingProcedureTimeId}")
    List<ScheduleOrderEO> getList(@Param("productOrderId") Long productOrderId, @Param("workingProcedureTimeId") Long workingProcedureTimeId);

    @Select("select prt.working_procedure_code,po.* from srm_schedule_order po " +
            "left join mes_working_procedure_time prt on prt.working_procedure_time_id = po.working_procedure_time_id  " +
            "where product_order_id = #{productOrderId} order by prt.working_procedure_code asc ")
    List<ScheduleOrderEO> getListByProductOrderId(Long productOrderId);

    @Delete("delete from srm_schedule_order where schedule_order_id in ${sqlStr}")
    void deleteScheduleOrder(@Param("sqlStr") String sqlStr);

	List<ScheduleOrderEO> getScheduleOrder(Map<String, Object> map) ;

	List<Map<String, Object>> getWorkShop(Map<String, Object> map);
	
    List<ScheduleOrderEO> getActualProduceQuantitys(@Param("sqlStr") String sqlStr, @Param("planProductDateStart") String planProductDateStart, @Param("planProductDateEnd") String planProductDateEnd);

    List<ScheduleOrderEO> getBiwActualProduceQuantitys(@Param("sqlStr") String sqlStr, @Param("planProductDateStart") String planProductDateStart, @Param("planProductDateEnd") String planProductDateEnd);

    List<ScheduleOrderEO> selectWorkingProcedureTime(@Param("elementNo") String elementNo, @Param("orgId") Long orgId, @Param("planProductDate") String planProductDate);

    List<ScheduleOrderEO> selectNewPage(Map map);

    @Select("select * from srm_schedule_order where schedule_order_id in ${sqlStr}")
    List<ScheduleOrderEO> getScheduleOrderByIds(@Param("sqlStr") String sqlStr);

    @Delete("delete from srm_schedule_order where product_order_id = #{sqlStr}")
    void removeByProductOrderId(@Param("sqlStr") Long sqlStr);

    List<ScheduleOrderEO> listAllWorkOrder(Map map);

    ScheduleOrderEO getWorkOrderById(@Param("scheduleOrderId") Long scheduleOrderId);

    List<ScheduleOrderEO> selectPageByViewMode(Map param);

    List<ScheduleOrderEO> getHumanAssessPage(Map map);

    @Update("update srm_schedule_order set open_status = #{openStatus} where schedule_order_id = #{scheduleOrderId}")
    void changeOpenStatus(@Param("scheduleOrderId") Long scheduleOrderId, @Param("openStatus") Integer openStatus);
}
