package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.PurchaseGroupEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhongy
 * @date 2019/4/6
 * @update
 */
@Mapper
public interface PurchaseGroupMapper extends BaseMapper<PurchaseGroupEO> {
    /**
     * 查询所有采购组
     * @return
     */
    @Select("select * from bsc_purchase_group where status = 1")
    List<PurchaseGroupEO> selectAll();

}
