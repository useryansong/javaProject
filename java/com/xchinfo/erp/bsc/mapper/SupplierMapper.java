package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.SupplierEO;
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
public interface SupplierMapper extends BaseMapper<SupplierEO> {
    /**
     * 查询所有供应商
     * @return
     */
    @Select("select * from bsc_supplier where status = 1")
    List<SupplierEO> selectAll();

    /**
     * 根据供应商名称查询供应商信息
     * @return
     */
    @Select("select * from bsc_supplier where supplier_name = #{name}")
    SupplierEO selectByName(String name);

    /**
     * 根据供应商id及状态更新供应商状态
     * @return
     */
    @Update("update bsc_supplier set status = #{status} where supplier_id = #{supplierId}")
    int updateStatusById(@Param("supplierId") Long supplierId, @Param("status") Integer status);

    /**
     * 根据供应商id及状态更新系统账号状态
     * @return
     */
    @Update("update bsc_supplier set account_status = #{accountStatus} where supplier_id = #{supplierId}")
    int updateAccountStatusById(@Param("supplierId") Long supplierId, @Param("accountStatus") Integer accountStatus);

    /**
     * 根据供应商编码获取供应商信息
     *
     * @return
     */
    @Select("select * from bsc_supplier where supplier_code = #{supplierCode}")
    SupplierEO getBySupplierCode(String supplierCode);


    @Select("select *  from bsc_supplier where supplier_code = #{code}")
    List<SupplierEO>  getSupplierByCode(@Param("code") String code);

    @Select("select * from bsc_supplier where supplier_name = #{supplierName} and status = 1")
    SupplierEO getBySupplierName(@Param("supplierName") String supplierName);
}
