package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.PartnerContactEO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface PartnerContactMapper extends BaseMapper<PartnerContactEO> {
    /**
     * 根据供应商查找联系人
     *
     * @param supplierId
     * @return
     */
    @Select("select pc.* " +
            "from bsc_supplier s " +
            "left join bsc_partner_contact pc on s.supplier_id = pc.partner_id " +
            "where pc.partner_type = 1 and s.supplier_id = #{supplierId} ")
    List<PartnerContactEO> selectBySupplier(Long supplierId);

    /**
     * 根据供应商删除
     *
     * @param supplierId
     * @return
     */
    @Delete("delete from bsc_partner_contact where partner_type = 1 and partner_id = #{supplierId}")
    int deleteBySupplier(Long supplierId);

    /**
     * 查询联系人
     *
     * @param partnerId
     * @return
     */
    @Select("select pm.*,d.address as address_name " +
            "from bsc_partner_contact pm " +
            "inner join bsc_customer m on m.customer_id = pm.partner_id " +
            "LEFT JOIN bsc_partner_address d on pm.address = d.address_id " +
            "where pm.partner_type = 2 and pm.partner_id = #{partnerId}")
    List<PartnerContactEO> selectByPartnerId(Long partnerId);

    /**
     * 删除联系人
     *
     * @param partnerId
     * @return
     */
    @Delete("delete from bsc_partner_contact where partner_type = 2 and partner_id = #{partnerId}")
    Integer removeByPartnerId(Long partnerId);

    /**
     * 查询客户联系人统计
     *
     * @param partnerId
     * @return
     */
    @Select("select count(1) from bsc_partner_contact where partner_type = 2 and partner_id = #{partnerId}")
    Integer selectCountByPartnerId(Long partnerId);

    /**
     * 更新客户联系人其他默认为0
     *
     * @param
     * @return
     */
    @Update("update bsc_partner_contact set default_contact = 0 where partner_type = 2 and contact_id != #{contact_id}")
    Integer updateDefaultByContactId(Long contact_id);

    /**
     * 查询客户联系人统计
     *
     * @param
     * @return
     */
    @Select("select count(1) from bsc_partner_contact where partner_type = 2 and partner_id = #{partnerId} and contact_id != #{contact_id} and default_contact = 1")
    Integer selectCountCByContactId(@Param("contact_id") Long contact_id, @Param("partnerId") Long partnerId);
}
