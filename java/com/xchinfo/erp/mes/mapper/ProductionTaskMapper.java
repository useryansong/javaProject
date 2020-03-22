package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.mes.entity.ProductionTaskEO;
import com.xchinfo.erp.mes.entity.ProductionTaskTreeEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface ProductionTaskMapper extends BaseMapper<ProductionTaskEO> {

    /**
     * 按天查看任务
     *
     * @param day
     * @return
     */
    @Select("select t.*, o.order_no, mt.material_code, mt.material_name, mc.machine_code, mc.machine_name, p.process_code, p.process_name " +
            "from mes_production_task t " +
            "left outer join mes_production_order o on o.order_id = t.order_id " +
            "inner join bsc_material mt on t.material_id = mt.material_id " +
            "inner join bsc_machine mc on t.machine_id = mc.machine_id " +
            "inner join bsc_process p on t.process_id = p.process_id " +
            "where date_format(t.start_time, '%Y-%m-%d') = str_to_date(#{day}, '%Y-%m-%d') " +
            "      or date_format(t.end_time, '%Y-%m-%d') = str_to_date(#{day}, '%Y-%m-%d') ")
    List<ProductionTaskEO> selectForDate(String day);

    /**
     * 根据订单查询任务
     *
     * @param orderId
     * @return
     */
    @Select("select t.*, o.order_no, mt.material_code, mt.material_name, mc.machine_code, mc.machine_name, p.process_code, p.process_name " +
            "from mes_production_task t " +
            "left outer join mes_production_order o on o.order_id = t.order_id " +
            "inner join bsc_material mt on t.material_id = mt.material_id " +
            "inner join bsc_machine mc on t.machine_id = mc.machine_id " +
            "inner join bsc_process p on t.process_id = p.process_id " +
            "where t.order_id = #{orderId}")
    List<ProductionTaskEO> selectForOrder(Long orderId);

    /**
     * 查找所有任务（树形结构）
     *
     * @return
     */
    @Select("select * from v_production_task_tree")
    List<ProductionTaskTreeEO> selectForTree();
}
