package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.SupplierErpCodeEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface SupplierErpCodeMapper  extends BaseMapper<SupplierErpCodeEO> {
    @Select("select count(1) from bsc_supplier_erp_code where supplier_id = #{supplierId} and org_id = #{orgId} ")
    int checkRepeat(@Param("supplierId") Long supplierId, @Param("orgId") Long orgId);

    @Select("select current_value from sys_sequence where name='bsc_supplier_erp_code'")
    int getMaxSequence();

    @Update("update sys_sequence set current_value = #{currentValue} where name='bsc_supplier_erp_code'")
    void updateToMaxSequence(Integer currentValue);
}
