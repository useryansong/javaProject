package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.mes.entity.ProductTaskEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductTaskMapper extends BaseMapper<ProductTaskEO> {
    @Update("update mes_product_task p set p.plan_status = 1 ,p.production_count=p.require_count " +
            " where product_task_id =#{productTaskId}")
    boolean comfirn(@Param("productTaskId") Long productTaskId);

    @Update("update mes_product_task p set p.plan_status = 1 ,p.production_count = " +
            " (select d.require_count from cmp_material_distribute d where d.serial_distribute_id = #{serialDistributeId}) " +
            "  ,p.require_count = " +
            " (select f.require_count from cmp_material_distribute f where f.serial_distribute_id = #{serialDistributeId}) " +
            "  ,p.plan_version = " +
            " (select g.plan_version  from cmp_material_distribute g where g.serial_distribute_id = #{serialDistributeId}) " +
            " where product_task_id =#{productTaskId}")
    boolean planChangeComfirm(@Param("serialDistributeId") Long serialDistributeId, @Param("productTaskId") Long productTaskId);

    @Update("update mes_product_task p set p.exe_status = 0 ,p.task_version=IFNULL(p.task_version,0)+1 " +
            " where product_task_id =#{productTaskId}")
    boolean publish(@Param("productTaskId") Long productTaskId);

    @Update("update mes_product_task p set p.exe_status = null " +
            " where product_task_id =#{productTaskId}")
    boolean canclePublish(@Param("productTaskId") Long productTaskId);

    @Update("update mes_product_task p set p.exe_status = 4 " +
            " where product_task_id =#{productTaskId}")
    boolean close(@Param("productTaskId") Long productTaskId);

    @Update("update mes_product_task p set p.exe_status = null , p.production_count=#{productionCount}" +
            " where product_task_id =#{productTaskId}")
    boolean updateNumber(@Param("productionCount") double productionCount,@Param("productTaskId") Long productTaskId);

}
