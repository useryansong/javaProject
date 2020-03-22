package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.CheckBillDetailEO;
import com.xchinfo.erp.scm.srm.entity.CheckBillEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhongye
 * @date 2019/5/24
 */
@Mapper
public interface CheckBillMapper extends BaseMapper<CheckBillEO> {

    List<CheckBillDetailEO> selectPageByCondition(Map param);

    List<CheckBillDetailEO> selectCustomerPageByCondition(Map param);

    List<CheckBillDetailEO> hasSelectPage(Map param);

    @Select("select t.* from  srm_check_bill t " +
            " where  t.check_id= #{checkId}")
    CheckBillEO getByCheckId(@Param("checkId") Serializable checkId);

    int insertdetail (@Param("checkId") Long checkId, @Param("orgId") Long orgId, @Param("supplierId")Long supplierId, @Param("endDate")Date endDate, @Param("userName")String userName);

    int insertCustomerdetail (@Param("checkId") Long checkId, @Param("orgId") Long orgId, @Param("customerId")Long customerId, @Param("endDate")Date endDate, @Param("userName")String userName);

    @Select("select DISTINCT u.* from sys_user u " +
            "left JOIN sys_user_auth x on x.user_id = u.user_id " +
            "left JOIN sys_role_menu rm on x.role_id = rm.role_id " +
            "LEFT JOIN sys_menu m on m.menu_id = rm.menu_id " +
            "where m.permissions = #{permissions}")
    List<Map> getApprovers(@Param("permissions") String permissions);


    @Update("update srm_check_bill set status = 3 , final_approver_id = #{approverId} ,final_approver = #{approver} " +
            "where check_id = #{checkId}")
    boolean submit(@Param("approverId") Long approverId, @Param("approver") String approver, @Param("checkId") Long checkId);

    @Update("update srm_check_bill set status = #{result},approver_idea = #{opinion} ,approver_time = now()" +
            "where check_id = #{checkId}")
    boolean approve(@Param("result") Long result, @Param("checkId") Long checkId, @Param("opinion") String opinion);

    @Update("update wms_receive_order_detail od " +
            "left join srm_check_bill_detail bd ON od.receive_detail_id = bd.receive_detail_id " +
            "set od.check_status = 2 ,od.check_id = #{checkId} " +
            "where bd.check_id = #{checkId} and bd.delivery_note_no != bd.receive_voucher_no")
    boolean updateReceiveCheckStatus(@Param("checkId") Long checkId);

    @Update("update wms_delivery_order_detail od " +
            "LEFT JOIN srm_check_bill_detail bd ON od.delivery_detail_id = bd.delivery_detail_id " +
            "set od.check_status = 1 ,od.check_id = #{checkId} " +
            "where bd.check_id = #{checkId}")
    boolean updateDeliveryCheckStatus(@Param("checkId") Long checkId);

    @Update("update srm_return_order_detail od " +
            "left join srm_check_bill_detail bd on od.return_order_detail_id = bd.receive_detail_id " +
            "set od.check_status = 2 ,od.check_id = #{checkId} " +
            "where bd.check_id = #{checkId} and bd.delivery_note_no = bd.receive_voucher_no ")
    boolean updateReturnCheckStatus(@Param("checkId") Long checkId);

    @Update("update wms_product_return_detail od " +
            "LEFT JOIN srm_check_bill_detail bd ON od.return_detail_id = bd.delivery_detail_id " +
            "set od.check_status = 1 ,od.check_id = #{checkId} " +
            "where bd.check_id = #{checkId}")
    boolean updateCustomerReturnCheckStatus(@Param("checkId") Long checkId);

    /**
     * 查找需导出对账单明细
     *
     * @param checkId
     * @return
     */
    @Select("select t.*,p.unit_name,ma.project_no from  srm_check_bill_detail t " +
            " left join bsc_measurement_unit p on p.unit_id = t.unit_id " +
            "left join bsc_material ma on ma.element_no = t.element_no and ma.org_id = t.org_id " +
            " where   t.check_id= #{checkId}")
    List<CheckBillDetailEO> listexportDataByCheckId(Long checkId);

    @Update("update srm_check_bill set total_quantity = (  select sum(quantity) from srm_check_bill_detail where check_id = #{checkId}  ) where check_id = #{checkId} ")
    boolean updateTotalQuantity(@Param("checkId") Long checkId);


    @Select("select  min(SUBSTRING(de.created_time,1,10)) as begin_date  from wms_receive_order_detail de " +
            "        LEFT JOIN wms_receive_order o on o.receive_id=de.receive_order_id " +
            "        LEFT JOIN srm_delivery_note dn on o.delivery_note_id=dn.delivery_note_id " +
            "        where o.receive_type in  ('1','3') and dn.supplier_id = #{supplierId} and o.org_id=#{orgId}  and  #{endDate} >=SUBSTRING(de.created_time,1,10) and check_status = 0")
    CheckBillEO getSupplierBeginDate(@Param("supplierId") Long supplierId,@Param("endDate") String endDate,@Param("orgId") Long orgId);

    @Select("select  min(SUBSTRING(de.created_time,1,10)) as begin_date  from wms_delivery_order_detail de " +
            "        LEFT JOIN wms_delivery_order o on o.delivery_id=de.delivery_order_id " +
            "        where o.delivery_type = 1 and o.customer_id = #{customerId} and o.org_id=#{orgId}  and  #{endDate} >=SUBSTRING(de.created_time,1,10) and check_status = 0")
    CheckBillEO getCustomerBeginDate(@Param("customerId") Long customerId,@Param("endDate") String endDate,@Param("orgId") Long orgId);

    @Update("update wms_receive_order_detail rod left join srm_check_bill_detail cbd on rod.receive_detail_id = cbd.receive_detail_id " +
            "set rod.join_check_status = 1 where cbd.check_id = #{checkId} ")
    boolean updateReceiveJoinStatus(@Param("checkId") Long checkId);

    @Update("update wms_delivery_order_detail dod left join srm_check_bill_detail cbd on dod.delivery_detail_id = cbd.delivery_detail_id " +
            "set dod.join_check_status = 1 where cbd.check_id =  #{checkId} ")
    boolean updateDeliveryJoinStatus(@Param("checkId") Long checkId);

    List<CheckBillDetailEO> listExportTableDataByCheckId(@Param("checkId") Long checkId);

    int updateReceiveCheck(@Param("receiveDetailIds") Set receiveDetailIds, @Param("checkStatus") Integer checkStatus);

    int updateReturnCheck(@Param("returnDetailIds") Set returnDetailIds, @Param("checkStatus") Integer checkStatus);

    List<CheckBillDetailEO> getByIds(@Param("checkBillDetailIds") Set checkBillDetailIds);
}
