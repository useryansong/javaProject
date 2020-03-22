package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.CustomerErpCodeEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface CustomerErpCodeMapper extends BaseMapper<CustomerErpCodeEO> {
    @Select("select count(1) from bsc_customer_erp_code where customer_id = #{customerId} and org_id = #{orgId} ")
    int checkRepeat(@Param("customerId") Long customerId, @Param("orgId") Long orgId);

    @Select("select current_value from sys_sequence where name='bsc_customer_erp_code'")
    int getMaxSequence();

    @Update("update sys_sequence set current_value = #{currentValue} where name='bsc_customer_erp_code'")
    void updateToMaxSequence(Integer currentValue);
}
