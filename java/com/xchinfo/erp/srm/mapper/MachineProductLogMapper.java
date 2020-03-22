package com.xchinfo.erp.srm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.MachineProductLogEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;

@Mapper
public interface MachineProductLogMapper extends BaseMapper<MachineProductLogEO> {

	List<MachineProductLogEO> getList(Map<String, Object> map);

	List<MachineProductLogEO> getExpandMachineStopList(Map<String, Object> map);
}
