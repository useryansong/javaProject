package com.xchinfo.erp.srm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.StopWarnLogEO;

@Mapper
public interface StopWarnLogMapper extends BaseMapper<StopWarnLogEO> {

    List<StopWarnLogEO> getPage(Map map);

}
