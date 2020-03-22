package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.ProductOrderReleaseDetailEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/12/24
 */
@Mapper
public interface ProductOrderReleaseDetailMapper extends BaseMapper<ProductOrderReleaseDetailEO> {

    List<ProductOrderReleaseDetailEO> getList(Map map);

    Map getMinAndMaxPlanArriveDate(@Param("type") Integer type);

    List<ProductOrderReleaseDetailEO> getSummary(Map map);

    List<ProductOrderReleaseDetailEO> getDetail(Map map);

    List<ProductOrderReleaseDetailEO> getByIds(@Param("productOrderReleaseDetailIds") List<Long> productOrderReleaseDetailIds);

    void reverseByIds(@Param("productOrderReleaseDetailIds") List<Long> productOrderReleaseDetailIds,
                        @Param("status") Integer status,
                        @Param("purchaseOrderId") Long purchaseOrderId,
                        @Param("voucherNo") String voucherNo);
}
