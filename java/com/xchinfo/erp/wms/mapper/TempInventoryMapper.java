package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.wms.entity.TempInventoryEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TempInventoryMapper extends BaseMapper<TempInventoryEO> {

    List<TempInventoryEO> getByInventoryId(@Param("inventoryId") Long inventoryId);
}
