package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.PartnerAddressEO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface PartnerAddressMapper extends BaseMapper<PartnerAddressEO> {
    /**
     * 根据供应商查找地址
     *
     * @param supplierId
     * @return
     */
    @Select("select pa.* " +
            "from bsc_supplier s " +
            "left join bsc_partner_address pa on s.supplier_id = pa.partner_id " +
            "where pa.partner_type = 1 and s.supplier_id = #{supplierId} ")
    List<PartnerAddressEO> selectBySupplier(Long supplierId);

    /**
     * 根据供应商删除
     *
     * @param supplierId
     * @return
     */
    @Delete("delete from bsc_partner_address where partner_type = 1 and partner_id = #{supplierId}")
    int deleteBySupplier(Long supplierId);


    /**
     * 查询地址
     *
     * @param partnerId
     * @return
     */
    @Select("select pm.* " +
            "from bsc_partner_address pm " +
            "inner join bsc_customer m on m.customer_id = pm.partner_id " +
            "where pm.partner_type = 2 and pm.partner_id = #{partnerId}")
    List<PartnerAddressEO> selectByPartnerId(Long partnerId);

    /**
     * 删除地址
     *
     * @param partnerId
     * @return
     */
    @Delete("delete from bsc_partner_address where partner_type = 2 and partner_id = #{partnerId}")
    Integer removeByPartnerId(Long partnerId);

    /**
     * 查询客户地址统计
     *
     * @param partnerId
     * @return
     */
    @Select("select count(1) from bsc_partner_address where partner_type = 2 and partner_id = #{partnerId}")
    Integer selectCountByPartnerId(Long partnerId);


    /**
     * 更新客户地址默认收发货地址其他默认为0
     *
     * @param
     * @return
     */
    @Update("update bsc_partner_address set default_recieve_delivery = 0 where partner_type = 2 and address_id != #{address_id}")
    Integer updateDefaultRDByContactId(Long address_id);

    /**
     * 更新客户地址默认开票地址其他默认为0
     *
     * @param
     * @return
     */
    @Update("update bsc_partner_address set default_invoice = 0 where partner_type = 2 and address_id != #{address_id}")
    Integer updateDefaultIByContactId(Long address_id);

    /**
     * 更新客户地址默认收付款地址其他默认为0
     *
     * @param
     * @return
     */
    @Update("update bsc_partner_address set default_collect_payment = 0 where partner_type = 2 and address_id != #{address_id}")
    Integer updateDefaultCPByContactId(Long address_id);

    /**
     * 查询客户默认收发货地址默认统计
     *
     * @param
     * @return
     */
    @Select("select count(1) from bsc_partner_address where partner_type = 2 and partner_id = #{partnerId} and address_id != #{address_id} and default_recieve_delivery = 1")
    Integer selectCountRDByContactId(@Param("address_id") Long address_id, @Param("partnerId") Long partnerId);

    /**
     * 查询客户默认开票地址默认统计
     *
     * @param
     * @return
     */
    @Select("select count(1) from bsc_partner_address where partner_type = 2 and partner_id = #{partnerId} and address_id != #{address_id} and default_invoice = 1")
    Integer selectCountIByContactId(@Param("address_id") Long address_id, @Param("partnerId") Long partnerId);

    /**
     * 查询客户默认收付款地址默认统计
     *
     * @param
     * @return
     */
    @Select("select count(1) from bsc_partner_address where partner_type = 2 and partner_id = #{partnerId} and address_id != #{address_id} and default_collect_payment = 1")
    Integer selectCountCPByContactId(@Param("address_id") Long address_id, @Param("partnerId") Long partnerId);

    @Select("select * from bsc_partner_address where address_id = #{addressId}")
    PartnerAddressEO queryById(Long addressId);
}
