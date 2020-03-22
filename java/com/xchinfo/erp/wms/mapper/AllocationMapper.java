package com.xchinfo.erp.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.scm.wms.entity.AllocationDetailEO;
import com.xchinfo.erp.scm.wms.entity.AllocationEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.yecat.mybatis.utils.Criteria;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface AllocationMapper extends BaseMapper<AllocationEO> {


}
