package com.xchinfo.erp.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.mes.entity.StampingMaterialConsumptionQuotaEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface StampingMaterialConsumptionQuotaMapper  extends BaseMapper<StampingMaterialConsumptionQuotaEO> {
    /**
     * 更改状态
     *
     * @return
     */
    @Update("update mes_stamping_material_consumption_quota  set status = #{status} where stamping_material_consumption_quota_id =#{id}")
    boolean updateStatusById(@Param("status") int status, @Param("id") Long id);

    List<Map> hasProject(Long userId);

    @Select("select * from mes_stamping_material_consumption_quota where element_no = #{elementNo} and org_id = #{orgId}")
    StampingMaterialConsumptionQuotaEO getByElementNoAndOrg(@Param("elementNo") String elementNo, @Param("orgId") Long orgId);

    List<StampingMaterialConsumptionQuotaEO> selectInandoutputsReport(Map map);

    List<StampingMaterialConsumptionQuotaEO> biwReport(Map map);

    @Select("select * from mes_stamping_material_consumption_quota where material_id = #{materialId} and original_material_id = #{originalMaterialId}")
    StampingMaterialConsumptionQuotaEO getByMaterialIdAndOriginalMaterialId(@Param("materialId") Long materialId, @Param("originalMaterialId") Long originalMaterialId);
}
