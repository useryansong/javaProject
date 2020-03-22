package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface MaterialCustomerMapper extends BaseMapper<MaterialCustomerEO> {

    /**
     * 查询所有客户
     *
     * @return
     */
    @Select("select * from bsc_material_customer")
    List<MaterialCustomerEO> selectAll();


    /**
     *设置默认
     *
     * @return
     */
    @Update("update bsc_material_customer set is_default = 1 where material_customer_id = #{materialCustomerId}")
    boolean updateStatusById(Long materialCustomerId);


    /**
     *设置其他为非默认
     *
     * @return
     */
    @Update("update bsc_material_customer set is_default = 0 where material_customer_id != #{materialCustomerId} and material_id = #{materialId}")
    boolean updateOtherStatusById(@Param("materialCustomerId") Long materialCustomerId, @Param("materialId") Long materialId);

    /**
     *查询数据统计
     *
     * @return
     */
    @Select("select count(1) from bsc_material_customer where material_id = #{materialId}")
    Integer selectCustomerMaterilaCount(Long materialId);

    /**
     *查询数据是否存在
     *
     * @return
     */
    @Select("select count(1) from bsc_material_customer where material_id = #{materialId} and customer_id = #{customerId}")
    Integer selectIsExistCount(MaterialCustomerEO entity);


    /**
     *查询更新后的数据是否存在
     *
     * @return
     */
    @Select("select count(1) from bsc_material_customer where material_id = #{materialId} and customer_id = #{customerId} and material_customer_id != #{materialCustomerId}")
    Integer selectUpdateIsExistCount(MaterialCustomerEO entity);

    @Select("select * from bsc_material_customer where material_id = #{materialId} and customer_id = #{customerId} ")
    MaterialCustomerEO getByMaterialIdAndCustomerId(@Param("materialId") Long materialId, @Param("customerId") Long customerId);
}
