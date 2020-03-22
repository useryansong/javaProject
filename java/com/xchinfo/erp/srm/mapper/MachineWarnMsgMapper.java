package com.xchinfo.erp.srm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.scm.srm.entity.MachineWarnMsgEO;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Mapper
public interface MachineWarnMsgMapper extends BaseMapper<MachineWarnMsgEO> {
	List<MachineWarnMsgEO> getPage(Map map);

	void insertMsg(MachineWarnMsgEO msg);
}
