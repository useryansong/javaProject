package com.xchinfo.erp.srm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.StopWarnNotifyEO;

@Mapper
public interface StopWarnNotifyMapper extends BaseMapper<StopWarnNotifyEO> {

    List<StopWarnNotifyEO> getPage(Map map);

}
